package uk.ac.ebi.quickgo.solr.query.service.statistics;

import java.util.Map;
import java.util.TreeMap;

/**
 * @Author Tony Wardell
 * Date: 14/10/2015
 * Time: 15:49
 * Created with IntelliJ IDEA.
 */
public class StatsCache {

	public final static StatsCache INSTANCE = new StatsCache();
	private Map<String, Long> totalGeneProducts;
	private Map<String, Long> totalAnnotations;

	private StatsCache() {
		totalGeneProducts  = new TreeMap<>();
		totalAnnotations = new TreeMap<>();
		totalGeneProducts.put("*:*", new Long(31598326));
	}

	public void setTotalGeneProducts(String query, long totalGeneProducts){
		this.totalGeneProducts.put(query, totalGeneProducts);
	}

	public long getTotalGeneProducts(String query){
		Long result = totalGeneProducts.get(query);
		return result==null?0:result.longValue();
	}

	public long getTotalAnnotations(String query) {
		Long result = totalAnnotations.get(query);
		return result==null?0:result.longValue();
	}

	public void setTotalAnnotations(String query, long totalAnnotations) {
		this.totalAnnotations.put(query, totalAnnotations);
	}

}

//goID:go
