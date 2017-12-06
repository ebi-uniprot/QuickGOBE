package uk.ac.ebi.quickgo.rest.search.results;

import uk.ac.ebi.quickgo.rest.search.AggregateFunction;

import com.google.common.base.Preconditions;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Builder for {@link AggregateResponse}
 */

public class AggregateResponseBuilder {
    private String name;
    private AggregationResultsManager aggregationResultsManager;
    private final Set<AggregateResponse> nestedAggregations;
    private final Set<AggregationBucket> buckets;
    private int distinctValuesCount;

    public AggregateResponseBuilder(String name) {
        Preconditions.checkArgument(name != null && !name.trim().isEmpty(), "Name cannot be null or empty");

        this.name = name;
        this.aggregationResultsManager = new AggregationResultsManager();
        nestedAggregations = new LinkedHashSet<>();
        buckets = new LinkedHashSet<>();
    }

    /**
     * Adds the result of the application of an aggregation function over a given field.
     *
     * @param function the aggregation function that was applied
     * @param field the field the function was applied over
     * @param result the result of the application of the function over the field
     */
    public void addAggregationResult(AggregateFunction function, String field, double result) {
        aggregationResultsManager.addAggregateResult(function, field, result);
    }

    /**
     * Adds a set of distinct values of a given field. The bucket can also potentially contain aggregation
     * results.
     *
     * @param bucket a set of distinct results of field
     */
    public void addBucket(AggregationBucket bucket) {
        Preconditions.checkArgument(bucket != null, "AggregationBucket cannot be null");
        buckets.add(bucket);
    }

    /**
     * Adds a lower level aggregation to the current one. Usually nested aggregations are drilled down results
     * applied to a different field than the current aggregation.
     *
     * @param aggregation an aggregation
     */
    public void addNestedAggregation(AggregateResponse aggregation) {
        Preconditions.checkArgument(aggregation != null, "Nested aggregation cannot be null");
        nestedAggregations.add(aggregation);
    }

    /**
     * Set the total number of distinct values for the aggregation.
     * @param distinctValuesCount calculated number of values of that type
     */
    public void setDistinctValuesCount(int distinctValuesCount) {
        this.distinctValuesCount = distinctValuesCount;
    }

    /**
     * Create an {@link AggregateResponse} instance from this class
     * @return AggregateResponse instance.
     */
    public AggregateResponse createAggregateResponse() {
        return new AggregateResponse(name, aggregationResultsManager, nestedAggregations, buckets, distinctValuesCount);
    }
}
