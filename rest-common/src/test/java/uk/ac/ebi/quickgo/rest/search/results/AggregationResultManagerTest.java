package uk.ac.ebi.quickgo.rest.search.results;

import uk.ac.ebi.quickgo.rest.search.AggregateFunction;

import java.util.Optional;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static uk.ac.ebi.quickgo.rest.search.results.AggregationResultTest.assertAggregateResult;

/**
 * Tests the behaviour of the {@link AggregationResultsManager} class.
 */
public class AggregationResultManagerTest {
    private AggregationResultsManager manager;

    @Before
    public void setUp() throws Exception {
        manager = new AggregationResultsManager();
    }

    @Test
    public void findsAggregationResultByFunctionAndName() throws Exception {
        AggregateFunction func = AggregateFunction.COUNT;
        String name = "name";
        double hits = 0;

        manager.addAggregateResult(func, name, hits);

        Optional<AggregationResult> retrievedResultOpt = manager.getAggregationResult(func, name);

        assertThat(retrievedResultOpt.isPresent(), is(true));

        AggregationResult retrievedResult = retrievedResultOpt.get();

        assertAggregateResult(retrievedResult, func, name, hits);
    }

    @Test
    public void doesNotFindAggregationResultWithUnknownName() throws Exception {
        AggregateFunction func = AggregateFunction.COUNT;
        String name = "name";
        double hits = 0;

        manager.addAggregateResult(func, name, hits);

        String unknownName = "unknown";
        Optional<AggregationResult> retrievedResultOpt = manager.getAggregationResult(func, unknownName);

        assertThat(retrievedResultOpt.isPresent(), is(false));
    }

    @Test
    public void aggregationResultGetStoredCorrectly() throws Exception {
        AggregateFunction func = AggregateFunction.COUNT;
        String name = "name";
        double hits = 0;

        manager.addAggregateResult(func, name, hits);

        Set<AggregationResult> results = manager.getAggregationResults();

        assertThat(results, hasSize(1));
        AggregationResult result = results.iterator().next();

        assertAggregateResult(result, func, name, hits);
    }
}