package uk.ac.ebi.quickgo.graphics.ontology;

import java.awt.*;

/**
 * Holds stylistic information for a Terms graph.
 * Refactored by Tony Wardell
 */
public class GraphPresentation {

    public final static int fontSize = 11;
    public final static boolean FILL = true;
    private static final String fontName = "Arial";
    public static final Font FONT = new Font(fontName, Font.PLAIN, fontSize);
    static boolean defaultShowKey = true;
    static boolean defaultShowTermIds = true;
    static int defaultWidth = 85;
    static int defaultHeight = 55;
    static boolean defaultShowSlimColours = false;
    static boolean defaultShowChildren = false;
    //changeable
    public final boolean termIds;
    public final boolean key;
    public final boolean subsetColours;
    public final boolean showChildren;
    public final int width;
    public final int height;

    private GraphPresentation(boolean termIds, boolean key, boolean subsetColours, boolean showChildren, int width,
            int height) {
        this.termIds = termIds;
        this.key = key;
        this.subsetColours = subsetColours;
        this.showChildren = showChildren;
        this.width = width;
        this.height = height;
    }

    public static class Builder {
        private boolean showIDs = defaultShowTermIds;
        private boolean showKey = defaultShowKey;
        private boolean showSlimColours = defaultShowSlimColours;
        private boolean showChildren = defaultShowChildren;
        private int width = defaultWidth;
        private int height = defaultHeight;

        public Builder() {
        }

        public Builder showKey(boolean val) {
            this.showKey = val;
            return this;
        }

        public Builder showIDs(boolean val) {
            this.showIDs = val;
            return this;
        }

        public Builder showSlimColours(boolean val) {
            this.showSlimColours = val;
            return this;
        }

        public Builder showChildren(boolean val) {
            this.showChildren = val;
            return this;
        }

        public Builder termBoxWidth(int val) {
            this.width = val;
            return this;
        }

        public Builder termBoxHeight(int val) {
            this.height = val;
            return this;
        }

        public GraphPresentation build() {
            return new GraphPresentation(
                    this.showIDs,
                    this.showKey,
                    this.showSlimColours,
                    this.showChildren,
                    this.width,
                    this.height);
        }
    }
}
