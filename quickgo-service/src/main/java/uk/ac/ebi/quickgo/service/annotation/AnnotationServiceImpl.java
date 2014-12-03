package uk.ac.ebi.quickgo.service.annotation;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField.Count;

import uk.ac.ebi.quickgo.annotation.Annotation;
import uk.ac.ebi.quickgo.cache.query.service.CacheRetrieval;
import uk.ac.ebi.quickgo.solr.exception.NotFoundException;
import uk.ac.ebi.quickgo.solr.query.service.annotation.AnnotationRetrieval;

/**
 * Annotation service implementation
 * @author cbonill
 *
 */
public class AnnotationServiceImpl implements AnnotationService {

	// Log
	private static final Logger logger = Logger.getLogger(AnnotationServiceImpl.class);
	
	CacheRetrieval<Annotation> annotationCacheRetrieval;
	
	AnnotationRetrieval annotationRetrieval;
	
	@Override
	public Annotation retrieveAnnotation(String id) {
		Annotation annotation = new Annotation();		
		try {
			annotation = annotationCacheRetrieval.retrieveEntry(id, Annotation.class);
		} catch (NotFoundException e) {			
			logger.error(e.getMessage());
		}
		return annotation;
	}

	@Override
	public List<Annotation> retrieveAnnotations(String query, int start, int rows) {
		List<Annotation> annotations = new ArrayList<>();
		try {
			annotations = annotationRetrieval.findByQuery(query, start, rows);
		} catch (SolrServerException e) {
			logger.error(e.getMessage());
		}
		return annotations;
	}

	@Override
	public List<Annotation> retrieveAll() {
		List<Annotation> annotations = new ArrayList<>();
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

	public CacheRetrieval<Annotation> getAnnotationCacheRetrieval() {
		return annotationCacheRetrieval;
	}

	public void setAnnotationCacheRetrieval(
			CacheRetrieval<Annotation> annotationCacheRetrieval) {
		this.annotationCacheRetrieval = annotationCacheRetrieval;
	}	
}