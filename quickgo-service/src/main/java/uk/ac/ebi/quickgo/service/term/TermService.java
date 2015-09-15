package uk.ac.ebi.quickgo.service.term;

import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServerException;

import uk.ac.ebi.quickgo.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.render.Format;

/**
 * Interface to define the operations for the Terms service
 *
 * @author cbonill
 *
 */

public interface TermService {

	/**
	 * Retrieve a GOTerm by id
	 * @param id Identifier of the GO Term
	 * @return GO Term object
	 */
	public GOTerm retrieveTerm(String id);

	/**
	 * Retrieve terms names
	 * @return Terms names
	 */
	public Map<String, Map<String, String>> retrieveNames();

	/**
	 * Convert to stream
	 * @param goTerm GO term to convert
	 * @param format Format to convert to
	 * @param outputStream Contains converted go term
	 */
	public void convertToStream(GenericTerm goTerm, Format format, OutputStream outputStream);

	/**
	 * Convert a GOTerm into XML
	 * @param goTerm GO term to transform
	 * @param outputStream Output stream
	 */
	public void convertToXML(GenericTerm goTerm, Format format, OutputStream outputStream);

	/**
	 * Convert a GOTerm into JSON format
	 * @param goTerm GO term to transform
	 * @param outputStream Output stream
	 */
	public void convertToJSON(GenericTerm goTerm, OutputStream outputStream);

	/**
	 * Convert to OBO format
	 * @param genericTerm Term to convert
	 * @return OBO representation
	 */
	public String convertToOBO(GenericTerm genericTerm);

	/**
	 * Auto suggest term names/ids based on the inserted text
	 * @param text Text to search for
	 * @param numResults Number results to return
	 * @return Suggestions
	 * @throws SolrServerException
	 */
	public List<GenericTerm> autosuggest(String text, String filterQuery, int numResults) throws SolrServerException;

	public List<GenericTerm> autosuggestOnlyGoTerms(String text, String filterQuery, int numResults) throws SolrServerException;

	/**
	 * Highlight functionality
	 * @param text Text to highlight
	 * @param fq Filter query
	 * @param start Start position
	 * @param rows Number of results to retrieve
	 * @return Terms with highlighted text
	 */
	public List<GenericTerm> highlight(String text, String fq, int start, int rows);

	/**
	 * Return total number highlight results
	 * @param text Text to search for
	 * @param fq Filter query
	 * @return total number highlight results
	 */
	public long getTotalNumberHighlightResults(String text, String fq);

	/**
	 * Retrieve go terms changes in a specific range
	 * @param from Starting date
	 * @param to To date
	 * @param limit Max number of changes to retrieve
	 * @return List of go terms changes
	 */
	public List<GOTerm> retrieveByHistoryDate(Date from, Date to, int limit);

}
