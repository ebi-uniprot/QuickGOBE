package uk.ac.ebi.quickgo.service.miscellaneous;

import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.ebi.quickgo.miscellaneous.Miscellaneous;
import uk.ac.ebi.quickgo.statistic.COOccurrenceStatsTerm;
import uk.ac.ebi.quickgo.statistic.StatisticsCalculation;

/**
 * Miscellaneous service interface
 * @author cbonill
 *
 */
public interface MiscellaneousService {

	/**
	 * Retrieve taxonomies names
	 * @return Taxonomies ids as keys and taxonomies names as values
	 */
	Map<String, Map<String, String>> retrieveTaxonomiesNames();

	/**
	 * Retrieve with Dbs
	 * @return Xref DBs
	 */
	public List<Miscellaneous> getWithDBs();

	/**
	 * Retrieve Blacklist entries
	 * @return Blacklist entries
	 */
	public List<Miscellaneous> getBlacklist();

	/**
	 * Retrieve Post Processing Rules entries
	 * @return Post Processing Rules entries
	 */
	public List<Miscellaneous> getPostProcessingRules();

	/**
	 * Retrieve co-occurrence statistics taking into account the entire annotation set
	 * @param ontologyTermID Ontology term id to get the stats for
	 * @return Stats for the giver term
	 */
	public Set<COOccurrenceStatsTerm> allCOOccurrenceStatistics(String ontologyTermID);

	/**
	 *
	 * Retrieve co-occurrence statistics taking into account non electronic annotations set
	 * @param ontologyTermID Ontology term id to get the stats for
	 * @return Stats for the giver term
	 */
	public Set<COOccurrenceStatsTerm> nonIEACOOccurrenceStatistics(	String ontologyTermID);

	/**
	 * Instead of calculating statistics at run time, save stats at indexing as if no filters had been applied.
	 * @return
	 */
	public StatisticsCalculation getPrecalculatedStatsNoFilters();
}
