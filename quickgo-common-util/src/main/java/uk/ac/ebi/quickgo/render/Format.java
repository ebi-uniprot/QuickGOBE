package uk.ac.ebi.quickgo.render;

/**
 * Possible output formats
 * @author cbonill
 *
 */
public enum Format {

	JSON("json"),
	JSONMINIMAL("jsonminimal"),
	XML("xml"),
	OBOXML("oboxml"),
	OBO("obo"),
	MINI("mini");

	String value;

	private Format(String value) {
		this.value = value;
	}
}
