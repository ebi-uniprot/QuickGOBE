package uk.ac.ebi.quickgo.rest.search.results;

import uk.ac.ebi.quickgo.rest.search.AggregateFunction;

import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;

/**
 * Tests the behaviour of the {@link AggregateResponse} class.
 *
 * Note: tests dealing with {@link AggregationResult} are tested in {@link AggregationResultsManager}.
 */
public class AggregateResponseTest {
    private final String name = "agg";
    private static final int DISTINCT_VALUES_COUNT = 12;
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private AggregateResponse aggregation;
    private AggregationResultsManager aggregationResultsManager;
    private Set<AggregateResponse> nestedAggregations;
    private Set<AggregationBucket> buckets;

    @Before
    public void setUp() throws Exception {
        aggregationResultsManager = new AggregationResultsManager();
        nestedAggregations = new LinkedHashSet<>();
        buckets = new LinkedHashSet<>();
        aggregation =
                new AggregateResponse(name, aggregationResultsManager, nestedAggregations, buckets,
                        DISTINCT_VALUES_COUNT);
    }

    @Test
    public void nullNameInConstructorThrowsException() throws Exception {

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Name cannot be null or empty");

        new AggregateResponse(null, aggregationResultsManager, nestedAggregations, buckets, 0);
    }

    @Test
    public void emptyNameInConstructorThrowsException() throws Exception {
        String name = "";

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Name cannot be null or empty");

        new AggregateResponse(name, aggregationResultsManager, nestedAggregations, buckets, 0);

    }

    @Test
    public void nullAggregationResultsManagerInConstructorThrowsException() throws Exception {
        aggregationResultsManager = null;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Aggregation Results Manager cannot be null");

        new AggregateResponse(name, aggregationResultsManager, nestedAggregations, buckets, 0);
    }

    @Test
    public void nullNestedAggregationsInConstructorThrowsException() throws Exception {
        nestedAggregations = null;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Nested Aggregations cannot be null");

        new AggregateResponse(name, aggregationResultsManager, nestedAggregations, buckets, 0);
    }

    @Test
    public void nullBucketsInConstructorThrowsException() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Buckets cannot be null");

        new AggregateResponse(name, aggregationResultsManager, nestedAggregations, null, 0);
    }

    @Test
    public void negativeDistinctValueCountsInConstructorThrowsException() throws Exception {

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("DistinctValueCount must be zero or greater");

        new AggregateResponse(name, aggregationResultsManager, nestedAggregations, buckets, -1);
    }


    @Test
    public void aggregationWithNoBucketsReturnsFalseWhenQueriedAboutThePresenceOfBuckets() throws Exception {
        assertThat(aggregation.hasBuckets(), is(false));
    }

    @Test
    public void aggregationWithOneBucketReturnsTrueWhenQueriedAboutThePresenceOfBuckets() throws Exception {
        AggregationBucket bucket = new AggregationBucket("value");
        buckets.add(bucket);

        assertThat(aggregation.hasBuckets(), is(true));
    }

    @Test
    public void nestedAggregateGetsAddedToStoredAggregates() throws Exception {
        AggregateResponse nestedAggregation =
                new AggregateResponse("nestedAggregation", new AggregationResultsManager(),
                        new LinkedHashSet(), new LinkedHashSet(), 5);
        nestedAggregations.add(nestedAggregation);

        Set<AggregateResponse> retrievedAggregates = aggregation.getNestedAggregations();

        assertThat(retrievedAggregates, hasSize(1));
        assertThat(retrievedAggregates, contains(nestedAggregation));
    }

    @Test
    public void aggregationWithNoNestedAggregationsReturnsFalseWhenQueriedAboutThePresenceOfNestedAggregations()
            throws Exception {
        assertThat(aggregation.hasNestedAggregations(), is(false));
    }

    @Test
    public void aggregationWithANestedAggregationsReturnsTrueWhenQueriedAboutThePresenceOfNestedAggregations()
            throws Exception {
        AggregateResponse nestedAggregation =
                new AggregateResponse("nestedAggregation", new AggregationResultsManager(),
                        new LinkedHashSet(), new LinkedHashSet(), 5);
        nestedAggregations.add(nestedAggregation);

        assertThat(aggregation.hasNestedAggregations(), is(true));
    }

    @Test
    public void aggregationWithNoAggregationsResultsReturnsFalseWhenQueriedAboutThePresenceOfAggregationResults()
            throws Exception {
        assertThat(aggregation.hasAggregationResults(), is(false));
    }

    @Test
    public void aggregationWithAnAggregationsResultsReturnsTrueWhenQueriedAboutThePresenceOfAggregationResults()
            throws Exception {
        aggregationResultsManager.addAggregateResult(AggregateFunction.COUNT, "field", 0);

        assertThat(aggregation.hasAggregationResults(), is(true));
    }

    @Test
    public void aggregationWithNoResultsOrNestedResultsOrBucketsReturnsFalseWhenQueriedIfItsPopulated() throws
                                                                                                        Exception {
        assertThat(aggregation.isPopulated(), is(false));
    }

    @Test
    public void aggregationWithAResultAndNoNestedResultsAndNoBucketsReturnsTrueWhenQueriedIfItsPopulated() throws
                                                                                                           Exception {
        aggregationResultsManager.addAggregateResult(AggregateFunction.COUNT, "field", 0);

        assertThat(aggregation.isPopulated(), is(true));
    }

    @Test
    public void aggregationWithANestedResultAndNoResultAndNoBucketsReturnsTrueWhenQueriedIfItsPopulated()
            throws Exception {
        AggregateResponse nestedAggregation =
                new AggregateResponse("nestedAggregation", new AggregationResultsManager(),
                        new LinkedHashSet(), new LinkedHashSet(), 5);
        nestedAggregations.add(nestedAggregation);

        assertThat(aggregation.isPopulated(), is(true));
    }

    @Test
    public void aggregationWithABucketAndNoResultAndNoNestedResultsReturnsTrueWhenQueriedIfItsPopulated()
            throws Exception {
        AggregationBucket bucket = new AggregationBucket("value");
        this.buckets.add(bucket);

        assertThat(aggregation.isPopulated(), is(true));
    }

    @Test
    public void aggregationSetDistinctValueCount() {
        assertThat(aggregation.getDistinctValuesCount(), is(DISTINCT_VALUES_COUNT));
    }
}
