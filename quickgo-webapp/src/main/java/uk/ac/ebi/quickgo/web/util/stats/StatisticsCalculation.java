package uk.ac.ebi.quickgo.web.util.stats;

import java.util.HashSet;
import java.util.Set;

import uk.ac.ebi.quickgo.bean.statistics.StatisticsBean;
import uk.ac.ebi.quickgo.service.statistic.StatisticService;
import uk.ac.ebi.quickgo.statistic.StatsTerm;
import uk.ac.ebi.quickgo.solr.query.model.annotation.enums.AnnotationField;

/**
 * To calculate annotation stats
 * @author cbonill
 */
public class StatisticsCalculation {

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
			StatisticsSummarizer goIDcalculationSummarizer = createCalculator(AnnotationField.GOID.getValue(), query, statisticsBean.getAnnotationsPerGOID(), statisticsBean.getProteinsPerGOID());
			goIDcalculationSummarizer.run();

			StatisticsSummarizer goAspectcalculationSummarizer = createCalculator(AnnotationField.GOASPECT.getValue(), query, statisticsBean.getAnnotationsPerAspect(), statisticsBean.getProteinsPerAspect());
			goAspectcalculationSummarizer.run();

			StatisticsSummarizer ecoIDcalculationSummarizer = createCalculator(AnnotationField.ECOID.getValue(), query, statisticsBean.getAnnotationsPerEvidence(), statisticsBean.getProteinsPerEvidence());
			ecoIDcalculationSummarizer.run();

			StatisticsSummarizer dbXrefcalculationSummarizer = createCalculator(AnnotationField.REFERENCE.getValue(), query, statisticsBean.getAnnotationsPerReference(), statisticsBean.getProteinsPerReference());
			dbXrefcalculationSummarizer.run();

			StatisticsSummarizer taxIDcalculationSummarizer = createCalculator(AnnotationField.TAXONOMYID.getValue(), query, statisticsBean.getAnnotationsPerTaxon(), statisticsBean.getProteinsPerTaxon());
			taxIDcalculationSummarizer.run();

			StatisticsSummarizer assignedBycalculationSummarizer = createCalculator(AnnotationField.ASSIGNEDBY.getValue(), query, statisticsBean.getAnnotationsPerAssignedBy(), statisticsBean.getProteinsPerAssignedBy());
			assignedBycalculationSummarizer.run();

			StatisticsSummarizer dbObjectIDcalculationSummarizer = createCalculator(AnnotationField.DBOBJECTID.getValue(), query, statisticsBean.getAnnotationsPerDBObjectID(), new HashSet<StatsTerm>());
			dbObjectIDcalculationSummarizer.run();

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
	private StatisticsSummarizer createCalculator(String fieldValue, String query, Set<StatsTerm> byAnnotation, Set<StatsTerm> byProtein){
		StatisticsSummarizer statsSummary = new StatisticsSummarizer(query, fieldValue);
		statsSummary.setStatisticService(statisticService);
		statsSummary.setByAnnotation(byAnnotation);
		statsSummary.setByProtein(byProtein);

		return statsSummary;
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
