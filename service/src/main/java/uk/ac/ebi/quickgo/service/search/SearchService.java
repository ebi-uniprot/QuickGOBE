package uk.ac.ebi.quickgo.service.search;

import uk.ac.ebi.quickgo.repo.solr.query.model.QueryRequest;
import uk.ac.ebi.quickgo.repo.solr.query.results.QueryResult;

/**
 * Search service layer for searching results from an underlying searchable data store.
 *
 * @param <T> the type of the results that are returned
 *
 * See also {@link uk.ac.ebi.quickgo.repo.solr.io.ontology.OntologyRepository}
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
     * @throws uk.ac.ebi.quickgo.solr.query.service.RetrievalException if there is an issue retrieving the data
     */
    QueryResult<T> findByQuery(QueryRequest request) ;
}
