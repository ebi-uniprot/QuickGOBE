package uk.ac.ebi.quickgo.solr.mapper.geneproduct;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

import uk.ac.ebi.quickgo.geneproduct.GeneProduct;
import uk.ac.ebi.quickgo.geneproduct.GeneProduct.GeneProductProperty;
import uk.ac.ebi.quickgo.solr.mapper.SolrMapper;
import uk.ac.ebi.quickgo.solr.model.SolrDocumentType;
import uk.ac.ebi.quickgo.solr.model.geneproduct.SolrGeneProduct;
import uk.ac.ebi.quickgo.solr.model.geneproduct.SolrGeneProduct.SolrGeneProductDocumentType;
import uk.ac.ebi.quickgo.util.XRef;

/**
 * To map Gene Products entities into Solr ones
 * @author cbonill
 *
 */
@Service("solrGeneProductMapper")
public class SolrGeneProductMapper implements SolrMapper<GeneProduct, SolrGeneProduct> {

	@Override
	public Collection<SolrGeneProduct> toSolrObject(GeneProduct genericObject) {
		return toSolrObject(genericObject, SolrGeneProductDocumentType.getAsInterfaces());
	}

	@Override
	public Collection<SolrGeneProduct> toSolrObject(GeneProduct geneProduct,
			List<SolrDocumentType> solrDocumentTypes) {
		
		List<SolrGeneProduct> solrGeneProducts = new ArrayList<SolrGeneProduct>();
		
		for (SolrDocumentType gpDocumentType : solrDocumentTypes) {
			SolrGeneProductDocumentType solrGPDocumentType = ((SolrGeneProductDocumentType) gpDocumentType);

			switch (solrGPDocumentType) {

			case GENEPRODUCT:
				solrGeneProducts.add(mapBasicInformation(geneProduct));
				break;
			case  PROPERTY:
				solrGeneProducts.addAll(mapProperties(geneProduct));
				break;
			case  XREF:
				solrGeneProducts.addAll(mapXrefs(geneProduct));
				break;
			}
		}
		return solrGeneProducts;
	}

	/**
     * Map Solr Gene Product for the basic information of a gene product object
     * @param geneProduct Gene Product
     * @return Solr Gene Product to be indexed
     */
	private SolrGeneProduct mapBasicInformation(GeneProduct geneProduct) {
		SolrGeneProduct solrGeneProduct = new SolrGeneProduct();
		solrGeneProduct.setDocType(SolrGeneProductDocumentType.GENEPRODUCT.getValue());
		solrGeneProduct.setDb(geneProduct.getDb());
		solrGeneProduct.setDbObjectId(geneProduct.getDbObjectId());
		solrGeneProduct.setDbObjectName(geneProduct.getDbObjectName());
		solrGeneProduct.setDbObjectSymbol(geneProduct.getDbObjectSymbol());
		solrGeneProduct.setDbObjectType(geneProduct.getDbObjectType());
		solrGeneProduct.setDbObjectSynonyms(geneProduct.getDbObjectSynonyms());
		solrGeneProduct.setTaxonId(geneProduct.getTaxonId());
		
		return solrGeneProduct;
	}
	
	/**
	 * Map gene product properties
	 * @param geneProduct Gene product object
	 * @return List of Solr Gene Products to index
	 */
	private Collection<SolrGeneProduct> mapProperties(GeneProduct geneProduct){
		Collection<SolrGeneProduct> solrGeneProducts = new ArrayList<>();		
		for(GeneProductProperty geneProductProperty : geneProduct.getGeneProductProperties()){
			SolrGeneProduct solrGeneProduct = new SolrGeneProduct();
			solrGeneProduct.setDbObjectId(geneProduct.getDbObjectId());
			solrGeneProduct.setDocType(SolrGeneProductDocumentType.PROPERTY.getValue());
			solrGeneProduct.setPropertyName(geneProductProperty.getPropertyName());
			solrGeneProduct.setPropertyValue(geneProductProperty.getPropertyValue());
			solrGeneProducts.add(solrGeneProduct);
		}
		return solrGeneProducts;
	}
	
	/**
	 * Map gene product xrefs
	 * @param geneProduct Gene product object
	 * @return List of Solr Gene Products to index
	 */
	private Collection<SolrGeneProduct> mapXrefs(GeneProduct geneProduct) {
		Collection<SolrGeneProduct> solrGeneProducts = new ArrayList<>();		
		for(XRef xRef : geneProduct.getXRefs()){
			SolrGeneProduct solrGeneProduct = new SolrGeneProduct();
			solrGeneProduct.setDbObjectId(geneProduct.getDbObjectId());
			solrGeneProduct.setDocType(SolrGeneProductDocumentType.XREF.getValue());
			solrGeneProduct.setXrefDb(xRef.getDb());
			solrGeneProduct.setXrefId(xRef.getId());
			solrGeneProducts.add(solrGeneProduct);
		}
		return solrGeneProducts;		
	}
}