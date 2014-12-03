package uk.ac.ebi.quickgo.solr.query.service.geneproduct;

import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;

import uk.ac.ebi.quickgo.geneproduct.GeneProduct;
import uk.ac.ebi.quickgo.solr.query.service.Retrieval;

/**
 * Interface for retrieving Gene Products from Solr
 */
public interface GeneProductRetrieval extends Retrieval<GeneProduct>{

	/**
	 * Auto suggest proteins names/ids based on the inserted text
	 * @param text Text to search for
	 * @param filterQuery Filter Query
	 * @param numResults Number results to return
	 * @return Suggestions
	 * @throws SolrServerException
	 */
	public List<GeneProduct> autosuggest(String text, String filterQuery, int numResults) throws SolrServerException;
	
	/**
	 * Highlight functionality
	 * @param text Text to highlight
	 * @param fq Filter query
	 * @param start Start position
	 * @param rows Number of results to retrieve
	 * @return Gene products with highlighted text
	 */
	public List<GeneProduct> highlight(String text, String fq, int start, int rows) throws SolrServerException;
	
	public long getTotalNumberHighlightResults(String text, String fq) throws SolrServerException;
}
