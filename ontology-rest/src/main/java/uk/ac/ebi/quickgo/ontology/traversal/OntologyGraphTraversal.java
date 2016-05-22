package uk.ac.ebi.quickgo.ontology.traversal;

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
     * Find the list of paths between two vertices in a graph, navigable via
     * a specified set of relations.
     *
     * @param v1 the start vertex from which returned paths must start
     * @param v2 the end vertex from which returned paths end
     * @param relations a varargs value containing the relationships over which paths can only travel.
     *                  By omitting a {@code relation} value, all paths will be returned.
     * @return a list of paths, ordered shorted first, from {@code v1} to {@code v2} via {@code relations}
     */
    List<List<OntologyGraph.StringEdge>> paths(String v1, String v2, OntologyRelation... relations);

    /**
     * Find the set of ancestor vertices reachable from a base vertex, navigable via a specified
     * set of relations.
     *
     * @param base the base vertex whose ancestors one is interested in
     * @param relations a varargs value containing the relationships over which paths can only travel.
     *                  By omitting a {@code relation} value, all paths will be returned.
     * @return the set of ancestor vertices.
     */
    Set<String> ancestors(String base, OntologyRelation... relations);

    /**
     * Find the set of descendant vertices reachable from a top vertex, navigable via a specified
     * set of relations.
     *
     * @param top the top-most vertex whose descendants one is interested in
     * @param relations a varargs value containing the relationships over which paths can only travel.
     *                  By omitting a {@code relation} value, all paths will be returned.
     * @return the set of descendant vertices.
     */
    Set<String> descendants(String top, OntologyRelation... relations);
}
