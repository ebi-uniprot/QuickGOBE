package uk.ac.ebi.quickgo.web.util.query.mapping;

import uk.ac.ebi.quickgo.webservice.model.FilterJson;
import uk.ac.ebi.quickgo.webservice.model.FilterRequestJson;

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

	public String toSolrQuery(FilterRequestJson filterRequest) {

		FiltersContainer filtersContainer = new FiltersContainer();

		for( FilterJson aFilter : filterRequest.getList()){
			MappingFactory.populateFiltersContainerWithSingleFilter(aFilter, filtersContainer);
		}

		return filtersContainer.And();


	}


}
