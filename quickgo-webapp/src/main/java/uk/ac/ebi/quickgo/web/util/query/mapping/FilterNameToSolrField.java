package uk.ac.ebi.quickgo.web.util.query.mapping;

import uk.ac.ebi.quickgo.solr.query.model.annotation.enums.AnnotationField;
import uk.ac.ebi.quickgo.webservice.definitions.FilterRequest;

/**
 * @Author Tony Wardell
 * Date: 17/06/2015
 * Time: 13:47
 * Will hold a map from input value to the name of the solr field to use
 */
public class FilterNameToSolrField {

	private FilterRequest filterRequest;
	private AnnotationField solrField;

	public FilterNameToSolrField(FilterRequest filterRequest, AnnotationField solrField) {
		this.filterRequest = filterRequest;
		this.solrField = solrField;
	}

	public FilterRequest getFilterRequest() {
		return filterRequest;
	}

	public AnnotationField getSolrField() {
		return solrField;
	}
}
