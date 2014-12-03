package uk.ac.ebi.quickgo.graphics;

import java.util.Collection;

/**
 * interface that defines an abstract graph (i.e., a set of nodes connected by edges)
 */
public interface IGraph<N extends INode, E extends IEdge<N>> {
    Collection<N> getNodes();
    Collection<E> getEdges();
}
