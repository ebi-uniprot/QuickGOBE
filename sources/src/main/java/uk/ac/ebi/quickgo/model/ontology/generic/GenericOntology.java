/**
 * 
 */
package uk.ac.ebi.quickgo.model.ontology.generic;

import uk.ac.ebi.quickgo.model.ontology.go.TaxonConstraintSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public int getTermCount() {
        return terms.size();
    }

    @Override
    public List<GenericTerm> getTerms() {
        return new ArrayList<>(terms.values());
    }

    @Override
    public List<String> getTermIds() {
        return new ArrayList<>(terms.keySet());
    }

    @Override
    public GenericTerm[] toArray() {
        return terms.values().toArray(new GenericTerm[terms.size()]);
    }

    @Override
    public GenericTerm getTerm(String id) {
        GenericTerm t = terms.get(id);
        if (t == null) {
            t = xrefFind.get(id);
        }
        return t;
    }

    @Override
    public void addTerm(GenericTerm t) {
        terms.put(t.getId(), t);
    }

}
