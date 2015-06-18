package uk.ac.ebi.quickgo.webservice.definitions;

import uk.ac.ebi.quickgo.webservice.definitions.WebServiceFilter;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Tony Wardell
 * Date: 18/06/2015
 * Time: 11:50
 * For each filter parameter that is received, look up the defined type
 */
public class RequestedFilterToFilterType {

	private static Map<String, WebServiceFilter> map = new HashMap<>();

	static {
		map.put("ecoid",WebServiceFilter.EcoEvidence);
		map.put("ecotermuse", WebServiceFilter.EcoEvidence);
	}

	public static WebServiceFilter lookupWsFilter(String requestedFilter) {
		return map.get(requestedFilter.toLowerCase());
	}
}
