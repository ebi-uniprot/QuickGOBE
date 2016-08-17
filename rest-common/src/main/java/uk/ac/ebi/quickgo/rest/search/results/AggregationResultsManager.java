package uk.ac.ebi.quickgo.rest.search.results;

import uk.ac.ebi.quickgo.rest.search.AggregateFunction;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Class to help manage aa collection of aggregation results.
 */
class AggregationResultsManager {
    private final Set<AggregationResult> aggregationResults;

    public AggregationResultsManager() {
        this.aggregationResults = new LinkedHashSet<>();
    }

    public void addAggregateResult(AggregateFunction function, String name, double result) {
        aggregationResults.add(new AggregationResult(function, name, result));
    }

    public Optional<AggregationResult> getAggregationResult(AggregateFunction function, String fieldName) {
        return aggregationResults.stream()
                .filter(aggregateResult -> aggregateResult.getFunction() == function)
                .filter(aggregateResult -> aggregateResult.getField().equalsIgnoreCase(fieldName))
                .findFirst();
    }

    public Set<AggregationResult> getAggregationResults() {
        return aggregationResults;
    }

    @Override public String toString() {
        return "AggregationResultsManager{" +
                "aggregationResults=" + aggregationResults +
                '}';
    }
}
