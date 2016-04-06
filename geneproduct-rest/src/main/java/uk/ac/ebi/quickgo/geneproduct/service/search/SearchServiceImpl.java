package uk.ac.ebi.quickgo.geneproduct.service.search;

import uk.ac.ebi.quickgo.geneproduct.model.GeneProduct;
import uk.ac.ebi.quickgo.rest.search.RequestRetrieval;
import uk.ac.ebi.quickgo.rest.search.SearchService;
import uk.ac.ebi.quickgo.rest.search.query.QueryRequest;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

/**
 * The search service implementation for gene products. This class implements the
 * {@link SearchService} interface, and delegates retrieval of gene product results
 * to a {@link RequestRetrieval} instance.
 *
 * Created 04/04/16
 * @author Edd
 */
public class SearchServiceImpl implements SearchService<GeneProduct> {
    private final RequestRetrieval<GeneProduct> requestRetrieval;

    public SearchServiceImpl(RequestRetrieval<GeneProduct> requestRetrieval) {
        this.requestRetrieval = requestRetrieval;
    }

    @Override
    public QueryResult<GeneProduct> findByQuery(QueryRequest request) {
        return this.requestRetrieval.findByQuery(request);
    }
}