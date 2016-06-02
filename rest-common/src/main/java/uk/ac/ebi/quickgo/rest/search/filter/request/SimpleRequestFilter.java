package uk.ac.ebi.quickgo.rest.search.filter.request;

import java.util.List;

/**
 * Created 02/06/16
 * @author Edd
 */
public class SimpleRequestFilter implements RequestFilter {

    private final String field;
    private final List<String> values;

    public SimpleRequestFilter(String field, List<String> values) {
        this.field = field;
        this.values = values;
    }

    public String getField() {
        return field;
    }

    public List<String> getValues() {
        return values;
    }

    @Override public String getSignature() {
        return getField();
    }
}
