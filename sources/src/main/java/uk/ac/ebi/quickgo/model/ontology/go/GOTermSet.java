package uk.ac.ebi.quickgo.model.ontology.go;

import uk.ac.ebi.quickgo.model.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.model.ontology.generic.GenericTermSet;

import java.io.IOException;
import java.io.Writer;

public class GOTermSet extends GenericTermSet {
	public GOTermSet(GeneOntology ontology, String name, int colour) {
		super(ontology, name, colour);
	}

	public GOTermSet(GeneOntology ontology, String name) {
		super(ontology, name);
	}

	public GOTermSet(GeneOntology ontology) {
		super(ontology);
	}

	public void write(Writer wr) throws IOException {
		for (GenericTerm t : contents.keySet()) {
			wr.write(t.getId() + "\t" + ((GOTerm)t).aspect.abbreviation + "\t" + t.getName() + "\n");
		}
	}
}
