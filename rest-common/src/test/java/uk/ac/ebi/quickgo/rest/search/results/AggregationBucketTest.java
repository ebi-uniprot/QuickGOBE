package uk.ac.ebi.quickgo.rest.search.results;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests the behaviour of the {@link AggregationBucket} class.
 *
 * Note: tests dealing with {@link AggregationResult} are tested in {@link AggregationResultsManager}.
 */
public class AggregationBucketTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void nullBucketValueThrowsException() throws Exception {
        String name = null;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("AggregationBucket name cannot be null");

        new AggregationBucket(name);
    }

    @Test
    public void creatingBucketWithValidValueIsSuccessful() throws Exception {
        String name = "name";

        AggregationBucket bucket = new AggregationBucket(name);

        assertThat(bucket.getValue(), is(name));
    }
}