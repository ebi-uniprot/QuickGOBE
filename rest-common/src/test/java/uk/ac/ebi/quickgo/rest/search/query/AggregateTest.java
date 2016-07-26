package uk.ac.ebi.quickgo.rest.search.query;

import uk.ac.ebi.quickgo.rest.search.AggregateFunction;

import java.util.Set;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;

/**
 * Tests the behaviour of the {@link Aggregate} class.
 */
public class AggregateTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Aggregate aggregate;

    @Before
    public void setUp() throws Exception {
        aggregate = new Aggregate("field");
    }

    @Test
    public void nullFieldInConstructorThrowsException() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Cannot create aggregate with null field");

        new Aggregate(null);
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
        Aggregate nestedAggregate1 = new Aggregate("field1");
        Aggregate nestedAggregate2 = new Aggregate("field2");

        aggregate.addNestedAggregate(nestedAggregate1);
        aggregate.addNestedAggregate(nestedAggregate2);

        Set<Aggregate> retrievedAggregates = aggregate.getNestedAggregates();
        assertThat(retrievedAggregates, hasSize(2));

        Aggregate[] expectedAggregates = {nestedAggregate1, nestedAggregate2};

        assertThat(retrievedAggregates, containsInAnyOrder(expectedAggregates));
    }
}