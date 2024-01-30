package uk.ac.ebi.quickgo.rest.search.results;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the behaviour of the {@link AggregationBucket} class.
 *
 * Note: tests dealing with {@link AggregationResult} are tested in {@link AggregationResultsManager}.
 */
class AggregationBucketTest {

    @Test
    void nullBucketValueThrowsException() {
        String name = null;
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new AggregationBucket(name));
        assertTrue(exception.getMessage().contains("AggregationBucket name cannot be null"));
    }

    @Test
    void creatingBucketWithValidValueIsSuccessful() {
        String name = "name";

        AggregationBucket bucket = new AggregationBucket(name);

        assertThat(bucket.getValue(), is(name));
    }
}