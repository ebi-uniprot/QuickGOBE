package uk.ac.ebi.quickgo.ontology.traversal;

import java.util.*;
import java.util.stream.Collectors;
import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.AllDirectedPaths;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;

/**
 * This class represents an ontology graph whose vertices are ontology terms,
 * and edges are the relationships between terms.
 * <p>
 * Created 18/05/16
 *
 * @author Edd
 */
public class OntologyGraph implements OntologyGraphTraversal {
    private final DirectedGraph<String, OntologyRelationship> ontology;
    private Map<String, Set<OntologyRelationship>> ancestorEdgesMap = new HashMap<>();

    public OntologyGraph() {
        ontology =
                new DirectedMultigraph<>(new ClassBasedEdgeFactory<>(OntologyRelationship.class));
    }

    public Set<OntologyRelationship> getEdges() {
        return ontology.edgeSet();
    }

    public Set<String> getVertices() {
        return ontology.vertexSet();
    }

    public void addRelationships(Collection<? extends OntologyRelationship> relationships) {
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
                            new OntologyRelationship(oEdge.child, oEdge.parent, oEdge.relationship));
                }
        );
    }

    @Override
    public List<List<OntologyRelationship>> paths(String v1, String v2, OntologyRelationType... relations) {
        Set<OntologyRelationType> relationsSet = createRelevantRelationsSet(relations);

        if (v1.equals(v2)) {
            throw new IllegalArgumentException("Cannot find paths between vertex and itself: " + v1);
        }

        List<List<OntologyRelationship>> interestingPaths = new ArrayList<>();
        List<OntologyRelationship> immediateSuccessors = successorEdges(v1, v2, relationsSet);
        if (immediateSuccessors.size() > 0) {
            immediateSuccessors.stream()
                    .map(Collections::singletonList)
                    .forEach(interestingPaths::add);
        }

        AllDirectedPaths<String, OntologyRelationship> allPaths = new AllDirectedPaths<>(ontology);
        List<GraphPath<String, OntologyRelationship>> v1v2Paths = allPaths.getAllPaths(v1, v2, true, null);
        List<GraphPath<String, OntologyRelationship>> invalidPaths = new ArrayList<>();

        for (GraphPath<String, OntologyRelationship> path : v1v2Paths) {
            if (!relationsSet
                    .containsAll(path.getEdgeList().stream().map(e -> e.relationship).collect(Collectors.toList()))) {
                invalidPaths.add(path);
            }
        }

        v1v2Paths.removeAll(invalidPaths);

        interestingPaths.addAll(v1v2Paths.stream().map(GraphPath::getEdgeList).collect(Collectors.toList()));

        return interestingPaths;
    }

    @Override
    public Set<String> ancestors(String base, OntologyRelationType... relations) {
        Set<String> ancestorsFound = new HashSet<>();
        if (relations.length == 0) {
            relations = new OntologyRelationType[1];
            relations[0] = OntologyRelationType.UNDEFINED;
        }

        for (OntologyRelationship relation : getAncestorEdges(base)) {
            if (relation.relationship.hasTransitiveType(relations)) {
                ancestorsFound.add(relation.parent);
            }
        }

        return ancestorsFound;
    }

    @Override
    public Set<String> descendants(String top, OntologyRelationType... relations) {
        Set<String> descendantsFound = new HashSet<>();
        descendantsFound.add(top); // as with ancestors, include itself (indicating IDENTITY relationship)

        Set<OntologyRelationType> relationsSet = createRelevantRelationsSet(relations);
        descendants(top, descendantsFound, relationsSet);
        return descendantsFound;
    }

    @Override public int hashCode() {
        int result = ontology != null ? ontology.hashCode() : 0;
        result = 31 * result + (ancestorEdgesMap != null ? ancestorEdgesMap.hashCode() : 0);
        return result;
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        OntologyGraph that = (OntologyGraph) o;

        if (ontology != null ? !ontology.equals(that.ontology) : that.ontology != null) {
            return false;
        }
        return ancestorEdgesMap != null ? ancestorEdgesMap.equals(that.ancestorEdgesMap) :
                that.ancestorEdgesMap == null;
    }

    private static String getOppositeVertex(Graph<String, OntologyRelationship> graph, OntologyRelationship edge, String vertex) {
        String source = graph.getEdgeSource(edge);
        String target = graph.getEdgeTarget(edge);
        if (vertex.equals(source)) {
            return target;
        } else if (vertex.equals(target)) {
            return source;
        } else {
            throw new IllegalArgumentException(
                    "No such vertex: " + vertex);
        }
    }

    private Set<OntologyRelationship> getAncestorEdges(String vertex) {
        if (!ancestorEdgesMap.containsKey(vertex)) {
            Set<OntologyRelationship> ancestorEdgesOfV = new HashSet<>();

            ancestorEdgesOfV.add(new OntologyRelationship(vertex, vertex, OntologyRelationType.IDENTITY));

            for (OntologyRelationship successorEdge : ontology.outgoingEdgesOf(vertex)) {
                for (OntologyRelationship grandparentSuccessorEdge : getAncestorEdges(successorEdge.parent)) {
                    OntologyRelationship combinedRelationship =
                            OntologyRelationship.combineRelationships(successorEdge, grandparentSuccessorEdge);
                    if (combinedRelationship.relationship != OntologyRelationType.UNDEFINED) {
                        ancestorEdgesOfV.add(combinedRelationship);
                    }
                }
            }

            ancestorEdgesMap.put(vertex, ancestorEdgesOfV);
        }
        return ancestorEdgesMap.get(vertex);
    }

    private List<OntologyRelationship> successorEdges(String from, String to, Set<OntologyRelationType> relations) {
        List<OntologyRelationship> successors = new ArrayList<>();

        Set<OntologyRelationship> outgoingEdgesOfV = ontology.outgoingEdgesOf(from);
        outgoingEdgesOfV.stream()
                .filter(e -> relations.contains(e.relationship) && e.parent.equals(to))
                .forEach(successors::add);

        return successors;
    }

    private HashSet<OntologyRelationType> createRelevantRelationsSet(OntologyRelationType... relations) {
        return new HashSet<>(Arrays.asList(relations.length == 0 ? OntologyRelationType.values() : relations));
    }

    private Set<String> getRelatives(String base, Set<OntologyRelationship> relativeEdges,
            Set<OntologyRelationType> relations) {
        Set<String> relatives = new HashSet<>();

        relativeEdges.forEach(
                e -> {
                    if (relations.contains(e.relationship)) {
                        relatives.add(getOppositeVertex(ontology, e, base));
                    }
                }
        );

        return relatives;
    }

    private void descendants(String top, Set<String> currentDescendants, Set<OntologyRelationType> relations) {
        Set<String> parents = getRelatives(top, ontology.incomingEdgesOf(top), relations);

        for (String parent : parents) {
            currentDescendants.add(parent);
            descendants(parent, currentDescendants, relations);
        }
    }
}
