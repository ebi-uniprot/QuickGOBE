package uk.ac.ebi.quickgo.rest.search.results;

import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.List;

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
        Preconditions.checkArgument(field != null, "Highlighted field can not be null");
        Preconditions.checkArgument(values != null, "Highlighted values can not be null");

        this.field = field;
        this.values = values;
    }

    public List<String> getValues() {
        return Collections.unmodifiableList(values);
    }

    public String getField() {
        return field;
    }
}
