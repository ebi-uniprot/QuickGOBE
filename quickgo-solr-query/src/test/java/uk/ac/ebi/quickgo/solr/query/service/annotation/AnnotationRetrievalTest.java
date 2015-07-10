package uk.ac.ebi.quickgo.solr.query.service.annotation;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import uk.ac.ebi.quickgo.solr.mapper.EntityMapper;
import uk.ac.ebi.quickgo.solr.model.annotation.GOAnnotation;
import uk.ac.ebi.quickgo.solr.server.SolrServerProcessor;

/**
 * Tests for the AnnotationRetrieval class
 * 
 * @author cbonill
 * 
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class AnnotationRetrievalTest {

	// Mock context
	private Mockery context;
	private AnnotationRetrieval annotationRetrieval;
	private SolrServerProcessor solRServerProcessor;
	private EntityMapper<GOAnnotation, GOAnnotation> gpEntityMapper;

	@Before
	public void before() throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		context = new JUnit4Mockery();
		annotationRetrieval = new AnnotationRetrievalImpl();

		// Mock
		solRServerProcessor = context.mock(SolrServerProcessor.class);
		gpEntityMapper = context.mock(EntityMapper.class);

		// Set cacheBuilder value in cacheRetrieval
		Field fieldCurrencyServices = annotationRetrieval.getClass().getDeclaredField("annotationServerProcessor");
		fieldCurrencyServices.setAccessible(true);
		fieldCurrencyServices.set(annotationRetrieval, solRServerProcessor);

		fieldCurrencyServices = annotationRetrieval.getClass().getDeclaredField("annotationEntityMapper");
		fieldCurrencyServices.setAccessible(true);
		fieldCurrencyServices.set(annotationRetrieval, gpEntityMapper);
	}

	/**
	 * Get top terms
	 */
	@Test
	public void testgetTopTerms() throws SolrServerException {

		String termFields = "goID";
		int numRows = 10;

		context.checking(new Expectations() {
			{
				allowing(solRServerProcessor).getTopTerms(with(any(SolrQuery.class)));
				will(returnValue(new ArrayList<>()));
			}
		});

		annotationRetrieval.getTopTerms(termFields, numRows);
		context.assertIsSatisfied();
	}
}