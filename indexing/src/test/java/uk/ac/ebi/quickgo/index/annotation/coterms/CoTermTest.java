package uk.ac.ebi.quickgo.index.annotation.coterms;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * @author Tony Wardell
 * Date: 16/11/2015
 * Time: 13:52
 * Created with IntelliJ IDEA.
 */
public class CoTermTest {

    private static final String TARGET_TERM = "GO:00000200";
    private static final String COMPARED_TERM = "GO:0003824";
    private static final long COMPARED = 8L;
    private static final long TOGETHER = 4L;
    private static final long SELECTED = 8L;
    private static final long ALL = 24L;
    private static final float PROBABILITY_RATIO = 1.5F;
    private static final float SIMILARITY_RATIO = 33.333336F;

    private CoTerm coTerm;

    @Before
    public void setup() {
        coTerm =
                new CoTerm.Builder().setTarget(TARGET_TERM).setComparedTerm(COMPARED_TERM).setCompared(COMPARED)
                        .setTogether(TOGETHER).setProbabilityRatio(PROBABILITY_RATIO)
                        .setSimilarityRatio(SIMILARITY_RATIO).build();
    }

    @Test
    public void successfullyCalculateSimilarityRatio() {
        assertThat(CoTerm.calculateSimilarityRatio(SELECTED, TOGETHER, COMPARED), equalTo(SIMILARITY_RATIO));
    }

    @Test
    public void successfullyProbabilityRatio() {
        assertThat(CoTerm.calculateProbabilityRatio(SELECTED, TOGETHER, ALL, COMPARED), equalTo(PROBABILITY_RATIO));
    }

    @Test(expected = IllegalArgumentException.class)
    public void calculateSimilarityRatioSelectEqualToZeroThrowsException() {
        CoTerm.calculateSimilarityRatio(0, TOGETHER, COMPARED);
    }

    @Test(expected = IllegalArgumentException.class)
    public void calculateProbabilityRatioSelectEqualToZeroThrowsException() {
        CoTerm.calculateProbabilityRatio(0L, TOGETHER, ALL, COMPARED);
    }

    @Test(expected = IllegalArgumentException.class)
    public void calculateProbabilityRatioAllEqualToZeroThrowsException() {
        CoTerm.calculateProbabilityRatio(SELECTED, TOGETHER, 0L, COMPARED);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ifSetTargetIsPassedNullAnExceptionIsThrown() {
        new CoTerm.Builder().setTarget(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ifSetTargetIsPassedEmptyStringAnExceptionIsThrown() {
        new CoTerm.Builder().setTarget("   ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void ifSetComparedTermIsPassedNullAnExceptionIsThrown() {
        new CoTerm.Builder().setComparedTerm(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ifSetComparedTermIsPassedEmptyStringAnExceptionIsThrown() {
        new CoTerm.Builder().setComparedTerm("   ");
    }

    @Test
    public void builtCoTermSuccessfully() {
        assertThat(coTerm.getTarget(), is(TARGET_TERM));
        assertThat(coTerm.getComparedTerm(), is(COMPARED_TERM));
        assertThat(coTerm.getCompared(), is(COMPARED));
        assertThat(coTerm.getTogether(), is(TOGETHER));
        assertThat(coTerm.getProbabilityRatio(), is(PROBABILITY_RATIO));
        assertThat(coTerm.getSimilarityRatio(), is(SIMILARITY_RATIO));
    }

    @Test
    public void similarityRatioNotCalculatedAsZero() {
        long selected = 1;
        long together = 1;
        long compared = 3201;
        assertThat(CoTerm.calculateSimilarityRatio(selected, together, compared), equalTo(0.031240238F));
    }
}
