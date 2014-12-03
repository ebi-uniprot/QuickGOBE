package uk.ac.ebi.quickgo.solr.query.model.miscellaneous.enums;

/**
 * Possible co-occurrence stats types 
 * @author cbonill
 * 
 */
public enum MiscellaneousValue {

	ALL("all"),
	NON_IEA("nonIEA");

	String value;

	private MiscellaneousValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}