package uk.ac.ebi.quickgo.ontology.go;

public class TaxonUnion {
	public int id;
	public String name;
	public String taxa;

	public TaxonUnion(int id, String name, String taxa) {
		this.id = id;
		this.name = name;
		this.taxa = taxa;
	}
}
