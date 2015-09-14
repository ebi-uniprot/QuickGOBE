package uk.ac.ebi.quickgo.indexer.statistics;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.ebi.quickgo.miscellaneous.Miscellaneous;
import uk.ac.ebi.quickgo.solr.indexing.service.miscellaneous.MiscellaneousIndexer;
import uk.ac.ebi.quickgo.solr.model.miscellaneous.SolrMiscellaneous.SolrMiscellaneousDocumentType;
import uk.ac.ebi.quickgo.solr.query.model.annotation.enums.AnnotationField;
import uk.ac.ebi.quickgo.solr.query.model.miscellaneous.enums.MiscellaneousField;
import uk.ac.ebi.quickgo.solr.query.service.annotation.AnnotationRetrieval;
import uk.ac.ebi.quickgo.statistic.COOccurrenceStatsTerm;

/**
 * QuickGO process to calculate and index co-occurrence stats
 * @author cbonill
 *
 */
@Service("quickGOCOOccurrenceStatsIndexer")
public class QuickGOCOOccurrenceStatsIndexer {

	@Autowired
	AnnotationRetrieval annotationRetrieval;

	/**
	 * Miscellaneous indexer
	 */
	@Autowired
	MiscellaneousIndexer miscellaneousIndexer;

	/**
	 * Contains number of proteins that each term is annotated to
	 */
	Map<String,Float> termsNumberProteins = new HashMap<String, Float>();

	/**
	 * List of proteins annotated to each term taking into account the entire set of annotations
	 */
	Map<String, Set<String>> allProteinsByTerm = new HashMap<String, Set<String>>();

	/**
	 * List of proteins annotated to each term taking into account non electronic annotations
	 */
	Map<String, Set<String>> nonIEAProteinsByTerm = new HashMap<String, Set<String>>();

	// Number of threads to calculate co-occurrence stats
	private int NUM_THREADS = 1;

	// Minimum Number of stats records to index
	private final int STATS_SIZE = 50000;

	// Non-IEA Solr query
	private final String nonIEAQuery = "NOT " + AnnotationField.GOEVIDENCE.getValue() + ":" + "IEA";

	// Log
	private static final Logger logger = LoggerFactory.getLogger(QuickGOCOOccurrenceStatsIndexer.class);
	private Properties properties;

	/**
	 * Main method for indexing co-occurrence stats
	 */
	public void index(){

		// Delete existing stats
		miscellaneousIndexer.deleteByQuery(MiscellaneousField.TYPE.getValue() + ":" + SolrMiscellaneousDocumentType.STATS.getValue());

		// Total number proteins annotated
		float totalNumberProteins = 0;

		// Total number proteins annotated in non IEA annotations
		float totalNumberProteinsNonIEA = 0;

		try {
			totalNumberProteins = annotationRetrieval.getTotalNumberProteins("*:*");
			totalNumberProteinsNonIEA = annotationRetrieval.getTotalNumberProteins(nonIEAQuery);

			// Non-IEA annotations
			logger.info("Indexing non IEA co-occurrence statistics");
			indexStats(true, totalNumberProteinsNonIEA);
			nonIEAProteinsByTerm.clear();
			logger.info("Non IEA co-occurrence statistics indexed");
			// Reset number of threads
			NUM_THREADS = 1;
			// All annotations
			logger.info("Indexing all co-occurrence statistics");
			indexStats(false, totalNumberProteins);
			logger.info("All co-occurrence statistics indexed");
			allProteinsByTerm.clear();
		} catch (SolrServerException e) {
			logger.error(e.getMessage());
		}
	}

	/**
	 * Index co-occurrence stats
	 * @param nonIEA True if only non-iEA annotations are taken into account
	 * @param totalNumberProteins Total number of annotated proteins
	 * @throws SolrServerException
	 */
	private void indexStats(boolean nonIEA, float totalNumberProteins) throws SolrServerException {
		// Initialise counts
		termsNumberProteins = new HashMap<String, Float>();
		// Store stats values
		List<Miscellaneous> stats = new ArrayList<>();
		// Get annotated GO ids
		List<String> goIDs = getAnnotatedTermIDS(nonIEA);
		goIDs.remove("go");//Remove "go" term
		int index = 0;

		while (index < goIDs.size()) {
			runStatsThreads(goIDs, index, totalNumberProteins, nonIEA, stats);
			index = index + NUM_THREADS;
			// Index when have more than STATS_SIZE
			if (stats.size() > STATS_SIZE) {
				miscellaneousIndexer.index(stats);
				stats = new ArrayList<>();
			}
			// Increase number threads after processing the first 10 GO terms.
			// After processing the first 10 terms, most of the go terms counts will have
			// been calculated ("allProteinsByTerm", "nonIEAProteinsByTerm" and
			// "termsNumberProteins" values) so we can increase the number of
			// threads to make it faster
			if (index == 10) {
				NUM_THREADS = 2;
			}
		}
		// Index the rest
		if(stats.size() > 0){
			miscellaneousIndexer.index(stats);
		}
	}

