package uk.ac.ebi.quickgo.web.util.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Service;

import uk.ac.ebi.quickgo.service.annotation.parameter.AnnotationParameters;
import uk.ac.ebi.quickgo.solr.query.model.annotation.enums.AnnotationField;
import uk.ac.ebi.quickgo.web.util.WebUtils;
import uk.ac.ebi.quickgo.web.util.annotation.AppliedFilterSet;
import uk.ac.ebi.quickgo.web.util.term.TermUtil;

/**
 * Service to calculate session and selected filters from a query
 * @author cbonill
 *
 */

@Service("queryProcessor")
public class QueryProcessorImpl implements QueryProcessor{

	// Field name for filtering by ECO name
	private final String ECONAME = "ecoName";

	/**
	 * Process query and calculate filter session parameters
	 * @param query Query to run
	 * @param annotationParameters Parameters set
	 * @param appliedFilterSet Session parameters
	 */
	public void processQuery(String query, AnnotationParameters annotationParameters, AppliedFilterSet appliedFilterSet, boolean advancedFilter){
		if (query != null) {
			String[] filterValues = query.split("\",\"");		//filter values are comma seperated

			for (String filterValue : filterValues) {

				//Strip out fluff
				filterValue = filterValue.replaceAll("\"", "")
						.replaceAll("\\{", "").replaceAll("\\}", "")
						.replaceAll("\\[", "").replaceAll("\\]", "")
						.replaceAll("=", ":");

				//Seperate out id value
				String[] idValue = filterValue.split(":",2);

				if(idValue.length > 1){

					if (advancedFilter && idValue[1].trim().length() == 0) {// No value specified, remove all of them
						if (appliedFilterSet.getParameters().get(idValue[0]) != null) {
							appliedFilterSet.getParameters().get(idValue[0]).clear();
						}
					} else if (idValue[1].trim().length() > 0) {
						processFilterValue(idValue, annotationParameters, filterValues, appliedFilterSet);
					}
				}
			}
			// If it's advanced filter, remove previous values
			if (advancedFilter) {
				if(annotationParameters.getParameters().keySet().size() == 0){// No filter applied, remove all previously applied filters
					appliedFilterSet.getParameters().clear();
				}
				else{// Filters applied
					for (String key : annotationParameters.getParameters().keySet()) {
						if (appliedFilterSet.getParameters().containsKey(key)) {
							appliedFilterSet.getParameters().get(key).clear();
						}
					}
				}
			}
			// Copy them to the applied ones
			appliedFilterSet.addParameters(annotationParameters.getParameters());
		}
	}

	/**
	 * Process a filter id and its values
	 * @param idValue Filter id and values
	 * @param annotationParameters Filters to apply
	 * @param filterValues
	 * @param appliedFilterSet
	 */
	private void processFilterValue(String[] idValue, AnnotationParameters annotationParameters, String[] filterValues, AppliedFilterSet appliedFilterSet){
		List<String> formatedValuesList = new ArrayList<>();

		if(idValue[0].equals(ECONAME) || idValue[0].equals(AnnotationField.QUALIFIER.getValue())){// Comma separated values
			formatedValuesList = new ArrayList<>(Arrays.asList(idValue[1].split(",")));
		} else {
			formatedValuesList = new ArrayList<String>(	WebUtils.processFilterValues(idValue[1]));
		}

		// Remove duplicated elements
		HashSet<String> hs = new HashSet<String>();
		hs.addAll(formatedValuesList);
		formatedValuesList.clear();
		formatedValuesList.addAll(hs);
		String key = idValue[0];

		List<String> filters = Arrays.asList(filterValues);
		String filtersString = filters.toString();
		if(idValue[0].equals(AnnotationField.GOID.getValue())){//TODO Check if it's exact match or not. If so, we don't need to replace the key value
			key = AnnotationField.ANCESTORSIPO.getValue();

			// Slimming
			if(filtersString.contains(AnnotationField.ANCESTORSIPOR.getValue())){
				key = AnnotationField.ANCESTORSIPOR.getValue();
			} else if(filtersString.contains(AnnotationField.ANCESTORSIPO.getValue())){
				key = AnnotationField.ANCESTORSIPO.getValue();
			} else if(filtersString.contains(AnnotationField.ANCESTORSI.getValue())){
				key = AnnotationField.ANCESTORSI.getValue();
			}
			// Check GO IDs are correct
			List<String> toRemove = new ArrayList<>();
			for(String goID : formatedValuesList){
				if(!goID.matches(AnnotationParameters.GO_ID_REG_EXP + "\\d{7}")){
					toRemove.add(goID);
				}
			}
			formatedValuesList.removeAll(toRemove);
		} else if(idValue[0].equals(AnnotationField.TAXONOMYID.getValue())){
			key = AnnotationField.TAXONOMYCLOSURE.getValue();
		} else if(idValue[0].equals(AnnotationField.DBOBJECTID.getValue())){// Search in the gp2protein field as well
			key = AnnotationField.GP2PROTEIN.getValue();
		} else if(idValue[0].equals(AnnotationField.ECOID.getValue())){
			key = processECOFilter(filtersString, appliedFilterSet);
		} else if(idValue[0].equals(AnnotationField.QUALIFIER.getValue())){
			List<String> notFormattedList = new ArrayList<>();
			for(String qualifier : formatedValuesList){
				String notFormatted = qualifier.replaceAll("NOT", "\"NOT\"");
				notFormattedList.add(notFormatted);
			}
			formatedValuesList = notFormattedList;
		}

		// Special case: filtering by ECO name
		if(idValue[0].equals(ECONAME)){
			List<String> names = formatedValuesList;
			List<String> ecoterms = TermUtil.getECOTermsByName(names);
			formatedValuesList = ecoterms;
			key = processECOFilter(filtersString, appliedFilterSet);
		}

		// Don't add filter values like ancestorsI:ancestorsI
		if (!formatedValuesList.contains(AnnotationField.ANCESTORSI.getValue())
				&& !formatedValuesList.contains(AnnotationField.ANCESTORSIPO
						.getValue())
				&& !formatedValuesList.contains(AnnotationField.ANCESTORSIPOR
						.getValue())) {
			annotationParameters.addParameter(key, formatedValuesList);
		}
	}

	/**
	 * Change ECO filters depending on checked options
	 * @param key ECO field used for filtering
	 * @param filtersString Filters to apply
	 * @param appliedFilterSet Applied filters
	 */
	private String processECOFilter(String filtersString,AppliedFilterSet appliedFilterSet){
		String key = AnnotationField.ECOID.getValue();
		if(filtersString.contains(AnnotationField.ECOANCESTORSI.getValue())){
			key = AnnotationField.ECOANCESTORSI.getValue();
			appliedFilterSet.getParameters().remove(AnnotationField.ECOID.getValue());//Remove previous exact match filter (if any)
		} else { //If ecoAncestorsI is present in current applied filters, then remove it
			appliedFilterSet.getParameters().remove(AnnotationField.ECOANCESTORSI.getValue());
		}
		return key;
	}
}
