package uk.ac.ebi.quickgo.graphics.ontology;

import java.awt.*;

/**
 * Interface defining an edge that can be routed
 */
public interface IRoutableEdge<N extends INode> extends IEdge<N> {
    /**
     * Set the shape of the path taken for the edge
     * @param route path
     */
    void setRoute(Shape route);
}