	/**
	 * Create threads, run them and get calculated stats by each of them
	 * @param goIDs List with GO ids to calculate the stats for
	 * @param index
	 * @param totalNumberProteins
	 * @param nonIEA True if non electronic annotations are taking into account
	 * @param stats Calculated stats
	 */
	public void runStatsThreads(List<String> goIDs, int index, float totalNumberProteins, boolean nonIEA, List<Miscellaneous> stats){
		Set<COOccurrenceStatsProcessor> statsProcessors = new HashSet<COOccurrenceStatsProcessor>(NUM_THREADS);
		// Create the threads
		for (int i = 0; i < NUM_THREADS; i++) {
			if(index + i < goIDs.size()){
				statsProcessors.add(createThread(goIDs.get(index + i), totalNumberProteins, nonIEA));
			}
		}

		// Start each of the threads
		for(COOccurrenceStatsProcessor coOccurrenceStatsProcessor : statsProcessors){
			coOccurrenceStatsProcessor.start();
		}

		// Wait for all the threads to finish
		for(COOccurrenceStatsProcessor coOccurrenceStatsProcessor : statsProcessors){
			try {
				coOccurrenceStatsProcessor.join();
			} catch (InterruptedException e) {
				logger.error(e.getMessage());
			}
		}

		// Check all the threads have finished
		getCalculatedStats(statsProcessors, stats);

		// Merge term proteins count
		for(COOccurrenceStatsProcessor coOccurrenceStatsProcessor : statsProcessors){
			termsNumberProteins.putAll(coOccurrenceStatsProcessor.getTermsNumberProteins());
			allProteinsByTerm.putAll(coOccurrenceStatsProcessor.getAllProteinsByTerm());
			nonIEAProteinsByTerm.putAll(coOccurrenceStatsProcessor.getNonIEAProteinsByTerm());
		}
	}

	/**
	 * Get stats calculated by each thread
	 * @param statsProcessors Set of threads
	 * @param stats Calculated stats
	 */
	private void getCalculatedStats(Set<COOccurrenceStatsProcessor> statsProcessors, List<Miscellaneous> stats) {
		for(COOccurrenceStatsProcessor coOccurrenceStatsProcessor : statsProcessors){
			toMiscellaneousObjects(stats, coOccurrenceStatsProcessor.getStatsTerms().descendingSet());
		}
	}

	/**
	 * Convert list of stats terms into miscellaneous ones
	 * @param stats List of stats to index
	 * @param descendingSet List of values to convert
	 */
	private void toMiscellaneousObjects(List<Miscellaneous> stats, NavigableSet<COOccurrenceStatsTerm> descendingSet) {
		for(COOccurrenceStatsTerm coOccurrenceStatsTerm1 : descendingSet){
			stats.add(coOccurrenceStatsTerm1.toMiscellaneousObject());
		}
	}

	/**
	 * Create a thread to calculate stats for a specific term
	 * @param termID Term to calculate the stats for
	 * @param totalNumberProteins Total number of proteins
	 * @return A stats thread
	 */
	private COOccurrenceStatsProcessor createThread(String termID, float totalNumberProteins, boolean nonIEA){
		COOccurrenceStatsProcessor coOccurrenceStatsProcessor = new COOccurrenceStatsProcessor();
		coOccurrenceStatsProcessor.setAnnotationRetrieval(annotationRetrieval);
		coOccurrenceStatsProcessor.setOntologyTermID(termID);
		coOccurrenceStatsProcessor.setTotalNumberProteins(totalNumberProteins);
		coOccurrenceStatsProcessor.setTermsNumberProteins(termsNumberProteins);
		coOccurrenceStatsProcessor.setAllProteinsByTerm(allProteinsByTerm);
		coOccurrenceStatsProcessor.setNonIEAProteinsByTerm(nonIEAProteinsByTerm);
		coOccurrenceStatsProcessor.setNonIEA(nonIEA);

		return coOccurrenceStatsProcessor;
	}

	/**
	 * Return all the annotated GO terms
	 * @return All the GO terms with annotations
	 * @throws SolrServerException
	 */
	public List<String> getAnnotatedTermIDS(boolean nonIEA) throws SolrServerException{
		String query = "*:*";
		if (nonIEA) {
			query = query + " AND " + nonIEAQuery;
		}
		List<Count> terms = annotationRetrieval.getFacetFields(query, null, AnnotationField.GOID.getValue(), Integer.MAX_VALUE);
		terms = terms.subList(1, terms.size());// Remove "go" term
		List<String> termsIDs = new ArrayList<>();
		for (Count term : terms) {
			termsIDs.add(term.getName());
		}
		return termsIDs;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

}
