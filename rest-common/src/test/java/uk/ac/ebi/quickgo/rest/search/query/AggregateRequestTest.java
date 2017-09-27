package uk.ac.ebi.quickgo.rest.search.query;

import uk.ac.ebi.quickgo.rest.search.AggregateFunction;

import java.util.Set;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static uk.ac.ebi.quickgo.rest.search.query.AggregateRequest.DEFAULT_AGGREGATE_LIMIT;

/**
 * Tests the behaviour of the {@link AggregateRequest} class.
 */
public class AggregateRequestTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private AggregateRequest aggregate;

    @Before
    public void setUp() throws Exception {
        aggregate = new AggregateRequest("field");
    }

    @Test
    public void nullFieldInConstructorThrowsException() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Cannot create aggregate with null name");

        new AggregateRequest(null);
    }

    @Test
    public void adding2AggregateFieldsStoresBoth() throws Exception {
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
    public void addingNullNestedAggregateThrowsException() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Cannot add null nested aggregate");

        aggregate.addNestedAggregate(null);
    }

    @Test
    public void adding2NestedAggregatesStoresBoth() throws Exception {
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
    public void canCreateAggregateWithPositiveLimit() {
        int limit = 1;
        AggregateRequest agg = new AggregateRequest("field1", limit);

        assertThat(agg.getLimit(), is(limit));
    }

    @Test
    public void aggregateWithoutLimitSetIndicatesEmptyLimit() {
        assertThat(aggregate.getLimit(), is(DEFAULT_AGGREGATE_LIMIT));
    }

    @Test
    public void aggregateWithLimitZeroMeansUseDefaultLimit() {
        AggregateRequest agg = new AggregateRequest("field1", 0);
        assertThat(agg.getLimit(), is(DEFAULT_AGGREGATE_LIMIT));
    }

    @Test
    public void aggregateWithNegativeLimitMeansUseDefaultLimit() {
        AggregateRequest agg = new AggregateRequest("field1", -1);
        assertThat(agg.getLimit(), is(DEFAULT_AGGREGATE_LIMIT));
    }
}