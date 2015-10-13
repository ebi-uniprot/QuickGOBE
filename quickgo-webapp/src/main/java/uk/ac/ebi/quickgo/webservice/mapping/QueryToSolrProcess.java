package uk.ac.ebi.quickgo.webservice.mapping;

import uk.ac.ebi.quickgo.solr.query.model.annotation.enums.AnnotationField;
import uk.ac.ebi.quickgo.web.util.annotation.AnnotationWSUtil;
import uk.ac.ebi.quickgo.webservice.definitions.FilterRequestDefinition;
import uk.ac.ebi.quickgo.webservice.definitions.WebServiceFilter;
import uk.ac.ebi.quickgo.webservice.model.FilterRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Tony Wardell
 * Date: 25/06/2015
 * Time: 13:30
 * Turn the filter request into a solr query, with a class dedicated to each filter type
 */
public class QueryToSolrProcess {

	private final Map<WebServiceFilter, FilterMapping> filterMappers;

	public QueryToSolrProcess(AnnotationWSUtil annotationWSUtil) {

		 filterMappers = createMappingsList(annotationWSUtil);
	}


	public String toSolrQuery(FilterRequest filterRequest) {

		boolean first=true;

		StringBuilder stringBuilder = new StringBuilder();
		for(FilterMapping aMapping: filterMappers.values()){

			aMapping.processRequestObject(filterRequest);
			String solrQueryForFilter = aMapping.toSolrQuery();
			if(solrQueryForFilter.length()>0){

				if(first){
					stringBuilder.append("(");
					stringBuilder.append(solrQueryForFilter);
					stringBuilder.append(")");
					first=false;
				}else {

					//
					stringBuilder.append(" AND (");
					stringBuilder.append(solrQueryForFilter);
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
		filterMappings.put(WebServiceFilter.GeneProductType, new FilterMappingSimple(FilterRequestDefinition.GeneProductType, AnnotationField.DBOBJECTTYPE));

		filterMappings.put(WebServiceFilter.Database, new FilterMappingSimple(FilterRequestDefinition.Database, AnnotationField.DB));

		//todo OK??
		filterMappings.put(WebServiceFilter.GeneProductSet, new FilterMappingSimple(FilterRequestDefinition.GeneProductSet, AnnotationField.TARGETSETS));

		//todo does this exist??
		//proteome --> something.

		//filterMappers.put(WebServiceFilter.GeneProductId, new FilterMappingSimple(FilterRequest.GeneProductID, AnnotationField.DBOBJECTID));
		filterMappings.put(WebServiceFilter.Aspect,new FilterMappingSimple(FilterRequestDefinition.Aspect, AnnotationField.GOASPECT));
		filterMappings.put(WebServiceFilter.Qualifier,new FilterMappingSimple(FilterRequestDefinition.Qualifier, AnnotationField.QUALIFIER));
		filterMappings.put(WebServiceFilter.Reference,new FilterMappingSimple(FilterRequestDefinition.Reference, AnnotationField.REFERENCE));
		filterMappings.put(WebServiceFilter.With,new FilterMappingSimple(FilterRequestDefinition.With, AnnotationField.WITH));
		filterMappings.put(WebServiceFilter.AssignedBy,new FilterMappingSimple(FilterRequestDefinition.AssignedBy, AnnotationField.ASSIGNEDBY));
		filterMappings.put(WebServiceFilter.Taxon,new FilterMappingSimple(FilterRequestDefinition.Taxon, AnnotationField.TAXONOMYID));


		//Here are the complicated mappings where there could be several parameters that affect the mapping to be used,
		//and sometimes change the operation completely

		filterMappings.put(WebServiceFilter.GeneProductId,new FilterMappingsGeneProductId());
		filterMappings.put(WebServiceFilter.GoTerm,new FilterMappingsGoTerm(annotationWSUtil));
		filterMappings.put(WebServiceFilter.EcoEvidence,new FilterMappingsEvidence());


		return filterMappings;
	}

	public boolean isSlimmingRequired() {
		FilterMappingsGoTerm filterMappingsGoTermMapping = ((FilterMappingsGoTerm)(filterMappers.get(WebServiceFilter.GoTerm)));
		return filterMappingsGoTermMapping.useTermsAsGoSlim();
	}
}
