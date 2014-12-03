package uk.ac.ebi.quickgo.graphics;

public interface IPositionableNode extends INode {
    /**
     * Get node width
     *
     * @return Width
     */
    int getWidth();

    /**
     * Set node width
     * 
     * @params width
     */
    void setWidth(int width);
    
    /**
     * Get node height
     * @return height
     */
    int getHeight();

    /**
     * Set node height
     * 
     * @params height
     */
    void setHeight(int height);
    
    /**
     * Set the location.
     * The layout algorithm assumes x and y will be the centre of the node.
     *
     * @param x Horizontal location
     * @param y Vertical location
     */
    void setLocation(int x, int y);
}
