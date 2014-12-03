package uk.ac.ebi.quickgo.web.configuration;

import org.w3c.dom.Element;

import uk.ac.ebi.quickgo.util.StringUtils;

public class StatisticsConfiguration {
	/**
	 * the number of statistics values to be returned by default
	 */
	public int defaultLimit = 80;

	StatisticsConfiguration(Element elt) {
		if (elt != null) {
			defaultLimit = StringUtils.parseInt(elt.getAttribute("defaultLimit"), defaultLimit);
		}
	}
}
