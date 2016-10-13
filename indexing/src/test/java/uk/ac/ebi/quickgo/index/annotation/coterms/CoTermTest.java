package uk.ac.ebi.quickgo.index.annotation.coterms;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * @Author Tony Wardell
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

    CoTerm coTerm;

    @Before
    public void setup(){
        coTerm =
                new CoTerm.Builder().setTarget(TARGET_TERM).setComparedTerm(COMPARED_TERM).setCompared(COMPARED)
                        .setTogether(TOGETHER).createCoTerm();
    }

    @Test
	public void successfullyCalculateStatistics(){
		coTerm.calculateProbabilitySimilarityRatio(SELECTED);
		assertThat(coTerm.getSimilarityRatio(), equalTo(33.33f) );

		int all = 24;
		coTerm.calculateProbabilityRatio(SELECTED, all);
		assertThat(coTerm.getProbabilityRatio(), equalTo( 1.5f));
	}

	@Test(expected = IllegalArgumentException.class)
	public void calculateProbabilitySimilarityRatioSelectEqualToZeroThrowsException(){
		coTerm.calculateProbabilitySimilarityRatio(0L);
	}

	@Test(expected = IllegalArgumentException.class)
	public void calculateProbabilityRatioSelectEqualToZeroThrowsException(){
		int all = 24;
		coTerm.calculateProbabilityRatio(0l, all);
	}


	@Test(expected = IllegalArgumentException.class)
	public void calculateProbabilityRatioAllEqualToZeroThrowsException(){
		coTerm.calculateProbabilityRatio(SELECTED, 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ifSetTargetIsPassedNullAnExceptionIsThrown(){
		new CoTerm.Builder().setTarget(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ifSetTargetIsPassedEmptyStringAnExceptionIsThrown(){
		new CoTerm.Builder().setTarget("   ");
	}

	@Test(expected = IllegalArgumentException.class)
	public void ifSetComparedTermIsPassedNullAnExceptionIsThrown(){
		new CoTerm.Builder().setComparedTerm(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ifSetComparedTermIsPassedEmptyStringAnExceptionIsThrown(){
		new CoTerm.Builder().setComparedTerm("   ");
	}

	@Test(expected = IllegalArgumentException.class)
	public void ifSetComparedIsPassedZeroAnExceptionIsThrown(){
		new CoTerm.Builder().setCompared(0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ifSetTogetherTermIsPassedEmptyStringAnExceptionIsThrown(){
		new CoTerm.Builder().setTogether(0);
	}
}
