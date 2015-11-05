package uk.ac.ebi.quickgo.service.statistic;

import java.util.Set;

import uk.ac.ebi.quickgo.statistic.StatsTerm;
import uk.ac.ebi.quickgo.statistic.COOccurrenceStatsTerm;

/**
 * Statistics interface
 * @author cbonill
 *
 */
public interface StatisticService {

	/**
	 * Return statistics by annotations
	 * @param query Filtering query
	 * @param field Field to calculate the statistics for
	 * @return Set of statistics values
	 */
	public Set<StatsTerm> statisticsByAnnotation(String query, String field);

	/**
	 * Return statistics by proteins
  	 * @param query Filtering query
	 * @param field Field to calculate the statistics for
	 * @return Set of statistics values
	 */
	public Set<StatsTerm> statisticsByProtein(String query, String field);

	/**
	 * Calculate co-occurrence statistics taking into account all the annotations
	 * @param ontologyTermID Ontology term ID to calculate the statistics for
	 * @return Set of ontology terms with the corresponding statistics values
	 */
	public Set<COOccurrenceStatsTerm> allCOOccurrenceStatistics(String ontologyTermID);

	/**
	 * Calculate co-occurrence statistics taking into account only manual annotations
	 * @param ontologyTermID Ontology term ID to calculate the statistics for
	 * @return Set of ontology terms with the corresponding statistics values
	 */
	public Set<COOccurrenceStatsTerm> nonIEACOOccurrenceStatistics(String ontologyTermID);
}
