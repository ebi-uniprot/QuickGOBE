package uk.ac.ebi.quickgo.solr.indexing.service.geneproduct;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

import uk.ac.ebi.quickgo.geneproduct.GeneProduct;
import uk.ac.ebi.quickgo.solr.mapper.geneproduct.SolrGeneProductMapper;
import uk.ac.ebi.quickgo.solr.model.geneproduct.SolrGeneProduct;
import uk.ac.ebi.quickgo.solr.model.geneproduct.SolrGeneProduct.SolrGeneProductDocumentType;
import uk.ac.ebi.quickgo.solr.server.SolrServerProcessor;

/**
 * Tests for he GeneProductIndexer class
 * @author cbonill
 *
 */
public class GeneProductIndexerTest {

	private Mockery context = new Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};
	
	// Mock context	
	private GeneProductIndexer gpIndexer;
	private SolrServerProcessor solrServerProcessor;
	private SolrGeneProductMapper solrMapper;
	
	@Before
	public void before() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		gpIndexer = new GeneProductIndexer();

		// Mock
		solrServerProcessor = context.mock(SolrServerProcessor.class);
		solrMapper = context.mock(SolrGeneProductMapper.class);
		
		// Set cacheBuilder value in cacheRetrieval
		Field fieldCurrencyServices = gpIndexer.getClass().getDeclaredField("solrServerProcessor");
		fieldCurrencyServices.setAccessible(true);
		fieldCurrencyServices.set(gpIndexer, solrServerProcessor);
		
		fieldCurrencyServices = gpIndexer.getClass().getDeclaredField("solrMapper");
		fieldCurrencyServices.setAccessible(true);
		fieldCurrencyServices.set(gpIndexer, solrMapper);	
	}
	
	/**
	 * Empty list 
	 * @throws Exception
	 */
	@Test
	public void testIndexEmptyList() throws Exception {

		context.checking(new Expectations() {
			{
				allowing(solrServerProcessor).indexBeansAutoCommit(new ArrayList());
				will(returnValue(null));
			}
		});

		gpIndexer.index(new ArrayList<GeneProduct>());
		context.assertIsSatisfied();
	}
	

	/**
	 * Index 1 Gene Product 
	 * @throws Exception
	 */
	@Test
	public void testIndex1GP() throws Exception {

		final GeneProduct gp = new GeneProduct();
		gp.setDbObjectName("GP Name");
		final SolrGeneProduct solrTerm = new SolrGeneProduct();		
		
		context.checking(new Expectations() {
			{
				allowing(solrMapper).toSolrObject(gp, SolrGeneProductDocumentType.getAsInterfaces());
				will(returnValue(Arrays.asList(solrTerm)));
				
				allowing(solrServerProcessor).indexBeansAutoCommit(with(Arrays.asList(solrTerm)));			
			}
		});

		gpIndexer.index(Arrays.asList(gp));
		context.assertIsSatisfied();
	}
}
