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
 * @author Tony Wardell
 * Date: 24/11/2017
 * Time: 15:27
 * Created with IntelliJ IDEA.
 */
class AggregateResponseBuilderTest {

    private AggregateResponseBuilder aggregateResponseBuilder;
    private AggregateResponse nestedAggregation;

    @BeforeEach
    void setUp() {
        aggregateResponseBuilder = new AggregateResponseBuilder("agg");
        nestedAggregation = new AggregateResponse("nestedAggregation", new AggregationResultsManager(),
                new LinkedHashSet<>(), new LinkedHashSet<>(), 5);
    }

    @Test
    void nullNameInConstructorThrowsException() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new AggregateResponseBuilder(null));
        assertTrue(exception.getMessage().contains("Name cannot be null or empty"));
    }

    @Test
    void emptyNameInConstructorThrowsException() {
        String name = "";
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new AggregateResponseBuilder(name));
        assertTrue(exception.getMessage().contains("Name cannot be null or empty"));
    }

    @Test
    void addingNullBucketThrowsException() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> aggregateResponseBuilder.addBucket(null));
        assertTrue(exception.getMessage().contains("AggregationBucket cannot be null"));
    }

    @Test
    void bucketGetsAddedToStoredBuckets() {
        AggregationBucket bucket = new AggregationBucket("value");

        aggregateResponseBuilder.addBucket(bucket);

        Set<AggregationBucket> retrievedBuckets = aggregateResponseBuilder.createAggregateResponse().getBuckets();

        assertThat(retrievedBuckets, hasSize(1));
        assertThat(retrievedBuckets, contains(bucket));
    }

    @Test
    void addingNullNestedAggregateThrowsException() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> aggregateResponseBuilder.addNestedAggregation(null));
        assertTrue(exception.getMessage().contains("Nested aggregation cannot be null"));
    }

    @Test
    void nestedAggregateGetsAddedToStoredAggregates() {
        aggregateResponseBuilder.addNestedAggregation(nestedAggregation);

        Set<AggregateResponse> retrievedAggregates =
                aggregateResponseBuilder.createAggregateResponse().getNestedAggregations();

        assertThat(retrievedAggregates, hasSize(1));
        assertThat(retrievedAggregates, contains(nestedAggregation));
    }

    @Test
    void aggregationWithNoNestedAggregationsReturnsFalseWhenQueriedAboutThePresenceOfNestedAggregations() {
        assertThat(aggregateResponseBuilder.createAggregateResponse().hasNestedAggregations(), is(false));
    }

    @Test
    void aggregationWithANestedAggregationsReturnsTrueWhenQueriedAboutThePresenceOfNestedAggregations() {
        aggregateResponseBuilder.addNestedAggregation(nestedAggregation);

        assertThat(aggregateResponseBuilder.createAggregateResponse().hasNestedAggregations(), is(true));
    }

    @Test
    void aggregationWithNoAggregationsResultsReturnsFalseWhenQueriedAboutThePresenceOfAggregationResults() {
        assertThat(aggregateResponseBuilder.createAggregateResponse().hasAggregationResults(), is(false));
    }

    @Test
    void aggregationWithAnAggregationsResultsReturnsTrueWhenQueriedAboutThePresenceOfAggregationResults()
            {
        aggregateResponseBuilder.addAggregationResult(AggregateFunction.COUNT, "field", 0);

        assertThat(aggregateResponseBuilder.createAggregateResponse().hasAggregationResults(), is(true));
    }

    @Test
    void aggregationWithNoResultsOrNestedingsOrBucketsReturnsFalseWhenQueriedIfItsPopulated() {
        assertThat(aggregateResponseBuilder.createAggregateResponse().isPopulated(), is(false));
    }

    @Test
    void aggregationWithAResultAndNoNestingsAndNoBucketsReturnsTrueWhenQueriedIfItsPopulated() {
        aggregateResponseBuilder.addAggregationResult(AggregateFunction.COUNT, "field", 0);

        assertThat(aggregateResponseBuilder.createAggregateResponse().isPopulated(), is(true));
    }

    @Test
    void aggregationWithANestingAndNoResultAndNoBucketsReturnsTrueWhenQueriedIfItsPopulated()
            {
        aggregateResponseBuilder.addNestedAggregation(nestedAggregation);

        assertThat(aggregateResponseBuilder.createAggregateResponse().isPopulated(), is(true));
    }

    @Test
    void aggregationWithABucketAndNoResultAndNoNestingsReturnsTrueWhenQueriedIfItsPopulated()
            {
        AggregationBucket bucket = new AggregationBucket("value");
        aggregateResponseBuilder.addBucket(bucket);

        assertThat(aggregateResponseBuilder.createAggregateResponse().isPopulated(), is(true));
    }

    @Test
    void aggregationSetDistinctValueCount() {
        aggregateResponseBuilder.setDistinctValuesCount(12);
        assertThat(aggregateResponseBuilder.createAggregateResponse().getDistinctValuesCount(), is(12));
    }
}
