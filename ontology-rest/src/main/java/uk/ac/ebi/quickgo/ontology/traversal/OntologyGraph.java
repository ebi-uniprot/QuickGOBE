package uk.ac.ebi.quickgo.ontology.traversal;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.AllDirectedPaths;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;
import uk.ac.ebi.quickgo.ontology.traversal.read.OntologyRelationship;

import java.util.*;
import java.util.function.Function;
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
    private final DirectedGraph<String, StringEdge> ontology;

    public OntologyGraph() {
        ontology =
                new DirectedMultigraph<>(new ClassBasedEdgeFactory<>(StringEdge.class));
    }

    public Set<StringEdge> getEdges() {
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
                            new StringEdge(oEdge.child, oEdge.parent,
                                    OntologyRelation.getByShortName(oEdge.relationship)));
                }
        );

        return true;
    }

    @Override
    public List<List<StringEdge>> paths(String v1, String v2, OntologyRelation... relations) {
        Set<String> relationsSet = new HashSet<>(Arrays.asList(relations.length == 0 ? OntologyRelation.values() : relations).stream().map(o -> o.getShortName()).collect(Collectors.toList()));
        AllDirectedPaths<String, StringEdge> allPaths = new AllDirectedPaths<>(ontology);
        List<GraphPath<String, StringEdge>> v1v2Paths = allPaths.getAllPaths(v1, v2, true, null);
        List<GraphPath<String, StringEdge>> invalidPaths = new ArrayList<>();

        for (GraphPath<String, StringEdge> path : v1v2Paths) {
            if (!relationsSet.containsAll(path.getEdgeList().stream().map(e -> e.relation).collect(Collectors.toList()))) {
                invalidPaths.add(path);
            }
        }

        v1v2Paths.removeAll(invalidPaths);

        return v1v2Paths.stream().map(GraphPath::getEdgeList).collect(Collectors.toList());
    }

    @Override
    public Set<String> ancestors(String base, OntologyRelation... relations) {
        Set<String> ancestorsFound = new HashSet<>();
        Set<String> relationsSet = new HashSet<>(Arrays.asList(relations.length == 0 ? OntologyRelation.values() : relations).stream().map(o -> o.getShortName()).collect(Collectors.toList()));
        ancestors(base, ancestorsFound, relationsSet);
        return ancestorsFound;
    }

    private void ancestors(String base, Set<String> currentAncestors, Set<String> relations) {
        Set<String> parents = getRelatives(base, ontology::outgoingEdgesOf, relations);

        for (String parent : parents) {
            currentAncestors.add(parent);
            ancestors(parent, currentAncestors, relations);
        }
    }

    private Set<String> getRelatives(String base, Function<String, Set<StringEdge>> relativeEdges, Set<String> relations) {
        Set<String> relatives = new HashSet<>();
        Set<StringEdge> edges = relativeEdges.apply(base);

        edges.forEach(
                e -> {
                    if (relations.contains(e.relation)) {
                        relatives.add(getOppositeVertex(ontology, e, base));
                    }
                }
        );

        return relatives;
    }

    private static String getOppositeVertex(Graph<String, StringEdge> g, StringEdge e, String v) {
        String source = g.getEdgeSource(e);
        String target = g.getEdgeTarget(e);
        if (v.equals(source)) {
            return target;
        } else if (v.equals(target)) {
            return source;
        } else {
            throw new IllegalArgumentException(
                    "No such vertex: " + v);
        }
    }

    @Override
    public Set<String> descendants(String top, OntologyRelation... relations) {
        Set<String> descendantsFound = new HashSet<>();
        Set<String> relationsSet = new HashSet<>(Arrays.asList(relations.length == 0 ? OntologyRelation.values() : relations).stream().map(o -> o.getShortName()).collect(Collectors.toList()));
        descendants(top, descendantsFound, relationsSet);
        return descendantsFound;
    }

    private void descendants(String top, Set<String> currentDescendants, Set<String> relations) {
        Set<String> parents = getRelatives(top, ontology::incomingEdgesOf, relations);

        for (String parent : parents) {
            currentDescendants.add(parent);
            descendants(parent, currentDescendants, relations);
        }
    }

    public static class StringEdge extends LabelledEdge<String> {

        StringEdge(String v1, String v2, OntologyRelation relation) {
            super(v1, v2, relation);
        }
    }


    public static class LabelledEdge<V> extends DefaultEdge {
        protected V v1;
        protected V v2;
        protected String relation;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OntologyGraph that = (OntologyGraph) o;

        return !(ontology != null ? !ontology.equals(that.ontology) : that.ontology != null);

    }

    @Override
    public int hashCode() {
        return ontology != null ? ontology.hashCode() : 0;
    }
}
