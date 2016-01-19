package uk.ac.ebi.quickgo.service.search;

import uk.ac.ebi.quickgo.repo.solr.query.model.QueryRequest;
import uk.ac.ebi.quickgo.repo.solr.query.results.QueryResult;

/**
 * An interface that searches a data source by using a domain specific {@link QueryRequest} object, and returns a
 * domain readable {@link QueryResult}.
 *
 * Created 18/01/16
 * @author Edd
 */
public interface RequestRetrieval<T> {
    /**
     * Suggest objects of type T based on the given query
     *
     * @param request contains the request parameters necessary to run the query
     * @return A response with the suggested results
     * @throws uk.ac.ebi.quickgo.solr.query.service.RetrievalException when in issue occurs whilst retrieving data
     * from a data source
     */
    QueryResult<T> findByQuery(QueryRequest request) ;
}
