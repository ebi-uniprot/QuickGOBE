package uk.ac.ebi.quickgo.geneproduct.service;

import uk.ac.ebi.quickgo.geneproduct.model.GeneProduct;

import java.util.List;

/**
 * @Author Tony Wardell
 * Date: 29/03/2016
 * Time: 10:55
 * Created with IntelliJ IDEA.
 */
public interface GeneProductService {


	/**
	 * Find the core data set stored for a specified list of geneProduct IDs.
	 * @param ids the ontology IDs
	 * @return a {@link List} of {@link GeneProduct} instances corresponding to the gene product ids containing the
	 * chosen information
	 */
	List<GeneProduct> findById(String[] ids);
}
