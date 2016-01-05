package uk.ac.ebi.quickgo.model.ontology.eco;

import uk.ac.ebi.quickgo.model.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.model.ontology.generic.GenericTermSet;

import java.io.IOException;
import java.io.Writer;

public class ECOTermSet extends GenericTermSet {

	public ECOTermSet(EvidenceCodeOntology ontology, String name, int colour) {
		super(ontology, name, colour);
	}

	public ECOTermSet(EvidenceCodeOntology ontology, String name) {
		super(ontology, name);
	}

	public ECOTermSet(EvidenceCodeOntology ontology) {
		super(ontology);
	}

	public void write(Writer wr) throws IOException {
		for (GenericTerm t : contents.keySet()) {
			wr.write(t.getId() + "\t" + ((ECOTerm)t).id + "\t" + t.getName() + "\n");
		}
	}
}
