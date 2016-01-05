package uk.ac.ebi.quickgo.model.ontology.generic;

public class TermCredit {
	public String code;
	public String url;

	public TermCredit(String code, String url) {
		this.code = code;
		this.url = url;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
