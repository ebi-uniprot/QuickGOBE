package uk.ac.ebi.quickgo.graphics.ontology;

import java.awt.geom.GeneralPath;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Layout a hierarchical graph.
 * <p/>
 * Layout similar to the algorithm implemented by
 * <a href="http://www.graphviz.org/">GraphViz, AT&T laboratories</a>.
 * </p>
 * <pre>
 * Usage:
 * Graph g=....
 * GraphLayout layout = new GraphLayout(g, GraphLayout.PARENT_TOP);
 * layout.layout();
 * ...layout.getWidth()...layout.getHeight()...
 * </pre>
 * <p/>
 * See SimpleGraphDraw.java for an example.
 * </p>
 * <p/>
 * The following documentation assumes orientation is PARENT_TOP,
 * in which case the parent nodes are at the top and a level is a horizontal group of nodes.
 * Normally the magic field values should be left at their default values.
 * </p>
 */
public class GraphLayout<N extends IPositionableNode, E extends IRoutableEdge<N>> {
    // Public interface

    public enum Orientation {
        TOP, LEFT, BOTTOM, RIGHT
    }

    private final static Logger logger = LoggerFactory.getLogger(GraphLayout.class);

    /**
     * Prepare for layout
     *
     * @param graph       Graph containing IPositionableNode and IRoutableEdge objects; will not be modified.
     * @param orientation indicates which side of the graph the parents will be placed.
     */

    GraphLayout(IGraph<N, E> graph, Orientation orientation) {
        this.orientation = orientation;

        for (N n : graph.getNodes()) {
            HierarchicalNode hnode = (orientation == Orientation.TOP || orientation == Orientation.BOTTOM) ?
                    new HierarchicalNode(n, n.getHeight(), n.getWidth()) :
                    new HierarchicalNode(n, n.getWidth(), n.getHeight());
            nodeMap.put(n, hnode);
            hierarchicalGraph.nodes.add(hnode);
        }

        for (E e : graph.getEdges()) {
            HierarchicalNode parent = nodeMap.get(e.getParent());
            HierarchicalNode child = nodeMap.get(e.getChild());

            if (parent == null || child == null) {
                logger.info("Failed edge " + e.getParent() + (parent == null ? ":NOT FOUND " : " ") + e.getChild() +
                        (child == null ? ":NOT FOUND " : " "));
            } else {
                originalEdges.add(new EdgeMapping(e, parent, child));
            }
        }
    }

    /**
     * Compute layout.
     * This method finally calls setLocation on all the nodes and
     * setRoute on all the edges.
     */
    void layout() {
        findLevels();

        rationalise();

        for (Level l : levels) {
            l.calcInitialPositions();
        }

        orderNodesInLevels();

        calcLevelLocations();

        int minStart = Integer.MAX_VALUE;

        for (Level l : levels) {
            minStart = Math.min(minStart, l.getStart());
        }

        for (Level l : levels) {
            l.shiftLeft(minStart);
        }

        for (Level l : levels) {
            withinLevelSize = Math.max(withinLevelSize, l.getWidth());
        }

        storeLayoutNodes();

        storeLayoutEdges();
    }

    /**
     * After calling layout() call getWidth and getHeight
     * <br/>
     * All nodes will be in this bounding box.
     * <br/>
     * 0&lt;node.x+/-node.getWidth/2&lt;layout.getWidth
     * <br/>
     * Noting that x and y are the centres of the nodes.
     * All edge routes will also be in the bounding box.
     *
     * @return width of layout
     */

    public int getWidth() {
        return horizontalMargin * 2 +
                (orientation == Orientation.TOP || orientation == Orientation.BOTTOM ? withinLevelSize :
                         betweenLevelSize);
    }

    /**
     * See getWidth()
     * <p/>
     * All nodes will be:
     * <p/>
     * 0&lt;node.y+/-node.getHeight/2&lt;layout.getHeight
     *
     * @return height of layout
     */
    public int getHeight() {
        return verticalMargin * 2 +
                (orientation == Orientation.TOP || orientation == Orientation.BOTTOM ? betweenLevelSize :
                         withinLevelSize);
    }
    //
    // Magic constants.
    // WARNING! Playing with these constants will mess with your head.
    //

