package uk.ac.ebi.quickgo.rest.search.query;

import uk.ac.ebi.quickgo.rest.search.AggregateFunction;

import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Data-source representing which supported aggregation aggregateFunctionRequests {@link AggregateFunction} should be
 * executed over the provided name.
 * <p/>
 * An instance of an aggregate could have one or several of the following:
 * <ul>
 *     <li>A name: If the aggregate is based on a field, then name is the field's name. If the aggregate is based on a
 *     query result then the name could be anything.</li>
 *     <li>A set of {@link AggregateFunctionRequest}s: An AggregateFunctionRequest represents a call to an
 *     {@link AggregateFunction} on a particular name. This name could be the aggregate name, or any other name that can
 *     be calculated within table/collection used by the data-source, example: sum(field1) or unique(field2)
 *     </li>
 *     <li>A set of {@link AggregateRequest#nestedAggregateRequests}: A nested aggregate represents the desire to
 *     provide further aggregation calculations based on a name that is different to that of the current aggregation.
 *     Think of it as a drilled down view of the current aggregation with results focused on another name.
 *     </li>
 * </ul>
 * As an example, assume that the data source has a table/collection of orders, with the following
 * aggregateFunctionRequests: order_item_id, quantity, cost.
 * An {@link AggregateRequest} could hold the following requests:
 * <ul>
 *
 *     <li>provide the total cost of all orders</li>
 *     <li>aggregate on order_item_id</li>
 *        <ul>
 *            <li>sum the quantity of all items with the same order_item_id</li>
 *        </ul>
 *     </li>
 * </ul>
 * The object model would something similar to this:
 * <pre>
 *     aggregate:
 *         - name: query_result
 *         - aggregateFunctionRequests: [sum(cost)]
 *         - nestedAggregateRequests: [
 *              - aggregate:
 *                  - name: order_item_id
 *                  - limit: 20
 *                  - aggregateResults: [sum(quantity)]
 * </pre>
 *
 * @author Ricardo Antunes
 */
public class AggregateRequest {
    public static final int DEFAULT_AGGREGATE_LIMIT = 10;
    private static final Logger LOGGER = getLogger(AggregateRequest.class);
    private final String name;
    private final Set<AggregateFunctionRequest> aggregateFunctionRequests;
    private final Set<AggregateRequest> nestedAggregateRequests;
    private final int limit;

    public AggregateRequest(String name) {
        this(name, DEFAULT_AGGREGATE_LIMIT);
    }

    public AggregateRequest(String name, int limit) {
        Preconditions.checkArgument(name != null, "Cannot create aggregate with null name");
        this.name = name;
        this.aggregateFunctionRequests = new HashSet<>();
        this.nestedAggregateRequests = new HashSet<>();

        if (limit <= 0) {
            LOGGER.warn("Attempt to set AggregateRequest limit to {}. Value must be greater than 0.", limit);
            this.limit = DEFAULT_AGGREGATE_LIMIT;
        } else {
            this.limit = limit;
        }
    }

    public String getName() {
        return name;
    }

    public int getLimit() {
        return limit;
    }

    public Set<AggregateFunctionRequest> getAggregateFunctionRequests() {
        return aggregateFunctionRequests;
    }

    public Set<AggregateRequest> getNestedAggregateRequests() {
        return Collections.unmodifiableSet(nestedAggregateRequests);
    }

    public void addField(String field, AggregateFunction function) {
        aggregateFunctionRequests.add(new AggregateFunctionRequest(field, function));
    }

    public void addNestedAggregate(AggregateRequest aggregate) {
        Preconditions.checkArgument(aggregate != null, "Cannot add null nested aggregate");
        nestedAggregateRequests.add(aggregate);
    }

    @Override public String toString() {
        return "AggregateRequest{" +
                "name='" + name + '\'' +
                ", aggregateFunctionRequests=" + aggregateFunctionRequests +
                ", nestedAggregateRequests=" + nestedAggregateRequests +
                ", limit=" + limit +
                '}';
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AggregateRequest that = (AggregateRequest) o;

        if (limit != that.limit) {
            return false;
        }
        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        if (aggregateFunctionRequests != null ? !aggregateFunctionRequests.equals(that.aggregateFunctionRequests) :
                that.aggregateFunctionRequests != null) {
            return false;
        }
        return nestedAggregateRequests != null ? nestedAggregateRequests.equals(that.nestedAggregateRequests) :
                that.nestedAggregateRequests == null;
    }

    @Override public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (aggregateFunctionRequests != null ? aggregateFunctionRequests.hashCode() : 0);
        result = 31 * result + (nestedAggregateRequests != null ? nestedAggregateRequests.hashCode() : 0);
        result = 31 * result + limit;
        return result;
    }
}
