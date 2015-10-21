package uk.ac.ebi.quickgo.indexer.annotation;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author Tony Wardell
 * Date: 20/10/2015
 * Time: 10:20
 * Created with IntelliJ IDEA.
 */
public class StatisticTuple {

	private String key;
	private long hits;
	private Set<String> hitKeys = new HashSet();

	public StatisticTuple(String key, long hits) {
		this.key = key;
		this.hits = hits;
	}


	public String getKey() {
		return key;
	}

	public long getHits() {
		return hits;
	}

	public void addHit(){
		hits++;
	}

	public void uniqueHit(String hitKey){
		if(hitKeys.add(hitKey)) hits++;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		StatisticTuple that = (StatisticTuple) o;

		if (hits != that.hits) return false;
		if (!key.equals(that.key)) return false;
		return hitKeys.equals(that.hitKeys);

	}

	@Override
	public int hashCode() {
		int result = key.hashCode();
		result = 31 * result + (int) (hits ^ (hits >>> 32));
		result = 31 * result + hitKeys.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "StatisticTuple{" +
				"key='" + key + '\'' +
				", hits=" + hits +
				'}';
	}
}
