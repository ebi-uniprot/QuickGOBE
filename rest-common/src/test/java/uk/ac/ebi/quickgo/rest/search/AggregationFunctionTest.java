package uk.ac.ebi.quickgo.rest.search;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests the behaviour of the {@link AggregateFunction} class.
 */
public class AggregationFunctionTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void nullFunctionTextInLookupThrowsException() throws Exception {
        String functionText = null;
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Unable to find aggregation function for: null");

        AggregateFunction.typeOf(functionText);
    }

    @Test
    public void nonSupportedFunctionTextInLookupThrowsException() throws Exception {
        String functionText = "function";

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Unable to find aggregation function for: " + functionText);

        AggregateFunction.typeOf(functionText);
    }

    @Test
    public void supportedFunctionTextInLookupReturnsTheRightAggregateFunction() throws Exception {
        String functionText = AggregateFunction.UNIQUE.getName();

        AggregateFunction retrievedFunction = AggregateFunction.typeOf(functionText);

        assertThat(retrievedFunction, is(AggregateFunction.UNIQUE));
    }
}
