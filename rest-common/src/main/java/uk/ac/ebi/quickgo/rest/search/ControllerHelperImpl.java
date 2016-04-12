package uk.ac.ebi.quickgo.rest.search;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author Tony Wardell
 * Date: 29/03/2016
 * Time: 10:39
 * Created with IntelliJ IDEA.
 *
 * Some logic is common to all REST services
 */
public class ControllerHelperImpl implements ControllerHelper{

	private static final String COMMA = ",";


	public List<String> csvToList(String csv) {
		if (!isNullOrEmpty(csv)) {
			return Arrays.asList(csv.split(COMMA));
		} else {
			return Collections.emptyList();
		}
	}

}
