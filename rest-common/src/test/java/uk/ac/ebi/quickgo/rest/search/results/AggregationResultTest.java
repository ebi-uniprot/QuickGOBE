package uk.ac.ebi.quickgo.rest.search.results;

import uk.ac.ebi.quickgo.rest.search.AggregateFunction;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the behaviour of the {@link AggregationResult}
 */
class AggregationResultTest {

    @Test
    void nullFunctionInConstructorThrowsException() {
        AggregateFunction func = null;
        String name = "name";
        double hits = 0;
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new AggregationResult(func, name, hits));
        assertTrue(exception.getMessage().contains("AggregateRequest function cannot be null."));
    }

    @Test
    void nullAggregationNameInConstructorThrowsException() {
        AggregateFunction func = AggregateFunction.COUNT;
        String name = null;
        double hits = 0;
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new AggregationResult(func, name, hits));
        assertTrue(exception.getMessage().contains("AggregateRequest field cannot be null or empty."));
    }

    @Test
    void emptyAggregationNameInConstructorThrowsException() {
        AggregateFunction func = AggregateFunction.COUNT;
        String name = "";
        double hits = 0;
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new AggregationResult(func, name, hits));
        assertTrue(exception.getMessage().contains("AggregateRequest field cannot be null or empty."));
    }

    @Test
    void creatingAggregationResultWithFunctionAndNameAndResultIsSuccessful() {
        AggregateFunction func = AggregateFunction.COUNT;
        String name = "name";
        double hits = 0;

        AggregationResult result = new AggregationResult(func, name, hits);

        assertAggregateResult(result, func, name, hits);
    }

    static void assertAggregateResult(AggregationResult result, AggregateFunction func, String name,
            double hits) {
        assertThat(result.getFunction(), is(func));
        assertThat(result.getField(), is(name));
        assertThat(result.getResult(), is(hits));
    }
}