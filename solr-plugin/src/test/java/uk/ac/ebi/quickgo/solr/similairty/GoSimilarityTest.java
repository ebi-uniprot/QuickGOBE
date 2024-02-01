package uk.ac.ebi.quickgo.solr.similairty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.ebi.quickgo.solr.similarity.GoSimilarity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Tests the behaviour of the {@link GoSimilarity} class.
 */
class GoSimilarityTest {
    private GoSimilarity similarity;

    @BeforeEach
    void setUp() {
        similarity = new GoSimilarity();
    }

    @Test
    void termsWithNoFrequencyGet0TermFrequencyScore() {
        assertThat(similarity.tf(0), is(0F));
    }

    @Test
    void termsWithFrequencyOf1Get1TermFrequencyScore() {
        assertThat(similarity.tf(1), is(1F));
    }

    @Test
    void termsWithMultipleFrequencyGet1TermFrequencyScore() {
        assertThat(similarity.tf(3), is(1F));
        assertThat(similarity.tf(7), is(1F));
        assertThat(similarity.tf(11), is(1F));
    }
}