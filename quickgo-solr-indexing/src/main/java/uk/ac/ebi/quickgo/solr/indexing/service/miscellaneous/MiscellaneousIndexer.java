package uk.ac.ebi.quickgo.solr.indexing.service.miscellaneous;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.stereotype.Service;

import uk.ac.ebi.quickgo.miscellaneous.Miscellaneous;
import uk.ac.ebi.quickgo.solr.indexing.Indexer;
import uk.ac.ebi.quickgo.solr.mapper.SolrMapper;
import uk.ac.ebi.quickgo.solr.model.SolrDocumentType;
import uk.ac.ebi.quickgo.solr.model.miscellaneous.SolrMiscellaneous;
import uk.ac.ebi.quickgo.solr.model.miscellaneous.SolrMiscellaneous.SolrMiscellaneousDocumentType;
import uk.ac.ebi.quickgo.solr.server.SolrServerProcessor;

/**
 * Index Miscellaneous data in Solr
 * @author cbonill
 *
 */
@Service("miscellaneousIndexer")
public class MiscellaneousIndexer implements Indexer<Miscellaneous>{

	private SolrServerProcessor solrServerProcessor;
	
	private SolrMapper<Miscellaneous, SolrMiscellaneous> solrMapper;
		
	// Log
	private static final Logger logger = LoggerFactory.getLogger(MiscellaneousIndexer.class);
	
	@Override
	public void index(List<Miscellaneous> list) {
		index(list, SolrMiscellaneousDocumentType.getAsInterfaces());		
	}

	/**
	 * Index specified types
	 */
	public void index(List<Miscellaneous> list, List<SolrDocumentType> solrDocumentTypes) {
		Collection<SolrMiscellaneous> gpBeans = mapBeans(list, solrDocumentTypes);
		try {
			solrServerProcessor.indexBeans(gpBeans);
		} catch (SolrServerException | IOException e) {
			logger.error(e.getMessage());
		}
	}
	
	/**
     * Indexes Miscellaneous data in Solr
     * @throws SolrServerException 
     */
	private Collection<SolrMiscellaneous> mapBeans(List<Miscellaneous> miscellaneous, List<SolrDocumentType> solrMiscellaneousDocumentTypes) {
		
		List<SolrMiscellaneous> beans = new ArrayList<SolrMiscellaneous>();
		
		//  Iterate over all the miscellaneous data and convert it into Solr objects to be indexed
		for (Miscellaneous misc : miscellaneous) {			
			beans.addAll(solrMapper.toSolrObject(misc, solrMiscellaneousDocumentTypes));
    	}
		return beans;
    }

	/**
	 * Deletes all the miscellaneous information
	 */
	public void deleteAll(){
		try {
			solrServerProcessor.deleteAll();
		} catch (SolrServerException | IOException e) {
			logger.error(e.getMessage());
		}
	}
	
	/**
	 * Deletes by query
	 */
	public void deleteByQuery(String query){
		try {
			solrServerProcessor.deleteByQuery(query);
		} catch (SolrServerException | IOException e) {
			logger.error(e.getMessage());
		}
	}
	
	public void setSolrServerProcessor(SolrServerProcessor solrServerProcessor) {
		this.solrServerProcessor = solrServerProcessor;
	}

	public void setSolrMapper(SolrMapper<Miscellaneous, SolrMiscellaneous> solrMapper) {
		this.solrMapper = solrMapper;
	}	
}
