package uk.ac.ebi.quickgo.rest.search.results.transformer;

import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * The purpose of this class is to store a list of {@link ResultTransformationRequest} instances
 * and any necessary associated contextual information.
 *
 * Created 10/04/17
 * @author Edd
 */
public class ResultTransformationRequests {
    private final Set<ResultTransformationRequest> requests;

    public ResultTransformationRequests() {
        this.requests = new HashSet<>();
    }

    public void addTransformationRequest(ResultTransformationRequest request) {
        checkArgument(request != null, "ResultTransformationRequest cannot be null");
        this.requests.add(request);
    }

    public Set<ResultTransformationRequest> getRequests() {
        return requests;
    }
}
