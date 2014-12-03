package uk.ac.ebi.quickgo.solr.query.model.miscellaneous.enums;

/**
 * Enum for miscellaneous Solr fields
 *
 * @author cbonill
 *
 */
public enum MiscellaneousField {

	TYPE("docType"),
	TAXONOMY_ID("taxonomyId"),
	TAXONOMY_NAME("taxonomyName"),
	TAXONOMY_CLOSURE("taxonomyClosure"),
	TERM("term"),
	STATS_TYPE("statsType"),
	DBOBJECTID("dbObjectID"),
	SEQUENCE("sequence"),
	PUBLICATIONID("publicationID"),
	AERNAME("aerName"),
	XREFABBREVIATION("xrefAbbreviation"),
	SUBSET("subset"),
	EVIDENCECODE("evidenceCode");
	
	String value;

	private MiscellaneousField(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
