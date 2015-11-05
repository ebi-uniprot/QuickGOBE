package uk.ac.ebi.quickgo.solr.model.statistics;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * @Author Tony Wardell
 * Date: 04/11/2015
 * Time: 11:39
 * Created with IntelliJ IDEA.
 */
public class StatisticTupleTest {

	@Test
	public void testPercentageCalculation(){

		String type = "Test";
		String key = "GO:12345";
		long hits = 10;

		//assertEquals(10.0f, (10f/100)*100);

		StatisticTuple statisticTuple = new StatisticTuple(type, key, hits);
		statisticTuple.calculateStatisticTuplePercentage(100);
		assertEquals(10.0f, statisticTuple.getStatisticTuplePercentage());

	}


}
