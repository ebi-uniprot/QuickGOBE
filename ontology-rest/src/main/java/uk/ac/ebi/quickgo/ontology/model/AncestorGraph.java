package uk.ac.ebi.quickgo.ontology.model;

import java.util.Set;

/**
 * Data Structure for sub graph of ontology.
 * @author Tony Wardell
 * Date: 14/06/2017
 * Time: 10:47
 * Created with IntelliJ IDEA.
 */
public class AncestorGraph {

    public final Set<OntologyRelationship> edges;
    public final Set<String> vertices;

    public AncestorGraph(Set<OntologyRelationship> edges, Set<String> vertices) {
        this.edges = edges;
        this.vertices = vertices;
    }
}
