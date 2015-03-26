package uk.ac.ebi.quickgo.web.util.query;

import uk.ac.ebi.quickgo.service.annotation.parameter.AnnotationParameters;
import uk.ac.ebi.quickgo.solr.query.model.annotation.enums.AnnotationField;
import uk.ac.ebi.quickgo.web.util.WebUtils;
import uk.ac.ebi.quickgo.web.util.annotation.AppliedFilterSet;

import java.util.*;

/**
 * @Author Tony Wardell
 * Date: 24/03/2015
 * Time: 15:56
 * Created with IntelliJ IDEA.
 */
public class QueryTo {

	// Field name for filtering by ECO name
	private final String ECONAME = "ecoName";

	/**
	 * Process query and calculate filter session parameters
	 * @param query Query to run
	 */
	public AnnotationParameters queryToAnnotationParameters(String query, boolean advancedFilter){

		AppliedFilterSet appliedFilterSet = new AppliedFilterSet();
		AnnotationParameters annotationParameters = new AnnotationParameters();

		if(query==null){
			return annotationParameters;
		}

		modifyAnnotationParametersAndAppliedFilterSetStage1(advancedFilter, appliedFilterSet, annotationParameters, query);

		// If it's advanced filter, remove previous values
		removeFilterValuesIfAdvanced(advancedFilter, appliedFilterSet, annotationParameters);
		// Copy them to the applied ones
		appliedFilterSet.addParameters(annotationParameters.getParameters());


		annotationParameters.setParameters(new HashMap<String, List<String>>(appliedFilterSet.getParameters()));
		return annotationParameters;
	}

	private void removeFilterValuesIfAdvanced(boolean advancedFilter, AppliedFilterSet appliedFilterSet, AnnotationParameters annotationParameters) {
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
	}


	/**
	 * Process query and calculate filter session parameters
	 * @param query Query to run
	 */
	public AppliedFilterSet queryToAppliedFilterSet(String query, boolean advancedFilter){

		AppliedFilterSet appliedFilterSet = new AppliedFilterSet();
		AnnotationParameters annotationParameters = new AnnotationParameters();

		if(query==null){
			return appliedFilterSet;
		}

		modifyAnnotationParametersAndAppliedFilterSetStage1(advancedFilter, appliedFilterSet, annotationParameters, query);

		// If it's advanced filter, remove previous values
		removeFilterValuesIfAdvanced(advancedFilter, appliedFilterSet, annotationParameters);

		// Copy them to the applied ones
		appliedFilterSet.addParameters(annotationParameters.getParameters());

		return appliedFilterSet;
	}


	/**
	 * filter values are comma separated
	 * @param advancedFilter
	 * @param appliedFilterSet
	 * @param annotationParameters
	 * @param query
	 */
	private void modifyAnnotationParametersAndAppliedFilterSetStage1(boolean advancedFilter, AppliedFilterSet appliedFilterSet,
										  AnnotationParameters annotationParameters, String query) {
		String[] filterValues = query.split("\",\"");

		for (String filterValue : filterValues) {

			filterValue = stripOutFluff(filterValue);

			//Separate out id value
			String[] idValue = filterValue.split(":",2);

			if(idValue.length > 1){

				if (advancedFilter && idValue[1].trim().length() == 0) {// No value specified, remove all of them
					if (appliedFilterSet.getParameters().get(idValue[0]) != null) {
						appliedFilterSet.getParameters().get(idValue[0]).clear();
					}

				} else if (idValue[1].trim().length() > 0) {

					//If there are id values listed for the filter
					String filtersString = Arrays.asList(filterValues).toString();
					String key = idValue[0];
					List<String> formattedValuesList = prepareFormattedValuesList(idValue);

					modifyAnnotationParametersAndAppliedFilterSetStage2(formattedValuesList, key, annotationParameters, filtersString,
							appliedFilterSet);
				}
			}
		}
	}

	private List<String> prepareFormattedValuesList(String[] idValue) {
		List<String> formattedValuesList = formatValuesList(idValue);
		removeDuplicatedElements( formattedValuesList);
		return formattedValuesList;
	}

	private String stripOutFluff(String filterValue) {
		filterValue = filterValue.replaceAll("\"", "")
				.replaceAll("\\{", "").replaceAll("\\}", "")
				.replaceAll("\\[", "").replaceAll("\\]", "")
				.replaceAll("=", ":");
		return filterValue;
	}


