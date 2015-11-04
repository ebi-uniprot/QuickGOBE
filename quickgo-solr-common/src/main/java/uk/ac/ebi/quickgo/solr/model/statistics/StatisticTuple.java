package uk.ac.ebi.quickgo.solr.model.statistics;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author Tony Wardell
 * Date: 20/10/2015
 * Time: 10:20
 * Created with IntelliJ IDEA.
 *
 * This class represents a single data point for a statistic
 * The hit keys are used to store
 */
public class StatisticTuple {

	private String statisticTupleType;
	private String statisticTupleKey;
	private long statisticTupleHits;
	private float statisticTuplePercentage;

	//Optional value. Since some data has the same key that appears multiple times
	//Eg annotations can have the same goId but different gene product ids (could have the same
	//gpId multiple times), we use the secondary key to ensure the uniqueness of the hit count.
	//Ie we get the number of distinct gene products per go term.
	private Set<String> secondaryKeys = new HashSet();


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

	public void uniqueHit(String secondaryKey){
		if(secondaryKeys.add(secondaryKey)) statisticTupleHits++;
	}

	public void calculateStatisticTuplePercentage(long totalHits){
		float pc = ((float) this.statisticTupleHits/(float)totalHits);
		statisticTuplePercentage = pc*100;
	}

	public float getStatisticTuplePercentage() {
		return statisticTuplePercentage;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		StatisticTuple that = (StatisticTuple) o;

		if (statisticTupleHits != that.statisticTupleHits) return false;
		if (!statisticTupleKey.equals(that.statisticTupleKey)) return false;
		return secondaryKeys.equals(that.secondaryKeys);

	}

	@Override
	public int hashCode() {
		int result = statisticTupleKey.hashCode();
		result = 31 * result + (int) (statisticTupleHits ^ (statisticTupleHits >>> 32));
		result = 31 * result + secondaryKeys.hashCode();
		return result;
	}

	public String getStatisticTupleType() {
		return statisticTupleType;
	}
}
