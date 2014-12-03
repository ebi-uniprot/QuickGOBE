package uk.ac.ebi.quickgo.graphics;

/**
 * General graph edge
 */
public interface IEdge<N extends INode> {
    /**
     * Parent
     * @return parent
     */
    N getParent();

    /**
     * Child
     * @return child
     */
    N getChild();
}
