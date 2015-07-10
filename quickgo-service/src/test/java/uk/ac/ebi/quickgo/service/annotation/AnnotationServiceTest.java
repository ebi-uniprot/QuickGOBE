package uk.ac.ebi.quickgo.service.annotation;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import uk.ac.ebi.quickgo.cache.query.service.CacheRetrieval;
import uk.ac.ebi.quickgo.service.annotation.parameter.AnnotationParameters;
import uk.ac.ebi.quickgo.solr.exception.NotFoundException;
import uk.ac.ebi.quickgo.solr.model.annotation.GOAnnotation;
import uk.ac.ebi.quickgo.solr.query.model.annotation.enums.AnnotationField;
import uk.ac.ebi.quickgo.solr.query.service.annotation.AnnotationRetrieval;

/**
 * Tests for {@link AnnotationServiceImpl} class
 * @author cbonill
 *
 */
public class AnnotationServiceTest {

	final AnnotationServiceImpl annotationService = new AnnotationServiceImpl();
	
	private Mockery context = new Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	/**
	 * Not found annotation
	 * @throws NotFoundException
	 */
	@Test
	public void testRetrieveNotFoundAnnotation() throws NotFoundException {
		
		final CacheRetrieval cacheRetrieval = context.mock(CacheRetrieval.class);
		annotationService.annotationCacheRetrieval = cacheRetrieval;
		
		context.checking(new Expectations() {
			{
				allowing(cacheRetrieval).retrieveEntry("1234567", GOAnnotation.class);
				will(throwException(new NotFoundException("Annotation not found")));	
			}
		});
		
		GOAnnotation annotation = annotationService.retrieveAnnotation("1234567");
		assertTrue(annotation.getDb() == null);
		context.assertIsSatisfied();
	}	
	
	/**
	 * Found annotation
	 * @throws NotFoundException
	 */
	@Test
	public void testRetrieveFoundAnnotation() throws NotFoundException {
		
		final String db = "UniProt";
		final CacheRetrieval cacheRetrieval = context.mock(CacheRetrieval.class);
		annotationService.annotationCacheRetrieval = cacheRetrieval;
		final GOAnnotation annotation = new GOAnnotation();
		annotation.setDb(db);
		context.checking(new Expectations() {
			{
				allowing(cacheRetrieval).retrieveEntry(db, GOAnnotation.class);
				will(returnValue(annotation));	
			}
		});
		
		GOAnnotation resAnnotation = annotationService.retrieveAnnotation(db);
		assertTrue(resAnnotation.getDb().equals(db));
		context.assertIsSatisfied();
	}
	
	/**
	 * Retrieve annotations for DBOBJECTID A00001
	 * @throws NotFoundException
	 * @throws SolrServerException 
	 */
	@Test
	public void testRetrieveAnnotationsDbObjectId() throws NotFoundException, SolrServerException {
		
		AnnotationParameters annotationParameters = new AnnotationParameters();
		annotationParameters.addParameter(AnnotationField.DBOBJECTID.name(), Arrays.asList("A00001"));
		final AnnotationRetrieval annotationRetrieval = context.mock(AnnotationRetrieval.class);		
		annotationService.annotationRetrieval = annotationRetrieval;
		
		GOAnnotation annotation = new GOAnnotation();
		annotation.setDbObjectID("A00001");
		final List<GOAnnotation> annotations = Arrays.asList(annotation);
		
		context.checking(new Expectations() {
			{
				allowing(annotationRetrieval).findByQuery("dbObjectID:(A00001) AND *:*",0, -1);
				will(returnValue(annotations));	
			}
		});
		
		List<GOAnnotation> resAnnotations = annotationService.retrieveAnnotations(annotationParameters.toSolrQuery(), 0, -1);
		assertTrue(resAnnotations.equals(annotations));
		context.assertIsSatisfied();
	}
	
	/**
	 * Retrieve annotations for AssignedBy InterPro
	 * @throws NotFoundException
	 * @throws SolrServerException 
	 */
	@Test
	public void testRetrieveAnnotationsAssignedBy() throws NotFoundException, SolrServerException {
		
		AnnotationParameters annotationParameters = new AnnotationParameters();
		annotationParameters.addParameter(AnnotationField.ASSIGNEDBY.name(), Arrays.asList("InterPro"));
		final AnnotationRetrieval annotationRetrieval = context.mock(AnnotationRetrieval.class);		
		annotationService.annotationRetrieval = annotationRetrieval;
		
		GOAnnotation annotation = new GOAnnotation();
		annotation.setAssignedBy("InterPro");
		final List<GOAnnotation> annotations = Arrays.asList(annotation);
		
		context.checking(new Expectations() {
			{
				allowing(annotationRetrieval).findByQuery("assignedBy:(InterPro) AND *:*", 0, -1);
				will(returnValue(annotations));	
			}
		});
		
		List<GOAnnotation> resAnnotations = annotationService.retrieveAnnotations(annotationParameters.toSolrQuery(), 0 ,-1);
		assertTrue(resAnnotations.equals(annotations));
		context.assertIsSatisfied();
	}
}
