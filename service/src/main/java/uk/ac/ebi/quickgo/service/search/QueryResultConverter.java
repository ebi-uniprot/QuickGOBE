package uk.ac.ebi.quickgo.service.search;

import uk.ac.ebi.quickgo.repo.solr.query.model.QueryRequest;
import uk.ac.ebi.quickgo.repo.solr.query.results.QueryResult;

/**
 * Converts a data response from the proprietary data source into a data object the domain can understand.
 */
public interface QueryResultConverter<T, S> {
    QueryResult<T> convert(S toConvert, QueryRequest request);
}
