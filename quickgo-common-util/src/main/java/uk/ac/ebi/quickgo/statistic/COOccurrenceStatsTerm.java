package uk.ac.ebi.quickgo.statistic;

import java.text.DecimalFormat;

import uk.ac.ebi.quickgo.miscellaneous.Miscellaneous;


/**
 * Type for the co-occurrence statistics
 * @author cbonill
 *
 */
public class COOccurrenceStatsTerm implements Comparable<COOccurrenceStatsTerm>{

	String term;
	String comparedTerm;
	String statsType;
	int together;
	int compared;
	float selected;
	float all;

	// Extra fields for displaying in web
	String aspect;
	String name;

	public COOccurrenceStatsTerm(){}

	/**
	 * Create a {@link COOccurrenceStatsTerm} from a {@link Miscellaneous} object
	 * @param miscellaneous Miscellaneous representation
	 */
	public COOccurrenceStatsTerm(Miscellaneous miscellaneous) {
		this.term = miscellaneous.getTerm();
		this.comparedTerm = miscellaneous.getComparedTerm();
		this.statsType = miscellaneous.getStatsType();
		this.together = (int)miscellaneous.getTogether();
		this.compared = (int)miscellaneous.getCompared();
		this.selected = miscellaneous.getSelected();
		this.all = miscellaneous.getAll();
	}

	public float getProbabilityRatio(){
		DecimalFormat twoDForm = new DecimalFormat("#.##");
		float probabilityRatio = (this.together/this.selected) / (this.compared/this.all);
		return Float.valueOf(twoDForm.format(probabilityRatio));// Round it with 2 decimals
	}

	public float getProbabilitySimilarityRatio() {
		DecimalFormat twoDForm = new DecimalFormat("#.##");
		float similarityRatio = 100 * ((this.together) / (this.selected+this.compared-this.together));
		float psRatio = Float.valueOf(twoDForm.format(similarityRatio));// Round it with 2 decimals
		if(Math.round(psRatio) == 100){
			return 100;
		}
		return psRatio;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getComparedTerm() {
		return comparedTerm;
	}

	public void setComparedTerm(String comparedTerm) {
		this.comparedTerm = comparedTerm;
	}

	public int getTogether() {
		return together;
	}

	public void setTogether(int together) {
		this.together = together;
	}

	public int getCompared() {
		return compared;
	}

	public void setCompared(int compared) {
		this.compared = compared;
	}

	public float getSelected() {
		return selected;
	}

	public void setSelected(float selected) {
		this.selected = selected;
	}

	public float getAll() {
		return all;
	}

	public void setAll(float all) {
		this.all = all;
	}

	public String getStatsType() {
		return statsType;
	}

	public void setStatsType(String statsType) {
		this.statsType = statsType;
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

	/**
	 * Convert the object into a {@link Miscellaneous} one
	 * @return {@link Miscellaneous} representation
	 */
	public Miscellaneous toMiscellaneousObject(){
		Miscellaneous miscellaneous = new Miscellaneous();
		miscellaneous.setAll(this.all);
		miscellaneous.setCompared(this.compared);
		miscellaneous.setComparedTerm(this.comparedTerm);
		miscellaneous.setSelected(this.selected);
		miscellaneous.setStatsType(this.statsType);
		miscellaneous.setTerm(this.term);
		miscellaneous.setTogether(this.together);

		return miscellaneous;
	}

	@Override
	public int compareTo(COOccurrenceStatsTerm o) {
		if (this.getProbabilitySimilarityRatio() > o.getProbabilitySimilarityRatio()) {
			return 1;
		} else if (this.getProbabilitySimilarityRatio() < o.getProbabilitySimilarityRatio()) {
			return -1;
		} else {
			return this.comparedTerm.compareTo(o.comparedTerm);
		}
	}
}
