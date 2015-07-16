package uk.ac.ebi.quickgo.solr.indexing.service.annotation;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.stereotype.Service;

import uk.ac.ebi.quickgo.solr.indexing.Indexer;
import uk.ac.ebi.quickgo.solr.model.annotation.GOAnnotation;
import uk.ac.ebi.quickgo.solr.server.SolrServerProcessor;

/**
 * Index Annotation objects in Solr
 *
 * @author cbonill
 *
 */
@Service("annotationIndexer")
public class AnnotationIndexer implements Indexer<GOAnnotation> {

	private SolrServerProcessor solrServerProcessor;

	// Log
	private static final Logger logger = LoggerFactory.getLogger(AnnotationIndexer.class);

	/**
	 * See {@link Indexer#index(List)}
	 */
	public void index(List<GOAnnotation> list) {
		//Collection<GOAnnotation> annotationBeans = mapBeans(list);

		try {
			solrServerProcessor.indexBeansAutoCommit(list);
		} catch (SolrServerException | IOException e) {
			logger.error(e.getMessage());
		}
	}

	/**
	 * Map Annotations to Solr objects
	 */
//	private Collection<GOAnnotation> mapBeans(List<GOAnnotation> annotations) {
//		// Iterate over all the annotations and convert them into Solr objects to be indexed
//		for (GOAnnotation annotation : annotations) {
//			annotation.setDocType(GOAnnotation.SolrAnnotationDocumentType.ANNOTATION.getValue());
//		}
//		return annotations;
//	}

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
}
