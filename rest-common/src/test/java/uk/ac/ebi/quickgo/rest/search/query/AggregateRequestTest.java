package uk.ac.ebi.quickgo.rest.search.query;

import uk.ac.ebi.quickgo.rest.search.AggregateFunction;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.ac.ebi.quickgo.rest.search.query.AggregateRequest.DEFAULT_AGGREGATE_LIMIT;

/**
 * Tests the behaviour of the {@link AggregateRequest} class.
 */
class AggregateRequestTest {

    private AggregateRequest aggregate;

    @BeforeEach
    void setUp() {
        aggregate = new AggregateRequest("field");
    }

    @Test
    void nullFieldInConstructorThrowsException() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new AggregateRequest(null));
        assertTrue(exception.getMessage().contains("Cannot create aggregate with null name"));
    }

    @Test
    void adding2AggregateFieldsStoresBoth() {
        AggregateFunction count = AggregateFunction.COUNT;

        String goIdField = "geneProductId";
        String annIdField = "annId";

        aggregate.addField(goIdField, count);
        aggregate.addField(annIdField, count);

        Set<AggregateFunctionRequest> retrievedFields = aggregate.getAggregateFunctionRequests();
        assertThat(retrievedFields, hasSize(2));

        AggregateFunctionRequest[] expectedAggregates =
                {new AggregateFunctionRequest(goIdField, count), new AggregateFunctionRequest(annIdField, count)};

        assertThat(retrievedFields, containsInAnyOrder(expectedAggregates));
    }

    @Test
    void addingNullNestedAggregateThrowsException() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> aggregate.addNestedAggregate(null));
        assertTrue(exception.getMessage().contains("Cannot add null nested aggregate"));
    }

    @Test
    void adding2NestedAggregatesStoresBoth() {
        AggregateRequest nestedAggregate1 = new AggregateRequest("field1");
        AggregateRequest nestedAggregate2 = new AggregateRequest("field2");

        aggregate.addNestedAggregate(nestedAggregate1);
        aggregate.addNestedAggregate(nestedAggregate2);

        Set<AggregateRequest> retrievedAggregates = aggregate.getNestedAggregateRequests();
        assertThat(retrievedAggregates, hasSize(2));

        AggregateRequest[] expectedAggregates = {nestedAggregate1, nestedAggregate2};

        assertThat(retrievedAggregates, containsInAnyOrder(expectedAggregates));
    }

    @Test
    void canCreateAggregateWithPositiveLimit() {
        int limit = 1;
        AggregateRequest agg = new AggregateRequest("field1", limit);

        assertThat(agg.getLimit(), is(limit));
    }

    @Test
    void aggregateWithoutLimitSetIndicatesEmptyLimit() {
        assertThat(aggregate.getLimit(), is(DEFAULT_AGGREGATE_LIMIT));
    }

    @Test
    void aggregateWithLimitZeroMeansUseDefaultLimit() {
        AggregateRequest agg = new AggregateRequest("field1", 0);
        assertThat(agg.getLimit(), is(DEFAULT_AGGREGATE_LIMIT));
    }

    @Test
    void aggregateWithNegativeLimitMeansUseDefaultLimit() {
        AggregateRequest agg = new AggregateRequest("field1", -1);
        assertThat(agg.getLimit(), is(DEFAULT_AGGREGATE_LIMIT));
    }
}