package uk.ac.ebi.quickgo.geneproduct.service.converter;

import uk.ac.ebi.quickgo.geneproduct.common.GeneProductDocument;
import uk.ac.ebi.quickgo.geneproduct.model.GeneProduct;

/**
 * The responsibility of this class is to convert a {@link GeneProductDocument} into a
 * domain model instance, e.g., for use by the RESTful service.
 *
 * @author Tony Wardell
 * Date: 29/03/2016
 * Time: 11:25
 * Created with IntelliJ IDEA.
 */
public interface GeneProductDocConverter {

    /**
     * Converts a {@link GeneProductDocument} into a domain model instance
     *
     * @param geneProductDocument the document to convert
     * @return the converted document
     */
    GeneProduct convert(GeneProductDocument geneProductDocument);
}
