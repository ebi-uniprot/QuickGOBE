package uk.ac.ebi.quickgo.web.util.query;

import uk.ac.ebi.quickgo.service.annotation.parameter.AnnotationParameters;
import uk.ac.ebi.quickgo.web.util.annotation.AppliedFilterSet;

/**
 * Service to calculate session and selected filters from a query
 * @author cbonill
 *
 */
public interface QueryProcessor {

	/**
	 * Process query and calculate filter session parameters
	 * @param query Query to run
	 * @param annotationParameters Parameters set
	 * @param advancedFilter True if it's advanced filter
	 * @param appliedFilterSet Session parameters
	 */
	public void processQuery(String query, AnnotationParameters annotationParameters, AppliedFilterSet appliedFilterSet, boolean advancedFilter);
}
