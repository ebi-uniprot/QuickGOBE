package uk.ac.ebi.quickgo.rest.search;

/**
 * Enumerates the aggregation functions supported by the domain.
 *
 * @author Ricardo Antunes
 */
public enum AggregateFunction {
    COUNT("count"),
    UNIQUE("unique"),
    SUM("sum");

    private String name;

    AggregateFunction(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static AggregateFunction typeOf(String functionText) {
        for(AggregateFunction function : AggregateFunction.values()) {
            if(function.getName().equals(functionText)) {
                return function;
            }
        }

        throw new IllegalArgumentException("Unable to find aggregation function for: " + functionText);
    }
}
