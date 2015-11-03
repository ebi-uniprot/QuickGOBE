package uk.ac.ebi.quickgo.service.statistic;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.util.ClientUtils;

import uk.ac.ebi.quickgo.service.annotation.AnnotationService;
import uk.ac.ebi.quickgo.statistic.StatsTerm;

/**
 * Thread to calculate a chunk of statistics
 * @author cbonill
 *
 */
public class StatisticsThread extends Thread {

	// Query
	String query;
	// Total number proteins
	long numberProteins;
	// Field
	String field;
	// Services
	StatisticsUtil statsUtil;
	AnnotationService annotationService;
	// Counts to process
	List<Count> counts = new ArrayList<>();
	// Stats results
	TreeSet<StatsTerm> statsTerms = new TreeSet<>();

	/**
	 * Main method of the thread
	 */
	public void run() {
		// Set filter query if different from "*:*"
		String filterQuery = "";
		if (!query.equals("*:*")) {
			filterQuery = " AND " + query;
		}
		for (Count count : counts) {
			long total = annotationService.getTotalNumberProteins(field + ":" + ClientUtils.escapeQueryChars(count.getName()) + filterQuery);
			StatsTerm statsTerm = new StatsTerm(count.getName(), statsUtil.getName(count.getName(), field), StatisticsMath.calculatePercentage(total, numberProteins), total);
			if(!count.getName().equalsIgnoreCase("go")){//TODO Don't add "go" term. This is because of faceting. Investigate why "go" id is returned
				statsTerms.add(statsTerm);
			}
		}
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public void setStatsUtil(StatisticsUtil statsUtil) {
		this.statsUtil = statsUtil;
	}

	public void setNumberProteins(long numberProteins) {
		this.numberProteins = numberProteins;
	}

	public void setField(String field) {
		this.field = field;
	}

	public void setAnnotationService(AnnotationService annotationService) {
		this.annotationService = annotationService;
	}

	public void setCounts(List<Count> counts) {
		this.counts = counts;
	}

	public void setStatsTerms(TreeSet<StatsTerm> statsTerms) {
		this.statsTerms = statsTerms;
	}

	public TreeSet<StatsTerm> getStatsTerms() {
		return statsTerms;
	}
}
