package uk.ac.ebi.quickgo.solr.model.statistics;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author Tony Wardell
 * Date: 20/10/2015
 * Time: 10:20
 * Created with IntelliJ IDEA.
 */
public class StatisticTuple {

	private String statisticTupleType;
	private String statisticTupleKey;
	private long statisticTupleHits;
	private Set<String> hitKeys = new HashSet();

	public StatisticTuple(String type, String key, long hits) {
		this.statisticTupleType = type;
		this.statisticTupleKey = key;
		this.statisticTupleHits = hits;
	}


	public String getstatisticTupleKey() {
		return statisticTupleKey;
	}

	public long getStatisticTupleHits() {
		return statisticTupleHits;
	}

	public void addHit(){
		statisticTupleHits++;
	}

	public void uniqueHit(String hitKey){
		if(hitKeys.add(hitKey)) statisticTupleHits++;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		StatisticTuple that = (StatisticTuple) o;

		if (statisticTupleHits != that.statisticTupleHits) return false;
		if (!statisticTupleKey.equals(that.statisticTupleKey)) return false;
		return hitKeys.equals(that.hitKeys);

	}

	@Override
	public int hashCode() {
		int result = statisticTupleKey.hashCode();
		result = 31 * result + (int) (statisticTupleHits ^ (statisticTupleHits >>> 32));
		result = 31 * result + hitKeys.hashCode();
		return result;
	}

	public String getStatisticTupleType() {
		return statisticTupleType;
	}
}
