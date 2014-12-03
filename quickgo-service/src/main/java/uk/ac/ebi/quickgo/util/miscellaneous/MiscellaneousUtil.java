package uk.ac.ebi.quickgo.util.miscellaneous;

import java.util.List;
import java.util.Map;

import uk.ac.ebi.quickgo.miscellaneous.Miscellaneous;

/**
 * Useful miscellaneous operations not published in Miscellaneous service
 * @author cbonill
 *
 */
public interface MiscellaneousUtil {

	/**
	 * Given a database abbreviation, retrieve its URLs and description
	 * @param abbreviation Database abbreviation
	 * @return URLs and description
	 */
	public Miscellaneous getDBInformation(String abbreviation);		

	/**
	 * Given a list of subsets, returns the count of terms in every subset
	 * @param subset Subsets to calculate the count for
	 * @return Number of terms in each subset
	 */
	public List<Miscellaneous> getSubsetCount(List<String> subset);
	
	/**
	 * Given a list of taxonomies ids, return their names
	 * @param taxonomiesIds Taxonomies ids
	 * @return Taxonomies names
	 */
	public Map<String, String> getTaxonomiesNames(List<String> taxonomiesIds);
	
	/**
	 * Return evidence types
	 * @return Evidence types
	 */
	public Map<String, String> getEvidenceTypes();

}
