package uk.ac.ebi.quickgo.service.statistic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.solr.client.solrj.response.FacetField.Count;
import org.springframework.stereotype.Service;

import uk.ac.ebi.quickgo.service.annotation.AnnotationService;
import uk.ac.ebi.quickgo.service.miscellaneous.MiscellaneousService;
import uk.ac.ebi.quickgo.service.statistic.type.StatsTerm;
import uk.ac.ebi.quickgo.solr.query.model.annotation.enums.AnnotationField;
import uk.ac.ebi.quickgo.statistic.COOccurrenceStatsTerm;

/**
 * Calculate statistics based on annotations or proteins
 * @author cbonill
 *
 */
@Service
public class StatisticServiceImpl implements StatisticService{

	public static final int NUM_OF_TOP_GOIDS = 8; //80;
	AnnotationService annotationService;

	MiscellaneousService miscellaneousService;

	//Loaded by spring therefore a singleton
	StatisticsUtil statisticsUtil;

	// Total number annotations
	private long numberAnnotations = -1;

	// Total number proteins
	private long numberProteins = -1;

	// Terms counts
	private  List<Count> terms;

	@Override
	public Set<StatsTerm> statisticsByAnnotation(String query, String field) {
		// Process query and calculate necessary data (if any)
		processQuery(query, field);

		// Remove "go" term
		if(field.equals(AnnotationField.GOID.getValue())){
			if(terms!=null && terms.size() > 0 && terms.get(0).getName().equals("go")){
				terms.remove(0);
			}
		}

		// Generate stats terms from the term results
		TreeSet<StatsTerm> statsTerms = new TreeSet<>();
		for (Count term : terms) {
			statsTerms.add(new StatsTerm(term.getName(), statisticsUtil.getName(term.getName(), field), StatisticsMath.calculatePercentage(term.getCount(), numberAnnotations),term.getCount()));
		}
		return statsTerms.descendingSet();// Highest first
	}

	@Override
	public Set<StatsTerm> statisticsByProtein(String query, String field) {
		// Total number of proteins
		//numberProteins = statisticsUtil.calculateNumberProteins(query, numberProteins);
		numberProteins = annotationService.getTotalNumberProteins(query);

		// Load names
		statisticsUtil.loadNames(field);
		List<Count> counts = annotationService.getFacetFields(query, null, field, NUM_OF_TOP_GOIDS);// Facet by field
		//List<Count> counts = annotationService.getFacetFields(query, null, field, Integer.MAX_VALUE);// TODO Uncomment this for downloading of statistics
		return statisticsByProteinInParallel(field, query, counts);
	}

	@Override
	public Set<COOccurrenceStatsTerm> allCOOccurrenceStatistics(String ontologyTermID) {
		return miscellaneousService.allCOOccurrenceStatistics(ontologyTermID);
	}

	@Override
	public Set<COOccurrenceStatsTerm> nonIEACOOccurrenceStatistics(	String ontologyTermID) {
		return miscellaneousService.nonIEACOOccurrenceStatistics(ontologyTermID);
	}

	/**
	 * Process query and calculate necessary data
	 * @param query Query to run
	 * @param field Field to calculate the statistics to
	 */
	private void processQuery(String query, String field) {
		// Replace taxon id queries with taxon closures ones because those will be used for the filtering
		if (query.contains(AnnotationField.TAXONOMYID.getValue())) {
			query = query.replace(AnnotationField.TAXONOMYID.getValue(), AnnotationField.TAXONOMYCLOSURE.getValue());
		}

		// Terms counts
		terms = annotationService.getFacetFields(query, null, field, 80);
		//terms = annotationService.getFacetFields(query, null, field, -1);// TODO Uncomment this for downloading of statistics
		// Total number of annotations
		//numberAnnotations = statisticsUtil.calculateNumberAnnotations(query, numberAnnotations);
		numberAnnotations = annotationService.getTotalNumberAnnotations(query);
		// Load terms/taxonomies names if necessary
		statisticsUtil.loadNames(field);
	}

	/**
	 * Create 4 threads to run stats for a specific field
	 * @param field Field to run the stats for
	 * @param counts Different values for the field
	 * @return Stats
	 */
	private Set<StatsTerm> statisticsByProteinInParallel(String field, String query, List<Count> counts) {

		List<Count> counts1 = new ArrayList<>() , counts2 = new ArrayList<>(), counts3 = new ArrayList<>(), counts4 = new ArrayList<>();

		//TODO Change this to make configurable the number of threads

		// Split values list in sublists. Each sublist will be processed by a different thread
		if (field.equals(AnnotationField.GOASPECT.getValue())) {
			if(counts.size() >= 1)
				counts1 = Arrays.asList(counts.get(0));
			if(counts.size() >= 2)
				counts2 = Arrays.asList(counts.get(1));
			if(counts.size() >= 3)
				counts3 = Arrays.asList(counts.get(2));
		} else {
			counts1 = counts.subList(0, counts.size() / 4);
			counts2 = counts.subList(counts.size() / 4, counts.size() / 2);
			counts3 = counts.subList(counts.size() / 2, 3 * counts.size() / 4);
			counts4 = counts.subList(3 * counts.size() / 4, counts.size());
		}
		// Thread 1
		StatisticsCalculator statsThread1 = createThread(field, query, counts1);
		statsThread1.run();

		// Thread 2
		StatisticsCalculator statsThread2 = createThread(field, query, counts2);
		statsThread2.run();

		// Thread 3
		StatisticsCalculator statsThread3 = createThread(field, query, counts3);
		statsThread3.run();

		// Thread 4
		StatisticsCalculator statsThread4 = createThread(field, query, counts4);
		statsThread4.run();

		boolean alive = true;
		TreeSet<StatsTerm> statsTerms = new TreeSet<>();

		// Check all threads have finished
//		while (alive) {
//			if (!statsThread1.isAlive() && !statsThread2.isAlive()
//					&& !statsThread3.isAlive() && !statsThread4.isAlive()) {
//				alive = false;
				statsTerms.addAll(statsThread1.getStatsTerms());
				statsTerms.addAll(statsThread2.getStatsTerms());
				statsTerms.addAll(statsThread3.getStatsTerms());
				statsTerms.addAll(statsThread4.getStatsTerms());
				return statsTerms.descendingSet();// Highest first
//			}
//		}
		//return statsTerms;
	}

	/**
	 * Create a thread with the specified values
	 * @param field Field to calculate the stats for
	 * @param counts Terms to process
	 * @param names Array with taxonomy/ontology names
	 * @return A thread that will process a chunk of stats
	 */
	private StatisticsCalculator createThread(String field, String query, List<Count> counts){

		StatisticsCalculator statsCalc = new StatisticsCalculator();
		statsCalc.setAnnotationService(annotationService);
		statsCalc.setStatsUtil(statisticsUtil);
		statsCalc.setField(field);
		statsCalc.setQuery(query);
		statsCalc.setCounts(counts);
		statsCalc.setNumberProteins(numberProteins);

		return statsCalc;
	}

	public void setAnnotationService(AnnotationService annotationService) {
		this.annotationService = annotationService;
	}

	public void setMiscellaneousService(MiscellaneousService miscellaneousService) {
		this.miscellaneousService = miscellaneousService;
	}

	public void setStatisticsUtil(StatisticsUtil statisticsUtil) {
		this.statisticsUtil = statisticsUtil;
	}
}
