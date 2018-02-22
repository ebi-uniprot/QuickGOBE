package uk.ac.ebi.quickgo.graphics.ontology;

import java.awt.*;

public class GraphPresentation {
    private static final String paramFontName = "fontName";
    private static final String paramFontSize = "fontSize";
    private static final String paramFill = "fill";
    private static final String paramShowIDs = "ids";
    private static final String paramShowKey = "key";
    private static final String paramShowSubsets = "subsets";
    private static final String paramShowChildren = "children";
    private static final String paramWidth = "width";
    private static final String paramHeight = "height";

    private static final String cookieKeyPrefix = "c$";

    private Font font;

    public String fontName = "Lucida Sans";
    public int fontSize = 11;
    public boolean fill = true;
    public boolean termIds = true;
    public boolean key = true;
    public boolean subsetColours = true;
    public boolean showChildren = false;
    public int width = 85;
    public int height = 55;

    Font getFont() {
        if (font == null) {
            font = new Font(fontName, Font.PLAIN, fontSize);
        }
        return font;
    }
}
