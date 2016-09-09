package uk.ac.ebi.quickgo.common.costats;

/**
 * Describes the contents of the Cooccurrence Stat Model
 * @author twardell
 * @tool IntelliJ
 */

public class CoOccurrenceStat {

	private static final int COLUMN_ID = 0;
	private static final int COLUMN_COMPARE = 1;
	private static final int COLUMN_PROB = 2;
	private static final int COLUMN_SIG = 3;
	private static final int COLUMN_TOGETHER = 4;
	private static final int COLUMN_COMPARED = 5;

	private String selected;
	private String compare;
	private float probabilityRatio;
	private float significance;
	private long together;
	private long compared;

	//These are no saved as part of the file
	String aspect;
	String name;

		public CoOccurrenceStat(String selected, String compare, float probabilityRatio, float significance, long
				together,
								long compared) {
			this.selected = selected;
			this.compare = compare;
			this.probabilityRatio = probabilityRatio;
			this.significance = significance;
			this.together = together;
			this.compared = compared;
		}

		public String toTSV(){
			return selected + '\t'
					+ compare + '\t'
					+ probabilityRatio + '\t'
					+ significance + '\t'
					+ together + '\t'
					+ compared;
		}

	public static CoOccurrenceStat fromFile(String line) {
		String[] columns = line.split("\t");
		CoOccurrenceStat coOccurrenceStat = new CoOccurrenceStat(columns[COLUMN_ID], columns[COLUMN_COMPARE],
				Float.parseFloat(columns[COLUMN_PROB]), Float.parseFloat(columns[COLUMN_SIG]),
				Long.parseLong(columns[COLUMN_TOGETHER]), Long.parseLong(columns[COLUMN_COMPARED]));
		return coOccurrenceStat;
	}

	public String getSelected() {
		return selected;
	}

	public String getCompare() {
		return compare;
	}

	/**
	 * Ratio of probability of compared term given selected term to probability of compared term
	 * =(#together/selected)/(#compared/#all)
	 *
	 * @return Probability of term here estimated as fraction of proteins annotated to term.
     */
	public float getProbabilityRatio() {
		return probabilityRatio;
	}

	/**
	 * Probability similarity ratio
	 * Ratio of probability of both terms to probability of either term
	 * =#together/(#selected+#compared-#together)
	 *
	 * @return Probability of term here estimated as fraction of proteins annotated to term.
     */
	public float getSignificance() {
		return significance;
	}

	public long getTogether() {
		return together;
	}

	public long getCompared() {
		return compared;
	}

	public String getAspect() {
		return aspect;
	}

	public void setAspect(String aspect) {
		this.aspect = aspect;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "CooccurrenceStat{" +
				"selected='" + selected + '\'' +
				", compare='" + compare + '\'' +
				", probabilityRatio=" + probabilityRatio +
				", significance=" + significance +
				", together=" + together +
				", compared=" + compared +
				", aspect='" + aspect + '\'' +
				", name='" + name + '\'' +
				'}';
	}
}
