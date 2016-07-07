package uk.ac.ebi.quickgo.rest.search.results;

import java.util.Set;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;

/**
 * Tests the behaviour of the {@link Aggregation} class.
 *
 * Note: tests dealing with {@link AggregationResult} are tested in {@link AggregationResultsManager}.
 */
public class AggregationTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Aggregation aggregation;

    @Before
    public void setUp() throws Exception {
        aggregation = new Aggregation("agg");
    }

    @Test
    public void nullNameInConstructorThrowsException() throws Exception {
        String name = null;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Name cannot be null or empty");

        new Aggregation(name);
    }

    @Test
    public void emptyNameInConstructorThrowsException() throws Exception {
        String name = "";

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Name cannot be null or empty");

        new Aggregation(name);
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
    public void addingNullNestedAggregateThrowsException() throws Exception {
        Aggregation nestedAggregation = null;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Nested aggregation cannot be null");

        aggregation.addAggregation(nestedAggregation);
    }

    @Test
    public void nestedAggregateGetsAddedToStoredAggregates() throws Exception {
        Aggregation nestedAggregation = new Aggregation("nestedAggregation");

        aggregation.addAggregation(nestedAggregation);

        Set<Aggregation> retrievedAggregates = aggregation.getNestedAggregations();

        assertThat(retrievedAggregates, hasSize(1));
        assertThat(retrievedAggregates, contains(nestedAggregation));
    }
}