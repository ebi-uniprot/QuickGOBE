package uk.ac.ebi.quickgo.rest.search.results;

import uk.ac.ebi.quickgo.rest.search.AggregateFunction;
import uk.ac.ebi.quickgo.rest.search.query.AggregateRequest;

import com.google.common.base.Preconditions;
import java.util.Optional;
import java.util.Set;

import static java.util.Collections.unmodifiableSet;

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
 *     <li>A set of {@link AggregateResponse#nestedAggregations}: A nested aggregation is a drilled down view of the
 *     main
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
 *       - approximateCount: estimated total number of order items (the results for the query may show only order
 *       items
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
    private final int distinctValuesCount;

    public AggregateResponse(String name, AggregationResultsManager aggregationResultsManager, Set<AggregateResponse>
            nestedAggregations, Set<AggregationBucket> buckets, int distinctValuesCount) {
        Preconditions.checkArgument(name != null && !name.trim().isEmpty(), "Name cannot be null or empty");
        Preconditions.checkArgument(aggregationResultsManager != null, "Aggregation Results Manager cannot be null");
        Preconditions.checkArgument(nestedAggregations != null && !name.trim().isEmpty(), "Nested Aggregations " +
                "cannot be null");
        Preconditions.checkArgument(buckets != null && !name.trim().isEmpty(), "Buckets " +
                "cannot be null");
        Preconditions.checkArgument(distinctValuesCount >= 0, "DistinctValueCount must be zero or greater");

        this.name = name;
        this.aggregationResultsManager = aggregationResultsManager;
        this.nestedAggregations = nestedAggregations;
        this.buckets = buckets;
        this.distinctValuesCount = distinctValuesCount;
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
     * @return a unmodifiable set of {@link AggregationResult}s.
     */
    public Set<AggregationResult> getAggregationResults() {
        return unmodifiableSet(aggregationResultsManager.getAggregationResults());
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
     * @return a unmodifiable set of the nested aggregations.
     */
    public Set<AggregateResponse> getNestedAggregations() {
        return unmodifiableSet(nestedAggregations);
    }

    /**
     * Indicates whether the aggregation has nested aggregations stored within it.
     * @return true if there are nested aggregations associated to this aggregation, false otherwise.
     */
    boolean hasNestedAggregations() {
        return !nestedAggregations.isEmpty();
    }

    /**
     * Returns the set of aggregation buckets associated to this aggregation.
     *
     * @return a unmodifiable set of {@link AggregationBucket}
     */
    public Set<AggregationBucket> getBuckets() {
        return unmodifiableSet(buckets);
    }

    /**
     * Indicates whether the aggregation, at the moment of the method call, has any buckets associated to it.
     * @return boolean indicating whether or not the aggregation has associated buckets
     */
    boolean hasBuckets() {
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

    /**
     * Checks if there are any aggregations results stored within the aggregation.
     *
     * @return true if there is at least one aggregation result, false otherwise.
     */
    boolean hasAggregationResults() {
        return !aggregationResultsManager.getAggregationResults().isEmpty();
    }
}
