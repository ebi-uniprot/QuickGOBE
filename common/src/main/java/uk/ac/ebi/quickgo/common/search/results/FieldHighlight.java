package uk.ac.ebi.quickgo.common.search.results;

import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Represents a highlighted field within a {@link DocHighlight}. The field
 * stores a {@link List} of {@link String} values, corresponding to the highlighted
 * hits associated with this field.
 *
 * Created 01/02/16
 * @author Edd
 */
public class FieldHighlight {
    private final List<String> values;
    private final String field;

    public FieldHighlight(String field, List<String> values) {
        this.field = requireNonNull(field);
        this.values = requireNonNull(values);
    }

    public List<String> getValues() {
        return Collections.unmodifiableList(values);
    }

    public String getField() {
        return field;
    }
}
