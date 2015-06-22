package uk.ac.ebi.quickgo.webservice.definitions;

import uk.ac.ebi.quickgo.solr.query.model.annotation.enums.AnnotationField;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Tony Wardell
 * Date: 18/06/2015
 * Time: 15:15
 * Created with IntelliJ IDEA.
 */
public enum FilterRequest {

	//Evidence
	EcoId("ecoid", WebServiceFilter.EcoEvidence,  WebServiceFilterType.ArgumentsAsValues, AnnotationField.ECOANCESTORSI),
	EcoTermUse("ecotermuse", WebServiceFilter.EcoEvidence,  WebServiceFilterType.ArgumentAsBehaviour, null),

	Taxon("taxon", WebServiceFilter.Taxon,  WebServiceFilterType.ArgumentsAsValues, AnnotationField.TAXONOMYID),

	AssignedBy("assignedBy", WebServiceFilter.Taxon,  WebServiceFilterType.ArgumentsAsValues, AnnotationField.TAXONOMYID);



	//Allow the enums to be looked up using their lowerCase value;
	private static Map<String, FilterRequest> map = new HashMap<>();

	static {

		for(FilterRequest name: FilterRequest.values()){
			map.put(name.getLowerCase(), name);

		}

	}


	private final WebServiceFilter wsFilter;
	private final String lc;
	private final WebServiceFilterType wsType;
	private final AnnotationField solrField;

	FilterRequest( String lowerCase, WebServiceFilter wsFilter, WebServiceFilterType wsType,
				  AnnotationField defaultSolr)  {
		this.lc = lowerCase;
		this.wsFilter = wsFilter;
		this.wsType = wsType;
		this.solrField=defaultSolr;
	}

	public String getLowerCase(){
		return lc;
	}

	public WebServiceFilter getWsFilter() {
		return wsFilter;
	}

	public WebServiceFilterType getWsType() {
		return wsType;
	}

	public static WebServiceFilter lookupWsFilter(String requestedFilter) {
		return map.get(requestedFilter.toLowerCase()).getWsFilter();
	}

	public static FilterRequest lookup(String filterLowerCase) {
		return map.get(filterLowerCase);
	}

	public AnnotationField getDefaultSolrField() {
		return this.solrField;
	}
}
