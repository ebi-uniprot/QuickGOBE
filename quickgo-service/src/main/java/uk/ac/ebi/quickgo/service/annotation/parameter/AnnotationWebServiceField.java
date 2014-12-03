package uk.ac.ebi.quickgo.service.annotation.parameter;

/**
 * Possible fields for the annotations web service 
 * @author cbonill
 *
 */
public enum AnnotationWebServiceField {
	
	ADVANCEDQUERY("q"),
	FORMAT("format"),
	LIMIT("limit"),
	GZ("gz"),	
	GOID("goid"),
	ECOID("ecoID"),
	ASPECT("aspect"),
	RELATION("relType"),
	TERMUSE("termUse"),
	EVIDENCE("evidence"),
	SOURCE("source"),
	REF("ref"),
	WITH("with"),
	TAX("tax"),
	PROTEIN("protein"),
	QUALIFIER("qualifier"),
	DB("db");	
	
	String value;

	private AnnotationWebServiceField(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}