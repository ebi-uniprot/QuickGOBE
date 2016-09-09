package uk.ac.ebi.quickgo.common.costats;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * @Author Tony Wardell
 * Date: 16/11/2015
 * Time: 13:52
 * Created with IntelliJ IDEA.
 */
public class CoOccurringTermTest {

	@Test
	public void successfullyCalculateCoOccurrenceStats(){
		String targetTerm = "GO:00000200";
		String comparedTerm = "GO:0003824";
		long compared = 8L;
		long together = 4L;

		//From the coStats class.
		long selected = 8L;

		CoOccurringTerm coOccurringTerm = new CoOccurringTerm(targetTerm, comparedTerm, compared, together);
		assertEquals( 33.33f , coOccurringTerm.calculateProbabilitySimilarityRatio(selected));

		int all = 24;
		assertEquals( 1.5f , coOccurringTerm.calculateProbabilityRatio(selected, all));
	}

	@Test(expected = IllegalArgumentException.class)
	public void calculateProbabilitySimilarityRatioSelectEqualToZeroThrowsException(){
		String targetTerm = "GO:00000200";
		String comparedTerm = "GO:0003824";
		long compared = 8L;
		long together = 4L;

		CoOccurringTerm coOccurringTerm = new CoOccurringTerm(targetTerm, comparedTerm, compared, together);
		coOccurringTerm.calculateProbabilitySimilarityRatio(0L);
	}

	@Test(expected = IllegalArgumentException.class)
	public void calculateProbabilityRatioSelectEqualToZeroThrowsException(){
		String targetTerm = "GO:00000200";
		String comparedTerm = "GO:0003824";
		long compared = 8L;
		long together = 4L;

		int all = 24;

		CoOccurringTerm coOccurringTerm = new CoOccurringTerm(targetTerm, comparedTerm, compared, together);
		coOccurringTerm.calculateProbabilityRatio(0l, all);
	}


	@Test(expected = IllegalArgumentException.class)
	public void calculateProbabilityRatioAllEqualToZeroThrowsException(){
		String targetTerm = "GO:00000200";
		String comparedTerm = "GO:0003824";
		long compared = 8L;
		long together = 4L;
		long selected = 8L;

		CoOccurringTerm coOccurringTerm = new CoOccurringTerm(targetTerm, comparedTerm, compared, together);
		coOccurringTerm.calculateProbabilityRatio(selected, 0);
	}
}
