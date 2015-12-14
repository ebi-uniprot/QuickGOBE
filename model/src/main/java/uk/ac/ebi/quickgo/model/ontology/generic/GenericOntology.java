/**
 * 
 */
package uk.ac.ebi.quickgo.model.ontology.generic;

import uk.ac.ebi.quickgo.model.ontology.go.TaxonConstraintSet;

import java.util.HashMap;
import java.util.Map;


/**
 * Class that represents a generic (OBOFoundry-style) ontology
 * 
 * @author tonys
 *
 */
public abstract class GenericOntology implements ITermContainer {
	public String namespace;
	public Map<String, GenericTerm> terms = new HashMap<>();
    public Map<String, GenericTerm> xrefFind = new HashMap<>();
    public Map<String, GenericTermSet> subsets = new HashMap<>();
	public TaxonConstraintSet taxonConstraints = new TaxonConstraintSet();
	public TermOntologyHistory history = new TermOntologyHistory();

	public CV fundingBodies;

	public Map<String, TermCredit> termCredits = new HashMap<String, TermCredit>();
	
    public String version;
    public String timestamp;
    public String url;

    public GenericOntology(String namespace) {
    	this.namespace = namespace;
    }

}
