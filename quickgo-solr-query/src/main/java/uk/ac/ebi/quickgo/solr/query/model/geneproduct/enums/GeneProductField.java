package uk.ac.ebi.quickgo.solr.query.model.geneproduct.enums;

/**
 * Enum for gene products Solr fields
 */
public enum GeneProductField {

	DBOBJECTID("dbObjectId"),
	DBOBJECTNAME("dbObjectName"),
	DBOBJECTTYPE("dbObjectType"),
	DOCTYPE("docType"),
	PROPERTYNAME("propertyName");
	// TODO Add the rest

	String value;

	private GeneProductField(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
