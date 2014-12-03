package uk.ac.ebi.quickgo.web.configuration;

import org.w3c.dom.Element;

import uk.ac.ebi.quickgo.util.StringUtils;

public class ChartConfiguration {
	/**
	 * the default image size limit (megapixels)
	 */
    public int defaultImageLimit = 10;
    /**
     * the absolute maximum image size limit (megapixels)
     */
    public int maximumImageLimit = 20;
    /**
     * the maximum number of ancestors we will attempt to display in an ancestor chart
     */
	public int ancestorLimit = 1000;

	ChartConfiguration(Element elt) {
		if (elt != null) {
	        defaultImageLimit = StringUtils.parseInt(elt.getAttribute("defaultImageLimit"), defaultImageLimit);
	        maximumImageLimit = StringUtils.parseInt(elt.getAttribute("maximumImageLimit"), maximumImageLimit);
		    ancestorLimit = StringUtils.parseInt(elt.getAttribute("ancestorLimit"), ancestorLimit);
		}
    }
}
