package uk.ac.ebi.quickgo.solr.mapper.geneproduct;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import uk.ac.ebi.quickgo.geneproduct.GeneProduct;
import uk.ac.ebi.quickgo.solr.mapper.EntityMapper;
import uk.ac.ebi.quickgo.solr.model.SolrDocumentType;
import uk.ac.ebi.quickgo.solr.model.geneproduct.SolrGeneProduct;
import uk.ac.ebi.quickgo.solr.model.geneproduct.SolrGeneProduct.SolrGeneProductDocumentType;
import uk.ac.ebi.quickgo.util.KeyValuePair;
import uk.ac.ebi.quickgo.util.XRef;

/**
 * For converting Solr gene products into GeneProduct entities
 * @author cbonill
 *
 */
public class EntityGeneProductMapper implements EntityMapper<SolrGeneProduct, GeneProduct> {

	@Override
	public GeneProduct toEntityObject(Collection<SolrGeneProduct> solrObjects) {
		return toEntityObject(solrObjects, SolrGeneProductDocumentType.getAsInterfaces());
	}

	@Override
	public GeneProduct toEntityObject(Collection<SolrGeneProduct> solrObjects,
			List<SolrDocumentType> solrDocumentTypes) {
		
		GeneProduct geneProduct = new GeneProduct();

		for (SolrDocumentType gpDocumentType : solrDocumentTypes) {
			SolrGeneProductDocumentType solrGeneProductDocumentType = ((SolrGeneProductDocumentType) gpDocumentType);

			switch (solrGeneProductDocumentType) {

			case GENEPRODUCT:
				if(getAssociatedSolrTerms(solrObjects, SolrGeneProductDocumentType.GENEPRODUCT).size() > 0){
					mapBasicInformation(
							getAssociatedSolrTerms(solrObjects, SolrGeneProductDocumentType.GENEPRODUCT).get(0),
							geneProduct);
				}
				break;
			case PROPERTY:
				mapProperty(
						getAssociatedSolrTerms(solrObjects,
								SolrGeneProductDocumentType.PROPERTY),
						geneProduct);
				break;
			case XREF:
				mapXrefs(
						getAssociatedSolrTerms(solrObjects,
								SolrGeneProductDocumentType.XREF), geneProduct);
				break;
			}
		}
		return geneProduct;
	}	


	/**
	 * Map basic information of gene product Solr object to GeneProduct one
	 * @param solrGeneProduct Solr gene product
	 * @param geneProduct Gene product to return
	 */
	
	private void mapBasicInformation(SolrGeneProduct solrGeneProduct,
			GeneProduct geneProduct) {
		geneProduct.setDb(solrGeneProduct.getDb());
		geneProduct.setDbObjectId(solrGeneProduct.getDbObjectId());
		geneProduct.setDbObjectSymbol(solrGeneProduct.getDbObjectSymbol());
		geneProduct.setDbObjectName(solrGeneProduct.getDbObjectName());				
		geneProduct.setDbObjectSynonyms(solrGeneProduct.getDbObjectSynonyms());	
		geneProduct.setDbObjectType(solrGeneProduct.getDbObjectType());
		geneProduct.setTaxonId(solrGeneProduct.getTaxonId());
	}

	/**
	 * Map gene product Solr properties to a GeneProduct object
	 * @param associatedSolrTerms Solr gene product properties
	 * @param geneProduct GeneProduct
	 */
	private void mapProperty(List<SolrGeneProduct> associatedSolrTerms, GeneProduct geneProduct) {
		List<KeyValuePair> geneProductProperties = new ArrayList<>();
		for (SolrGeneProduct gpProperty : associatedSolrTerms) {
			if (geneProduct.getDbObjectId() == null) {
				geneProduct.setDbObjectId(gpProperty.getDbObjectId());
			}
			KeyValuePair geneProductProperty = new KeyValuePair(gpProperty.getPropertyName(), gpProperty.getPropertyValue());
			geneProductProperties.add(geneProductProperty);
		}
		geneProduct.setGeneProductProperties(geneProductProperties);
	}

	/**
	 * Map gene product Solr xrefs to a GeneProduct object
	 * @param associatedSolrTerms Solr gene product xrefs
	 * @param geneProduct GeneProduct
	 */
	private void mapXrefs(List<SolrGeneProduct> associatedSolrTerms,
			GeneProduct geneProduct) {
		List<XRef> xRefs = new ArrayList<>();
		for (SolrGeneProduct gpXref : associatedSolrTerms) {
			XRef ref = new XRef(gpXref.getXrefDb(), gpXref.getXrefId());
			xRefs.add(ref);
		}		
		if(associatedSolrTerms.size() > 0){
			geneProduct.setDbObjectId(associatedSolrTerms.get(0).getDbObjectId());
		}
		geneProduct.setXRefs(xRefs);
	}

	
	/**
	 * Given a list of Solr gene products, returns the ones that match with the
	 * specified document type
	 * 
	 * @param solrObjects
	 *            Solr gene product objects
	 * @param solrGeneProductDocumentType
	 *            Type to check
	 * @return Solr gene products that match with the specified document type
	 */
	protected List<SolrGeneProduct> getAssociatedSolrTerms(Collection<SolrGeneProduct> solrObjects,
			SolrGeneProductDocumentType solrGeneProductDocumentType) {
		List<SolrGeneProduct> solrGeneProducts = new ArrayList<>();
		for (SolrGeneProduct solrGeneProduct : solrObjects) {
			if (SolrGeneProductDocumentType.valueOf(solrGeneProduct.getDocType().toUpperCase()) == solrGeneProductDocumentType) {
				solrGeneProducts.add(solrGeneProduct);
			}
		}
		return solrGeneProducts;
	}

}
