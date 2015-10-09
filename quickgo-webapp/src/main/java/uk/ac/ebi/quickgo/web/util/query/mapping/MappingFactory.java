package uk.ac.ebi.quickgo.web.util.query.mapping;

import uk.ac.ebi.quickgo.solr.query.model.annotation.enums.AnnotationField;
import uk.ac.ebi.quickgo.webservice.definitions.*;
import uk.ac.ebi.quickgo.webservice.model.Filter;

/**
 * @Author Tony Wardell
 * Date: 17/06/2015
 * Time: 15:49
 * Created with IntelliJ IDEA.
 *
 *
 *
 * If the requested filter doesn't have values to pass through to Solr, then it must be name
 * or behaviour modifying filter, so use the mapping for that filter instead.
 *
 * We don't need to replace the existing filter if it has just been created.
 *
 */
public class MappingFactory {


	public static void populateFiltersContainerWithSingleFilter(Filter requestedFilter, FiltersContainer container){

		//lookup definitions for parameters
		FilterRequest filterRequest = FilterRequest.lookup(requestedFilter.getType().toLowerCase());
		FilterParameter filterParameter = FilterParameter.lookup(requestedFilter.getValue().toLowerCase());

		//Does this filter already exist in the container?
		SolrFilter solrFilter = container.lookupFilter(filterRequest);

		//No it doesn't so create it
		if(solrFilter ==null){

			if(filterRequest.getWsType() == WebServiceFilterType.ArgumentsAsValues){
				solrFilter = new SolrFilter(filterRequest.getDefaultSolrField());
				solrFilter.addArg(requestedFilter.getValue());

			}else{

				//WebServiceFilterType.ArgumentAsBehaviour
				AnnotationField field = FilterNameToSolrFieldMapper.lookup(filterRequest, filterParameter);
				if(field!=null) {
					solrFilter = new SolrFilter(field);
				}
			}

			container.saveFilter(filterRequest.getWsFilter(), solrFilter);

		}else{

			//Filter already exists
			if(filterRequest.getWsType() == WebServiceFilterType.ArgumentsAsValues){
				solrFilter.addArg(requestedFilter.getValue());
			}else{

				//Lookup new mapping, use if its defined (some aren't deliberately)
				AnnotationField field = FilterNameToSolrFieldMapper.lookup(filterRequest, filterParameter);
				if(field!=null) {
					solrFilter.replace(field);
				}

			}
		}


		//Check to see if the filter contains a slimming request
		//I hate to do  this as an exception
		if(!container.isSlim()){
			container.setSlim(filterRequest == FilterRequest.GoTermUse && filterParameter == FilterParameter.Slim);
		}

		if(filterRequest == FilterRequest.GoSlim && filterParameter == FilterParameter.Slim){
			//Create a filter and populate for goids for slim set.
		}
	}

}
