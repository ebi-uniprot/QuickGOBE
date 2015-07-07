package uk.ac.ebi.quickgo.webservice.mapping;

import uk.ac.ebi.quickgo.solr.query.model.annotation.enums.AnnotationField;
import uk.ac.ebi.quickgo.webservice.definitions.FilterParameter;
import uk.ac.ebi.quickgo.webservice.definitions.FilterRequest;
import uk.ac.ebi.quickgo.webservice.model.FilterJson;
import uk.ac.ebi.quickgo.webservice.model.FilterRequestJson;

/**
 * @Author Tony Wardell
 * Date: 25/06/2015
 * Time: 14:00
 * A generic solution to mapping from the filter parameter for a QuickGo query to the solr field holding the data.
 */
public class FilterMappingSimple extends FilterMapping {

	FilterRequest requestField;

	public FilterMappingSimple(FilterRequest requestField, AnnotationField solrField) {
		super(solrField);
		this.requestField=requestField;
	}

	@Override
	public void processRequestObject(FilterRequestJson filterRequestJson) {

		for( FilterJson aFilter : filterRequestJson.getList()){

			if(requestField.getLowerCase().equals(aFilter.getType().toLowerCase())){
				args.add(aFilter.getValue());
			}
		}
	}
}
