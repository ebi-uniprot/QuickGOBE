package uk.ac.ebi.quickgo.index.annotation.coterms;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * @Author Tony Wardell
 * Date: 16/11/2015
 * Time: 13:52
 * Created with IntelliJ IDEA.
 */
public class Co_occurringTermTest {

	@Test
	public void successfullyCalculateCoOccurrenceStats(){
		String targetTerm = "GO:00000200";
		String comparedTerm = "GO:0003824";
		long compared = 8L;
		long together = 4L;

		//From the coStats class.
		long selected = 8L;

		Co_occurringTerm coOccurringTerm =
				new Co_occurringTerm.Co_occurringTermBuilder().setTarget(targetTerm).setComparedTerm(comparedTerm).setCompared(compared)
						.setTogether(together).createCo_occurringTerm();
		coOccurringTerm.calculateProbabilitySimilarityRatio(selected);
		assertEquals( 33.33f , coOccurringTerm.getSimilarityRatio() );

		int all = 24;
		coOccurringTerm.calculateProbabilityRatio(selected, all);
		assertEquals( 1.5f ,coOccurringTerm.getProbabilityRatio() );
	}

	@Test(expected = IllegalArgumentException.class)
	public void calculateProbabilitySimilarityRatioSelectEqualToZeroThrowsException(){
		String targetTerm = "GO:00000200";
		String comparedTerm = "GO:0003824";
		long compared = 8L;
		long together = 4L;

		Co_occurringTerm coOccurringTerm =
				new Co_occurringTerm.Co_occurringTermBuilder().setTarget(targetTerm).setComparedTerm(comparedTerm).setCompared(compared)
						.setTogether(together).createCo_occurringTerm();
		coOccurringTerm.calculateProbabilitySimilarityRatio(0L);
	}

	@Test(expected = IllegalArgumentException.class)
	public void calculateProbabilityRatioSelectEqualToZeroThrowsException(){
		String targetTerm = "GO:00000200";
		String comparedTerm = "GO:0003824";
		long compared = 8L;
		long together = 4L;

		int all = 24;

		Co_occurringTerm coOccurringTerm =
				new Co_occurringTerm.Co_occurringTermBuilder().setTarget(targetTerm).setComparedTerm(comparedTerm).setCompared(compared)
						.setTogether(together).createCo_occurringTerm();
		coOccurringTerm.calculateProbabilityRatio(0l, all);
	}


	@Test(expected = IllegalArgumentException.class)
	public void calculateProbabilityRatioAllEqualToZeroThrowsException(){
		String targetTerm = "GO:00000200";
		String comparedTerm = "GO:0003824";
		long compared = 8L;
		long together = 4L;
		long selected = 8L;

		Co_occurringTerm coOccurringTerm =
				new Co_occurringTerm.Co_occurringTermBuilder().setTarget(targetTerm).setComparedTerm(comparedTerm).setCompared(compared)
						.setTogether(together).createCo_occurringTerm();
		coOccurringTerm.calculateProbabilityRatio(selected, 0);
	}
}
