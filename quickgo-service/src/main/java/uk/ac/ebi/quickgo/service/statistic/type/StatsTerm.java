package uk.ac.ebi.quickgo.service.statistic.type;

/**
 * Type to represent the stats values 
 * @author cbonill
 * 
 */
public class StatsTerm implements Comparable<StatsTerm> {

	private String code;
	private String name;
	float percentage;
	long count;

	public StatsTerm(String code, String name, float percentage, long count) {
		this.code = code;
		this.name = name;
		this.percentage = percentage;
		this.count = count;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getPercentage() {
		return percentage;
	}

	public void setPercentage(float percentage) {
		this.percentage = percentage;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	@Override
	public int compareTo(StatsTerm o) {
		if (this.getCount() > o.getCount()) {
			return 1;
		} else if (this.getCount() < o.getCount()) {
			return -1;
		} else {
			return this.getCode().compareTo(o.getCode());
		}
	}
}