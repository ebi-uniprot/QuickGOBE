package uk.ac.ebi.quickgo.rest.search.results;

import uk.ac.ebi.quickgo.rest.search.query.Aggregate;
import uk.ac.ebi.quickgo.rest.search.AggregateFunction;

import com.google.common.base.Preconditions;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Stores the result of the aggregation section found within the response of the underlying data source.
 *
 * If {@link Aggregate} instances are attached to the
 * {@link uk.ac.ebi.quickgo.rest.search.query.QueryRequest}, then the underlying data source will always return
 * aggregate specific data.
 *
 * @author Ricardo Antunes
 */
public class Aggregation {
    private final String name;
    private final AggregationResultsManager aggregationResultsManager;
    private final Set<Aggregation> nestedAggregations;
    private final Set<AggregationBucket> buckets;

    public Aggregation(String name) {
        Preconditions.checkArgument(name != null && !name.trim().isEmpty(), "Name cannot be null or empty");

        this.name = name;

        this.aggregationResultsManager = new AggregationResultsManager();
        nestedAggregations = new LinkedHashSet<>();
        buckets = new LinkedHashSet<>();
    }

    public void addAggregationResult(AggregateFunction function, String name, double result) {
        aggregationResultsManager.addAggregateResult(function, name, result);
    }

    public void addBucket(AggregationBucket bucket) {
        Preconditions.checkArgument(bucket != null, "AggregationBucket cannot be null");
        buckets.add(bucket);
    }

    public void addAggregation(Aggregation aggregation) {
        Preconditions.checkArgument(aggregation != null, "Nested aggregation cannot be null");
        nestedAggregations.add(aggregation);
    }

    public String getName() {
        return name;
    }

    public Set<AggregationResult> getAggregationResults() {
        return aggregationResultsManager.getAggregationResults();
    }

    public Optional<AggregationResult> getAggregationResult(AggregateFunction function, String fieldName) {
        return aggregationResultsManager.getAggregationResult(function, fieldName);
    }

    public Set<Aggregation> getNestedAggregations() {
        return nestedAggregations;
    }

    public Set<AggregationBucket> getBuckets() {
        return buckets;
    }

}