package uk.ac.ebi.quickgo.solr.query.service.geneproduct;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
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

import uk.ac.ebi.quickgo.geneproduct.GeneProduct;
import uk.ac.ebi.quickgo.solr.mapper.EntityMapper;
import uk.ac.ebi.quickgo.solr.model.SolrDocumentType;
import uk.ac.ebi.quickgo.solr.model.geneproduct.SolrGeneProduct;
import uk.ac.ebi.quickgo.solr.model.geneproduct.SolrGeneProduct.SolrGeneProductDocumentType;
import uk.ac.ebi.quickgo.solr.server.SolrServerProcessor;

/**
 * Tests for the GeneProductRetrieval class
 * @author cbonill
 *
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class GeneProductRetrievalTest {

		// Mock context
		private Mockery context;
		private GeneProductRetrieval geneProductRetrieval;
		private SolrServerProcessor solRServerProcessor;
		private EntityMapper<SolrGeneProduct , GeneProduct> gpEntityMapper;
		
		@Before
		public void before() throws NoSuchFieldException, SecurityException,
				IllegalArgumentException, IllegalAccessException {
			context = new JUnit4Mockery();
			geneProductRetrieval = new GeneProductRetrievalImpl();

			// Mock
			solRServerProcessor = context.mock(SolrServerProcessor.class);
			gpEntityMapper = context.mock(EntityMapper.class);
			
			// Set cacheBuilder value in cacheRetrieval
			Field fieldCurrencyServices = geneProductRetrieval.getClass().getDeclaredField("serverProcessor");
			fieldCurrencyServices.setAccessible(true);
			fieldCurrencyServices.set(geneProductRetrieval, solRServerProcessor);
			
			fieldCurrencyServices = geneProductRetrieval.getClass().getDeclaredField("geneProductEntityMapper");
			fieldCurrencyServices.setAccessible(true);
			fieldCurrencyServices.set(geneProductRetrieval, gpEntityMapper);
		}

		/**
		 * Entry exists 
		 */
		@Test
		public void testFindByIdExists() throws SolrServerException {

			String id = "A00001";
			SolrGeneProduct solrGeneProduct = new SolrGeneProduct();
			solrGeneProduct.setDbObjectId(id);
			final List<SolrGeneProduct> solrTerms = Arrays.asList(solrGeneProduct);
			final List<SolrDocumentType> termType = SolrGeneProductDocumentType.getAsInterfaces ();
			
			final GeneProduct geneProduct = new GeneProduct();
			geneProduct.setDbObjectId(id);
			
			context.checking(new Expectations() {
				{
					allowing(solRServerProcessor).findByQuery(with(any(SolrQuery.class)), with(any(Class.class)), with(any(Integer.class)));
					will(returnValue(solrTerms));
					
					allowing(gpEntityMapper).toEntityObject(solrTerms, termType);
					will(returnValue(geneProduct));
				}
			});

			GeneProduct foundGP = (GeneProduct) geneProductRetrieval.findById(id);
			context.assertIsSatisfied();
			assertTrue(foundGP.getDbObjectId() == geneProduct.getDbObjectId());
		}
}
