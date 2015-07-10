package uk.ac.ebi.quickgo.solr.query.service;

import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.TermsResponse.Term;

/**
 * Interface all retrieval classes have to implement
 * @author cbonill
 *
 * @param <T>
 */
public interface Retrieval<T> {

	/**
	 * Get entries by id
	 * @param id Id
	 * @return Entry with the specified id
	 */
	T findById(String id) throws SolrServerException;
	
	/**
	 * Get entries by name
	 * @param name Name
	 * @return Entries with the specified name
	 */
	List<T> findByName(String name) throws SolrServerException;

	/**
	 * Get all the entries
	 * @return All entries
	 */
	List<T> findAll() throws SolrServerException;
	
	/**
	 * Get Term entries for a specific query
	 * @return All entries
	 */
	List<T> findByQuery(String query, int numRows) throws SolrServerException;
	
	/**
	 * Return the top terms of the whole index
	 * @param termFields Term fields
	 * @param numRows Number of top terms to retrieve
	 * @return Top terms
	 * @throws SolrServerException 
	 */
	List<Term> getTopTerms(String termFields, int numRows) throws SolrServerException;
			
	/**
	 * Return list of facet counts (Value , NumFound). This method is used for the annotations statistics
	 * @param query Query
	 * @param facetQuery Facet query
	 * @param facetFields Fields to facet to
	 * @param numTerms Number of facet values to return
	 * @return Map with the facet fields and the corresponding values
	 * @throws SolrServerException 
	 */
	List<Count> getFacetFields(String query, String facetQuery, String facetFields, int numTerms) throws SolrServerException;
	
	/**
	 * Return list of facet fields with the count of pivot results
	 * @param query Query
	 * @param facetQuery Facet query
	 * @param facetFields Fields to facet to
	 * @param pivotFields Fields used as pivots
	 * @param numTerms Number of facet values to return
	 * @return Map with the facet fields and the corresponding values
	 * @throws SolrServerException 
	 */
	Map<String, Integer> getFacetFieldsWithPivots(String query, String facetQuery, String facetFields, String pivotFields, int numTerms) throws SolrServerException;
}
