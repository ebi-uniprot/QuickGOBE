package uk.ac.ebi.quickgo.rest.search.results;

import uk.ac.ebi.quickgo.rest.search.AggregateFunction;

import com.google.common.base.Preconditions;
import java.util.Optional;
import java.util.Set;

/**
 * Represents a distinct value, within a field.
 *
 * Aggregations can be executed over bucket values.
 */
public class AggregationBucket {
    private final String value;
    private final AggregationResultsManager aggregationResultsManager;

    public AggregationBucket(String value) {
        Preconditions.checkArgument(value != null, "AggregationBucket name cannot be null");

        this.value = value;
        aggregationResultsManager = new AggregationResultsManager();
    }

    public void addAggregateResult(AggregateFunction function, String name, double result) {
        aggregationResultsManager.addAggregateResult(function, name, result);
    }

    public String getValue() {
        return value;
    }

    public Optional<AggregationResult> getAggregationResult(AggregateFunction function, String fieldName) {
        return aggregationResultsManager.getAggregationResult(function, fieldName);
    }

    public Set<AggregationResult> getAggregationResults() {
        return aggregationResultsManager.getAggregationResults();
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AggregationBucket bucket = (AggregationBucket) o;

        if (!value.equals(bucket.value)) {
            return false;
        }
        return aggregationResultsManager.getAggregationResults()
                .equals(bucket.aggregationResultsManager.getAggregationResults());
    }

    @Override public int hashCode() {
        int result = value.hashCode();
        result = 31 * result + aggregationResultsManager.getAggregationResults().hashCode();
        return result;
    }

    @Override public String toString() {
        return "AggregationBucket{" +
                "value='" + value + '\'' +
                ", results=" + aggregationResultsManager.getAggregationResults() +
                '}';
    }
}
