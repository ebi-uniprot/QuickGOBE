package uk.ac.ebi.quickgo.rest.search.query;

import com.google.common.base.Preconditions;

/**
 * A simple abstract field representation.
 *
 * Created 11/02/16
 * @author Edd
 */
public abstract class AbstractField implements Field {
    protected String field;

    public AbstractField(String field) {
        Preconditions.checkArgument(field != null && field.length() > 0, "Field cannot be null or empty");

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

        AbstractField that = (AbstractField) o;

        return field != null ? field.equals(that.field) : that.field == null;

    }

    @Override public int hashCode() {
        return field != null ? field.hashCode() : 0;
    }

    @Override public String toString() {
        return "AbstractField{" +
                "field='" + field + '\'' +
                '}';
    }
}
