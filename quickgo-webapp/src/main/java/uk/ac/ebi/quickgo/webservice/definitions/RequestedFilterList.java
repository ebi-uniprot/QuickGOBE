package uk.ac.ebi.quickgo.webservice.definitions;

import uk.ac.ebi.quickgo.webservice.definitions.FilterRequestName;
import uk.ac.ebi.quickgo.webservice.definitions.WebServiceFilterType;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Tony Wardell
 * Date: 18/06/2015
 * Time: 11:20
 * Created with IntelliJ IDEA.
 */
public class RequestedFilterList {

	private static Map<FilterRequestName, WebServiceFilterType> map = new HashMap<>();

	static {
		map.put(FilterRequestName.ecoID,      WebServiceFilterType.ArgumentsAsValues);
		map.put(FilterRequestName.ecoTermUse, WebServiceFilterType.ArgumentAsBehaviour);
	}

	public static boolean isFilterWithArgsAsValues(String requestedFilter) {
		 return map.get(requestedFilter) == WebServiceFilterType.ArgumentsAsValues;
	}
}
