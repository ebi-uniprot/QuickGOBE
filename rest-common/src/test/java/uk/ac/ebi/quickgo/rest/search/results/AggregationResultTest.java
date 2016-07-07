package uk.ac.ebi.quickgo.rest.search.results;

import uk.ac.ebi.quickgo.rest.search.AggregateFunction;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests the behaviour of the {@link AggregationResult}
 */
public class AggregationResultTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void nullFunctionInConstructorThrowsException() throws Exception {
        AggregateFunction func = null;
        String name = "name";
        double hits = 0;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Aggregate function cannot be null.");

        new AggregationResult(func, name, hits);
    }

    @Test
    public void nullAggregationNameInConstructorThrowsException() throws Exception {
        AggregateFunction func = AggregateFunction.COUNT;
        String name = null;
        double hits = 0;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Aggregate field cannot be null or empty.");

        new AggregationResult(func, name, hits);
    }

    @Test
    public void emptyAggregationNameInConstructorThrowsException() throws Exception {
        AggregateFunction func = AggregateFunction.COUNT;
        String name = "";
        double hits = 0;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Aggregate field cannot be null or empty.");

        new AggregationResult(func, name, hits);
    }

    @Test
    public void creatingAggregationResultWithFunctionAndNameAndResultIsSuccessful() throws Exception {
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