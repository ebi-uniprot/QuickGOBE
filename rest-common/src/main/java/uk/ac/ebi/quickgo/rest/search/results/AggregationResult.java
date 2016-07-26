package uk.ac.ebi.quickgo.rest.search.results;

import uk.ac.ebi.quickgo.rest.search.AggregateFunction;

import com.google.common.base.Preconditions;

/**
 * A key value pair that represents the result of an aggregation done over a field within a table/collection.
 * @see AggregateFunction
 */
public class AggregationResult {
    private final AggregateFunction function;

    /**
     * Name given to the aggregation result
     */
    private final String field;

    /**
     * The value of the aggregation
     */
    private final double result;

    AggregationResult(AggregateFunction function, String field, double result) {
        Preconditions.checkArgument(function != null, "AggregateRequest function cannot be null.");
        Preconditions.checkArgument(field != null && !field.trim().isEmpty(), "AggregateRequest field cannot be null or " +
                "empty.");

        this.function = function;
        this.field = field;
        this.result = result;
    }

    public AggregateFunction getFunction() {
        return function;
    }

    public String getField() {
        return field;
    }

    public double getResult() {
        return result;
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AggregationResult that = (AggregationResult) o;

        if (Double.compare(that.result, result) != 0) {
            return false;
        }
        return field.equals(that.field);

    }

    @Override public int hashCode() {
        int result1;
        long temp;
        result1 = field.hashCode();
        temp = Double.doubleToLongBits(result);
        result1 = 31 * result1 + (int) (temp ^ (temp >>> 32));
        return result1;
    }

    @Override public String toString() {
        return "AggregationResult{" +
                "field='" + field + '\'' +
                ", result=" + result +
                '}';
    }

}