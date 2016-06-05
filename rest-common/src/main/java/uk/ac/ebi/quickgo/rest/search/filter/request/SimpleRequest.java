package uk.ac.ebi.quickgo.rest.search.filter.request;

import java.util.List;

/**
 * Created 02/06/16
 * @author Edd
 */
public class SimpleRequest implements ControllerRequest {

    private final String field;
    private final List<String> values;

    public SimpleRequest(String field, List<String> values) {
        this.field = field;
        this.values = values;
    }

    public List<String> getValues() {
        return values;
    }

    @Override public String getSignature() {
        return field;
    }
}