	/**
	 * Process a filter id and its values
	 * @param annotationParameters Filters to apply
	 * @param appliedFilterSet
	 */
	private void modifyAnnotationParametersAndAppliedFilterSetStage2(List<String> formattedValuesList, String key,
																	 AnnotationParameters annotationParameters,
																	 String filtersString,
																	 AppliedFilterSet appliedFilterSet){


		if(key.equals(AnnotationField.GOID.getValue())){		//TODO Check if it's exact match or not. If so, we don't need to replace the key value
			key = populateKeyForAncestors(filtersString);
			removeIncorrectGoIds(formattedValuesList);

		} else if(key.equals(AnnotationField.TAXONOMYID.getValue())){
			key = AnnotationField.TAXONOMYCLOSURE.getValue();

		} else if(key.equals(AnnotationField.DBOBJECTID.getValue())){// Search in the gp2protein field as well
			key = AnnotationField.GP2PROTEIN.getValue();

		} else if(key.equals(AnnotationField.ECOID.getValue())){
			key = processECOFilter(filtersString, appliedFilterSet);

		} else if(key.equals(AnnotationField.QUALIFIER.getValue())){
			formattedValuesList = reformatValuesListForNots(formattedValuesList);
		}

		//Drop filtering by ECO name
		// Special case: filtering by ECO name
//		if(key.equals(ECONAME)){
////			List<String> names = formattedValuesList;
////			List<String> ecoterms = TermUtil.getECOTermsByName(names);
////			formattedValuesList = ecoterms;
//			formattedValuesList = TermUtil.getECOTermsByName(formattedValuesList);
//			key = processECOFilter(filtersString, appliedFilterSet);
//		}

		// Don't add filter values like ancestorsI:ancestorsI
		if (!formattedValuesList.contains(AnnotationField.ANCESTORSI.getValue())
				&& !formattedValuesList.contains(AnnotationField.ANCESTORSIPO.getValue())
				&& !formattedValuesList.contains(AnnotationField.ANCESTORSIPOR.getValue())
				) {
			annotationParameters.addParameter(key, formattedValuesList);
		}
	}

	private List<String> reformatValuesListForNots(List<String> formattedValuesList) {
		List<String> notFormattedList = new ArrayList<>();
		for(String qualifier : formattedValuesList){
			String notFormatted = qualifier.replaceAll("NOT", "\"NOT\"");
			notFormattedList.add(notFormatted);
		}
		formattedValuesList = notFormattedList;
		return formattedValuesList;
	}

	private String populateKeyForAncestors(String filtersString) {

		String key;
		key = AnnotationField.ANCESTORSIPO.getValue();

		// Slimming
		if(filtersString.contains(AnnotationField.ANCESTORSIPOR.getValue())){
			key = AnnotationField.ANCESTORSIPOR.getValue();
		} else if(filtersString.contains(AnnotationField.ANCESTORSIPO.getValue())){
			key = AnnotationField.ANCESTORSIPO.getValue();
		} else if(filtersString.contains(AnnotationField.ANCESTORSI.getValue())){
			key = AnnotationField.ANCESTORSI.getValue();
		}


		return key;
	}

	private void removeIncorrectGoIds(List<String> formattedValuesList) {
		List<String> toRemove = new ArrayList<>();
		for(String goID : formattedValuesList){
			if(!goID.matches(AnnotationParameters.GO_ID_REG_EXP + "\\d{7}")){
				toRemove.add(goID);
			}
		}

		formattedValuesList.removeAll(toRemove);
	}

	private void removeDuplicatedElements( List<String> formattedValuesList) {
		HashSet<String> hs = new HashSet<>();
		hs.addAll(formattedValuesList);
		formattedValuesList.clear();
		formattedValuesList.addAll(hs);
	}

	private List<String> formatValuesList(String[] idValue) {
		List<String> formattedValuesList;
		if(idValue[0].equals(ECONAME) || idValue[0].equals(AnnotationField.QUALIFIER.getValue())){// Comma separated values
			formattedValuesList = new ArrayList<>(Arrays.asList(idValue[1].split(",")));
		} else {
			formattedValuesList = new ArrayList<>(	WebUtils.parseAndFormatFilterValues(idValue[1]));
		}

		removeDuplicatedElements( formattedValuesList);
		return formattedValuesList;
	}

	/**
	 * Change ECO filters depending on checked options
	 * @param filtersString Filters to apply
	 * @param appliedFilterSet Applied filters
	 */
	private String processECOFilter(String filtersString, AppliedFilterSet appliedFilterSet){
		String key = AnnotationField.ECOID.getValue();
		if(filtersString.contains(AnnotationField.ECOANCESTORSI.getValue())){
			key = AnnotationField.ECOANCESTORSI.getValue();
			appliedFilterSet.getParameters().remove(AnnotationField.ECOID.getValue());//Remove previous exact match filter (if any)
		} else { //If ecoAncestorsI is present in current applied filters, then remove it
			appliedFilterSet.getParameters().remove(AnnotationField.ECOANCESTORSI.getValue());
		}
		return key;
	}

	private class KeyAndFormattedValueList{
		List<String> formattedValuesList;
		String key;

		public List<String> getFormattedValuesList() {
			return formattedValuesList;
		}

		public void setFormattedValuesList(List<String> formattedValuesList) {
			this.formattedValuesList = formattedValuesList;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}
	}
}
