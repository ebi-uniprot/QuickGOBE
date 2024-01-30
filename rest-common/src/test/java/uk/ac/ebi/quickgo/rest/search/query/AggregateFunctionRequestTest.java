package uk.ac.ebi.quickgo.rest.search.query;

import uk.ac.ebi.quickgo.rest.search.AggregateFunction;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the behaviour of the {@link AggregateFunctionRequest} class
 */
class AggregateFunctionRequestTest {

    @Test
    void nullFieldInConstructorThrowsException() {
        String field = null;
        AggregateFunction function = AggregateFunction.COUNT;
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new AggregateFunctionRequest(field, function));
        assertTrue(exception.getMessage().contains("Field cannot be null or empty"));
    }

    @Test
    void emptyFieldInConstructorThrowsException() {
        String field = "";
        AggregateFunction function = AggregateFunction.COUNT;
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new AggregateFunctionRequest(field, function));
        assertTrue(exception.getMessage().contains("Field cannot be null or empty"));
    }

    @Test
    void nullFunctionInConstructorThrowsException() {
        String field = "field";
        AggregateFunction function = null;
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new AggregateFunctionRequest(field, function));
        assertTrue(exception.getMessage().contains("AggregateRequest function cannot be null"));
    }

    @Test
    void createsValidAggregateFieldWithValidFieldAndFunction() {
        String field = "field";
        AggregateFunction function = AggregateFunction.COUNT;

        AggregateFunctionRequest aggField = new AggregateFunctionRequest(field, function);

        assertThat(aggField.getField(), is(field));
        assertThat(aggField.getFunction(), is(function));
    }
}
