package uk.ac.ebi.quickgo.common.search.query;

import com.google.common.base.Preconditions;

/**
 * Expresses a field that should be highlighted if a search matches part of this field.
 *
 * Created 10/02/16
 * @author Edd
 */
public class FieldHighlight {

    private String field;

    public FieldHighlight(String field) {
        Preconditions.checkArgument(field != null && field.length() > 0, "Highlighted field cannot be null or empty");

        this.field = field;
    }

    public String getField() {
        return field;
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FieldHighlight that = (FieldHighlight) o;

        return field != null ? field.equals(that.field) : that.field == null;

    }

    @Override public int hashCode() {
        return field != null ? field.hashCode() : 0;
    }

    @Override public String toString() {
        return "FieldHighlight{" +
                "field='" + field + '\'' +
                '}';
    }
}
