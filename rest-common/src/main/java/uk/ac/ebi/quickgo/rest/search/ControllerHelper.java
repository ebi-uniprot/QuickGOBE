package uk.ac.ebi.quickgo.rest.search;

import java.util.List;

/**
 * @author Tony Wardell
 * Date: 01/04/2016
 * Time: 10:13
 * Created with IntelliJ IDEA.
 */
public interface ControllerHelper {

	/**
	 * Creates a list of items from a scalar representation of a list, in CSV format. If the
	 * parameter is null, an empty list is returned.
	 *
	 * @param csv a CSV list of items
	 * @return a list of values originally comprising the CSV input String
	 */
	public List<String> csvToList(String csv);
}
