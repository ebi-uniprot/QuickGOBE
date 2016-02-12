package uk.ac.ebi.quickgo.client.service.search.ontology;

import uk.ac.ebi.quickgo.client.model.ontology.OntologyTerm;
import uk.ac.ebi.quickgo.rest.search.RequestRetrieval;
import uk.ac.ebi.quickgo.rest.search.SearchService;
import uk.ac.ebi.quickgo.rest.search.query.QueryRequest;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

/**
 * The search service implementation for ontologies. This class implements the
 * {@link SearchService} interface, and delegates retrieval of ontology results
 * to a {@link RequestRetrieval} instance.
 *
 * Created 18/01/16
 * @author Edd
 */
public class OntologySearchServiceImpl implements SearchService<OntologyTerm> {
    private final RequestRetrieval<OntologyTerm> requestRetrieval;

    public OntologySearchServiceImpl(RequestRetrieval<OntologyTerm> requestRetrieval) {
        this.requestRetrieval = requestRetrieval;
    }

    @Override
    public QueryResult<OntologyTerm> findByQuery(QueryRequest request) {
        return this.requestRetrieval.findByQuery(request);
    }
}
