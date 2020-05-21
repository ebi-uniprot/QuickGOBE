package uk.ac.ebi.quickgo.graphics.ontology;

import java.awt.*;
import java.util.Objects;

/**
 * Holds stylistic information for a Terms graph.
 * Refactored by Tony Wardell
 */
public class GraphPresentation {

    final static boolean FILL = true;
    private static final String fontName = "Lucida Sans";

    //default show Information chart on right hand
    public static boolean defaultShowKey = true;
    //default box will have header id
    public static boolean defaultShowTermIds = true;
    public static int defaultWidth = 85;
    public static int defaultHeight = 55;
    public static boolean defaultShowSlimColours = false;
    public static boolean defaultShowChildren = false;
    public static int defaultFontSize = 11;
    //changeable
    //box header which contain id
    final boolean termIds;
    //Information chart on right hand key
    public final boolean key;
    //Show Slim Colours
    final boolean subsetColours;
    final boolean showChildren;
    public final int width;
    public final int height;
    final int fontSize;

    final Font font;
    final Font labelFont;
    final Font infoFont;
    final Font errorFont;

    private GraphPresentation(boolean termIds, boolean key, boolean subsetColours, boolean showChildren, int width,
            int height, int fontSize) {
        this.termIds = termIds;
        this.key = key;
        this.subsetColours = subsetColours;
        this.showChildren = showChildren;
        this.width = width;
        this.height = height;
        this.fontSize = fontSize;

        this.font = new Font(fontName, Font.PLAIN, fontSize);
        this.labelFont = new Font(fontName, Font.PLAIN, fontSize - relativeSize(1));
        this.infoFont = new Font(fontName, Font.PLAIN, fontSize - relativeSize(2));
        this.errorFont = new Font(fontName, Font.PLAIN, fontSize + relativeSize(5));
    }

    public static class Builder {
        private boolean showIDs = defaultShowTermIds;
        private boolean showKey = defaultShowKey;
        private boolean showSlimColours = defaultShowSlimColours;
        private boolean showChildren = defaultShowChildren;
        private int width = defaultWidth;
        private int height = defaultHeight;
        private int fontSize = defaultFontSize;

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

        public Builder fontSize(int fontSize) {
            this.fontSize = fontSize;
            return this;
        }

        public GraphPresentation build() {
            return new GraphPresentation(
                    this.showIDs,
                    this.showKey,
                    this.showSlimColours,
                    this.showChildren,
                    this.width,
                    this.height,
                    this.fontSize);
        }
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GraphPresentation that = (GraphPresentation) o;
        return termIds == that.termIds &&
                key == that.key &&
                subsetColours == that.subsetColours &&
                showChildren == that.showChildren &&
                width == that.width &&
                height == that.height;
    }

    @Override public int hashCode() {

        return Objects.hash(termIds, key, subsetColours, showChildren, width, height);
    }

    @Override public String toString() {
        return "GraphPresentation{" +
                "termIds=" + termIds +
                ", key=" + key +
                ", subsetColours=" + subsetColours +
                ", showChildren=" + showChildren +
                ", width=" + width +
                ", height=" + height +
                '}';
    }

    int getIdHeaderFontSize(){
        return this.fontSize + 1;
    }

    Stroke getBoxBorder(){
        return new BasicStroke(getBoxBorderSize());
    }

    float getBoxBorderSize(){
        int defaultBorderForDefaultSize = 1;
        return relativeFontSizeIncrease() * defaultBorderForDefaultSize;
    }

    float relativeFontSizeIncrease(){
        return (float) fontSize / defaultFontSize;
    }

    Stroke arrowLineRelativeFont(Stroke basicStroke){
        BasicStroke s = (BasicStroke) basicStroke;
        float widthRelativeToFont = s.getLineWidth() * relativeFontSizeIncrease();
        return new BasicStroke(widthRelativeToFont, s.getEndCap(), s.getLineJoin(), s.getMiterLimit(), s.getDashArray(), s.getDashPhase());
    }

    float getArrowHeadStyleSize(){
        int defaultWidth = 2;
        float proportionalIncrease = relativeFontSizeIncrease() <= 1 ? 0 : relativeFontSizeIncrease();
        return (relativeFontSizeIncrease() * defaultWidth) + proportionalIncrease;
    }

    Stroke getArrowHeadStyle(){
        return new BasicStroke(getArrowHeadStyleSize());
    }

    int relativeSize(int font){
        return (int) relativeFontSizeIncrease() * font;
    }

    int getSlimBoxWidth(){
        int slimNameCanBeGreaterThanGoId = 55;
        int fontIncreaseShouldIncreaseWidth = relativeFontSizeIncrease() <= 1 ? 0 : (int) (relativeFontSizeIncrease() / 2 * slimNameCanBeGreaterThanGoId);
        return width + slimNameCanBeGreaterThanGoId + fontIncreaseShouldIncreaseWidth;
    }
}
