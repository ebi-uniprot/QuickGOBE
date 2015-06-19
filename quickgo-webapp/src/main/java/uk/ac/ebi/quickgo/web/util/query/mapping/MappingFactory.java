package uk.ac.ebi.quickgo.web.util.query.mapping;

import uk.ac.ebi.quickgo.webservice.definitions.FilterNameToSolrFieldMapper;
import uk.ac.ebi.quickgo.webservice.definitions.RequestedFilterList;
import uk.ac.ebi.quickgo.webservice.definitions.RequestedFilterToFilterType;
import uk.ac.ebi.quickgo.webservice.definitions.WebServiceFilter;
import uk.ac.ebi.quickgo.webservice.model.FilterJson;

import java.util.List;

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

		boolean newFilter = false;
		WebServiceFilter wsFilter = RequestedFilterToFilterType.lookupWsFilter(requestedFilter.getType());

		//Does this filter already exist in the container?
		SingleFilter singleFilter = container.lookupFilter(wsFilter);

		if(singleFilter==null){
			newFilter=true;
			singleFilter = new SingleFilter(FilterNameToSolrFieldMapper.map(requestedFilter.getType()));
			container.saveFilter(wsFilter, singleFilter);
		}

		if(RequestedFilterList.isFilterWithArgsAsValues(requestedFilter.getType())){
			singleFilter.addArg(requestedFilter.getValue());
		}else{
			if(!newFilter) {
				singleFilter.replace(FilterNameToSolrFieldMapper.map(requestedFilter.getType(),requestedFilter.getValue()));
			}
		}
	}

}
