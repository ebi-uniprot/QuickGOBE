package uk.ac.ebi.quickgo.rest.search;

/**
 * Enumerates the aggregation functions supported by the domain.
 *
 * @author Ricardo Antunes
 */
public enum AggregateFunction {
    COUNT("count"),
    UNIQUE("unique");

    private String name;

    AggregateFunction(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
