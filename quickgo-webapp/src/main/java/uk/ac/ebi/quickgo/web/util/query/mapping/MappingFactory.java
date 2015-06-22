package uk.ac.ebi.quickgo.web.util.query.mapping;

import uk.ac.ebi.quickgo.webservice.definitions.*;
import uk.ac.ebi.quickgo.webservice.model.FilterJson;

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


	public static void populateFiltersContainerWithSingleFilter(FilterJson requestedFilter, FiltersContainer container){


		//lookup definitions for parameters
		FilterRequest filterRequest = FilterRequest.lookup(requestedFilter.getType().toLowerCase());

		//Does this filter already exist in the container?
		SingleFilter singleFilter = container.lookupFilter(filterRequest);

		//No it doesn't so create it
		if(singleFilter==null){

			if(filterRequest.getWsType() == WebServiceFilterType.ArgumentsAsValues){
				singleFilter = new SingleFilter(filterRequest.getDefaultSolrField());
				singleFilter.addArg(requestedFilter.getValue());

			}else{
				//WebServiceFilterType.ArgumentAsBehaviour
				FilterParameter filterParameter = FilterParameter.lookup(requestedFilter.getValue().toLowerCase());
				singleFilter = new SingleFilter(FilterNameToSolrFieldMapper.lookup(filterRequest, filterParameter));
			}

			container.saveFilter(filterRequest.getWsFilter(), singleFilter);

		}else{

			//Filter already exists
			if(filterRequest.getWsType() == WebServiceFilterType.ArgumentsAsValues){
				singleFilter.addArg(requestedFilter.getValue());
			}else{
				FilterParameter filterParameter = FilterParameter.lookup(requestedFilter.getValue().toLowerCase());
				singleFilter.replace(FilterNameToSolrFieldMapper.lookup(filterRequest, filterParameter));

			}

		}


	}

}
