package uk.ac.ebi.quickgo.solr.indexing.service.annotation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.stereotype.Service;

import uk.ac.ebi.quickgo.annotation.Annotation;
import uk.ac.ebi.quickgo.solr.indexing.Indexer;
import uk.ac.ebi.quickgo.solr.mapper.SolrMapper;
import uk.ac.ebi.quickgo.solr.model.annotation.SolrAnnotation;
import uk.ac.ebi.quickgo.solr.server.SolrServerProcessor;

/**
 * Index Annotation objects in Solr
 * 
 * @author cbonill
 * 
 */
@Service("annotationIndexer")
public class AnnotationIndexer implements Indexer<Annotation> {

	private SolrServerProcessor solrServerProcessor;

	private SolrMapper<Annotation, SolrAnnotation> solrMapper;

	// Log
	private static final Logger logger = Logger.getLogger(AnnotationIndexer.class);

	/**
	 * See {@link Indexer#index(List)}
	 */
	public void index(List<Annotation> list) {
		Collection<SolrAnnotation> annotationBeans = mapBeans(list);

		try {
			solrServerProcessor.indexBeansAutoCommit(annotationBeans);
		} catch (SolrServerException | IOException e) {
			logger.error(e.getMessage());
		}
	}

	/**
	 * Map Annotations to Solr objects
	 * 
	 * @throws SolrServerException
	 */
	private Collection<SolrAnnotation> mapBeans(List<Annotation> annotations) {

		List<SolrAnnotation> beans = new ArrayList<SolrAnnotation>();

		// Iterate over all the annotations and convert them into Solr objects to be indexed
		for (Annotation annotation : annotations) {
			beans.addAll(solrMapper.toSolrObject(annotation));
		}
		return beans;
	}

	/**
	 * Deletes everything from the schema
	 * @throws SolrServerException
	 * @throws IOException
	 */
	public void deleteAll() throws SolrServerException, IOException{
		solrServerProcessor.deleteAll();		
	}
	
	public void setSolrServerProcessor(SolrServerProcessor solrServerProcessor) {
		this.solrServerProcessor = solrServerProcessor;
	}

	public void setSolrMapper(SolrMapper<Annotation, SolrAnnotation> solrMapper) {
		this.solrMapper = solrMapper;
	}	
}