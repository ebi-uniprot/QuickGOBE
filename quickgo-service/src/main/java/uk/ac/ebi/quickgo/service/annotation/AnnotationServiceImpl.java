package uk.ac.ebi.quickgo.service.annotation;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField.Count;

import uk.ac.ebi.quickgo.cache.query.service.CacheRetrieval;
import uk.ac.ebi.quickgo.solr.exception.NotFoundException;
import uk.ac.ebi.quickgo.solr.model.annotation.GOAnnotation;
import uk.ac.ebi.quickgo.solr.query.service.annotation.AnnotationRetrieval;

/**
 * Annotation service implementation
 * @author cbonill
 *
 */
public class AnnotationServiceImpl implements AnnotationService {

	// Log
	private static final Logger logger = LoggerFactory.getLogger(AnnotationServiceImpl.class);
	
	CacheRetrieval<GOAnnotation> annotationCacheRetrieval;
	
	AnnotationRetrieval annotationRetrieval;
	
	@Override
	public GOAnnotation retrieveAnnotation(String id) {
		GOAnnotation annotation = new GOAnnotation();
		try {
			annotation = annotationCacheRetrieval.retrieveEntry(id, GOAnnotation.class);
		} catch (NotFoundException e) {			
			logger.error(e.getMessage());
		}
		return annotation;
	}

	@Override
	public List<GOAnnotation> retrieveAnnotations(String query, int start, int rows) {
		List<GOAnnotation> annotations = new ArrayList<>();
		try {
			annotations = annotationRetrieval.findByQuery(query, start, rows);
		} catch (SolrServerException e) {
			logger.error(e.getMessage());
		}
		return annotations;
	}

	@Override
	public List<GOAnnotation> retrieveAll() {
		List<GOAnnotation> annotations = new ArrayList<>();
		try {
			annotations = annotationRetrieval.findAll();
		} catch (SolrServerException e) {
			logger.error(e.getMessage());
		}
		return annotations;
	}

	@Override
	public List<Count> getFacetFields(String query, String facetQuery, String facetFields, int numTerms) {
		List<Count> facets = new ArrayList<>();
		try {
			facets = annotationRetrieval.getFacetFields(query, facetQuery, facetFields, numTerms);
		} catch (SolrServerException e) {
			logger.error(e.getMessage());
		}
		return facets;
	}

	@Override
	public long getTotalNumberAnnotations(String query) {
		try {
			return annotationRetrieval.getTotalNumberAnnotations(query);
		} catch (SolrServerException e) {
			logger.error(e.getMessage());
		}
		return -1;
	}

	@Override
	public long getTotalNumberProteins(String query) {
		try {
			return annotationRetrieval.getTotalNumberProteins(query);
		} catch (SolrServerException e) {
			logger.error(e.getMessage());
		}
		return -1;
	}

	public AnnotationRetrieval getAnnotationRetrieval() {
		return annotationRetrieval;
	}

	public void setAnnotationRetrieval(AnnotationRetrieval annotationRetrieval) {
		this.annotationRetrieval = annotationRetrieval;
	}

	public CacheRetrieval<GOAnnotation> getAnnotationCacheRetrieval() {
		return annotationCacheRetrieval;
	}

	public void setAnnotationCacheRetrieval(CacheRetrieval<GOAnnotation> annotationCacheRetrieval) {
		this.annotationCacheRetrieval = annotationCacheRetrieval;
	}	
}