    /**
     * Ratio of maximum edge vertical distance to horizontal distance
     */
    int edgeLengthHeightRatio = 3;
    /**
     * Number of passes up and down the levels to attempt to optimise node positions
     */
    private final int reorderIterations = 25;
    /**
     * Minimum gap between levels
     */
    private int minLevelGap = 10;
    /**
     * Levels may be split if they have more than this number of nodes
     */
    public int maxLevelSize = 100;
    /**
     * Edges running though levels will be allocated this much horizontal space
     */
    private int insertedEdgeWidth = 20;
    /**
     * Minimum gap between nodes within a level
     */
    private int withinLevelGap = 20;
    /**
     * Extra gap between lines and nodes in a level
     */
    public int edgeRouteGap = 10;
    /**
     * Extra gap between lines and nodes in a level
     */
    private int betweenLevelExtraGap = 1;
    /**
     * Horizontal margin
     */
    int horizontalMargin = 2;
    /**
     * Vertical margin
     */
    int verticalMargin = 2;
    //

    // Internal implementation

    // fields

    private GenericGraph<HierarchicalNode, HierarchicalEdge> hierarchicalGraph =
            new GenericGraph<HierarchicalNode, HierarchicalEdge>();
    private Orientation orientation;
    private ArrayList<Level> levels = new ArrayList<>();
    private List<EdgeMapping> originalEdges = new ArrayList<>();
    private int betweenLevelSize;
    private int withinLevelSize;
    private final Map<N, HierarchicalNode> nodeMap = new HashMap<>();

    // classes

    private class HierarchicalNode implements INode {
        // Underlying node will be null for inserted nodes
        IPositionableNode underlying;

        /*
                HierarchicalNode[] above;
                HierarchicalNode[] below;
        */
        ArrayList<HierarchicalNode> above;
        ArrayList<HierarchicalNode> below;

        /**
         * Minimum level on which this node could be located.
         * Used only by findLevel
         */
        int minLevelNumber = -1;

        /**
         * Level on which node is located
         */
        Level level;

        /**
         * Location within level
         */
        int location;

        /**
         * Size of node expressed wrt level
         */
        int withinLevelSize;

        /**
         * Size of node expressed wrt level
         */
        int betweenLevelSize;

        /**
         * Create nodedata for node
         *
         * @param node             Original node
         * @param betweenLevelSize size between levels (x or y depending on orientation)
         * @param withinLevelSize  size within levels
         */
        HierarchicalNode(IPositionableNode node, int betweenLevelSize, int withinLevelSize) {
            underlying = node;
            this.betweenLevelSize = betweenLevelSize;
            this.withinLevelSize = withinLevelSize;
        }

        HierarchicalNode(int betweenLevelSize, int withinLevelSize) {
            this.betweenLevelSize = betweenLevelSize;
            this.withinLevelSize = withinLevelSize;
        }

        Set<HierarchicalNode> getRealParents() {
            Set<HierarchicalNode> parents = new HashSet<>();
            for (EdgeMapping e : originalEdges) {
                if (e.child == this) {
                    parents.add(e.parent);
                }
            }
            return parents;
        }

        Set<HierarchicalNode> getRealChildren() {
            Set<HierarchicalNode> children = new HashSet<>();
            for (EdgeMapping e : originalEdges) {
                if (e.parent == this) {
                    children.add(e.child);
                }
            }
            return children;
        }

        private int findLevel() {
            if (minLevelNumber != -1) {
                return minLevelNumber;
            }

            int maxParentLevel = -1;

            for (HierarchicalNode parent : getRealParents()) {
                maxParentLevel = Math.max(parent.findLevel(), maxParentLevel);
            }

            return minLevelNumber = maxParentLevel + 1;
        }

        private int assignLevel() {
            int minChildLevel = Integer.MAX_VALUE;

            for (HierarchicalNode child : getRealChildren()) {
                minChildLevel = Math.min(child.findLevel(), minChildLevel);
            }

            if (minChildLevel == Integer.MAX_VALUE) {
                minChildLevel = minLevelNumber + 1;
            }

            int levelNumber = (minLevelNumber + minChildLevel) / 2;

            while (levelNumber >= levels.size()) {
                levels.add(new Level(hierarchicalGraph, levels.size()));
            }

            level = levels.get(levelNumber);

            level.nodes.add(this);

            return levelNumber;
        }
    }

    private class EdgeMapping {
        E underlying = null;
        List<HierarchicalEdge> componentEdges = new ArrayList<>();
        private HierarchicalNode parent;
        private HierarchicalNode child;

        EdgeMapping(E underlying, HierarchicalNode parent, HierarchicalNode child) {
            this.underlying = underlying;

            this.parent = parent;
            this.child = child;
        }
    }

    private class HierarchicalEdge implements IEdge<HierarchicalNode>, Comparable<HierarchicalEdge> {
        HierarchicalNode parent, child;

        public HierarchicalNode getParent() {
            return parent;
        }

        public HierarchicalNode getChild() {
            return child;
        }

