/**
 * 
 */
package uk.ac.ebi.quickgo.model.ontology.go;

import uk.ac.ebi.quickgo.model.ontology.generic.GenericOntology;

/**
 * Class that represents the Gene Ontology (GO)
 * 
 * @author tonys
 *
 */
public class GeneOntology extends GenericOntology {
    public static final String root = "GO:0003673";
	public static final String NAME_SPACE = "GO";

    public GeneOntology() {
    	super(NAME_SPACE);
    }
}
