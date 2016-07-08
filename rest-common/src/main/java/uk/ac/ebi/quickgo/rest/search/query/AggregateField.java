package uk.ac.ebi.quickgo.rest.search.query;

import uk.ac.ebi.quickgo.rest.search.AggregateFunction;

import com.google.common.base.Preconditions;

/**
 * Represents a field that will have an {@link AggregateFunction} applied to it.
 *
 * @author Ricardo Antunes
 */
public class AggregateField {
    private final AggregateFunction function;
    private final String field;

    public AggregateField(String field, AggregateFunction function) {
        Preconditions.checkArgument(field != null && !field.trim().isEmpty(), "Field cannot be null or empty");
        Preconditions.checkArgument(function != null, "Aggregate function cannot be null");

        this.function = function;
        this.field = field;
    }

    public AggregateFunction getFunction() {
        return function;
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

        AggregateField that = (AggregateField) o;

        if (function != that.function) {
            return false;
        }
        return field.equals(that.field);

    }

    @Override public int hashCode() {
        int result = function.hashCode();
        result = 31 * result + field.hashCode();
        return result;
    }

    @Override public String toString() {
        return "AggregateField{" +
                "function=" + function +
                ", field='" + field + '\'' +
                '}';
    }
}