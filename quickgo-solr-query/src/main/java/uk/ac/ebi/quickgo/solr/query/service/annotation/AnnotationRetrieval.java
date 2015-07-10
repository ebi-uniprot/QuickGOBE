package uk.ac.ebi.quickgo.solr.query.service.annotation;

import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;

import uk.ac.ebi.quickgo.solr.model.annotation.GOAnnotation;
import uk.ac.ebi.quickgo.solr.query.service.Retrieval;

/**
 * Interface for retrieving Annotations from Solr 
 * @author cbonill
 *
 */
public interface AnnotationRetrieval extends Retrieval<GOAnnotation> {

	/**
	 * Allow pagination for annotations 
	 * @param query Query 
	 * @param start The offset to start at in the result set
	 * @param rows Number of annotations to retrieve
	 * @return List of annotations
	 */
	List<GOAnnotation> findByQuery(String query, int start, int rows) throws SolrServerException;
	
	/**
	 * Return number of annotations for a query
	 * @param query Filtering query
	 * @return Number of annotations in the schema
	 * @throws SolrServerException
	 */
	public long getTotalNumberAnnotations(String query) throws SolrServerException;
	
	/**
	 * Return total number of proteins for a specific query
	 * @param query
	 * @return Number of distinct proteins
	 */
	public long getTotalNumberProteins(String query) throws SolrServerException;
	
	/**
	 * Runs a query in Solr
	 */
	public QueryResponse query(String query, String fields, int numRows) throws SolrServerException;

}
