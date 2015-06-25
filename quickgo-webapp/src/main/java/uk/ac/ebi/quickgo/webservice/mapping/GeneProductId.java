package uk.ac.ebi.quickgo.webservice.mapping;

import uk.ac.ebi.quickgo.webservice.model.FilterJson;
import uk.ac.ebi.quickgo.webservice.model.FilterRequestJson;

/**
 * @Author Tony Wardell
 * Date: 25/06/2015
 * Time: 14:52
 * Created with IntelliJ IDEA.
 */
public class GeneProductId extends FilterMapping{


	@Override
	public void processRequestObject(FilterRequestJson filterRequestJson) {


		
		for( FilterJson aFilter : filterRequestJson.getList()) {

		}

	}
}
