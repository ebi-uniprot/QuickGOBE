package uk.ac.ebi.quickgo.geneproduct.service.converter;

import uk.ac.ebi.quickgo.geneproduct.common.document.GeneProductDocument;
import uk.ac.ebi.quickgo.geneproduct.common.document.GeneProductType;
import uk.ac.ebi.quickgo.geneproduct.model.GeneProduct;

import java.util.ArrayList;

/**
 * Turn a GeneProduct document from Solr into a model to be used by the RESTful service.
 *
 * @author Tony Wardell
 * Date: 01/04/2016
 * Time: 14:31
 * Created with IntelliJ IDEA.
 */
public class GeneProductDocConverterImpl implements GeneProductDocConverter {

	/**
	 * Convert a Gene Product Document from Solr into a model to be returned to the user
	 * @param geneProductDocument
	 * @return
	 */
	@Override
	public GeneProduct convert(GeneProductDocument geneProductDocument) {

		GeneProduct geneProduct			= new GeneProduct();
		geneProduct.database 			= geneProductDocument.database;
		geneProduct.databaseSubset 		= new ArrayList<>(geneProductDocument.databaseSubsets);
		geneProduct.id 					= geneProductDocument.id;
		geneProduct.isIsoform			= geneProductDocument.isIsoform;
		geneProduct.name				= geneProductDocument.name;
		geneProduct.referenceProteome 	= geneProductDocument.referenceProteome;
		geneProduct.synonyms 			= new ArrayList<>(geneProductDocument.synonyms);
		geneProduct.symbol				= geneProductDocument.symbol;
		geneProduct.taxonomy			= geneProductDocument.taxonId == 0? null : new GeneProduct.Taxonomy(geneProductDocument.taxonId, geneProductDocument.taxonName);
		geneProduct.type				= GeneProductType.typeOf(geneProductDocument.type);
		geneProduct.isCompleteProteome	= geneProductDocument.isCompleteProteome;
		geneProduct.parentId			= geneProductDocument.parentId;

		return geneProduct;
	}
}
