package uk.ac.ebi.quickgo.solr.indexing.service.geneproduct;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.stereotype.Service;

import uk.ac.ebi.quickgo.geneproduct.GeneProduct;
import uk.ac.ebi.quickgo.solr.indexing.Indexer;
import uk.ac.ebi.quickgo.solr.mapper.SolrMapper;
import uk.ac.ebi.quickgo.solr.model.SolrDocumentType;
import uk.ac.ebi.quickgo.solr.model.geneproduct.SolrGeneProduct;
import uk.ac.ebi.quickgo.solr.model.geneproduct.SolrGeneProduct.SolrGeneProductDocumentType;
import uk.ac.ebi.quickgo.solr.server.SolrServerProcessor;

/**
 * Index Gene Products objects in Solr
 * @author cbonill
 *
 */
@Service("geneProductIndexer")
public class GeneProductIndexer  implements Indexer<GeneProduct>{

	private SolrServerProcessor solrServerProcessor;
		
	private SolrMapper<GeneProduct, SolrGeneProduct> solrMapper;
	
	// Log
	private static final Logger logger = LoggerFactory.getLogger(GeneProductIndexer.class);

	/**
	 * See {@link Indexer#index(List)}
	 */
	public void index(List<GeneProduct> list) {
		index(list, SolrGeneProductDocumentType.getAsInterfaces());		
	}

	/**
	 * Index specified types
	 */
	public void index(List<GeneProduct> list, List<SolrDocumentType> solrDocumentTypes) {
		Collection<SolrGeneProduct> gpBeans = mapBeans(list, solrDocumentTypes);
		try {
			solrServerProcessor.indexBeansAutoCommit(gpBeans);
		} catch (SolrServerException | IOException e) {
			logger.error(e.getMessage());
		}
	}
	
	/**
	 * Deletes everything from the schema
	 * @throws SolrServerException
	 * @throws IOException
	 */
	public void deleteAll() throws SolrServerException, IOException{
		solrServerProcessor.deleteAll();
	}
	
	public void setSolrMapper(SolrMapper<GeneProduct, SolrGeneProduct> solrMapper) {
		this.solrMapper = solrMapper;
	}

	/**
     * Indexes Gene Products in Solr
     * @throws SolrServerException 
     */
	private Collection<SolrGeneProduct> mapBeans(List<GeneProduct> geneProducts, List<SolrDocumentType> solrDocumentTypes) {
		
		List<SolrGeneProduct> beans = new ArrayList<SolrGeneProduct>();
		
		// Iterate over all the gene products and convert them into Solr objects to be indexed
		for (GeneProduct geneProduct : geneProducts) {			
			beans.addAll(solrMapper.toSolrObject(geneProduct, solrDocumentTypes));
    	}
		return beans;
    }

	public void setSolrServerProcessor(SolrServerProcessor solRServerProcessor) {
		this.solrServerProcessor = solRServerProcessor;
	}
}
