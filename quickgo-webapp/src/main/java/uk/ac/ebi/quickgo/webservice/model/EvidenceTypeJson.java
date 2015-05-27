package uk.ac.ebi.quickgo.webservice.model;

/**
 * @Author Tony Wardell
 * Date: 05/03/2015
 * Time: 16:58
 * Created with IntelliJ IDEA.
 */
public class EvidenceTypeJson {
	private String ecoTerm;
	private String evidence;
	private String evidenceKey;

	public void setKey(String ecoTerm) {
		this.ecoTerm = ecoTerm;
	}

	public String getEcoTerm() {
		return ecoTerm;
	}


	public void setEvidence(String evidence) {
		this.evidence = evidence;
	}

	public String getEvidence() {
		return evidence;
	}

	public void setEvidenceKey(String evidenceKey) {
		this.evidenceKey = evidenceKey;
	}

	public String getEvidenceKey() {
		return evidenceKey;
	}
}
