package uk.ac.ebi.quickgo.rest.service;

import java.util.List;

/**
 * @author Tony Wardell
 * Date: 01/04/2016
 * Time: 13:32
 *
 * There is some code that is common to the Service layer of the RESTful services
 *
 * Created with IntelliJ IDEA.
 */
public interface ServiceHelper {
	List<String> buildIdList(List<String> ids);
}
