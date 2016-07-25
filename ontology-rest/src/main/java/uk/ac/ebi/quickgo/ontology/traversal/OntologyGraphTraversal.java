package uk.ac.ebi.quickgo.ontology.traversal;

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
     * Find the set of descendant vertices reachable from a top vertex, navigable via a specified
     * set of relations.
     *
     * @param topVertices a {@link Set} vertices whose descendants one is interested in
     * @param relations a varargs value containing the relationships over which paths can only travel.
     *                  By omitting a {@code relation} value, all paths will be returned.
     * @return the list of descendant vertices.
     */
    List<String> descendants(Set<String> topVertices, OntologyRelationType... relations);
}
