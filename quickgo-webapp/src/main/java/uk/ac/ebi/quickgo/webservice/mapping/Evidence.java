package uk.ac.ebi.quickgo.webservice.mapping;


import uk.ac.ebi.quickgo.solr.query.model.annotation.enums.AnnotationField;
import uk.ac.ebi.quickgo.webservice.definitions.FilterParameter;
import uk.ac.ebi.quickgo.webservice.definitions.FilterRequest;
import uk.ac.ebi.quickgo.webservice.model.FilterJson;
import uk.ac.ebi.quickgo.webservice.model.FilterRequestJson;

/**
 * @Author Tony Wardell
 * Date: 19/06/2015
 * Time: 14:49
 * Mapping behaviour for Evidence filters.
 */
public class Evidence extends FilterMapping{

	public void processRequestObject(FilterRequestJson filterRequestJson){

		for( FilterJson aFilter : filterRequestJson.getList()){

			if(FilterRequest.EcoId.getLowerCase().equals(aFilter.getType().toLowerCase()) && solrField !=null) {
				solrField = AnnotationField.ECOANCESTORSI;
				args.add(aFilter.getValue());
			}

			//Indicates whether the ECO term(s) is/are to be used as an ancestor (the default), or an exact match
			if (FilterRequest.EcoTermUse.getLowerCase().equals(aFilter.getType().toLowerCase())) {

				//EcoTermUse as AncestorsI is the default so we don't need to test for it

				//Exact Eco Id match
				if (FilterParameter.Exact.toLowerCase().equals(aFilter.getValue().toLowerCase())) {
						solrField = AnnotationField.ECOID;
				}
			}
		}
	}

}
