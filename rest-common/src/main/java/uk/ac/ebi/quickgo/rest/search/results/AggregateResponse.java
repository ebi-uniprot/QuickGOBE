package uk.ac.ebi.quickgo.rest.search.results;

import uk.ac.ebi.quickgo.rest.search.AggregateFunction;
import uk.ac.ebi.quickgo.rest.search.query.AggregateRequest;

import com.google.common.base.Preconditions;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Stores the result of the aggregation section found within the response of the underlying data source.
 * <p/>
 * An instance of an aggregation could have one or several of the following:
 * <ul>
 *     <li>The name of the aggregation. If the aggregation is based on a field, then the name is name of the field,
 *     however if the aggregation is based on a query result then the name could be anything.</li>
 *     <li>A set of {@link AggregationResult}s: An aggregation result is a value that reflects the application of
 *     an {@link AggregateFunction} on a particular field. This field could be the aggregate field, or any other
 *     field that can be calculated within the response's ability, example: sum(field1) or unique(field2)</li>
 *     <li>A set of {@link AggregationBucket}: Each bucket represents a distinct value of the field being
 *     aggregated upon, as well as any {@link AggregationResult} applied to the bucket value</li>
 *     <li>A set of {@link AggregateResponse#nestedAggregations}: A nested aggregation is a drilled down view of the main
 *     aggregation. This means that it tries to retrieve metrics on a different aggregate field, but always based on
 *     the result of the current aggregation</li>
 *     <li>A distinct value count. The number of buckets contained in an instance of this class is constrained by
 *     the limits applied at the time the aggregation was defined. This value holds the total number of distinct
 *     values (i.e. the number of buckets there would be if no limits were applied)</li>
 * </ul>
 * <p/>
 * As an example, assume that the data source has a table/collection of orders, with the following fields:
 * order_item_id, quantity, cost.
 * Sending an {@link AggregateRequest} object, to the data store, with the following requests:
 * <ul>
 *     <li>provide the total cost of all orders</li>
 *     <li>aggregate on order_item_id</li>
 *        <ul>
 *            <li>sum the quantity of all items with the same order_item_id</li>
 *        </ul>
 *     </li>
 * </ul>
 * Aggregating on the field would turn up an the following aggregation:
 * <pre>
 * aggregation:
 *    - name: query_result
 *    - aggregationResults: [5] - where 5 represents the total cost of all orders
 *    - nestedAggregations: [
 *        - name: order_item_id
 *        - aggregationBuckets: [
 *            - value: order_item_1
 *                - aggregationResults: [10] - where 10 represents the sum of the quantities for order_item_1
 *            - value: order_item_2
 *                - aggregationResults: [5] - where 5 represents the sum of the quantities for order_item_2
 *            ...
 *        ]
 *       - distinctValueCount: the total number of order items (the results for the query may show only order items
 *       only over a certain value, or goods type.
 * </pre>
 * <p/>
 * If {@link AggregateRequest} instances are attached to the
 * {@link uk.ac.ebi.quickgo.rest.search.query.QueryRequest}, then the underlying data source will always return
 * aggregate specific data.
 *
 * @author Ricardo Antunes
 */
public class AggregateResponse {
    private final String name;
    private final AggregationResultsManager aggregationResultsManager;
    private final Set<AggregateResponse> nestedAggregations;
    private final Set<AggregationBucket> buckets;
    private int distinctValuesCount;

    public AggregateResponse(String name) {
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
    public Set<AggregateResponse> getNestedAggregations() {
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
     * Indicates whether the aggregation, at the moment of the method call, has any buckets associated to it.
     * @return boolean indicating whether or not the aggregation has associated buckets
     */
    public boolean hasBuckets() {
        return !buckets.isEmpty();
    }

    /**
     * Indicates whether the aggregation, at the time of the method call, has at least one of its state elements
     * populated.
     *
     * @return true if it has at least one object stored within it, false otherwise
     */
    public boolean isPopulated() {
        return this.hasAggregationResults() || this.hasNestedAggregations() || this.hasBuckets();
    }

    /**
     * Set the total number of distinct values for the aggregation.
     * @param distinctValuesCount
     */
    public void setDistinctValuesCount(int distinctValuesCount) {
        this.distinctValuesCount = distinctValuesCount;
    }

    /**
     * Return the total number of distinct values for the aggregation.
     * @return total number of distinct values
     */
    public int getDistinctValuesCount() {
        return distinctValuesCount;
    }

    @Override public String toString() {
        return "AggregateResponse{" +
                "name='" + name + '\'' +
                ", aggregationResultsManager=" + aggregationResultsManager +
                ", nestedAggregations=" + nestedAggregations +
                ", buckets=" + buckets +
                ", distinctValuesCount=" + distinctValuesCount +
                '}';
    }
}
