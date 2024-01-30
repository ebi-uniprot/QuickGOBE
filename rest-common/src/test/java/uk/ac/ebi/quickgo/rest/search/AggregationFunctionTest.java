package uk.ac.ebi.quickgo.rest.search;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the behaviour of the {@link AggregateFunction} class.
 */
class AggregationFunctionTest {

    @Test
    void nullFunctionTextInLookupThrowsException() {
        String functionText = null;
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> AggregateFunction.typeOf(functionText));
        assertTrue(exception.getMessage().contains("Unable to find aggregation function for: null"));
    }

    @Test
    void nonSupportedFunctionTextInLookupThrowsException() {
        String functionText = "function";
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> AggregateFunction.typeOf(functionText));
        assertTrue(exception.getMessage().contains("Unable to find aggregation function for: " + functionText));
    }

    @Test
    void supportedFunctionTextInLookupReturnsTheRightAggregateFunction() {
        String functionText = AggregateFunction.UNIQUE.getName();

        AggregateFunction retrievedFunction = AggregateFunction.typeOf(functionText);

        assertThat(retrievedFunction, is(AggregateFunction.UNIQUE));
    }
}
