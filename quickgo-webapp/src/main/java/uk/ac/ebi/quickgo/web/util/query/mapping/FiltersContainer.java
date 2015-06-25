package uk.ac.ebi.quickgo.web.util.query.mapping;

import uk.ac.ebi.quickgo.webservice.definitions.FilterRequest;
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

	private Map<WebServiceFilter, SolrFilter> filters;

	private boolean slim=false;

	public FiltersContainer() {
		this.filters = new HashMap<>();
	}

	public SolrFilter lookupFilter(FilterRequest filterRequest){
		return  filters.get(filterRequest.getWsFilter());
	}

	public void saveFilter(WebServiceFilter webServiceFilter, SolrFilter solrFilter) {
		filters.put(webServiceFilter, solrFilter);
	}

	public String And() {
		if(filters.size()==0) return SOLR_QUERY_NO_FILTERS;

		StringBuffer solrQuery = new StringBuffer();
		for(SolrFilter solrFilter :filters.values()){
			solrQuery.append(solrFilter.writeSolrQueryFragment());
			solrQuery.append(" ");
		}
		return solrQuery.toString();
	}

	public void setSlim(boolean slim) {
		this.slim = slim;
	}

	public boolean isSlim(){
		return this.slim;
	}

}
