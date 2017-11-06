package uk.ac.ebi.quickgo.ontology.traversal;

import uk.ac.ebi.quickgo.ontology.model.OntologyRelationType;
import uk.ac.ebi.quickgo.ontology.model.OntologyRelationship;
import uk.ac.ebi.quickgo.ontology.model.graph.AncestorEdge;
import uk.ac.ebi.quickgo.ontology.model.graph.AncestorGraph;
import uk.ac.ebi.quickgo.ontology.model.graph.AncestorGraphRequest;

import com.google.common.base.Preconditions;
import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.AllDirectedPaths;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.toSet;
import uk.ac.ebi.quickgo.ontology.common.OntologyType;
import uk.ac.ebi.quickgo.ontology.model.OntologyRelationType;
import uk.ac.ebi.quickgo.ontology.model.OntologyRelationship;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(OntologyGraph.class);

    static final String MOLECULAR_FUNCTION_STOP_NODE = "GO:0003674";
    static final String BIOLOGICAL_PROCESS_STOP_NODE = "GO:0008150";
    static final String CELLULAR_COMPONENT_STOP_NODE = "GO:0005575";

    private static final EnumMap<OntologyType, Matcher> ONTOLOGY_TYPE_PATTERN_MAP = new EnumMap<>(OntologyType.class);
    private static final List<String> STOP_NODES =
            Arrays.asList(MOLECULAR_FUNCTION_STOP_NODE,
                          BIOLOGICAL_PROCESS_STOP_NODE,
                          CELLULAR_COMPONENT_STOP_NODE);

    static {
        ONTOLOGY_TYPE_PATTERN_MAP.put(OntologyType.GO, Pattern.compile("^GO:[0-9]+").matcher(""));
    }

    private final DirectedGraph<String, OntologyRelationship> ontology;
    private final Map<String, Set<OntologyRelationship>> ancestorEdgesMap = new HashMap<>();
    private final EnumMap<OntologyType, Set<String>> typeToVertexMap;

    public OntologyGraph() {
        ontology = new DirectedMultigraph<>(new ClassBasedEdgeFactory<>(OntologyRelationship.class));
        typeToVertexMap = new EnumMap<>(OntologyType.class);
    }

    public Set<OntologyRelationship> getEdges() {
        return ontology.edgeSet();
    }

    @Override
    public Set<String> getVertices(OntologyType ontologyType) {
        if (typeToVertexMap.containsKey(ontologyType)) {
            return typeToVertexMap.get(ontologyType);
        } else {
            throw new IllegalArgumentException("Terms of OntologyType "+ontologyType.name()+" were not stored, so cannot " +
                    "provide them. Please update OntologyGraph configuration");
        }
    }

    public Set<String> getVertices() {
        return ontology.vertexSet();
    }

    public void addRelationships(Collection<? extends OntologyRelationship> relationships) {
        Preconditions.checkArgument(relationships != null, "Relationships to add to the graph cannot be null");

        // populate graph with edges, whilst recording the vertices
        relationships.forEach(
                oEdge -> {
                    if (!ontology.containsVertex(oEdge.child)) {
                        ontology.addVertex(oEdge.child);
                        categoriseVertexIfRequired(oEdge.child);
                    }
                    if (!ontology.containsVertex(oEdge.parent)) {
                        ontology.addVertex(oEdge.parent);
                        categoriseVertexIfRequired(oEdge.parent);
                    }
                    ontology.addEdge(
                            oEdge.child,
                            oEdge.parent,
                            new OntologyRelationship(oEdge.child, oEdge.parent, oEdge.relationship));
                }
        );
    }

    @Override
    public List<List<OntologyRelationship>> paths(
            Set<String> startingVertices,
            Set<String> endingVertices,
            OntologyRelationType... relations) {

        Preconditions.checkArgument(notEmpty(startingVertices), "Starting vertices cannot be null/empty.");
        Preconditions.checkArgument(notEmpty(endingVertices), "Ending vertices cannot be null/empty.");
        errorIfAllStartingVerticesAreEndingVertices(startingVertices, endingVertices);

        Set<OntologyRelationType> relationsSet = createRelevantRelationsSet(relations);

        Set<String> startEndIntersection = new HashSet<>(startingVertices);
        startEndIntersection.retainAll(endingVertices);
        if (!startEndIntersection.isEmpty()) {
            throw new IllegalArgumentException("Cannot find paths intersecting start/end vertex sets:\n"
                    + "\tstarting vertices: " + startingVertices
                    + "\tending vertices: " + endingVertices);
        }

        List<List<OntologyRelationship>> interestingPaths = new ArrayList<>();

        List<OntologyRelationship> immediateSuccessors = successorEdges(startingVertices, endingVertices, relationsSet);
        if (immediateSuccessors.size() > 0) {
            immediateSuccessors.stream()
                    .map(Collections::singletonList)
                    .forEach(interestingPaths::add);
        }

        AllDirectedPaths<String, OntologyRelationship> allPaths = new AllDirectedPaths<>(ontology);
        List<GraphPath<String, OntologyRelationship>> v1v2Paths =
                allPaths.getAllPaths(startingVertices, endingVertices, true, null);
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

//    @Override
//    public List<List<OntologyRelationship>> paths(
//            Set<String> startingVertices,
//            Set<String> endingVertices,
//            OntologyRelationType... relations) {
//
//        Preconditions.checkArgument(notEmpty(startingVertices), "Starting vertices cannot be null/empty.");
//        Preconditions.checkArgument(notEmpty(endingVertices), "Ending vertices cannot be null/empty.");
//        errorIfAllStartingVerticesAreEndingVertices(startingVertices, endingVertices);
//
//        Set<OntologyRelationType> relationsSet = createRelevantRelationsSet(relations);
//        List<List<OntologyRelationship>> interestingPaths =
//                calculateInterestingPaths(startingVertices, endingVertices, relationsSet);
//
//        AllDirectedPaths<String, OntologyRelationship> allPaths = new AllDirectedPaths<>(ontology);
//        List<GraphPath<String, OntologyRelationship>> v1v2Paths =
//                allPaths.getAllPaths(startingVertices, endingVertices, true, null);
//        removeInvalidPaths(relationsSet, v1v2Paths);
//
//        interestingPaths.addAll(v1v2Paths.stream().map(GraphPath::getEdgeList).collect(Collectors.toList()));
//
//        return interestingPaths;
//    }

//    private void removeInvalidPaths(Set<OntologyRelationType> relationsSet,
//            List<GraphPath<String, OntologyRelationship>> v1v2Paths) {
//        List<GraphPath<String, OntologyRelationship>> invalidPaths = new ArrayList<>();
//
//        for (GraphPath<String, OntologyRelationship> path : v1v2Paths) {
//            if (!relationsSet
//                    .containsAll(path.getEdgeList().stream().map(e -> e.relationship).collect(Collectors.toList()))) {
//                invalidPaths.add(path);
//            }
//        }
//
//        v1v2Paths.removeAll(invalidPaths);
//    }

//    private List<List<OntologyRelationship>> calculateInterestingPaths(Set<String> startingVertices,
//            Set<String> endingVertices, Set<OntologyRelationType> relationsSet) {
//        List<List<OntologyRelationship>> interestingPaths = new ArrayList<>();
//        List<OntologyRelationship> immediateSuccessors = successorEdges(startingVertices, endingVertices, relationsSet);
//        if (immediateSuccessors.size() > 0) {
//            immediateSuccessors.stream()
//                    .map(Collections::singletonList)
//                    .forEach(interestingPaths::add);
//        }
//        return interestingPaths;
//    }

    @Override
    public List<String> ancestors(Set<String> baseVertices, OntologyRelationType... relations) {
        Preconditions.checkArgument(notEmpty(baseVertices), "Base vertices cannot be null/empty.");

        Set<String> ancestorsFound = new HashSet<>();
        if (relations.length == 0) {
            relations = new OntologyRelationType[1];
            relations[0] = OntologyRelationType.UNDEFINED;
        }

        for (String base : baseVertices) {
            for (OntologyRelationship relation : getAncestorEdges(base)) {
                if (relation.relationship.hasTransitiveType(relations)) {
                    ancestorsFound.add(relation.parent);
                }
            }
        }

        return new ArrayList<>(ancestorsFound);
    }

    @Override
    public Set<OntologyRelationship> parents(String baseVertex, OntologyRelationType... relations) {
        Preconditions.checkArgument(baseVertex != null && !baseVertex.trim().isEmpty(),
                                    "Base vertex cannot be null or empty");

        Set<OntologyRelationType> relationsSet = createRelevantRelationsSet(relations);
        return ontology.outgoingEdgesOf(baseVertex).stream()
                       .filter(ontologyRelationship -> relationsSet.contains(ontologyRelationship.relationship))
                       .collect(toSet());
    }

    @Override
    public List<String> descendants(Set<String> topVertices, OntologyRelationType... relations) {
        Preconditions.checkArgument(notEmpty(topVertices), "Top vertices cannot be null/empty.");

        Set<String> descendantsFound = new HashSet<>();
        descendantsFound.addAll(topVertices); // as with ancestors, include itself (indicating IDENTITY relationship)

        Set<OntologyRelationType> relationsSet = createRelevantRelationsSet(relations);
        descendants(topVertices, descendantsFound, relationsSet);
        return new ArrayList<>(descendantsFound);
    }

    @Override
    public Set<OntologyRelationship> children(String topVertex, OntologyRelationType... relations) {
        Preconditions.checkArgument(topVertex != null && !topVertex.trim().isEmpty(),
                                    "Top vertex cannot be null or empty");

        Set<OntologyRelationType> relationsSet = createRelevantRelationsSet(relations);

        return ontology.incomingEdgesOf(topVertex).stream()
                       .filter(ontologyRelationship -> relationsSet.contains(ontologyRelationship.relationship))
                       .collect(toSet());
    }

    /**
     *  Calculate a sub-graph on the ontology using the specified starting and stopping vertices.
     * @param startVertices the base vertices which are the lowest level of the sub-graph
     * @param stopVertices the ending vertices beyond which ontology vertices and edges are not returned. If this
     * value is empty or null then the default stop nodes for the ontology are used.
     * @param relations a varargs value used to filter edges to the sub-graph. By omitting a {@code relation} value,
     * edges of all relation types will be returned. Valid relationship list is ontology dependent and therefore must be
     * supplied.  We can't use the full list as there maybe edges with relationships that should not be displayed in the
     * sub-graph.
     * @return sub-graph
     */
    @Override
    public AncestorGraph<String> subGraph(Set<String> startVertices, Set<String> stopVertices,
            OntologyRelationType... relations) {
        Preconditions.checkArgument(notEmpty(startVertices), "Starting vertices cannot be null/empty.");
        Preconditions.checkArgument(Objects.nonNull(relations) && relations.length > 0, "Relations cannot be null");
        Queue<String> targetVertices = buildTargetVertices(startVertices);
        stopVertices.addAll(STOP_NODES);
        OntologyRelationType[] targetRelations = OntologyRelationType.relevantRelations(relations);
        AncestorGraphRequest request = new AncestorGraphRequest(targetVertices, stopVertices, targetRelations);
        return populateAncestorGraphForRequest(request);
    }


    private Queue<String> buildTargetVertices(Set<String> baseVertices) {
        Queue<String> targetVertices = new LinkedList<>();
        baseVertices.stream()
                .filter(ontology::containsVertex)
                .forEach(targetVertices::add);
        return targetVertices;
    }

     private AncestorGraph populateAncestorGraphForRequest(AncestorGraphRequest request) {
         AncestorGraph<String> ancestorGraph = AncestorGraph.newAncestorGraphString();

         while (!request.targetVertices.isEmpty() ){
             String target = request.targetVertices.poll();
             if (Objects.nonNull(target)) {
                 if (ancestorGraph.vertices.add(target) && !request.stopVertices.contains(target)) {
                     try {
                         Set<OntologyRelationship> parents = this.parents(target, request.targetRelations);
                         addParentsToWorkQueue(request, parents);
                         ancestorGraph.edges.addAll(mapOntologyRelationshipsToAncestorEdges(parents));

                     } catch (Exception e) {
                         LOGGER.error("SubGraphCalculator#calculateGraph looked up parents for " + target + " but " +
                                              "received exception ", e);
                     }
                 }
             }
         }
        return ancestorGraph;
    }

    private static Set<AncestorEdge> mapOntologyRelationshipsToAncestorEdges(Set<OntologyRelationship> parents) {
        Set<AncestorEdge> edgeSet = new HashSet<>();
        parents.stream()
               .map(or -> new AncestorEdge(or.child, or.relationship.getLongName(), or.parent))
               .forEach(edgeSet::add);
        return edgeSet;
    }

    private static void addParentsToWorkQueue(AncestorGraphRequest request, Set<OntologyRelationship> parents) {
        parents.stream()
               .map(p -> p.parent)
               .forEach(request.targetVertices::add);
    }

    @Override
    public BitSet getAncestorsBitSet(String vertex, List<String> range, OntologyRelationType... requestedRelations) {
        BitSet results = new BitSet();
        Set<String> filteredAncestors = getFilteredAncestors(vertex, requestedRelations);
        for (int i = 0; i < range.size(); i++) {
            if (filteredAncestors.contains(range.get(i))) {
                results.set(i);
            }
        }

        return results;
    }

    @Override
    public int hashCode() {
        int result = ontology != null ? ontology.hashCode() : 0;
        result = 31 * result + (ancestorEdgesMap != null ? ancestorEdgesMap.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
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

    private static String getOppositeVertex(Graph<String, OntologyRelationship> graph, OntologyRelationship edge,
            String vertex) {
        String source = graph.getEdgeSource(edge);
        String target = graph.getEdgeTarget(edge);
        if (vertex.equals(source)) {
            return target;
        } else if (vertex.equals(target)) {
            return source;
        } else {
            throw new IllegalArgumentException(
                    "No such graph vertex: " + vertex);
        }
    }



    private void errorIfAllStartingVerticesAreEndingVertices(Set<String> baseVertices, Set<String> topVertices) {
        Set<String> startEndIntersection = new HashSet<>(baseVertices);
        startEndIntersection.retainAll(topVertices);

        if (!startEndIntersection.isEmpty()) {
            throw new IllegalArgumentException("Cannot find paths intersecting start/end vertex sets:\n"
                    + "\tstarting vertices: " + baseVertices
                    + "\tending vertices: " + topVertices);
        }
    }

    /**
     * Given a vertex in the ontology graph, store it in an explicit vertex set if matches one of the regular
     * expressions in {@link #ONTOLOGY_TYPE_PATTERN_MAP}.
     *
     * @param vertex the vertex to possibly categorise and store
     */
    private void categoriseVertexIfRequired(String vertex) {
        for (Map.Entry<OntologyType, Matcher> entry : ONTOLOGY_TYPE_PATTERN_MAP.entrySet()) {
            Matcher ontologyTypeMatcher = entry.getValue().reset(vertex);
            if (ontologyTypeMatcher.matches()) {
                typeToVertexMap
                        .computeIfAbsent(entry.getKey(), key -> new HashSet<>())
                        .add(vertex);
            }
        }
    }

    private boolean isNullOrEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    private boolean notEmpty(Collection<?> collection) {
        return collection != null && !collection.isEmpty();
    }

    private Set<OntologyRelationship> getAncestorEdges(String vertex) {
        if (!ancestorEdgesMap.containsKey(vertex)) {
            Set<OntologyRelationship> ancestorEdgesOfV = new HashSet<>();

            ancestorEdgesOfV.add(new OntologyRelationship(vertex, vertex, OntologyRelationType.IDENTITY));

            if (isNotStopNode(vertex)) {
                for (OntologyRelationship successorEdge : ontology.outgoingEdgesOf(vertex)) {
                    for (OntologyRelationship grandparentSuccessorEdge : getAncestorEdges(successorEdge.parent)) {
                        OntologyRelationship combinedRelationship =
                                OntologyRelationship.combineRelationships(successorEdge, grandparentSuccessorEdge);
                        if (combinedRelationship.relationship != OntologyRelationType.UNDEFINED) {
                            ancestorEdgesOfV.add(combinedRelationship);
                        }
                    }
                }
            }

            ancestorEdgesMap.put(vertex, ancestorEdgesOfV);
        }
        return ancestorEdgesMap.get(vertex);
    }

    /**
     * <p>
     * Finds the outgoing, i.e., successor, edges originating from a {@link Collection} of starting vertices,
     * whose target vertices must be in a specified {@link Collection} of vertices, and whose target vertices
     * are reached by a specified {@link Set} of {@link OntologyRelationType}s.
     * <p>
     * Note that in this context, an edge is the formal name of an {@link OntologyRelationship}.
     * @param fromVertices the starting vertices
     * @param toVertices the target vertices
     * @param relations the edge labels over which starting vertices can reach target vertices
     * @return the {@link List} of {@link OntologyRelationship} instances defining the edges between {@code
     * fromVertices}
     *         to {@code toVertices} via {@code relations}
     */
    private List<OntologyRelationship> successorEdges(
            Collection<String> fromVertices,
            Collection<String> toVertices,
            Set<OntologyRelationType> relations) {
        List<OntologyRelationship> successors = new ArrayList<>();

        fromVertices.forEach(
                from ->
                        ontology.outgoingEdgesOf(from).stream()
                                .filter(e -> relations.contains(e.relationship) && toVertices.contains(e.parent))
                                .forEach(successors::add)
        );

        return successors;
    }

    private HashSet<OntologyRelationType> createRelevantRelationsSet(OntologyRelationType[] relations) {
        return new HashSet<>(Arrays.asList(OntologyRelationType.relevantRelations(relations)));
    }

    private Set<String> getFilteredAncestors(String vertex, OntologyRelationType... requestedRelations) {
        OntologyRelationType[] relations;
        if (requestedRelations.length == 0) {
            relations = new OntologyRelationType[]{OntologyRelationType.UNDEFINED};
        } else {
            relations = requestedRelations;
        }

        Set<String> results = new HashSet<>();
        for (OntologyRelationship ancestorRel : getAncestorEdges(vertex)) {
            if (ancestorRel.relationship.hasTransitiveType(relations)) {
                results.add(ancestorRel.parent);
            }
        }

        return results;
    }

    private boolean isNotStopNode(String id) {
        return !STOP_NODES.contains(id);
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

    private void descendants(Set<String> topVertices, Set<String> currentDescendants, Set<OntologyRelationType>
            relations) {
        for (String top : topVertices) {
            Set<String> descendants = getRelatives(top, ontology.incomingEdgesOf(top), relations);

            currentDescendants.addAll(descendants);
            descendants(descendants, currentDescendants, relations);
        }
    }
}
