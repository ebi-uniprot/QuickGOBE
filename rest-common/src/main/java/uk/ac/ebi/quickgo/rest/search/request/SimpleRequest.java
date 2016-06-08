package uk.ac.ebi.quickgo.rest.search.request;

import com.google.common.base.Preconditions;
import java.util.List;

import static java.util.Collections.emptyList;

/**
 * Represents a simple client request over a single field, which allows
 * the field to have one of multiple values.
 *
 * Created 02/06/16
 * @author Edd
 */
public class SimpleRequest implements ClientRequest {

    private final String field;
    private final List<String> values;

    /**
     * Creates a request upon a field that may have one of many values.
     * @param field the field of interest to the client
     * @param values the list of values the client specifies as allowable for the {@code field}
     */
    public SimpleRequest(String field, List<String> values) {
        Preconditions.checkArgument(field != null && !field.trim().isEmpty(), "Field name cannot be null or empty");
        Preconditions.checkArgument(values != null, "Values cannot be null");

        this.field = field;
        this.values = values;
    }

    /**
     * Creates a request upon a field, but without supplying any values. This might be used
     * for example when one is creating a request on a field whose configuration comes entirely from
     * an external source, and does not require any values supplied by the user.
     *
     * @param field the field involved in the request
     */
    public SimpleRequest(String field) {
        this(field, emptyList());
    }

    public List<String> getValues() {
        return values;
    }

    @Override public String getSignature() {
        return field;
    }
}
