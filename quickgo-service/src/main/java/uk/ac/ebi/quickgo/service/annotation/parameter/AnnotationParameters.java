package uk.ac.ebi.quickgo.service.annotation.parameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import uk.ac.ebi.quickgo.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.solr.query.model.annotation.enums.AnnotationField;

/**
 * Possible filter values for retrieving annotations
 * 
 * @author cbonill
 * 
 */
public class AnnotationParameters {

	public final static String GO_ID_REG_EXP = "(go:|GO:|gO:|Go:)"; 
	public final static String ECO_ID_REG_EXP = "(eco:|ECO:|Eco:|eCo:|ECo:|eCO:|ecO:|EcO:)";
	
	private Map<String, List<String>> parameters = new HashMap<String, List<String>>();

	/**
	 * Convert the map of parameters/values to a Solr query
	 * @return Solr query
	 */
	public String toSolrQuery() {
		String solrQuery = "*:*";

		// The Solr query will be the result of the advanced query plus the rest of the fields 
		String fieldsQuery = processFields();
		String advancedQuery = processAdvancedQuery();
		solrQuery = fieldsQuery + " AND " + advancedQuery;
		
		// Remove redundant queries
		if(fieldsQuery.equals(advancedQuery)){
			solrQuery = fieldsQuery;
		}
		
		return solrQuery;
	}

	/**
	 * Process advanced query if any
	 * @return Solr representation of the advanced query
	 */
	private String processAdvancedQuery() {
		String solrQuery = "*:*";
		if (parameters.keySet().contains(AnnotationWebServiceField.ADVANCEDQUERY.name())) {
			if (!parameters.get(AnnotationWebServiceField.ADVANCEDQUERY.name()).isEmpty()) {
				String query = parameters.get(AnnotationWebServiceField.ADVANCEDQUERY.name()).get(0);
				// Escape GO ID value
				if (query.toLowerCase().contains("go:")) {
					query = query.replaceAll(GO_ID_REG_EXP, "*").replaceAll(":","\\\\:");// TODO Find another way to do it
				}
				solrQuery = query;
			}
		}
		return solrQuery;
	}
	
	/**
	 * Process all the fields to build a Solr query
	 * @return Solr query
	 */
	private String processFields() {		
		String solrQuery = "*:*";
		// List of queries
		List<String> queries = new ArrayList<>();
		// Iterate over all the parameters
		Iterator<String> keys = parameters.keySet().iterator();
		// If not, process all the fields
		while (keys.hasNext()) {		
			String key = keys.next();
			if(parameters.get(key).size() > 0){
				AnnotationField annotationField = null;
				try {
					// Field name
					annotationField = AnnotationField.valueOf(key.toUpperCase());
				} catch (Exception e) {
					continue;
				}
				String query = annotationField.getValue() + ":(";
				// Field values
				String values = StringUtils.arrayToDelimitedString(parameters.get(key).toArray(), " OR ");
				// Add values to the query
				query = query + values.replaceAll(GO_ID_REG_EXP, "*").replaceAll(":","\\\\:") + ")";// TODO Find another way to do it
				// Add the query to the queries list
				queries.add(query);
			}
		}
		if (!queries.isEmpty()) {
			// Link the queries with "AND"
			solrQuery = StringUtils.arrayToDelimitedString(queries.toArray(), " AND ");
		}
		return solrQuery;
	}
	
	/**
	 * Add a field and values into the hash
	 * @param key
	 * @param values
	 */
	public void addParameter(String key, List<String> values) {
		
		// Convert GAnnotation web service fields into Solr ones
		
		if (key.equals(AnnotationWebServiceField.ASPECT.getValue())) {
			key = AnnotationField.GOASPECT.name();
			// Convert abbreviation to description
			List<String> textValues = new ArrayList<>();
			for (String value : values) {
				if (value.equals(GOTerm.EGOAspect.F.abbreviation)) {
					textValues.add(GOTerm.EGOAspect.F.text);
				} else if (value.equals(GOTerm.EGOAspect.C.abbreviation)) {
					textValues.add(GOTerm.EGOAspect.C.text);
				} else if (value.equals(GOTerm.EGOAspect.P.abbreviation)) {
					textValues.add(GOTerm.EGOAspect.P.text);
				}
			}
			if(!textValues.isEmpty()){
				values = textValues;
			}
		}
		if (key.equals(AnnotationWebServiceField.EVIDENCE.getValue())) {
			key = AnnotationField.GOEVIDENCE.name();
		}
		if (key.equals(AnnotationWebServiceField.GOID.getValue())) {
			key = AnnotationField.GOID.name();
		}
		if (key.equals(AnnotationWebServiceField.SOURCE.getValue())) {
			key = AnnotationField.ASSIGNEDBY.name();
		}
		if (key.equals(AnnotationWebServiceField.PROTEIN.getValue())) {
			key = AnnotationField.DBOBJECTID.name();
		}
		if (key.equals(AnnotationWebServiceField.REF.getValue())) {
			key = AnnotationField.DBXREF.name();
		}
		if (key.equals(AnnotationWebServiceField.RELATION.getValue())) {//TODO Check this one
			key = AnnotationField.ANCESTORSI.name();
			if(values.contains("I")){
				key = AnnotationField.ANCESTORSI.name();
			}
			if(values.contains("P")){
				key = AnnotationField.ANCESTORSIPO.name();
			}
			if(values.contains("R")){
				key = AnnotationField.ANCESTORSIPOR.name();
			}			
		}		
		if (key.equals(AnnotationWebServiceField.ECOID.getValue())) {
			key = AnnotationField.ECOID.name();
		}
		if (key.equals(AnnotationWebServiceField.TAX.getValue())) {
			key = AnnotationField.TAXONOMYID.name();
		}
		
		HashSet<String> newValues = new HashSet<>(values);
		if(this.parameters.get(key) != null && !this.parameters.get(key).isEmpty()){
			List<String> previousValues = this.parameters.get(key);
			newValues = new HashSet<>(values);
			newValues.addAll(previousValues);
		}
		this.parameters.put(key, new ArrayList<>(newValues));
	}

	public Map<String, List<String>> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, List<String>> parameters) {
		this.parameters = parameters;
	}	
}