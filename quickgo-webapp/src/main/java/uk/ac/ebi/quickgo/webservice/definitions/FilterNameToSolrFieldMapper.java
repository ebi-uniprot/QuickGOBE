package uk.ac.ebi.quickgo.webservice.definitions;

import uk.ac.ebi.quickgo.solr.query.model.annotation.enums.AnnotationField;
import uk.ac.ebi.quickgo.web.util.query.mapping.FilterNameToSolrField;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Tony Wardell
 * Date: 18/06/2015
 * Time: 11:46
 * Created with IntelliJ IDEA.
 */
public class FilterNameToSolrFieldMapper {


	private static Map<String, FilterNameToSolrField> map = new HashMap<>();


	static{
		map.put("ecoId",               new FilterNameToSolrField( FilterRequestName.ecoID,      AnnotationField.ECOANCESTORSI));
		map.put("ecotermuse:ancestor", new FilterNameToSolrField( FilterRequestName.ecoTermUse, AnnotationField.ECOANCESTORSI));
		map.put("ecotermuse:exact",    new FilterNameToSolrField( FilterRequestName.ecoTermUse, AnnotationField.ECOID));
	}

	public static FilterNameToSolrField map(String requestedFilter, String arg) {
		return map.get(requestedFilter.toLowerCase()+":"+arg.toLowerCase());
	}

	public static FilterNameToSolrField map(String requestedFilter) {
		return map.get(requestedFilter.toLowerCase());
	}
}
