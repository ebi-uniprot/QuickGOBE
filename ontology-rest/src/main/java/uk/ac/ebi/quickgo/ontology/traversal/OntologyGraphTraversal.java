package uk.ac.ebi.quickgo.ontology.traversal;

import uk.ac.ebi.quickgo.ontology.model.AncestorGraph;
import uk.ac.ebi.quickgo.ontology.model.OntologyRelationType;
import uk.ac.ebi.quickgo.ontology.model.OntologyRelationship;

import java.util.List;
import java.util.Set;

/**
 * This class defines a contract for typical ontology graph traversal operations.
 *
 * Created 20/05/16
 * @author Edd
 */
public interface OntologyGraphTraversal {
    /**
     * Find the list of paths between two sets of vertices in a graph, navigable via
     * a specified set of relations.
     *
     * @param startingVertices the starting vertices from which returned paths must start
     * @param endingVertices the ending vertices from which returned paths end
     * @param relations a varargs value containing the relationships over which paths can only travel.
     *                  By omitting a {@code relation} value, all paths will be returned.
     * @return a list of paths from {@code child} to {@code parent} via {@code relations}
     */
    List<List<OntologyRelationship>> paths(
            Set<String> startingVertices,
            Set<String> endingVertices,
            OntologyRelationType... relations);

    /**
     * Find the set of ancestor vertices reachable from a base vertex, navigable via a specified
     * set of relations.
     *
     * @param baseVertices a {@link Set} of vertices whose ancestors one is interested in
     * @param relations a varargs value containing the relationships over which paths can only travel.
     *                  By omitting a {@code relation} value, all paths will be returned.
     * @return the list of ancestor vertices.
     */
    List<String> ancestors(Set<String> baseVertices, OntologyRelationType... relations);

    /**
     * Finds a set of all the parent vertices of the {@code baseVertex} that fulfill the provided {@code relations}.
     * <p/>
     * <b>Note:</b> If no relations are provided, it is assumed that all relations will be searched for.
     *
     * @param baseVertex the vertex whose parents are to be retrieved
     * @param relations the relations that a parent vertex has with its child
     * @return a set of {@link OntologyRelationship} relationships between the {@code baseVertex} and the retrieved
     * parent vertices
     * @throws IllegalArgumentException if the {@code baseVertex} is null, empty or does not exist in the graph
     */
    Set<OntologyRelationship> parents(String baseVertex, OntologyRelationType... relations);

    /**
     * Find the set of descendant vertices reachable from a top vertex, navigable via a specified
     * set of relations.
     *
     * @param topVertices a {@link Set} vertices whose descendants one is interested in
     * @param relations a varargs value containing the relationships over which paths can only travel.
     *                  By omitting a {@code relation} value, all paths will be returned.
     * @return the list of descendant vertices.
     */
    List<String> descendants(Set<String> topVertices, OntologyRelationType... relations);

    /**
     * Finds a set of all the child vertices of the {@code topVertex} that fulfill the provided {@code relations}.
     * <p/>
     * <b>Note:</b> If no relations are provided, it is assumed that all relations will be searched for.
     *
     * @param topVertex the vertex whose children are to be retrieved
     * @param relations the relations that a child vertex has with its parent
     * @return a set of {@link OntologyRelationship} relationships between the {@code topVertex} and the retrieved
     * child vertices
     * @throws IllegalArgumentException if the {@code topVertex} is null, empty or does not exist in the graph
     */
    Set<OntologyRelationship> children(String topVertex, OntologyRelationType... relations);

    /**
     * Find the sub-graph between two sets of vertices in a graph, navigable via
     * a specified set of relations.
     *
     * @param baseVertices the base vertices which are the lowest level of the sub-graph
     * @param stopVertices the ending vertices beyond which Ontology vertices and edges are not returned. If this
     * value is empty or null then the default stop nodes for the ontology are used.
     * @param relations a varargs value used to filter edges to the sub-graph. By omitting a {@code relation} value,
     * edges of all relation types will be returned.
     * @return a graph from {@code child} to {@code parent} via {@code relations}
     */
    AncestorGraph<String> subGraph(Set<String> baseVertices, Set<String> stopVertices,
            OntologyRelationType... relations);
}
