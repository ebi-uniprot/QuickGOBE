package uk.ac.ebi.quickgo.index.annotation.coterms;

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

	@Test
	public void successfullyCalculateStatistics(){
		String targetTerm = "GO:00000200";
		String comparedTerm = "GO:0003824";
		long compared = 8L;
		long together = 4L;

		long selected = 8L;

		CoTerm coTerm =
				new CoTerm.Builder().setTarget(targetTerm).setComparedTerm(comparedTerm).setCompared(compared)
						.setTogether(together).createCoTerm();
		coTerm.calculateProbabilitySimilarityRatio(selected);
		assertThat(coTerm.getSimilarityRatio(), equalTo(33.33f) );

		int all = 24;
		coTerm.calculateProbabilityRatio(selected, all);
		assertThat(coTerm.getProbabilityRatio(), equalTo( 1.5f));
	}

	@Test(expected = IllegalArgumentException.class)
	public void calculateProbabilitySimilarityRatioSelectEqualToZeroThrowsException(){
		String targetTerm = "GO:00000200";
		String comparedTerm = "GO:0003824";
		long compared = 8L;
		long together = 4L;

		CoTerm coTerm =
				new CoTerm.Builder().setTarget(targetTerm).setComparedTerm(comparedTerm).setCompared(compared)
						.setTogether(together).createCoTerm();
		coTerm.calculateProbabilitySimilarityRatio(0L);
	}

	@Test(expected = IllegalArgumentException.class)
	public void calculateProbabilityRatioSelectEqualToZeroThrowsException(){
		String targetTerm = "GO:00000200";
		String comparedTerm = "GO:0003824";
		long compared = 8L;
		long together = 4L;

		int all = 24;

		CoTerm coTerm =
				new CoTerm.Builder().setTarget(targetTerm).setComparedTerm(comparedTerm).setCompared(compared)
						.setTogether(together).createCoTerm();
		coTerm.calculateProbabilityRatio(0l, all);
	}


	@Test(expected = IllegalArgumentException.class)
	public void calculateProbabilityRatioAllEqualToZeroThrowsException(){
		String targetTerm = "GO:00000200";
		String comparedTerm = "GO:0003824";
		long compared = 8L;
		long together = 4L;
		long selected = 8L;

		CoTerm coTerm =
				new CoTerm.Builder().setTarget(targetTerm).setComparedTerm(comparedTerm).setCompared(compared)
						.setTogether(together).createCoTerm();
		coTerm.calculateProbabilityRatio(selected, 0);
	}
}
