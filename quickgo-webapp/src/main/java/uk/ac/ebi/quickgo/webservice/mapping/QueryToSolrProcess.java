package uk.ac.ebi.quickgo.webservice.mapping;

import uk.ac.ebi.quickgo.solr.query.model.annotation.enums.AnnotationField;
import uk.ac.ebi.quickgo.web.util.query.mapping.MappingFactory;
import uk.ac.ebi.quickgo.webservice.definitions.FilterRequest;
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
 * Created with IntelliJ IDEA.
 */
public class QueryToSolrProcess {

	public void execute(FilterRequestJson filterRequestJson) {

		List<FilterMapping> filterMappings =  createMappingsList();

	}

	private List<FilterMapping> createMappingsList() {

		List<FilterMapping> filterMappings = new ArrayList<>();

		//These mappings are simple - the argument to the filter is the value to use in the solr query
		//so there is a simple one to one mapping
		filterMappings.add(new FilterMappingSimple(FilterRequest.GeneProductType, AnnotationField.DBOBJECTTYPE));
		filterMappings.add(new FilterMappingSimple(FilterRequest.Database, AnnotationField.DBXREF));
		filterMappings.add(new FilterMappingSimple(FilterRequest.GeneProductID, AnnotationField.DBOBJECTID));
		filterMappings.add(new FilterMappingSimple(FilterRequest.Aspect, AnnotationField.GOASPECT));
		filterMappings.add(new FilterMappingSimple(FilterRequest.Qualifier, AnnotationField.QUALIFIER));
		filterMappings.add(new FilterMappingSimple(FilterRequest.Reference, AnnotationField.REFERENCE));
		filterMappings.add(new FilterMappingSimple(FilterRequest.With, AnnotationField.WITH));
		filterMappings.add(new FilterMappingSimple(FilterRequest.AssignedBy, AnnotationField.ASSIGNEDBY));
		filterMappings.add(new FilterMappingSimple(FilterRequest.Taxon, AnnotationField.TAXONOMYID));

		//Here are the complicated mappings where there could be several parameters that affect the mapping to be used,
		//and sometimes change the operation completely

		filterMappings.add(new GeneProductId());
		filterMappings.add(new GoTerm());
		filterMappings.add(new Evidence());


		return filterMappings;
	}
}
