package uk.ac.ebi.quickgo.web.util.query.mapping;

import uk.ac.ebi.quickgo.webservice.model.Filter;
import uk.ac.ebi.quickgo.webservice.model.FilterRequest;

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

 */
public class FilterRequestToSolr {

	public String toSolrQuery(FilterRequest filterRequest) {

		FiltersContainer filtersContainer = new FiltersContainer();

		for( Filter aFilter : filterRequest.getList()){
			MappingFactory.populateFiltersContainerWithSingleFilter(aFilter, filtersContainer);
		}

		//Could do it this way
//		if(filtersContainer.explodeGoSlim()){
//			//Get
//		}


		return filtersContainer.And();


	}


}
