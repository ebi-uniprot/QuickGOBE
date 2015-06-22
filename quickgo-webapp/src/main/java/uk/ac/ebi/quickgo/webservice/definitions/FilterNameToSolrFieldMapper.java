package uk.ac.ebi.quickgo.webservice.definitions;

import uk.ac.ebi.quickgo.solr.query.model.annotation.enums.AnnotationField;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Tony Wardell
 * Date: 18/06/2015
 * Time: 11:46
 * Created with IntelliJ IDEA.
 */
public class FilterNameToSolrFieldMapper {


	private static Map<CompositeKey, AnnotationField> map = new HashMap<>();

	static{

		map.put(new CompositeKey(FilterRequest.EcoTermUse, FilterParameter.Ancestor), AnnotationField.ECOANCESTORSI );
		map.put(new CompositeKey(FilterRequest.EcoTermUse, FilterParameter.Exact), AnnotationField.ECOID);
	}

	public static AnnotationField lookup(FilterRequest name, FilterParameter value) {
		return map.get(new CompositeKey(name, value));
	}

}

class CompositeKey {
	private FilterRequest filterRequest;
	private FilterParameter filterParameter;

	public CompositeKey(FilterRequest filterRequest, FilterParameter filterParameter) {
		this.filterRequest = filterRequest;
		this.filterParameter = filterParameter;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		CompositeKey that = (CompositeKey) o;

		if (filterRequest != that.filterRequest) return false;
		return filterParameter == that.filterParameter;

	}

	@Override
	public int hashCode() {
		int result = filterRequest.hashCode();
		result = 31 * result + filterParameter.hashCode();
		return result;
	}
}
