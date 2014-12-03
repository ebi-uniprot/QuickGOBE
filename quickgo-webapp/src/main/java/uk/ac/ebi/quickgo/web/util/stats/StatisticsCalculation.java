package uk.ac.ebi.quickgo.web.util.stats;

import java.util.HashSet;
import java.util.Set;

import uk.ac.ebi.quickgo.bean.statistics.StatisticsBean;
import uk.ac.ebi.quickgo.service.statistic.StatisticService;
import uk.ac.ebi.quickgo.service.statistic.type.StatsTerm;
import uk.ac.ebi.quickgo.solr.query.model.annotation.enums.AnnotationField;

/**
 * To calculate annotation stats
 * @author cbonill
 */
public class StatisticsCalculation extends Thread {

	StatisticService statisticService;
	
	StatisticsBean statisticsBean;
	
	String query;	
		
	public StatisticsCalculation(StatisticsBean statisticsBean, String query) {		
		this.statisticsBean = statisticsBean;
		this.query = query;
	}

	public void run() {
		try{
			// Create a thread for each type of stats and run all of them
			StatisticsCalculationThread goIDcalculationThread = createThread(AnnotationField.GOID.getValue(), query, statisticsBean.getAnnotationsPerGOID(), statisticsBean.getProteinsPerGOID());		
			goIDcalculationThread.start();		
			goIDcalculationThread.join();
			
			StatisticsCalculationThread goAspectcalculationThread = createThread(AnnotationField.GOASPECT.getValue(), query, statisticsBean.getAnnotationsPerAspect(), statisticsBean.getProteinsPerAspect());
			goAspectcalculationThread.start();
			goAspectcalculationThread.join();
			
			StatisticsCalculationThread ecoIDcalculationThread = createThread(AnnotationField.ECOID.getValue(), query, statisticsBean.getAnnotationsPerEvidence(), statisticsBean.getProteinsPerEvidence());
			ecoIDcalculationThread.start();
			ecoIDcalculationThread.join();
			
			StatisticsCalculationThread dbXrefcalculationThread = createThread(AnnotationField.DBXREF.getValue(), query, statisticsBean.getAnnotationsPerReference(), statisticsBean.getProteinsPerReference());
			dbXrefcalculationThread.start();
			dbXrefcalculationThread.join();
			
			StatisticsCalculationThread taxIDcalculationThread = createThread(AnnotationField.TAXONOMYID.getValue(), query, statisticsBean.getAnnotationsPerTaxon(), statisticsBean.getProteinsPerTaxon());
			taxIDcalculationThread.start();
			taxIDcalculationThread.join();
			
			StatisticsCalculationThread assignedBycalculationThread = createThread(AnnotationField.ASSIGNEDBY.getValue(), query, statisticsBean.getAnnotationsPerAssignedBy(), statisticsBean.getProteinsPerAssignedBy());
			assignedBycalculationThread.start();
			assignedBycalculationThread.join();
			
			StatisticsCalculationThread dbObjectIDcalculationThread = createThread(AnnotationField.DBOBJECTID.getValue(), query, statisticsBean.getAnnotationsPerDBObjectID(), new HashSet<StatsTerm>());
			dbObjectIDcalculationThread.start();
			dbObjectIDcalculationThread.join();

		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create a simple thread to retrieve the stats of a given field type
	 * @param fieldValue Field type
	 * @param query Query
	 * @return Thread to retrieve stats
	 */
	private StatisticsCalculationThread createThread(String fieldValue, String query, Set<StatsTerm> byAnnotation, Set<StatsTerm> byProtein){
		StatisticsCalculationThread statsThread = new StatisticsCalculationThread(query, fieldValue);
		statsThread.setStatisticService(statisticService);		
		statsThread.setByAnnotation(byAnnotation);
		statsThread.setByProtein(byProtein);
		
		return statsThread;
	}
	
	public StatisticsBean getStatisticsBean() {
		return this.statisticsBean;
	}

	public void setStatisticsBean(StatisticsBean statisticsBean) {
		this.statisticsBean = statisticsBean;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public StatisticService getStatisticService() {
		return statisticService;
	}

	public void setStatisticService(StatisticService statisticService) {
		this.statisticService = statisticService;
	}	
}
