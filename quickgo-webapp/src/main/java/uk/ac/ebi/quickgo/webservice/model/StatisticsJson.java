package uk.ac.ebi.quickgo.webservice.model;

import uk.ac.ebi.quickgo.bean.statistics.StatisticsBean;

/**
 * @Author Tony Wardell
 * Date: 15/04/2015
 * Time: 15:23
 * Created with IntelliJ IDEA.
 */
public class StatisticsJson {
	private StatisticsBean statsBean;
	private long totalNumberAnnotations;
	private long totalNumberProteins;

	public void setStatsBean(StatisticsBean statsBean) {
		this.statsBean = statsBean;
	}

	public StatisticsBean getStatsBean() {
		return statsBean;
	}

	public void setTotalNumberAnnotations(long totalNumberAnnotations) {
		this.totalNumberAnnotations = totalNumberAnnotations;
	}

	public long getTotalNumberAnnotations() {
		return totalNumberAnnotations;
	}

	public void setTotalNumberProteins(long totalNumberProteins) {
		this.totalNumberProteins = totalNumberProteins;
	}

	public long getTotalNumberProteins() {
		return totalNumberProteins;
	}
}
