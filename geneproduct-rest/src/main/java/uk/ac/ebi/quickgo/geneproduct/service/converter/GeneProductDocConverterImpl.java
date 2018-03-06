package uk.ac.ebi.quickgo.geneproduct.service.converter;

import uk.ac.ebi.quickgo.geneproduct.common.GeneProductDocument;
import uk.ac.ebi.quickgo.geneproduct.common.GeneProductType;
import uk.ac.ebi.quickgo.geneproduct.model.GeneProduct;

/**
 * The responsibility of this class is to convert a {@link GeneProductDocument} into a
 * domain model instance, e.g., for use by the RESTful service.
 *
 * @author Tony Wardell
 * Date: 01/04/2016
 * Time: 14:31
 * Created with IntelliJ IDEA.
 */
public class GeneProductDocConverterImpl implements GeneProductDocConverter {

    static final int DEFAULT_TAXON_ID = 0;

    /**
     * Converts a {@link GeneProductDocument} into a domain model instance
     *
     * @param geneProductDocument the document to convert
     * @return the converted document
     */
    @Override
    public GeneProduct convert(GeneProductDocument geneProductDocument) {
        GeneProduct geneProduct = new GeneProduct();

        geneProduct.id = geneProductDocument.id;
        geneProduct.database = geneProductDocument.database;
        geneProduct.databaseSubset = geneProductDocument.databaseSubset;
        geneProduct.isAnnotated = geneProductDocument.isAnnotated;
        geneProduct.synonyms = geneProductDocument.synonyms;
        geneProduct.isIsoform = geneProductDocument.isIsoform;
        geneProduct.name = geneProductDocument.name;
        geneProduct.referenceProteome = geneProductDocument.referenceProteome;
        geneProduct.isCompleteProteome = geneProductDocument.isCompleteProteome;
        geneProduct.proteomeMembership = geneProductDocument.proteomeMembership;
        geneProduct.parentId = geneProductDocument.parentId;
        geneProduct.symbol = geneProductDocument.symbol;
        geneProduct.taxonId = geneProductDocument.taxonId;
        geneProduct.type = GeneProductType.typeOf(geneProductDocument.type);

        return geneProduct;
    }
}
