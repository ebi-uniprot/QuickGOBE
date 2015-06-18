package uk.ac.ebi.quickgo.web.util.query;

import org.apache.commons.lang3.StringUtils;
import uk.ac.ebi.quickgo.service.annotation.parameter.AnnotationParameters;
import uk.ac.ebi.quickgo.web.util.annotation.AppliedFilterSet;
import uk.ac.ebi.quickgo.webservice.model.FilterJson;
import uk.ac.ebi.quickgo.webservice.model.FilterRequestJson;

import java.util.*;

/**
 * @Author Tony Wardell
 * Date: 15/06/2015
 * Time: 10:20
 *
 *
 * SolrQueryPrototype holds the state of the filter request
 * SolrQueryPrototype will be modified as the filter params are processed
 * SolrQueryPrototype will hold the filter parameters as they are to be used in solr
 * Some state can only be determined at the end of the filter processing i.e.
 *
 *	Evidence:
 *		if ecoTermUse='ancestor' or is not defined then the solr field to be used is ecoAncestorID
 *	    if ecoTermUser=exact then the solr field to be used is ecoID
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
public class FilterRequestToSolr {

	public static final String SOLR_QUERY_NO_FILTERS = "*:*";
	public static final String GO_ID_REG_EXP = "(go:|GO:|gO:|Go:)";

	public FilterRequestToSolr() {



		//Build the chain of responsibility


	}

	//@Override
	public AnnotationParameters queryToAnnotationParameters(FilterRequestJson filterRequest) {

		AppliedFilterSet appliedFilterSet = new AppliedFilterSet();
		AnnotationParameters annotationParameters = new AnnotationParameters();

		if(filterRequest==null){
			return annotationParameters;
		}

		return null;

	}

	public String toSolrQuery(FilterRequestJson filterRequest) {

		SolrQueryPrototype solrQueryPrototype = new SolrQueryPrototype();

		List<String> queries = new ArrayList<>();
		Map<String, Set<String>> filterValuesBySolrField = new HashMap<>();

		//Create 1 to n map of filter type to request values
		for( FilterJson aFilter : filterRequest.getList()){

			prototypeSolrQuery(aFilter, solrQueryPrototype);
		}

		//Now convert each filter by type into a (part) Solr query
		for( String solrField : filterValuesBySolrField.keySet()){

			StringBuilder query = new StringBuilder();
			query.append(StringUtils.join(filterValuesBySolrField.get(solrField).toArray(), " OR "));
			queries.add(solrField + ":(" +  (query.toString()).replaceAll(GO_ID_REG_EXP, "*").replaceAll(":","\\\\:"));

		}

		StringBuilder solrQuery = new StringBuilder();

		if (!queries.isEmpty()) {
			solrQuery.append(StringUtils.join(queries.toArray(), " AND "));
			solrQuery.append(")");
		}else{
			solrQuery.append(SOLR_QUERY_NO_FILTERS);
		}

		return solrQuery.toString();
	}

	private void prototypeSolrQuery(FilterJson aFilter, SolrQueryPrototype solrQueryPrototype) {


		solrQueryPrototype.addFilter(aFilter);

		//Change the request filter type into the Solr type
		String solrField = FilterToSolrMap.getSolrField(aFilter.getType());

		//Look up to see if solr field is saved
		Set filtersForSolrField = filterValuesBySolrField.get(solrField);

		//If it isn't create it
		if(filtersForSolrField==null){
			filtersForSolrField = new HashSet();
		}

		//Save filter value to filter list
		filtersForSolrField.add(aFilter.getValue());
		filterValuesBySolrField.put(solrField, filtersForSolrField);

	}


}