        HierarchicalEdge(HierarchicalNode parent, HierarchicalNode child) {
            this.parent = parent;
            this.child = child;
        }

        private int direction() {
            return parent.location - child.location;
        }

        private int centre() {
            return (parent.location + child.location) / 2;
        }

        public int compareTo(HierarchicalEdge e) {
            if (direction() > 0 && e.direction() > 0) {
                return centre() - e.centre();
            } else if (direction() < 0 && e.direction() < 0) {
                return e.centre() - centre();
            } else {
                return 0;
            }
        }
    }

    private class Level {
        int levelNumber;
        int location, height;
        List<HierarchicalNode> nodes = new ArrayList<>();
        GenericGraph<HierarchicalNode, HierarchicalEdge> hierarchicalGraph;

        Level(GenericGraph<HierarchicalNode, HierarchicalEdge> hierarchicalGraph, int levelNumber) {
            this.hierarchicalGraph = hierarchicalGraph;
            this.levelNumber = levelNumber;
        }

        void removeOverlaps() {
            while (true) {
                Collections.sort(nodes, nodeLayoutComparator);

                boolean foundOverlap = false;
                for (int i = 1; i < nodes.size(); i++) {
                    HierarchicalNode a = nodes.get(i - 1);
                    HierarchicalNode b = nodes.get(i);

                    int overlap =
                            minLevelGap + (a.location + a.withinLevelSize / 2) - (b.location - b.withinLevelSize / 2);
                    if (overlap > 0) {
                        foundOverlap = true;
                        a.location = a.location - overlap / 2 - 1;
                        b.location = b.location + overlap / 2 + 1;
                    }
                }

                if (!foundOverlap) {
                    break;
                }
            }
        }

        void reorder(boolean down) {
            reorderAveragePosition(down);
            removeOverlaps();
        }

        private void reorderAveragePosition(boolean down) {
            for (HierarchicalNode node : nodes) {
                double total = 0;
                int connected = 0;

                if (node.above != null && down) {
                    for (HierarchicalNode cf : node.above) {
                        connected++;
                        total += cf.location;
                    }
                }

                if (node.below != null && !down) {
                    for (HierarchicalNode cf : node.below) {
                        connected++;
                        total += cf.location;
                    }
                }

                if (connected == 0) {
                    continue;
                } else {
                    total /= connected;
                }

                node.location = (int) total;
            }
        }

        void calcInitialPositions() {
            int width = 0;
            for (HierarchicalNode node : nodes) {
                node.location = width + node.withinLevelSize / 2;
                width += node.withinLevelSize + withinLevelGap;
            }
        }

        void shiftLeft(int delta) {
            for (HierarchicalNode node : nodes) {
                node.location -= delta;
            }
        }

        void getHeight() {
            int maxHeight = 0;

            for (HierarchicalNode node : nodes) {
                maxHeight = Math.max(maxHeight, node.betweenLevelSize);
            }

            this.height = maxHeight + (betweenLevelExtraGap * 2);
        }

        void setLocation(int location) {
            this.location = location;
        }

        int getWidth() {
            final HierarchicalNode nd = nodes.get(nodes.size() - 1);
            return nd.location + nd.withinLevelSize / 2;
        }

        int getStart() {
            final HierarchicalNode nd = nodes.get(0);
            return nd.location - nd.withinLevelSize / 2;
        }

        void attach(Level above, Level below) {
            for (int j = 0; j < nodes.size(); j++) {
                HierarchicalNode nj = nodes.get(j);

                if (above != null) {
                    nj.above = new ArrayList<>();
                    for (HierarchicalNode na : above.nodes) {
                        if (hierarchicalGraph.connected(na, nj)) {
                            nj.above.add(na);
                        }
                    }
                }

                if (below != null) {
                    nj.below = new ArrayList<>();
                    for (HierarchicalNode na : below.nodes) {
                        if (hierarchicalGraph.connected(na, nj)) {
                            nj.below.add(na);
                        }
                    }
                }
            }
        }
    }

    private Comparator<HierarchicalNode> nodeLayoutComparator = new Comparator<HierarchicalNode>() {
        public int compare(HierarchicalNode h1, HierarchicalNode h2) {
            return h1.location - h2.location;
        }
    };

    // methods

    private void findLevels() {
        for (HierarchicalNode n : hierarchicalGraph.nodes) {
            n.findLevel();
        }
        for (HierarchicalNode n : hierarchicalGraph.nodes) {
            n.assignLevel();
        }
    }

