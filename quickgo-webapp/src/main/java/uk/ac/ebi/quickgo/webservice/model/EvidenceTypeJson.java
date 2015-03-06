package uk.ac.ebi.quickgo.webservice.model;

/**
 * @Author Tony Wardell
 * Date: 05/03/2015
 * Time: 16:58
 * Created with IntelliJ IDEA.
 */
public class EvidenceTypeJson {
	private String ecoTerm;
	private String value;

	public void setKey(String ecoTerm) {
		this.ecoTerm = ecoTerm;
	}

	public String getEcoTerm() {
		return ecoTerm;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
