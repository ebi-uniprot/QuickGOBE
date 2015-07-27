package uk.ac.ebi.quickgo.solr.indexing.service.ontology;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import uk.ac.ebi.quickgo.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.solr.mapper.term.go.GOTermToSolrMapper;
import uk.ac.ebi.quickgo.solr.model.ontology.SolrTerm;
import uk.ac.ebi.quickgo.solr.server.SolrServerProcessor;

/**
 * Tests for the TermIndexer class
 *
 * @author cbonill
 *
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class TermIndexerTest {

	private Mockery context = new Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	// Mock context
	private TermIndexer termIndexer;
	private SolrServerProcessor solrServerProcessor;
	private GOTermToSolrMapper solrMapper;

	@Before
	public void before() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		termIndexer = new TermIndexer();

		// Mock
		solrServerProcessor = context.mock(SolrServerProcessor.class);
		solrMapper = context.mock(GOTermToSolrMapper.class);

		// Set cacheBuilder value in cacheRetrieval
		Field fieldCurrencyServices = termIndexer.getClass().getDeclaredField("solrServerProcessor");
		fieldCurrencyServices.setAccessible(true);
		fieldCurrencyServices.set(termIndexer, solrServerProcessor);

		fieldCurrencyServices = termIndexer.getClass().getDeclaredField("solrMapper");
		fieldCurrencyServices.setAccessible(true);
		fieldCurrencyServices.set(termIndexer, solrMapper);
	}

	/**
	 * Empty list
	 * @throws Exception
	 */
	@Test
	public void testIndexEmptyList() throws Exception {

		context.checking(new Expectations() {
			{
				allowing(solrServerProcessor).indexBeans(new TreeSet<GOTerm>());
				will(returnValue(null));
			}
		});

		termIndexer.index(new ArrayList<GenericTerm>());
		context.assertIsSatisfied();
	}


	/**
	 * Index 1 term
	 * @throws Exception
	 */
	@Test
	public void testIndex1Term() throws Exception {

		final GenericTerm term = new GOTerm("GO:00001234", "name","P","false");
		final SolrTerm solrTerm = new SolrTerm();

		context.checking(new Expectations() {
			{
				allowing(solrMapper).toSolrObject(term);
				will(returnValue(Arrays.asList(solrTerm)));

				allowing(solrServerProcessor).indexBeans(with(any(TreeSet.class)));
			}
		});

		termIndexer.index(Arrays.asList(term));
		context.assertIsSatisfied();
	}

}
