package uk.ac.ebi.quickgo.annotation.service.statistics;

import uk.ac.ebi.quickgo.annotation.model.AnnotationRequest;
import uk.ac.ebi.quickgo.annotation.model.StatisticsGroup;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

/**
 * Contract for a service that provides annotation based statistics.
 *
 * @author Ricardo Antunes
 */
public interface StatisticsService {

    /**
     * Calculates the statistics based on the results returned by
     * an {@link AnnotationRequest}.
     *
     * @param request the {@link AnnotationRequest} whose results will have statistics calculated
     * @return a {@link QueryResult} containing a {@link StatisticsGroup}, representing the
     * statistics corresponding to the annotation data-set
     */
    QueryResult<StatisticsGroup> calculate(AnnotationRequest request);
}
