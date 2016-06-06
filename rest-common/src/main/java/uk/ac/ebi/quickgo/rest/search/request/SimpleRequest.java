package uk.ac.ebi.quickgo.rest.search.request;

import com.google.common.base.Preconditions;
import java.util.List;

import static java.util.Collections.emptyList;

/**
 * Created 02/06/16
 * @author Edd
 */
public class SimpleRequest implements ClientRequest {

    private final String field;
    private final List<String> values;

    public SimpleRequest(String field, List<String> values) {
        Preconditions.checkArgument(field != null && !field.trim().isEmpty(), "Field name cannot be null or empty");
        Preconditions.checkArgument(values != null, "Values cannot be null");

        this.field = field;
        this.values = values;
    }

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
