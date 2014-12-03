package uk.ac.ebi.quickgo.solr.server;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.TermsResponse.Term;

/**
 * Interface for SolR server processor
 * @author cbonill
 *
 */
public interface SolrServerProcessor {
	
	/**
	 * Run a query in SolR server
	 * @param solRQuery Query to run
	 * @param type Type of the results (ontology, annotation, gene product, ...)
	 * @param numRows Number of rows to retrieve (-1 to set the default value)
	 * @return Results of specified type
	 * @throws SolrServerException SolR exception
	 */
	public <T> List<T> findByQuery(SolrQuery solRQuery, Class<T> type, int numRows) throws SolrServerException;
	
	/**
	 * Index a collection of beans in Solr
	 * @param beans Collection of beans to index
	 */
	public <T> void indexBeans (Collection<T> beans) throws SolrServerException, IOException;
	
	/**
	 * Index a collection of beans in Solr using auto commit option. To use this
	 * method in the Solr "solrconfig.xml" file you have to enable the
	 * <autoCommit> or <autoSoftCommit> option
	 * 
	 * @param beans
	 *            Collection of beans to index
	 */
	public <T> void indexBeansAutoCommit (Collection<T> beans) throws SolrServerException, IOException;	
	
	/**
	 * Deletes everything
	 */
	public void deleteAll() throws SolrServerException, IOException;
	
	/**
	 * Delete by query
	 * @throws SolrServerException
	 * @throws IOException
	 */
	public void deleteByQuery(String query) throws SolrServerException, IOException;		
	
	/**
	 * Return total number of documents in the schema for a query
	 * @param query Query
	 * @return Number of documents in the schema
	 */
	public long getTotalNumberDocuments(SolrQuery query) throws SolrServerException;
	
	/**
	 * Return total number of distinct values of a field 
	 * @param query Query to run
	 * @param field Field
	 * @return Total number of distinct values of the field
	 * @throws SolrServerException
	 */
	public long getTotalNumberDistinctValues(String query, String field) throws SolrServerException;
	
	/**
	 * Return the specified list of fields using the field id as main key
	 * The returned structure is like this: MAP <ID, MAP <FIELD1, VALUE_FIELD1> <FIELD2, VALUE_FIELD2> ...>
	 * @param fields Fields to return
	 */
	public Map<String, Map<String, String>> getFields(String query, String fieldID, String fields) throws SolrServerException;	
	
	/**
	 * Return the top terms of the whole index
	 * @param solrQuery Query
	 * @return Top terms
	 * @throws SolrServerException 
	 */
	public List<Term> getTopTerms(SolrQuery solrQuery) throws SolrServerException;	
	
	/**
	 * Return list of facet counts
	 * @param solrQuery Query
	 * @return Map of counts
	 */
	public List<Count> getFacetTerms(SolrQuery solrQuery) throws SolrServerException;
	
	/**
	 * Return map with id and total number of values for a specific query using pivot fields
	 * @param solrQuery Query
	 * @return Map of counts
	 */
	public Map<String, Integer> getFacetTermsWithPivot(SolrQuery solrQuery) throws SolrServerException;
	
	/**
	 * Run Solr query
	 * @param query Solr query to run
	 * @return Query response
	 * @throws SolrServerException 
	 */
	public QueryResponse query(SolrQuery query) throws SolrServerException;
} 