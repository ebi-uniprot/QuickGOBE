package uk.ac.ebi.quickgo.ontology.traversal;

import uk.ac.ebi.quickgo.ontology.traversal.read.OntologyRelationshipTuple;

import java.util.HashSet;
import java.util.Set;

/**
 * Created 18/05/16
 * @author Edd
 */
public class OntologyGraph {
    private final Set<OntologyRelationshipTuple> tuples;

    public OntologyGraph() {
        tuples = new HashSet<>();
    }

    public Set<OntologyRelationshipTuple> getTuples() {
        return tuples;
    }

    public boolean removeTuple(OntologyRelationshipTuple tuple) {
        return tuples.remove(tuple);
    }
}
