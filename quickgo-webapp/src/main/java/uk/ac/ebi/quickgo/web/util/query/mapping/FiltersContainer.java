package uk.ac.ebi.quickgo.web.util.query.mapping;

import uk.ac.ebi.quickgo.webservice.definitions.WebServiceFilter;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Tony Wardell
 * Date: 17/06/2015
 * Time: 13:58
 * Created with IntelliJ IDEA.
 */
public class FiltersContainer {

	public static final String SOLR_QUERY_NO_FILTERS = "*:*";

	private Map<WebServiceFilter, SingleFilter> filters;

	public FiltersContainer() {
		this.filters = new HashMap<>();
	}

	public SingleFilter lookupFilter(WebServiceFilter webServiceFilter){
		return  filters.get(webServiceFilter);
	}

	public void saveFilter(WebServiceFilter webServiceFilter, SingleFilter singleFilter) {
		filters.put(webServiceFilter, singleFilter);
	}

	public String And() {
		if(filters.size()==0) return SOLR_QUERY_NO_FILTERS;

		StringBuffer solrQuery = new StringBuffer();
		for(SingleFilter singleFilter:filters.values()){
			solrQuery.append(singleFilter.writeSolrQueryFragment());
			solrQuery.append(" ");
		}
		return solrQuery.toString();
	}
}
