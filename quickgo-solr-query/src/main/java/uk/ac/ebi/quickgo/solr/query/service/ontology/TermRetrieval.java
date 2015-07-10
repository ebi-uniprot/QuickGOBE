package uk.ac.ebi.quickgo.solr.query.service.ontology;

import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServerException;

import uk.ac.ebi.quickgo.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.solr.model.ontology.SolrTerm;
import uk.ac.ebi.quickgo.solr.query.service.Retrieval;

public interface TermRetrieval extends Retrieval<GOTerm>{
	/**
	 * Get entries by type
	 * @param type Type
	 * @return Entries with the specified type
	 */
	List<GOTerm> findByType(SolrTerm.SolrTermDocumentType type) throws SolrServerException;

	/**
	 * Return term identifiers and the specified list of fields
	 * @param fieldID Field id
	 * @param fields Fields to retrieve
	 */
	Map<String, Map<String, String>> getFieldValues(String query, String fieldID, String fields) throws SolrServerException;
	
	/**
	 * Autosuggest term names/ids based on the inserted text
	 * @param text Text to search for
	 * @param fq Filter query
	 * @param numResults Number of results to return
	 * @return Suggestions
	 * @throws SolrServerException
	 */
	List<GenericTerm> autosuggest(String text, String fq,int numResults) throws SolrServerException;

	/**
	 * Highlight functionality
	 * @param text Text to highlight
	 * @param fq Filter query
	 * @param start Start position
	 * @param rows Number of results to retrieve
	 * @return Terms with highlighted text
	 */
	List<GenericTerm> highlight(String text, String fq, int start, int rows) throws SolrServerException;
	
	/**
	 * Return total number highlight results
	 * @param text Text to search for
	 * @param fq Filter query
	 * @return total number highlight results
	 */
	long getTotalNumberHighlightResults(String text, String fq) throws SolrServerException;
}
