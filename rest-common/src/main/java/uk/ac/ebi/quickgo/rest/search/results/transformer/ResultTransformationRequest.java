package uk.ac.ebi.quickgo.rest.search.results.transformer;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * The purpose of this class is to record an identifier from a request, which identifies
 * a transformation of the results. For example, if the request included a request parameter, "includeFields=goName",
 * where "goName" was an externally derived value (e.g., from some other RESTful service), then "goName" would be the
 * identifier of the {@link ResultTransformationRequest}; since it indicates that the results need to be
 * transformed so that the GO name is included in each result.
 *
 * Created 10/04/17
 * @author Edd
 */
public class ResultTransformationRequest {
    private String id;

    public ResultTransformationRequest(String id) {
        checkArgument(id != null && !id.isEmpty(), "ID cannot be null or empty");
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override public String toString() {
        return "ResultTransformationRequest{" +
                "id='" + id + '\'' +
                '}';
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ResultTransformationRequest that = (ResultTransformationRequest) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
