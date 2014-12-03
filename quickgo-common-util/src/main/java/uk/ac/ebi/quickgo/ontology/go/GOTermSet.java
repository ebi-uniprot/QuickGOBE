package uk.ac.ebi.quickgo.ontology.go;

import java.io.IOException;
import java.io.Writer;

import uk.ac.ebi.quickgo.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.ontology.generic.GenericTermSet;

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
