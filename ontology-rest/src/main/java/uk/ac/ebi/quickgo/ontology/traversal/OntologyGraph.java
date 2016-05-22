package uk.ac.ebi.quickgo.ontology.traversal;

import org.jgrapht.DirectedGraph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.AllDirectedPaths;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;
import uk.ac.ebi.quickgo.ontology.traversal.read.OntologyRelationship;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class represents an ontology graph whose vertices are ontology terms,
 * and edges are the relationships between terms.
 * <p>
 * Created 18/05/16
 *
 * @author Edd
 */
public class OntologyGraph implements OntologyGraphTraversal {
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


    public boolean addRelationships(Collection<? extends OntologyRelationship> relationships) {
        // populate graph with edges, whilst recording the vertices
        relationships.stream().forEach(
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

    @Override
    public List<List<LabelledEdge>> paths(String v1, String v2, OntologyRelation... relations) {
        Set<String> relationsSet = new HashSet<>(Arrays.asList(relations.length == 0 ? OntologyRelation.values() : relations).stream().map(o -> o.getShortName()).collect(Collectors.toList()));
        AllDirectedPaths<String, LabelledEdge> allPaths = new AllDirectedPaths<>(ontology);
        List<GraphPath<String, LabelledEdge>> v1v2Paths = allPaths.getAllPaths(v1, v2, true, null);
        List<GraphPath<String, LabelledEdge>> invalidPaths = new ArrayList<>();

        for (GraphPath<String, LabelledEdge> path : v1v2Paths) {
            if (!relationsSet.containsAll(path.getEdgeList().stream().map(e -> e.relation).collect(Collectors.toList()))) {
                invalidPaths.add(path);
            }
        }

        v1v2Paths.removeAll(invalidPaths);

        return v1v2Paths.stream().map(GraphPath::getEdgeList).collect(Collectors.toList());
    }

    @Override
    public Set<String> ancestors(String base, OntologyRelation... relations) {
        return null;
    }

    @Override
    public Set<String> descendants(String top, OntologyRelation... relations) {
        return null;
    }

    public static class LabelledEdge<V> extends DefaultEdge {
        private V v1;
        private V v2;
        private String relation;

        LabelledEdge(V v1, V v2, OntologyRelation relation) {
            this.v1 = v1;
            this.v2 = v2;
            this.relation = relation.getShortName();
        }

        public V getV1() {
            return v1;
        }

        public V getV2() {
            return v2;
        }

        public String toString() {
            return relation;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            LabelledEdge<?> that = (LabelledEdge<?>) o;

            if (v1 != null ? !v1.equals(that.v1) : that.v1 != null) return false;
            if (v2 != null ? !v2.equals(that.v2) : that.v2 != null) return false;
            return !(relation != null ? !relation.equals(that.relation) : that.relation != null);

        }

        @Override
        public int hashCode() {
            int result = v1 != null ? v1.hashCode() : 0;
            result = 31 * result + (v2 != null ? v2.hashCode() : 0);
            result = 31 * result + (relation != null ? relation.hashCode() : 0);
            return result;
        }
    }
}
