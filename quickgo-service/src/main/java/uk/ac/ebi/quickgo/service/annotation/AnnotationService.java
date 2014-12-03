package uk.ac.ebi.quickgo.service.annotation;

import java.util.List;

import org.apache.solr.client.solrj.response.FacetField.Count;

import uk.ac.ebi.quickgo.annotation.Annotation;

/**
 * Interface to define all the annotation operations
 * @author cbonill
 *
 */
public interface AnnotationService {

	/**
	 * Retrieve an annotation by id
	 * @param id Annotation id
	 * @return Annotation with the specified id
	 */
	public Annotation retrieveAnnotation(String id);
		
	/**
	 * Retrieve annotations filtered by annotation parameters
	 * @param query Filtering values
	 * @param start Annotation number to start the retrieving from 
	 * @param rows Number of annotations to retrieve
	 * @return List of annotations filtered
	 */
	public List<Annotation> retrieveAnnotations(String query, int start, int rows);
	
	/**
	 * Retrieve all the annotations
	 * @return All the annotations
	 */
	public List<Annotation> retrieveAll();
	
	/**
	 * Get term values using facet field/s  
	 * @param query Query to run
	 * @param facetQuery Facet query (if any)
	 * @param facetFields Field to use for the faceting
	 * @param numTerms Number of values to retrieve
	 * @return List containing the facet values
	 */
	public List<Count> getFacetFields(String query, String facetQuery, String facetFields, int numTerms);
	
	/**
	 * Return total number of annotations for the specified query
	 * @param query Filtering query
	 * @return Number of annotations
	 */
	public long getTotalNumberAnnotations(String query);

	/**
	 * Return total number of distinct proteins for a specific query
	 * @param query Filtering query
	 * @return Number of proteins
	 */
	public long getTotalNumberProteins(String query);
}
