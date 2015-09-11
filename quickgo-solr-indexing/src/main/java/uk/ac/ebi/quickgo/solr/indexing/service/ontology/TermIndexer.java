package uk.ac.ebi.quickgo.solr.indexing.service.ontology;

import java.io.IOException;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.stereotype.Service;

import uk.ac.ebi.quickgo.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.solr.indexing.Indexer;
import uk.ac.ebi.quickgo.solr.mapper.SolrMapper;
import uk.ac.ebi.quickgo.solr.model.ontology.SolrTerm;
import uk.ac.ebi.quickgo.solr.model.ontology.SolrTermComparator;
import uk.ac.ebi.quickgo.solr.server.SolrServerProcessor;

/**
 * Index Term objects in Solr
 * @author cbonill
 *
 */
@Service("termIndexer")
public class TermIndexer implements Indexer<GenericTerm> {

	private SolrServerProcessor solrServerProcessor;

	private SolrMapper<GenericTerm, SolrTerm> solrMapper;

	// Log
	private static final Logger logger = LoggerFactory.getLogger(TermIndexer.class);
	private Properties properties;

	/**
	 * See {@link Indexer#index(List)}
	 */
	public void index(List<GenericTerm> list) {
		Collection<SolrTerm> termBeans = mapBeans(list);
		try {
			solrServerProcessor.indexBeans(termBeans);
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


	public void setSolrMapper(SolrMapper<GenericTerm, SolrTerm> solrMapper) {
		this.solrMapper = solrMapper;
	}

	/**
     * Indexes GO Terms in SolR
     * @throws SolrServerException
     */
	private Collection<SolrTerm> mapBeans(List<GenericTerm> terms) {

		// TreeSet to avoid duplicated Relations
		Set<SolrTerm> beans = new TreeSet<SolrTerm>(new SolrTermComparator());
		//  Iterate over all the GO terms and convert them into Solr objects to be indexed
		for (GenericTerm term : terms) {
			beans.addAll(solrMapper.toSolrObject(term));
    	}
		return beans;
    }

	public void setSolrServerProcessor(SolrServerProcessor solRServerProcessor) {
		this.solrServerProcessor = solRServerProcessor;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
		solrServerProcessor.setProperties(properties);
	}
}
