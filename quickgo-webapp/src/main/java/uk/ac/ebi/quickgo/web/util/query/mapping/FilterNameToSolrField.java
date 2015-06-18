package uk.ac.ebi.quickgo.web.util.query.mapping;

import uk.ac.ebi.quickgo.solr.query.model.annotation.enums.AnnotationField;
import uk.ac.ebi.quickgo.webservice.definitions.FilterRequestName;

import java.util.List;

/**
 * @Author Tony Wardell
 * Date: 17/06/2015
 * Time: 13:47
 * Will hold a map from input value to the name of the solr field to use
 */
public class FilterNameToSolrField {

	private FilterRequestName filterRequestName;
	private AnnotationField solrField;

	public FilterNameToSolrField(FilterRequestName filterRequestName, AnnotationField solrField) {
		this.filterRequestName = filterRequestName;
		this.solrField = solrField;
	}

	public FilterRequestName getFilterRequestName() {
		return filterRequestName;
	}

	public AnnotationField getSolrField() {
		return solrField;
	}
}
