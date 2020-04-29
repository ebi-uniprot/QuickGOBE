package uk.ac.ebi.quickgo.graphics.model;

import uk.ac.ebi.quickgo.graphics.ontology.GraphImage;

import java.util.ArrayList;
import java.util.List;

/**
 * Coordinate information (in pixels) DTO corresponding to a {@link GraphImage} and its contents.
 *
 * Created 06/10/16
 * @author Edd
 */
public class GraphImageLayout {
    public GraphImageLayout() {
        legendPositions = new ArrayList<>();
        nodePositions = new ArrayList<>();
    }

    public final List<LegendPosition> legendPositions;
    public int imageWidth;
    public int imageHeight;
    public String title;
    public final List<NodePosition> nodePositions;

    public static class LegendPosition {
        public int left;
        public int right;
        public int top;
        public int bottom;

        public int height;
        public int width;
        public int xCentre;
        public int yCentre;
    }

    public static class NodePosition {
        public String id;
        public String name;
        public int left;
        public int right;
        public int top;
        public int bottom;
    }
}
