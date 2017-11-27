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
 * @author Tony Wardell
 * Date: 24/11/2017
 * Time: 15:27
 * Created with IntelliJ IDEA.
 */
public class AggregateResponseBuilderTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private AggregateResponseBuilder aggregateResponseBuilder;
    private AggregateResponse nestedAggregation;

    @Before
    public void setUp() throws Exception {
        aggregateResponseBuilder = new AggregateResponseBuilder("agg");
        nestedAggregation = new AggregateResponse("nestedAggregation", new AggregationResultsManager(),
                new LinkedHashSet<>(), new LinkedHashSet<>(), 5);
    }

    @Test
    public void nullNameInConstructorThrowsException() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Name cannot be null or empty");

        new AggregateResponseBuilder(null);
    }

    @Test
    public void emptyNameInConstructorThrowsException() throws Exception {
        String name = "";

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Name cannot be null or empty");

        new AggregateResponseBuilder(name);
    }

    @Test
    public void addingNullBucketThrowsException() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("AggregationBucket cannot be null");

        aggregateResponseBuilder.addBucket(null);
    }

    @Test
    public void bucketGetsAddedToStoredBuckets() throws Exception {
        AggregationBucket bucket = new AggregationBucket("value");

        aggregateResponseBuilder.addBucket(bucket);

        Set<AggregationBucket> retrievedBuckets = aggregateResponseBuilder.createAggregateResponse().getBuckets();

        assertThat(retrievedBuckets, hasSize(1));
        assertThat(retrievedBuckets, contains(bucket));
    }

    @Test
    public void addingNullNestedAggregateThrowsException() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Nested aggregation cannot be null");

        aggregateResponseBuilder.addNestedAggregation(null);
    }

    @Test
    public void nestedAggregateGetsAddedToStoredAggregates() throws Exception {
        aggregateResponseBuilder.addNestedAggregation(nestedAggregation);

        Set<AggregateResponse> retrievedAggregates =
                aggregateResponseBuilder.createAggregateResponse().getNestedAggregations();

        assertThat(retrievedAggregates, hasSize(1));
        assertThat(retrievedAggregates, contains(nestedAggregation));
    }

    @Test
    public void aggregationWithNoNestedAggregationsReturnsFalseWhenQueriedAboutThePresenceOfNestedAggregations()
            throws Exception {
        assertThat(aggregateResponseBuilder.createAggregateResponse().hasNestedAggregations(), is(false));
    }

    @Test
    public void aggregationWithANestedAggregationsReturnsTrueWhenQueriedAboutThePresenceOfNestedAggregations()
            throws Exception {
        aggregateResponseBuilder.addNestedAggregation(nestedAggregation);

        assertThat(aggregateResponseBuilder.createAggregateResponse().hasNestedAggregations(), is(true));
    }

    @Test
    public void aggregationWithNoAggregationsResultsReturnsFalseWhenQueriedAboutThePresenceOfAggregationResults()
            throws Exception {
        assertThat(aggregateResponseBuilder.createAggregateResponse().hasAggregationResults(), is(false));
    }

    @Test
    public void aggregationWithAnAggregationsResultsReturnsTrueWhenQueriedAboutThePresenceOfAggregationResults()
            throws Exception {
        aggregateResponseBuilder.addAggregationResult(AggregateFunction.COUNT, "field", 0);

        assertThat(aggregateResponseBuilder.createAggregateResponse().hasAggregationResults(), is(true));
    }

    @Test
    public void aggregationWithNoResultsOrNestedingsOrBucketsReturnsFalseWhenQueriedIfItsPopulated() throws Exception {
        assertThat(aggregateResponseBuilder.createAggregateResponse().isPopulated(), is(false));
    }

    @Test
    public void aggregationWithAResultAndNoNestingsAndNoBucketsReturnsTrueWhenQueriedIfItsPopulated() throws Exception {
        aggregateResponseBuilder.addAggregationResult(AggregateFunction.COUNT, "field", 0);

        assertThat(aggregateResponseBuilder.createAggregateResponse().isPopulated(), is(true));
    }

    @Test
    public void aggregationWithANestingAndNoResultAndNoBucketsReturnsTrueWhenQueriedIfItsPopulated()
            throws Exception {
        aggregateResponseBuilder.addNestedAggregation(nestedAggregation);

        assertThat(aggregateResponseBuilder.createAggregateResponse().isPopulated(), is(true));
    }

    @Test
    public void aggregationWithABucketAndNoResultAndNoNestingsReturnsTrueWhenQueriedIfItsPopulated()
            throws Exception {
        AggregationBucket bucket = new AggregationBucket("value");
        aggregateResponseBuilder.addBucket(bucket);

        assertThat(aggregateResponseBuilder.createAggregateResponse().isPopulated(), is(true));
    }

    @Test
    public void aggregationSetDistinctValueCount() {
        aggregateResponseBuilder.setDistinctValuesCount(12);
        assertThat(aggregateResponseBuilder.createAggregateResponse().getDistinctValuesCount(), is(12));
    }
}
