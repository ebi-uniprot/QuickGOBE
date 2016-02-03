package uk.ac.ebi.quickgo.common.search;

import uk.ac.ebi.quickgo.common.search.query.QueryRequest;
import uk.ac.ebi.quickgo.common.search.results.QueryResult;

/**
 * Search service layer for searching results from an underlying searchable data store.
 *
 * @param <T> the type of the results that are returned
 *
 * Created 18/01/16
 * @author Edd
 */
public interface SearchService<T> {
    /**
     * Suggest gene products based on the given query
     *
     * @param request contains the request parameters necessary to run the query
     * @return A response with the suggested results
     * @throws RetrievalException if there is an issue retrieving the data
     */
    QueryResult<T> findByQuery(QueryRequest request) ;
}
