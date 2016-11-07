package uk.ac.ebi.quickgo.geneproduct.service;

import uk.ac.ebi.quickgo.geneproduct.model.GeneProduct;

import java.util.List;

/**
 * @author Tony Wardell
 * Date: 29/03/2016
 * Time: 10:55
 * Created with IntelliJ IDEA.
 */
public interface GeneProductService {


	/**
	 * Find the core data set stored for a specified list of geneProduct IDs.
	 * @param ids the gene product IDs
	 * @return a {@link List} of {@link GeneProduct} instances corresponding to the gene product ids containing the
	 * chosen information
	 */
	List<GeneProduct> findById(List<String> ids);

	/**
	 * Find gene products core data for all gene products associated with the named target set.
	 * @param name value to match for target gene product set name.
	 * @return a list of gene product instances that match the target set name, or empty list if no matches are found.
	 */
    List<GeneProduct> findByTargetSet(String name);
}
