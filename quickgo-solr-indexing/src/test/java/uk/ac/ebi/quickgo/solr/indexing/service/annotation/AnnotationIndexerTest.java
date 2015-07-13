package uk.ac.ebi.quickgo.solr.indexing.service.annotation;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import uk.ac.ebi.quickgo.solr.mapper.annotation.SolrAnnotationMapper;
import uk.ac.ebi.quickgo.solr.model.annotation.GOAnnotation;
import uk.ac.ebi.quickgo.solr.server.SolrServerProcessor;

/**
 * Tests for the AnnotationIndexer class
 * @author cbonill
 *
 */
public class AnnotationIndexerTest {

	private Mockery context = new Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	// Mock context
	private AnnotationIndexer annotationIndexer;
	private SolrServerProcessor solrServerProcessor;

	@Before
	public void before() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		annotationIndexer = new AnnotationIndexer();

		// Mock
		solrServerProcessor = context.mock(SolrServerProcessor.class);
		SolrAnnotationMapper solrMapper = context.mock(SolrAnnotationMapper.class);

		// Set cacheBuilder value in cacheRetrieval
		Field fieldCurrencyServices = annotationIndexer.getClass().getDeclaredField("solrServerProcessor");
		fieldCurrencyServices.setAccessible(true);
		fieldCurrencyServices.set(annotationIndexer, solrServerProcessor);

		//todo why is the solrMapper field not available.
//		fieldCurrencyServices = annotationIndexer.getClass().getDeclaredField("solrMapper");
//		fieldCurrencyServices.setAccessible(true);
//		fieldCurrencyServices.set(annotationIndexer, solrMapper);
	}

	/**
	 * Empty list
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testIndexEmptyList() throws Exception {

		context.checking(new Expectations() {
			{
				allowing(solrServerProcessor).indexBeansAutoCommit(new ArrayList());
				will(returnValue(null));
			}
		});

		annotationIndexer.index(new ArrayList<GOAnnotation>());
		context.assertIsSatisfied();
	}

	/**
	 * Index 1 Annotation
	 * @throws Exception
	 */
/*
	@Test
	public void testIndex1Annotation() throws Exception {

		final Annotation annotation = new Annotation();
		annotation.setDbObjectID("A00000");
		final SolrAnnotation solrAnnotation = new SolrAnnotation();

		context.checking(new Expectations() {
			{
				allowing(solrMapper).toSolrObject(annotation);
				will(returnValue(Arrays.asList(solrAnnotation)));

				allowing(solrServerProcessor).indexBeansAutoCommit(with(Arrays.asList(solrAnnotation)));
			}
		});

		annotationIndexer.index(Arrays.asList(annotation));
		context.assertIsSatisfied();
	}
*/
}
