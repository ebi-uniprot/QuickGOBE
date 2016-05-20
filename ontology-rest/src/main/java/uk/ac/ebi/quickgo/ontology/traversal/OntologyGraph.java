package uk.ac.ebi.quickgo.ontology.traversal;

import uk.ac.ebi.quickgo.ontology.traversal.read.OntologyRelationship;

import java.util.Collection;
import java.util.Set;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;

/**
 * This class represents an ontology graph whose vertices are ontology terms,
 * and edges are the relationships between terms.
 *
 * Created 18/05/16
 * @author Edd
 */
public class OntologyGraph {
    private final DirectedGraph<String, LabelledEdge> ontology;

    public OntologyGraph() {
        ontology =
                new DirectedMultigraph<>(new ClassBasedEdgeFactory<>(LabelledEdge.class));
    }

    public Set<LabelledEdge> getEdges() {
        return ontology.edgeSet();
    }

    public Set<String> getVertices() {
        return ontology.vertexSet();
    }


    public boolean addRelationships(Collection<? extends OntologyRelationship> tuples) {
        // populate graph with edges, whilst recording the vertices
        tuples.stream().forEach(
                oEdge -> {
                    if (!ontology.containsVertex(oEdge.child)) {
                        ontology.addVertex(oEdge.child);
                    }
                    if (!ontology.containsVertex(oEdge.parent)) {
                        ontology.addVertex(oEdge.parent);
                    }
                    ontology.addEdge(
                            oEdge.child,
                            oEdge.parent,
                            new LabelledEdge<>(oEdge.child, oEdge.parent,
                                    OntologyRelation.getByShortName(oEdge.relationship)));
                }
        );

        return true;
    }

    public static class LabelledEdge<V> extends DefaultEdge {
        private V v1;
        private V v2;
        private OntologyRelation relation;

        LabelledEdge(V v1, V v2, OntologyRelation relation) {
            this.v1 = v1;
            this.v2 = v2;
            this.relation = relation;
        }

        public V getV1() {
            return v1;
        }

        public V getV2() {
            return v2;
        }

        public String toString() {
            return relation.getShortName();
        }
    }
}
