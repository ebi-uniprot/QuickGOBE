package uk.ac.ebi.quickgo.repo.solr;

import uk.ac.ebi.quickgo.repo.solr.similarity.GoSimilarity;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Tests the behaviour of the {@link GoSimilarity} class.
 */
public class GoSimilarityTest {
    private GoSimilarity similarity;

    @Before
    public void setUp() throws Exception {
        similarity = new GoSimilarity();
    }

    @Test
    public void termsWithNoFrequencyGet0TermFrequencyScore() throws Exception {
        assertThat(similarity.tf(0), is(0F));
    }

    @Test
    public void termsWithFrequencyOf1Get1TermFrequencyScore() throws Exception {
        assertThat(similarity.tf(1), is(1F));
    }

    @Test
    public void termsWithMultipleFrequencyGet1TermFrequencyScore() throws Exception {
        assertThat(similarity.tf(3), is(1F));
        assertThat(similarity.tf(7), is(1F));
        assertThat(similarity.tf(11), is(1F));
    }

}
