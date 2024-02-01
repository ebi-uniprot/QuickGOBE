package uk.ac.ebi.quickgo.rest.search.results;

import uk.ac.ebi.quickgo.rest.search.AggregateFunction;

import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the behaviour of the {@link AggregateResponse} class.
 *
 * Note: tests dealing with {@link AggregationResult} are tested in {@link AggregationResultsManager}.
 */
class AggregateResponseTest {
    private final String name = "agg";
    private static final int DISTINCT_VALUES_COUNT = 12;
    private AggregateResponse aggregation;
    private AggregationResultsManager aggregationResultsManager;
    private Set<AggregateResponse> nestedAggregations;
    private Set<AggregationBucket> buckets;

    @BeforeEach
    void setUp() {
        aggregationResultsManager = new AggregationResultsManager();
        nestedAggregations = new LinkedHashSet<>();
        buckets = new LinkedHashSet<>();
        aggregation =
                new AggregateResponse(name, aggregationResultsManager, nestedAggregations, buckets,
                        DISTINCT_VALUES_COUNT);
    }

    @Test
    void nullNameInConstructorThrowsException() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new AggregateResponse(null, aggregationResultsManager, nestedAggregations, buckets, 0));
        assertTrue(exception.getMessage().contains("Name cannot be null or empty"));
    }

    @Test
    void emptyNameInConstructorThrowsException() {
        String name = "";
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new AggregateResponse(name, aggregationResultsManager, nestedAggregations, buckets, 0));
        assertTrue(exception.getMessage().contains("Name cannot be null or empty"));
    }

    @Test
    void nullAggregationResultsManagerInConstructorThrowsException() {
        aggregationResultsManager = null;
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new AggregateResponse(name, aggregationResultsManager, nestedAggregations, buckets, 0));
        assertTrue(exception.getMessage().contains("Aggregation Results Manager cannot be null"));
    }

    @Test
    void nullNestedAggregationsInConstructorThrowsException() {
        nestedAggregations = null;
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new AggregateResponse(name, aggregationResultsManager, nestedAggregations, buckets, 0));
        assertTrue(exception.getMessage().contains("Nested Aggregations cannot be null"));
    }

    @Test
    void nullBucketsInConstructorThrowsException() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new AggregateResponse(name, aggregationResultsManager, nestedAggregations, null, 0));
        assertTrue(exception.getMessage().contains("Buckets cannot be null"));
    }

    @Test
    void negativeDistinctValueCountsInConstructorThrowsException() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new AggregateResponse(name, aggregationResultsManager, nestedAggregations, buckets, -1));
        assertTrue(exception.getMessage().contains("DistinctValueCount must be zero or greater"));
    }


    @Test
    void aggregationWithNoBucketsReturnsFalseWhenQueriedAboutThePresenceOfBuckets() {
        assertThat(aggregation.hasBuckets(), is(false));
    }

    @Test
    void aggregationWithOneBucketReturnsTrueWhenQueriedAboutThePresenceOfBuckets() {
        AggregationBucket bucket = new AggregationBucket("value");
        buckets.add(bucket);

        assertThat(aggregation.hasBuckets(), is(true));
    }

    @Test
    void nestedAggregateGetsAddedToStoredAggregates() {
        AggregateResponse nestedAggregation =
                new AggregateResponse("nestedAggregation", new AggregationResultsManager(),
                        new LinkedHashSet(), new LinkedHashSet(), 5);
        nestedAggregations.add(nestedAggregation);

        Set<AggregateResponse> retrievedAggregates = aggregation.getNestedAggregations();

        assertThat(retrievedAggregates, hasSize(1));
        assertThat(retrievedAggregates, contains(nestedAggregation));
    }

    @Test
    void aggregationWithNoNestedAggregationsReturnsFalseWhenQueriedAboutThePresenceOfNestedAggregations()
            {
        assertThat(aggregation.hasNestedAggregations(), is(false));
    }

    @Test
    void aggregationWithANestedAggregationsReturnsTrueWhenQueriedAboutThePresenceOfNestedAggregations()
            {
        AggregateResponse nestedAggregation =
                new AggregateResponse("nestedAggregation", new AggregationResultsManager(),
                        new LinkedHashSet(), new LinkedHashSet(), 5);
        nestedAggregations.add(nestedAggregation);

        assertThat(aggregation.hasNestedAggregations(), is(true));
    }

    @Test
    void aggregationWithNoAggregationsResultsReturnsFalseWhenQueriedAboutThePresenceOfAggregationResults()
            {
        assertThat(aggregation.hasAggregationResults(), is(false));
    }

    @Test
    void aggregationWithAnAggregationsResultsReturnsTrueWhenQueriedAboutThePresenceOfAggregationResults()
            {
        aggregationResultsManager.addAggregateResult(AggregateFunction.COUNT, "field", 0);

        assertThat(aggregation.hasAggregationResults(), is(true));
    }

    @Test
    void aggregationWithNoResultsOrNestedResultsOrBucketsReturnsFalseWhenQueriedIfItsPopulated() {
        assertThat(aggregation.isPopulated(), is(false));
    }

    @Test
    void aggregationWithAResultAndNoNestedResultsAndNoBucketsReturnsTrueWhenQueriedIfItsPopulated() {
        aggregationResultsManager.addAggregateResult(AggregateFunction.COUNT, "field", 0);

        assertThat(aggregation.isPopulated(), is(true));
    }

    @Test
    void aggregationWithANestedResultAndNoResultAndNoBucketsReturnsTrueWhenQueriedIfItsPopulated()
            {
        AggregateResponse nestedAggregation =
                new AggregateResponse("nestedAggregation", new AggregationResultsManager(),
                        new LinkedHashSet(), new LinkedHashSet(), 5);
        nestedAggregations.add(nestedAggregation);

        assertThat(aggregation.isPopulated(), is(true));
    }

    @Test
    void aggregationWithABucketAndNoResultAndNoNestedResultsReturnsTrueWhenQueriedIfItsPopulated()
            {
        AggregationBucket bucket = new AggregationBucket("value");
        this.buckets.add(bucket);

        assertThat(aggregation.isPopulated(), is(true));
    }

    @Test
    void aggregationSetDistinctValueCount() {
        assertThat(aggregation.getDistinctValuesCount(), is(DISTINCT_VALUES_COUNT));
    }
}
