package uk.ac.ebi.quickgo.web.util.annotation;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import uk.ac.ebi.quickgo.solr.query.model.annotation.enums.AnnotationField;

/**
 * Applied filters
 *
 * @author cbonill
 *
 */
public class AppliedFilterSet {

	private Map<String, List<String>> parameters = new HashMap<String, List<String>>();

	public Map<String, List<String>> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, List<String>> parameters) {
		this.parameters = parameters;
	}

	public void setValues(String key, List<String> values) {
		this.parameters.put(key, values);
	}

	public void addParameters(Map<String, List<String>> otherParameters) {
		for (String key : otherParameters.keySet()) {
			if (this.parameters.containsKey(key)) {
				List<String> appliedValues = new LinkedList<String>(this.parameters.get(key));
				List<String> filtersToApply = otherParameters.get(key);
				for (String paremeter : filtersToApply) {
					if (!containsIgnoreCase(appliedValues, paremeter)) {
						appliedValues.add(paremeter);
					}
				}
				this.parameters.put(key, appliedValues);
			} else {
				this.parameters.put(key, otherParameters.get(key));
			}
		}
	}

	/**
	 * Check if a list contains a specific value (case insensitive)
	 * @param appliedValues List of values
	 * @param value Value to check if it's present
	 * @return True if it's contained, false otherwise
	 */
	public boolean containsIgnoreCase(List<String> appliedValues, String value){
		if (appliedValues != null) {
			for(String appliedValue : appliedValues){
				if(appliedValue.equalsIgnoreCase(value)){
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * To check if a filter value is present
	 */
	public boolean isApplied(String value){
		for(List<String> values : this.parameters.values()){
			if(values.contains(value)){
				return true;
			}
		}
		return false;
	}

	/**
	 * Given a field id, returns its corresponding name
	 * @param key Field id
	 * @return Correponding name
	 */
	public String toAnnotationColumn (String key){
		AnnotationColumn annotationColumn = AnnotationColumn.fromID(key);
		if (annotationColumn != null) {
			return annotationColumn.getName();
		} else {
			if(key.toLowerCase().equals(AnnotationField.GP2PROTEIN.getValue().toLowerCase())){
				key = AnnotationColumn.PROTEIN.getName();
			}
			if(key.toLowerCase().equals(AnnotationField.ANCESTORSI.getValue().toLowerCase())){
				key = AnnotationColumn.GOID.getName();
			}
			if(key.toLowerCase().equals(AnnotationField.ANCESTORSIPO.getValue().toLowerCase())){
				key = AnnotationColumn.GOID.getName();
			}
			if(key.toLowerCase().equals(AnnotationField.ANCESTORSIPOR.getValue().toLowerCase())){
				key = AnnotationColumn.GOID.getName();
			}
			return key;
		}
	}

	/**
	 * Get number of applied filter values
	 */
	public int numValues() {
		int numValues = 0;
		for (String key : parameters.keySet()) {
			numValues = numValues + parameters.get(key).size();
		}
		return numValues;
	}

	/**
	 * Get URL for the applied filters
	 * @return URL for the applied filters
	 * @throws UnsupportedEncodingException
	 */
	public String getURL() throws UnsupportedEncodingException{
		String url = "";
		for(String filterId : parameters.keySet()){
			url = url + "\"{" + filterId + ":" + StringUtils.arrayToDelimitedString(parameters.get(filterId).toArray(), ",") + "}\",";
		}
		if(url.length() > 0){
			url = url.substring(0, url.length()-1);
		}
		return URLEncoder.encode(url, "UTF-8");
	}

	@Override
	public String toString() {
		return "AppliedFilterSet{" +
				"parameters=" + parameters +
				'}';
	}
}
