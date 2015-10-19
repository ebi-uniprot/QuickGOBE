package uk.ac.ebi.quickgo.service.statistic;

import java.text.DecimalFormat;

/**
 * @Author Tony Wardell
 * Date: 14/10/2015
 * Time: 13:32
 * Created with IntelliJ IDEA.
 */
public class StatisticsMath {

	/**
	 * Calculates percentage
	 * @param count Number of annotations
	 * @return Percentage based on the total number of annotations
	 */
	public static float calculatePercentage(long count, long total) {
		DecimalFormat twoDForm = new DecimalFormat("#.##");
		float percentage = ((float) count / (float) total) * 100;
		return Float.valueOf(twoDForm.format(percentage));// Round it with 2 decimals
	}

}
