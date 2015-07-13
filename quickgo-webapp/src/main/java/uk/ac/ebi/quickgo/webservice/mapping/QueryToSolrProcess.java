package uk.ac.ebi.quickgo.webservice.mapping;

import uk.ac.ebi.quickgo.solr.query.model.annotation.enums.AnnotationField;
import uk.ac.ebi.quickgo.web.util.annotation.AnnotationWSUtil;
import uk.ac.ebi.quickgo.web.util.query.mapping.MappingFactory;
import uk.ac.ebi.quickgo.webservice.definitions.FilterRequest;
import uk.ac.ebi.quickgo.webservice.definitions.WebServiceFilter;
import uk.ac.ebi.quickgo.webservice.model.FilterJson;
import uk.ac.ebi.quickgo.webservice.model.FilterRequestJson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Tony Wardell
 * Date: 25/06/2015
 * Time: 13:30
 * Turn the filter request into a solr query, with a class dedicated to each filter type
 */
public class QueryToSolrProcess {

	private final FilterRequestJson filterRequest;
	private final Map<WebServiceFilter, FilterMapping> filterMappings;

	public QueryToSolrProcess(FilterRequestJson filterRequest, AnnotationWSUtil annotationWSUtil) {

		this.filterRequest = filterRequest;

		 filterMappings =  createMappingsList(annotationWSUtil);
	}


	public String toSolrQuery() {

		boolean first=true;

		StringBuilder stringBuilder = new StringBuilder();
		for(FilterMapping aMapping: filterMappings.values()){

			aMapping.processRequestObject(this.filterRequest);
			String fragment = aMapping.solrQueryFragment();
			if(fragment.length()>0){

				if(first){
					stringBuilder.append("(");
					stringBuilder.append(fragment);
					stringBuilder.append(")");
					first=false;
				}else {

					//
					stringBuilder.append(" AND (");
					stringBuilder.append(fragment);
					stringBuilder.append(")");
				}
			}

		}

		String solrQuery = stringBuilder.toString();

		if(solrQuery.length()==0){
			return  "*:*";
		}else{
			return solrQuery;
		}
	}

	private Map<WebServiceFilter, FilterMapping> createMappingsList(AnnotationWSUtil annotationWSUtil) {

		Map<WebServiceFilter, FilterMapping> filterMappings = new HashMap<>();

		//These mappings are simple - the argument to the filter is the value to use in the solr query
		//so there is a simple one to one mapping

		//Gene Product Type
		//Limits the scope to annotations where the target (annotated gene product) is of the specified type(s)
		//Eg protein, rna, complex
		filterMappings.put(WebServiceFilter.GeneProductType, new FilterMappingSimple(FilterRequest.GeneProductType, AnnotationField.DBOBJECTTYPE));

		filterMappings.put(WebServiceFilter.Database, new FilterMappingSimple(FilterRequest.Database, AnnotationField.DB));

		//todo OK??
		filterMappings.put(WebServiceFilter.GeneProductSet, new FilterMappingSimple(FilterRequest.GeneProductSet, AnnotationField.TARGETSETS));

		//todo does this exist??
		//proteome --> something.

		//filterMappings.put(WebServiceFilter.GeneProductId, new FilterMappingSimple(FilterRequest.GeneProductID, AnnotationField.DBOBJECTID));
		filterMappings.put(WebServiceFilter.Aspect,new FilterMappingSimple(FilterRequest.Aspect, AnnotationField.GOASPECT));
		filterMappings.put(WebServiceFilter.Qualifier,new FilterMappingSimple(FilterRequest.Qualifier, AnnotationField.QUALIFIER));
		filterMappings.put(WebServiceFilter.Reference,new FilterMappingSimple(FilterRequest.Reference, AnnotationField.REFERENCE));
		filterMappings.put(WebServiceFilter.With,new FilterMappingSimple(FilterRequest.With, AnnotationField.WITH));
		filterMappings.put(WebServiceFilter.AssignedBy,new FilterMappingSimple(FilterRequest.AssignedBy, AnnotationField.ASSIGNEDBY));
		filterMappings.put(WebServiceFilter.Taxon,new FilterMappingSimple(FilterRequest.Taxon, AnnotationField.TAXONOMYID));


		//Here are the complicated mappings where there could be several parameters that affect the mapping to be used,
		//and sometimes change the operation completely

		filterMappings.put(WebServiceFilter.GeneProductId,new GeneProductId());
		filterMappings.put(WebServiceFilter.GoTerm,new GoTerm(annotationWSUtil));
		filterMappings.put(WebServiceFilter.EcoEvidence,new Evidence());


		return filterMappings;
	}

	public boolean isSlimmingRequired() {
		GoTerm goTermMapping = ((GoTerm)(filterMappings.get(WebServiceFilter.GoTerm)));
		return goTermMapping.useTermsAsGoSlim();
	}
}