    private void rationalise(EdgeMapping e, GenericGraph<HierarchicalNode, HierarchicalEdge> g) {
        int parentLevel = e.parent.level.levelNumber;
        int childLevel = e.child.level.levelNumber;

        HierarchicalNode a = e.parent;
        for (int i = parentLevel + 1; i <= childLevel; i++) {
            HierarchicalNode b;
            if (i == childLevel) {
                b = e.child;
            } else {
                b = new HierarchicalNode(-1, insertedEdgeWidth);
                b.level = levels.get(i);
                b.level.nodes.add(b);
            }
            HierarchicalEdge insertedEdge = new HierarchicalEdge(a, b);
            g.edges.add(insertedEdge);
            g.nodes.add(b);
            e.componentEdges.add(insertedEdge);

            a = b;
        }
    }

    private void rationalise() {
        for (EdgeMapping e : originalEdges) {
            rationalise(e, hierarchicalGraph);
        }

        int s = levels.size();

        for (int i = 0; i < s; i++) {
            Level p = (i == 0) ? null : levels.get(i - 1);
            Level l = levels.get(i);
            Level n = (i == s - 1) ? null : levels.get(i + 1);
            l.attach(p, n);
        }
    }

    private void orderNodesInLevels() {
        for (int j = 0; j < reorderIterations; j++) {
            int s = levels.size();

            for (int i = 0; i < s; i++) {
                Level l = levels.get(i);
                l.reorder(true);
            }

            for (int i = s - 1; i >= 0; i--) {
                Level l = levels.get(i);
                l.reorder(false);
            }
        }

        for (Level level : levels) {
            level.removeOverlaps();
        }
    }

    private void calcLevelLocations() {
        int height = -betweenLevelExtraGap;

        Level p = null;

        for (Level l : levels) {
            int maxLength = 0;

            // Calculate maximum edge length
            if (p != null) {
                for (HierarchicalNode n1 : l.nodes) {
                    for (HierarchicalNode n2 : p.nodes) {
                        if (hierarchicalGraph.connected(n1, n2)) {
                            maxLength = Math.max(maxLength, Math.abs(n1.location - n2.location));
                        }
                    }
                }
                height += Math.max(minLevelGap, maxLength / edgeLengthHeightRatio);
            }

            int maxHeight = 0;

            for (HierarchicalNode node : l.nodes) {
                maxHeight = Math.max(maxHeight, (node).betweenLevelSize);
            }

            maxHeight += betweenLevelExtraGap * 2;

            l.getHeight();

            height += l.height / 2;

            l.setLocation(height);

            height += l.height / 2;

            p = l;
        }

        betweenLevelSize = height - betweenLevelExtraGap;
    }

    private int x(int within, int between) {
        switch (orientation) {
            case LEFT:
                return horizontalMargin + between;
            case RIGHT:
                return horizontalMargin + betweenLevelSize - between;
            case TOP:
            case BOTTOM:
                return horizontalMargin + within;
        }
        return 0;
    }

    private int y(int within, int between) {
        switch (orientation) {
            case TOP:
                return verticalMargin + between;
            case BOTTOM:
                return verticalMargin + betweenLevelSize - between;
            case LEFT:
            case RIGHT:
                return verticalMargin + within;
        }
        return 0;
    }

    private void storeLayoutNodes() {
        for (HierarchicalNode n : hierarchicalGraph.nodes) {
            if (n.underlying != null) {
                n.underlying.setLocation(x(n.location, n.level.location), y(n.location, n.level.location));
            }
        }
    }

    private void storeLayoutEdges() {
        for (EdgeMapping e : originalEdges) {
            GeneralPath shape = new GeneralPath();
            boolean first = true;

            for (HierarchicalEdge edge : e.componentEdges) {

                HierarchicalNode parent = edge.getParent();
                HierarchicalNode child = edge.getChild();

                int parentLocation = parent.location;
                int childLocation = child.location;

                int levelParent = parent.level.location + parent.level.height / 2;
                int levelChild = child.level.location - child.level.height / 2;

                int levelCentre = (levelParent + levelChild) / 2;

                int nodeParent = parent.level.location + parent.betweenLevelSize / 2;
                int nodeChild = child.level.location - child.betweenLevelSize / 2;

                if (first) {
                    shape.moveTo(x(parentLocation, nodeParent), y(parentLocation, nodeParent));
                }
                shape.lineTo(x(parentLocation, levelParent), y(parentLocation, levelParent));

                shape.curveTo(x(parentLocation, levelCentre), y(parentLocation, levelCentre),
                        x(childLocation, levelCentre), y(childLocation, levelCentre),
                        x(childLocation, levelChild), y(childLocation, levelChild));

                shape.lineTo(x(childLocation, nodeChild), y(childLocation, nodeChild));
                first = false;
            }

            e.underlying.setRoute(shape);
        }
    }
}
