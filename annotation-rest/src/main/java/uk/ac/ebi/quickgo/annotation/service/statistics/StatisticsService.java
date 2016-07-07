package uk.ac.ebi.quickgo.annotation.service.statistics;

import uk.ac.ebi.quickgo.annotation.model.AnnotationRequest;
import uk.ac.ebi.quickgo.annotation.model.StatisticsGroup;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

/**
 * TODO: write something here
 *
 * @author Ricardo Antunes
 */
public interface StatisticsService {

    QueryResult<StatisticsGroup> calculate(AnnotationRequest request);
}
