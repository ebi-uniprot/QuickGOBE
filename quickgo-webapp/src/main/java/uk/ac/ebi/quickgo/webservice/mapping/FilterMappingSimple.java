package uk.ac.ebi.quickgo.webservice.mapping;

import uk.ac.ebi.quickgo.solr.query.model.annotation.enums.AnnotationField;
import uk.ac.ebi.quickgo.webservice.definitions.FilterRequestDefinition;
import uk.ac.ebi.quickgo.webservice.model.Filter;

/**
 * @Author Tony Wardell
 * Date: 25/06/2015
 * Time: 14:00
 * A generic solution to mapping from the filter parameter for a QuickGo query to the solr field holding the data.
 */
public class FilterMappingSimple extends FilterMapping {

	FilterRequestDefinition requestField;

	public FilterMappingSimple(FilterRequestDefinition requestField, AnnotationField solrField) {
		super(solrField);
		this.requestField=requestField;
	}

	@Override
	public void processRequestObject(uk.ac.ebi.quickgo.webservice.model.FilterRequest filterRequest) {

		for( Filter aFilter : filterRequest.getList()){

			if(requestField.getLowerCase().equals(aFilter.getType().toLowerCase())){
				args.add(aFilter.getValue());
			}
		}
	}
}
