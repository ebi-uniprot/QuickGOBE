package uk.ac.ebi.quickgo.ontology.go;

/**
 * class that represents a single entry in the annotation blacklist
 * 
 * @author tonys
 *
 */
public class AnnotationBlacklistEntry {
	public String proteinAc;
	public int taxonId;
	public String goId;
	public String reason;
	public String methodId;

	public AnnotationBlacklistEntry(String proteinAc, String taxonId, String goId, String reason, String methodId) {
		this.proteinAc = proteinAc;
		this.taxonId = Integer.parseInt(taxonId);
		this.goId = goId;
		this.reason = reason.intern();
		this.methodId = methodId;
	}

	public String getProteinAc() {
		return proteinAc;
	}

	public void setProteinAc(String proteinAc) {
		this.proteinAc = proteinAc;
	}

	public int getTaxonId() {
		return taxonId;
	}

	public void setTaxonId(int taxonId) {
		this.taxonId = taxonId;
	}

	public String getGoId() {
		return goId;
	}

	public void setGoId(String goId) {
		this.goId = goId;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getMethodId() {
		return methodId;
	}

	public void setMethodId(String methodId) {
		this.methodId = methodId;
	}	
}
