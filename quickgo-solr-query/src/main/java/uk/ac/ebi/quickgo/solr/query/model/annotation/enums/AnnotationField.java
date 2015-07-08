package uk.ac.ebi.quickgo.solr.query.model.annotation.enums;

/**
 * Enum for Solr Annotation fields
 * @author cbonill
 *
 */
public enum AnnotationField {

	ID("id"),
	DBOBJECTTYPE("dbObjectType"),
	DBOBJECTID("dbObjectID"),
	DB("db"),
	TARGETSET("targetSet"),
	DOCTYPE("docType"),
	GOASPECT("goAspect"),
	ASSIGNEDBY("assignedBy"),
	GOEVIDENCE("goEvidence"),
	WITH("with"),
	GOID("goID"),
	ECOID("ecoID"),
	QUALIFIER("qualifier"),
	TAXONOMYID("taxonomyId"),
	TAXONOMYCLOSURE("taxonomyClosure"),
	ANCESTORSI("ancestorsI"),
	ANCESTORSIPO("ancestorsIPO"),
	ANCESTORSIPOR("ancestorsIPOR"),
	ECOANCESTORSI("ecoAncestorsI"),
	XREFS("xrefs"),
	REFERENCE("reference");


	String value;

	private AnnotationField(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
