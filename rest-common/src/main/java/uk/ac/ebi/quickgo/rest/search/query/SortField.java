package uk.ac.ebi.quickgo.rest.search.query;

/**
 * Represents a field that should be sorted.
 *
 * Created 16/01/17
 * @author Edd
 */
public class SortField extends AbstractField {
    SortField(String field) {
        super(field);
    }

    @Override public String toString() {
        return "SortField{" +
                "field='" + field + '\'' +
                '}';
    }
}
