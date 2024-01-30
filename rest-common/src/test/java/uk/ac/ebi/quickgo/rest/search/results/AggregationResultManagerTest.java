package uk.ac.ebi.quickgo.rest.search.results;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.ebi.quickgo.rest.search.AggregateFunction;

import java.util.Optional;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static uk.ac.ebi.quickgo.rest.search.results.AggregationResultTest.assertAggregateResult;

/**
 * Tests the behaviour of the {@link AggregationResultsManager} class.
 */
class AggregationResultManagerTest {
    private AggregationResultsManager manager;

    @BeforeEach
    void setUp()  {
        manager = new AggregationResultsManager();
    }

    @Test
    void findsAggregationResultByFunctionAndName()  {
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
    void doesNotFindAggregationResultWithUnknownName()  {
        AggregateFunction func = AggregateFunction.COUNT;
        String name = "name";
        double hits = 0;

        manager.addAggregateResult(func, name, hits);

        String unknownName = "unknown";
        Optional<AggregationResult> retrievedResultOpt = manager.getAggregationResult(func, unknownName);

        assertThat(retrievedResultOpt.isPresent(), is(false));
    }

    @Test
    void aggregationResultGetStoredCorrectly()  {
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