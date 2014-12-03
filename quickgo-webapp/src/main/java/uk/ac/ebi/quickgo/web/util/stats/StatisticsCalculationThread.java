package uk.ac.ebi.quickgo.web.util.stats;

import java.util.Set;
import java.util.TreeSet;

import uk.ac.ebi.quickgo.service.statistic.StatisticService;
import uk.ac.ebi.quickgo.service.statistic.type.StatsTerm;

/**
 * To retrieve stats for a specific annotation field type
 * @author cbonill
 *
 */
public class StatisticsCalculationThread extends Thread {

	private String query;
	private String annotationField;
	private StatisticService statisticService;

	private Set<StatsTerm> byAnnotation = new TreeSet<>();
	private Set<StatsTerm> byProtein = new TreeSet<>();
	
	public StatisticsCalculationThread(String query, String annotationField) {
		this.query = query;
		this.annotationField = annotationField;
	}

	public void run() {		
		byAnnotation.addAll(statisticService.statisticsByAnnotation(query, annotationField));		
		byProtein.addAll(statisticService.statisticsByProtein(query, annotationField));
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getAnnotationField() {
		return annotationField;
	}

	public void setAnnotationField(String annotationField) {
		this.annotationField = annotationField;
	}

	public StatisticService getStatisticService() {
		return statisticService;
	}

	public void setStatisticService(StatisticService statisticService) {
		this.statisticService = statisticService;
	}

	public Set<StatsTerm> getByAnnotation() {
		return byAnnotation;
	}
	
	public Set<StatsTerm> getByProtein() {
		return byProtein;
	}

	public void setByAnnotation(Set<StatsTerm> byAnnotation) {
		this.byAnnotation = byAnnotation;
	}

	public void setByProtein(Set<StatsTerm> byProtein) {
		this.byProtein = byProtein;
	}	
}
