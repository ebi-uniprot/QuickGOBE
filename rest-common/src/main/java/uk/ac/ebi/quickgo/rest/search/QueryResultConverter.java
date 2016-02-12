package uk.ac.ebi.quickgo.rest.search;

import uk.ac.ebi.quickgo.rest.search.query.QueryRequest;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

/**
 * Converts a data response from the proprietary data source into a data object the domain can understand.
 */
public interface QueryResultConverter<T, S> {
    QueryResult<T> convert(S toConvert, QueryRequest request);
}
