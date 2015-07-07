package uk.ac.ebi.quickgo.webservice.mapping;

import uk.ac.ebi.quickgo.solr.query.model.annotation.enums.AnnotationField;
import uk.ac.ebi.quickgo.web.util.annotation.AnnotationWSUtil;
import uk.ac.ebi.quickgo.webservice.definitions.FilterRequest;
import uk.ac.ebi.quickgo.webservice.model.FilterJson;
import uk.ac.ebi.quickgo.webservice.model.FilterRequestJson;

/**
 * @Author Tony Wardell
 * Date: 25/06/2015
 * Time: 14:52
 * Created with IntelliJ IDEA.
 */
public class GeneProductId extends FilterMapping{


	public GeneProductId() {
		super(AnnotationField.DBOBJECTID);
	}

	@Override
	public void processRequestObject(FilterRequestJson filterRequestJson) {


		for( FilterJson aFilter : filterRequestJson.getList()) {

			//When the go ids are specified, as them as values to query against
			if(FilterRequest.GeneProductID.getLowerCase().equals(aFilter.getType().toLowerCase())){
				args.add(aFilter.getValue());
			}


			//proteome	R, C (Refence/ Complete)
			//The thing that allows you to specify whether gene product id is part of the complete or reference product
			//todo

			//gpSet eg KRUK	A name of a set of gene products
//			if(FilterRequest.GeneProductSet.getLowerCase().equals(aFilter.getType().toLowerCase())){
//
//				//todo shall we do a double lookup here too. If not, then this should be treated as a seperate filter list
//
//
//			}



		}

	}
}
