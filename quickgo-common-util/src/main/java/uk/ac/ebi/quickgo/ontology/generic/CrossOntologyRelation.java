package uk.ac.ebi.quickgo.ontology.generic;

public class CrossOntologyRelation {
    public String relation;
    public String otherNamespace;
    public String foreignID;
    public String foreignTerm;
    public String url;

    public CrossOntologyRelation(String relation, String otherNamespace, String foreignID, String foreignTerm, String url) {
        this.relation = relation;
        this.otherNamespace = otherNamespace;
        this.foreignID = foreignID;
        this.foreignTerm = foreignTerm;
        this.url = url;
    }

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public String getOtherNamespace() {
		return otherNamespace;
	}

	public void setOtherNamespace(String otherNamespace) {
		this.otherNamespace = otherNamespace;
	}

	public String getForeignID() {
		return foreignID;
	}

	public void setForeignID(String foreignID) {
		this.foreignID = foreignID;
	}

	public String getForeignTerm() {
		return foreignTerm;
	}

	public void setForeignTerm(String foreignTerm) {
		this.foreignTerm = foreignTerm;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
