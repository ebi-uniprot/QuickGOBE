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
    public void addNestedAggregation(Aggregation aggregation) {
        Preconditions.checkArgument(aggregation != null, "Nested aggregation cannot be null");
        nestedAggregations.add(aggregation);
    }

    /**
     * The name of the aggregation.
     * <p/>
     * Usually the name will take the form of the name of the field(s) the aggregation is focused upon. Although this
     * is not strictly necessary.
     *
     * @return the name of the aggregation
     */
    public String getName() {
        return name;
    }

    /**
     * Returns all the results of aggregation functions applied on fields.
     *
     * @return a set of {@link AggregationResult}s.
     */
    public Set<AggregationResult> getAggregationResults() {
        return aggregationResultsManager.getAggregationResults();
    }

    /**
     * Checks if there are any aggregations results stored within the aggregation.
     *
     * @return true if there is at least one aggregation result, false otherwise.
     */
    public boolean hasAggregationResults() {
        return !aggregationResultsManager.getAggregationResults().isEmpty();
    }

    /**
     * Returns a specific aggregation result, identified by the aggregation function and the field it was applied upon.
     *
     * @param function the aggregation function to lookup
     * @param fieldName the field the {@param function} was applied on
     * @return the {@link AggregationResult}
     */
    public Optional<AggregationResult> getAggregationResult(AggregateFunction function, String fieldName) {
        return aggregationResultsManager.getAggregationResult(function, fieldName);
    }

    /**
     * Returns a set of all nested aggregations.
     *
     * @return a set of the nested aggregations.
     */
    public Set<Aggregation> getNestedAggregations() {
        return nestedAggregations;
    }

    /**
     * Indicates whether the aggregation has nested aggregations stored within it.
     * @return true if there are nested aggregations associated to this aggregation, false otherwise.
     */
    public boolean hasNestedAggregations() {
        return !nestedAggregations.isEmpty();
    }

    /**
     * Returns the set of aggregation buckets associated to this aggregation.
     *
     * @return a set of {@link AggregationBucket}
     */
    public Set<AggregationBucket> getBuckets() {
        return buckets;
    }

    /**
     * Indicates whther the aggregation, at the moment of the method call, has any buckets associated to it.
     * @return
     */
    public boolean hasBuckets() {
        return !buckets.isEmpty();
    }

    /**
     * Inidcates whether the aggregation, at the time of the method call, has at least one of its state elements
     * populated.
     *
     * @return true if it has at least one object stored within it, false otherwise
     */
    public boolean isPopulated() {
        return this.hasAggregationResults() || this.hasNestedAggregations() || this.hasBuckets();
    }
}