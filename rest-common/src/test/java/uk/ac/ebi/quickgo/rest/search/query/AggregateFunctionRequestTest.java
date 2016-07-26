package uk.ac.ebi.quickgo.rest.search.query;

import uk.ac.ebi.quickgo.rest.search.AggregateFunction;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests the behaviour of the {@link AggregateFunctionRequest} class
 */
public class AggregateFunctionRequestTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void nullFieldInConstructorThrowsException() throws Exception {
        String field = null;
        AggregateFunction function = AggregateFunction.COUNT;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Field cannot be null or empty");

        new AggregateFunctionRequest(field, function);
    }

    @Test
    public void emptyFieldInConstructorThrowsException() throws Exception {
        String field = "";
        AggregateFunction function = AggregateFunction.COUNT;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Field cannot be null or empty");

        new AggregateFunctionRequest(field, function);
    }

    @Test
    public void nullFunctionInConstructorThrowsException() throws Exception {
        String field = "field";
        AggregateFunction function = null;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("AggregateRequest function cannot be null");

        new AggregateFunctionRequest(field, function);
    }

    @Test
    public void createsValidAggregateFieldWithValidFieldAndFunction() throws Exception {
        String field = "field";
        AggregateFunction function = AggregateFunction.COUNT;

        AggregateFunctionRequest aggField = new AggregateFunctionRequest(field, function);

        assertThat(aggField.getField(), is(field));
        assertThat(aggField.getFunction(), is(function));
    }
}
