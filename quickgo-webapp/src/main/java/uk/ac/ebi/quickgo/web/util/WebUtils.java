package uk.ac.ebi.quickgo.web.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class WebUtils {

	static Pattern pattern = Pattern.compile("\\s");

	/**
	 * Utility to retrieve a named cookie's value.
	 * 
	 * @param req
	 *            Request containing cookie
	 * @param name
	 * @return Cookie value - or null
	 */
	public static String getCookieValue(HttpServletRequest req, String name) {
		Cookie[] ck = req.getCookies();
		if (ck != null) {
			for (Cookie c : ck) {
				if (c.getName().equals(name)) {
					return c.getValue();
				}
			}
		}
		return null;
	}

	/**
	 * Given a string with all the filtering values, process them and return a list with each of them formatted 
	 * @param filteringValues Values as a simple string
	 * @return List with parameters values
	 */
	public static List<String> processFilterValues(String filteringValues) {
		List<String> formattedValuesList = new ArrayList<>();
		// Values separated by comma, space or new line character
		String[] values = null;

		if (filteringValues.contains(",")) {
			values = filteringValues.split(",");
		} else if (filteringValues.contains("\\r")) {
			values = filteringValues.split("\\\\r");
		} else if (filteringValues.contains("\\n")) {
			values = filteringValues.split("\\\\n");
		} else if (pattern.matcher(filteringValues).find()) { // Separate by space
			values = filteringValues.split("\\s+");
		} else {
			return Arrays.asList(filteringValues.replaceAll("\\\\t", ""));
		}

		// Keep processing values until all of them are formatted
		for (String value : values) {
			if (value.trim().length() > 0 && value.replaceAll("\\\\t", "").length() > 0) {// Skip empty strings
				formattedValuesList.addAll(processFilterValues(value));
			}
		}

		return formattedValuesList;
	}
}
