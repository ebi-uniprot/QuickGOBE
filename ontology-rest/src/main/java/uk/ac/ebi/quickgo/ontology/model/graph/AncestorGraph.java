package uk.ac.ebi.quickgo.ontology.model.graph;


import java.util.HashSet;
import java.util.Set;

/**
 * Data Structure for sub graph of ontology.
 * @author Tony Wardell
 * Date: 14/06/2017
 * Time: 10:47
 * Created with IntelliJ IDEA.
 */
public class AncestorGraph<T> {

    public final Set<AncestorEdge> edges;
    public final Set<T> vertices;

    public AncestorGraph(Set<AncestorEdge> edges, Set<T> vertices) {
        this.edges = edges;
        this.vertices = vertices;
    }

    public static AncestorGraph<String> newAncestorGraphString() {
        return new AncestorGraph<>(new HashSet<>(), new HashSet<>());
    }
}
