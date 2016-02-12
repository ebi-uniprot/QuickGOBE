package uk.ac.ebi.quickgo.common.search.query;

/**
 * Expresses a field to be highlighted if a search matches part of this field.
 *
 * Created 10/02/16
 * @author Edd
 */
public class FieldHighlight extends AbstractField {

    public FieldHighlight(String field) {
        super(field);
    }

    @Override public String toString() {
        return "FieldHighlight{" +
                "field='" + field + '\'' +
                '}';
    }
}
