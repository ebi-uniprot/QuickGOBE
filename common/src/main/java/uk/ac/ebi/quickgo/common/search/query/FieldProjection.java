package uk.ac.ebi.quickgo.common.search.query;

import com.google.common.base.Preconditions;

/**
 * Created 10/02/16
 * @author Edd
 */
public class FieldProjection {

    private String field;

    public FieldProjection(String field) {
        Preconditions.checkArgument(field != null && field.length() > 0, "Projected field cannot be null or empty");

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

        FieldProjection that = (FieldProjection) o;

        return field != null ? field.equals(that.field) : that.field == null;

    }

    @Override public int hashCode() {
        return field != null ? field.hashCode() : 0;
    }

    @Override public String toString() {
        return "FieldProjection{" +
                "field='" + field + '\'' +
                '}';
    }
}
