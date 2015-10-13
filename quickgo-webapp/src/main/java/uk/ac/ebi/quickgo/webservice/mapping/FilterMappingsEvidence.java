package uk.ac.ebi.quickgo.webservice.mapping;


import uk.ac.ebi.quickgo.solr.query.model.annotation.enums.AnnotationField;
import uk.ac.ebi.quickgo.webservice.definitions.FilterParameter;
import uk.ac.ebi.quickgo.webservice.definitions.FilterRequestDefinition;
import uk.ac.ebi.quickgo.webservice.model.Filter;

/**
 * @Author Tony Wardell
 * Date: 19/06/2015
 * Time: 14:49
 * Mapping behaviour for Evidence filters.
 */
public class FilterMappingsEvidence extends FilterMapping{

	public FilterMappingsEvidence() {
		super(null);
	}

	public void processRequestObject(uk.ac.ebi.quickgo.webservice.model.FilterRequest filterRequest){

		for( Filter aFilter : filterRequest.getList()){

			if(FilterRequestDefinition.EcoId.getLowerCase().equals(aFilter.getType().toLowerCase()) && solrField == null) {
				solrField = AnnotationField.ECOANCESTORSI;
				args.add(aFilter.getValue());
			}

			//Indicates whether the ECO term(s) is/are to be used as an ancestor (the default), or an exact match
			if (FilterRequestDefinition.EcoTermUse.getLowerCase().equals(aFilter.getType().toLowerCase())) {

				//EcoTermUse as AncestorsI is the default so we don't need to test for it

				//Exact Eco Id match
				if (FilterParameter.Exact.toLowerCase().equals(aFilter.getValue().toLowerCase())) {
						solrField = AnnotationField.ECOID;
				}
			}
		}
	}

}
