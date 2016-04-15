package uk.ac.ebi.quickgo.geneproduct.service.converter;

import uk.ac.ebi.quickgo.geneproduct.common.document.GeneProductDocument;
import uk.ac.ebi.quickgo.geneproduct.model.GeneProduct;

/**
 * @author Tony Wardell
 * Date: 29/03/2016
 * Time: 11:25
 * Created with IntelliJ IDEA.
 */
public interface GeneProductDocConverter {

	/**
	 * Convert a Gene Product Document from Solr into a model to be returned to the user
	 * @param geneProductDocument
	 * @return
	 */
	GeneProduct convert(GeneProductDocument geneProductDocument);

}
