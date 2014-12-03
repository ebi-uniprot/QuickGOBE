package uk.ac.ebi.quickgo.graphics;

import java.awt.Font;
import java.util.Map;

import uk.ac.ebi.quickgo.util.StringUtils;
import uk.ac.ebi.quickgo.web.servlet.QuickGOCookie;

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
	
	public String fontName = "Arial";
    public int fontSize = 11;
	public boolean fill = true;
    public boolean termIds = true;
	public boolean key = true;
    public boolean subsetColours = true;
	public boolean showChildren = false;
    public int width = 85;
	public int height = 55;
	
	public GraphPresentation() {
	}

	public GraphPresentation(QuickGOCookie c) {
		fontName = StringUtils.nvl(c.getParameter(cookieKeyPrefix + paramFontName), fontName);
		fontSize = StringUtils.parseInt(c.getParameter(cookieKeyPrefix + paramFontSize), fontSize);
		fill = StringUtils.parseBoolean(c.getParameter(cookieKeyPrefix + paramFill), fill);
		termIds = StringUtils.parseBoolean(c.getParameter(cookieKeyPrefix + paramShowIDs), termIds);
		key = StringUtils.parseBoolean(c.getParameter(cookieKeyPrefix + paramShowKey), key);
		subsetColours = StringUtils.parseBoolean(c.getParameter(cookieKeyPrefix + paramShowSubsets), subsetColours);
		showChildren = StringUtils.parseBoolean(c.getParameter(cookieKeyPrefix + paramShowChildren), showChildren);
		width = StringUtils.parseInt(c.getParameter(cookieKeyPrefix + paramWidth), width);
		height = StringUtils.parseInt(c.getParameter(cookieKeyPrefix + paramHeight), height);
	}

    public GraphPresentation(Map<String, String> parameters) {
	    initialise(parameters);
    }

	public void initialise(Map<String, String> parameters) {
		fontName = StringUtils.nvl(parameters.get(paramFontName), fontName);
		fontSize = StringUtils.parseInt(parameters.get(paramFontSize), fontSize);
		fill = StringUtils.parseBoolean(parameters.get(paramFill), fill);
		termIds = StringUtils.parseBoolean(parameters.get(paramShowIDs), termIds);
		key = StringUtils.parseBoolean(parameters.get(paramShowKey), key);
		subsetColours = StringUtils.parseBoolean(parameters.get(paramShowSubsets), subsetColours);
		showChildren = StringUtils.parseBoolean(parameters.get(paramShowChildren), showChildren);
		width = StringUtils.parseInt(parameters.get(paramWidth), width);
		height = StringUtils.parseInt(parameters.get(paramHeight), height);
	}

	public void saveSettings(QuickGOCookie c) {
		c.setParameter(cookieKeyPrefix + paramFontName, fontName);
		c.setParameter(cookieKeyPrefix + paramFontSize, Integer.toString(fontSize));
		c.setParameter(cookieKeyPrefix + paramFill, fill ? "true" : "false");
		c.setParameter(cookieKeyPrefix + paramShowIDs, termIds ? "true" : "false");
		c.setParameter(cookieKeyPrefix + paramShowKey, key ? "true" : "false");
		c.setParameter(cookieKeyPrefix + paramShowSubsets, subsetColours ? "true" : "false");
		c.setParameter(cookieKeyPrefix + paramShowChildren, showChildren ? "true" : "false");
		c.setParameter(cookieKeyPrefix + paramWidth, Integer.toString(width));
		c.setParameter(cookieKeyPrefix + paramHeight, Integer.toString(height));
	}

	Font getFont() {
	    if (font == null) {
		    font = new Font(fontName, Font.PLAIN, fontSize);
	    }
	    return font;
	}
}
