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

		//Go Term
		map.put(new CompositeKey(FilterRequestDefinition.GoRelations, FilterParameter.I), AnnotationField.ANCESTORSI);

		//Don't need to include the goTermUse=ancestor mapping, since this is the default for GoId
		//map.put(new CompositeKey(FilterRequest.GoTermUse, FilterParameter.Ancestor), AnnotationField.ANCESTORSI);


		map.put(new CompositeKey(FilterRequestDefinition.GoTermUse, FilterParameter.Slim), null);
		map.put(new CompositeKey(FilterRequestDefinition.GoTermUse, FilterParameter.Exact), AnnotationField.GOID);

		//Go Term - Go Relations
		map.put(new CompositeKey(FilterRequestDefinition.GoRelations, FilterParameter.I), AnnotationField.ANCESTORSI);
		map.put(new CompositeKey(FilterRequestDefinition.GoRelations, FilterParameter.IPO), AnnotationField.ANCESTORSIPO);
		map.put(new CompositeKey(FilterRequestDefinition.GoRelations, FilterParameter.IPOR), AnnotationField.ANCESTORSIPOR);

		//Evidence
		map.put(new CompositeKey(FilterRequestDefinition.EcoTermUse, FilterParameter.Ancestor), AnnotationField.ECOANCESTORSI );
		map.put(new CompositeKey(FilterRequestDefinition.EcoTermUse, FilterParameter.Exact), AnnotationField.ECOID);
	}

	public static AnnotationField lookup(FilterRequestDefinition name, FilterParameter value) {
		return map.get(new CompositeKey(name, value));
	}

}

class CompositeKey {
	private FilterRequestDefinition filterRequestDefinition;
	private FilterParameter filterParameter;

	public CompositeKey(FilterRequestDefinition filterRequestDefinition, FilterParameter filterParameter) {
		this.filterRequestDefinition = filterRequestDefinition;
		this.filterParameter = filterParameter;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		CompositeKey that = (CompositeKey) o;

		if (filterRequestDefinition != that.filterRequestDefinition) return false;
		return filterParameter == that.filterParameter;

	}

	@Override
	public int hashCode() {
		int result = filterRequestDefinition.hashCode();
		result = 31 * result + filterParameter.hashCode();
		return result;
	}
}
