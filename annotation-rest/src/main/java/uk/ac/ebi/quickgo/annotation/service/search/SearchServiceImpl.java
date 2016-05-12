package uk.ac.ebi.quickgo.annotation.service.search;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.rest.search.RequestRetrieval;
import uk.ac.ebi.quickgo.rest.search.SearchService;
import uk.ac.ebi.quickgo.rest.search.query.QueryRequest;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import com.google.common.base.Preconditions;

/**
 * The search service implementation for annotations. This class implements the
 * {@link SearchService} interface, and delegates retrieval of annotation results
 * to a {@link RequestRetrieval} instance.
 *
 * @author Tony Wardell
 */
class SearchServiceImpl implements SearchService<Annotation> {
    private final RequestRetrieval<Annotation> requestRetrieval;

    public SearchServiceImpl(RequestRetrieval<Annotation> requestRetrieval) {
        Preconditions.checkArgument(requestRetrieval!=null, "The instance of RequestRetrieval<Annotation> passed to " +
                "the constructor of uk.ac.ebi.quickgo.annotation.service.search.SearchServiceImpl is null");
        this.requestRetrieval = requestRetrieval;
    }

    @Override
    public QueryResult<Annotation> findByQuery(QueryRequest request) {
        return this.requestRetrieval.findByQuery(request);
    }
}
