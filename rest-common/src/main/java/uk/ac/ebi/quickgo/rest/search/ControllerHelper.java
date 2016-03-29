package uk.ac.ebi.quickgo.rest.search;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @Author Tony Wardell
 * Date: 29/03/2016
 * Time: 10:39
 * Created with IntelliJ IDEA.
 */
public class ControllerHelper {

	private static final String COMMA = ",";

	/**
	 * Creates a list of items from a scalar representation of a list, in CSV format. If the
	 * parameter is null, an empty list is returned.
	 *
	 * @param csv a CSV list of items
	 * @return a list of values originally comprising the CSV input String
	 */
	public List<String> csvToList(String csv) {
		if (!isNullOrEmpty(csv)) {
			return Arrays.asList(csv.split(COMMA));
		} else {
			return Collections.emptyList();
		}
	}

}
