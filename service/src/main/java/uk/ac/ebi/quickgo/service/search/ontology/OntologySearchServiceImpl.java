package uk.ac.ebi.quickgo.service.search.ontology;

import uk.ac.ebi.quickgo.repo.solr.query.model.QueryRequest;
import uk.ac.ebi.quickgo.repo.solr.query.results.QueryResult;
import uk.ac.ebi.quickgo.service.model.ontology.OBOTerm;
import uk.ac.ebi.quickgo.service.search.RequestRetrieval;
import uk.ac.ebi.quickgo.service.search.SearchService;

/**
 * Created 18/01/16
 * @author Edd
 */
public class OntologySearchServiceImpl implements SearchService<OBOTerm> {
    private final RequestRetrieval<OBOTerm> requestRetrieval;

    public OntologySearchServiceImpl(
            RequestRetrieval<OBOTerm> requestRetrieval) {
        this.requestRetrieval = requestRetrieval;
    }

    @Override
    public QueryResult<OBOTerm> findByQuery(QueryRequest request) {
        return this.requestRetrieval.findByQuery(request);
    }
}
