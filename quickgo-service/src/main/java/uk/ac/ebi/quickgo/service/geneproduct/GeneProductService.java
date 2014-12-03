package uk.ac.ebi.quickgo.service.geneproduct;

import java.io.OutputStream;
import java.util.List;

import uk.ac.ebi.quickgo.geneproduct.GeneProduct;
import uk.ac.ebi.quickgo.render.Format;

/**
 * Gene Products Service interface
 * @author cbonill
 *
 */
public interface GeneProductService {

	/**
	 * Retrieve a Gene Product by id
	 * @param id Gene Product Id
	 * @return Gene Product
	 */
	public GeneProduct findById(String id);
	
	/**
	 * Find gene products by query
	 * @param query Query
	 * @return Gene products match the query
	 */
	public List<GeneProduct> findByQuery(String query);
	
	/**
	 * Convert to stream
	 * @param geneProduct Gene Product to convert
	 * @param format Format to convert to
	 * @param outputStream Contains converted gene product
	 */
	public void convertToStream(GeneProduct geneProduct, Format format, OutputStream outputStream);
	
	/**
	 * Convert a Gene Product into XML
	 * @param geneProduct Gene product to transform
	 * @param outputStream Output stream
	 */
	public void convertToXML(GeneProduct geneProduct, OutputStream outputStream);
	
	/**
	 * Convert a Gene Product into JSON format
	 * @param geneProduct Gene product to transform
	 * @param outputStream Output stream
	 */
	public void convertToJSON(GeneProduct geneProduct, OutputStream outputStream);
	
	/**
	 * Auto suggest functionality 
	 * @param query Query to search for
	 * @param filterQuery Filter query
	 * @param numResults Number resutls to return
	 * @return List of auto suggested gene products with specified query
	 */
	List<GeneProduct> autosuggest(String query, String filterQuery, int numResults);
	
	/**
	 * Highlight functionality
	 * @param text Text to highlight
	 * @param fq Filter query
	 * @param start Start position
	 * @param rows Number of results to retrieve
	 * @return Gene products with highlighted text
	 */
	public List<GeneProduct> highlight(String text, String fq, int start, int rows);
	
	public long getTotalNumberHighlightResults(String text, String fq);
}
