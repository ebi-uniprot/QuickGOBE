package uk.ac.ebi.quickgo.solr.query.model.ontology.enums;

/**
 * Solr Term fields. Must have the same names as in the schema.xml solR file
 * @author cbonill
 *
 */
public enum TermField {

	ID("id"),
	NAME("name"),
	TYPE("docType"),
	CHILD("child"),
	PARENT("parent"),
	OBSOLETE_ID("obsoleteId"),
	SECONDARYID("secondaryId"),
	HISTORYTIMESTAMP("historyTimeStamp"),
	DOCTYPE("docType");
	// TODO Add the rest

	String value;

	TermField(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
