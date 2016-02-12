package uk.ac.ebi.quickgo.model.ontology.generic;

public class NamedXRef extends XRef {
	protected String name;

	public NamedXRef(String db, String id, String name) {
		super(db, id);
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
