package uk.ac.ebi.quickgo.rest.search.results;

import uk.ac.ebi.quickgo.rest.search.AggregateFunction;

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
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private AggregateResponse aggregation;

    @Before
    public void setUp() throws Exception {
        aggregation = new AggregateResponse("agg");
    }

    @Test
    public void nullNameInConstructorThrowsException() throws Exception {
        String name = null;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Name cannot be null or empty");

        new AggregateResponse(name);
    }

    @Test
    public void emptyNameInConstructorThrowsException() throws Exception {
        String name = "";

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Name cannot be null or empty");

        new AggregateResponse(name);
    }

    @Test
    public void addingNullBucketThrowsException() throws Exception {
        AggregationBucket bucket = null;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("AggregationBucket cannot be null");

        aggregation.addBucket(bucket);
    }

    @Test
    public void bucketGetsAddedToStoredBuckets() throws Exception {
        AggregationBucket bucket = new AggregationBucket("value");

        aggregation.addBucket(bucket);

        Set<AggregationBucket> retrievedBuckets = aggregation.getBuckets();

        assertThat(retrievedBuckets, hasSize(1));
        assertThat(retrievedBuckets, contains(bucket));
    }

    @Test
    public void aggregationWithNoBucketsReturnsFalseWhenQueriedAboutThePresenceOfBuckets() throws Exception {
        assertThat(aggregation.hasBuckets(), is(false));
    }

    @Test
    public void aggregationWithOneBucketReturnsTrueWhenQueriedAboutThePresenceOfBuckets() throws Exception {
        AggregationBucket bucket = new AggregationBucket("value");
        aggregation.addBucket(bucket);

        assertThat(aggregation.hasBuckets(), is(true));
    }

    @Test
    public void addingNullNestedAggregateThrowsException() throws Exception {
        AggregateResponse nestedAggregation = null;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Nested aggregation cannot be null");

        aggregation.addNestedAggregation(nestedAggregation);
    }

    @Test
    public void nestedAggregateGetsAddedToStoredAggregates() throws Exception {
        AggregateResponse nestedAggregation = new AggregateResponse("nestedAggregation");

        aggregation.addNestedAggregation(nestedAggregation);

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
        AggregateResponse nestedAggregation = new AggregateResponse("nestedAggregation");
        aggregation.addNestedAggregation(nestedAggregation);

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
        aggregation.addAggregationResult(AggregateFunction.COUNT, "field", 0);

        assertThat(aggregation.hasAggregationResults(), is(true));
    }

    @Test
    public void aggregationWithNoResultsOrNestedingsOrBucketsReturnsFalseWhenQueriedIfItsPopulated() throws Exception {
        assertThat(aggregation.isPopulated(), is(false));
    }

    @Test
    public void aggregationWithAResultAndNoNestingsAndNoBucketsReturnsTrueWhenQueriedIfItsPopulated() throws Exception {
        aggregation.addAggregationResult(AggregateFunction.COUNT, "field", 0);

        assertThat(aggregation.isPopulated(), is(true));
    }

    @Test
    public void aggregationWithANestingAndNoResultAndNoBucketsReturnsTrueWhenQueriedIfItsPopulated()
            throws Exception {
        AggregateResponse nestedAggregation = new AggregateResponse("nestedAggregation");
        aggregation.addNestedAggregation(nestedAggregation);

        assertThat(aggregation.isPopulated(), is(true));
    }

    @Test
    public void aggregationWithABucketAndNoResultAndNoNestingsReturnsTrueWhenQueriedIfItsPopulated()
            throws Exception {
        AggregationBucket bucket = new AggregationBucket("value");
        aggregation.addBucket(bucket);

        assertThat(aggregation.isPopulated(), is(true));
    }

    @Test
    public void aggregationSetDistinctValueCount(){
        aggregation.setDistinctValuesCount(12);
        assertThat(aggregation.getDistinctValuesCount(), is(12));
    }
}
