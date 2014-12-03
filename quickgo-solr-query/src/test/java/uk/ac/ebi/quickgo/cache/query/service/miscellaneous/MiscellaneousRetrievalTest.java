package uk.ac.ebi.quickgo.cache.query.service.miscellaneous;

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

import uk.ac.ebi.quickgo.miscellaneous.Miscellaneous;
import uk.ac.ebi.quickgo.solr.mapper.EntityMapper;
import uk.ac.ebi.quickgo.solr.model.SolrDocumentType;
import uk.ac.ebi.quickgo.solr.model.miscellaneous.SolrMiscellaneous;
import uk.ac.ebi.quickgo.solr.model.miscellaneous.SolrMiscellaneous.SolrMiscellaneousDocumentType;
import uk.ac.ebi.quickgo.solr.query.model.miscellaneous.enums.MiscellaneousField;
import uk.ac.ebi.quickgo.solr.query.service.miscellaneous.MiscellaneousRetrieval;
import uk.ac.ebi.quickgo.solr.query.service.miscellaneous.MiscellaneousRetrievalImpl;
import uk.ac.ebi.quickgo.solr.server.SolrServerProcessor;

/**
 * Tests for the MiscellaneousRetrieval class
 * 
 * @author cbonill
 * 
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class MiscellaneousRetrievalTest {

	// Mock context
	private Mockery context;
	private MiscellaneousRetrieval miscellaneousRetrieval;
	private SolrServerProcessor solRServerProcessor;
	private EntityMapper<SolrMiscellaneous, Miscellaneous> miscellaneousEntityMapper;

	@Before
	public void before() throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		context = new JUnit4Mockery();
		miscellaneousRetrieval = new MiscellaneousRetrievalImpl();

		// Mock
		solRServerProcessor = context.mock(SolrServerProcessor.class);
		miscellaneousEntityMapper = context.mock(EntityMapper.class);

		// Set cacheBuilder value in cacheRetrieval
		Field fieldCurrencyServices = miscellaneousRetrieval.getClass()
				.getDeclaredField("serverProcessor");
		fieldCurrencyServices.setAccessible(true);
		fieldCurrencyServices.set(miscellaneousRetrieval, solRServerProcessor);

		fieldCurrencyServices = miscellaneousRetrieval.getClass()
				.getDeclaredField("miscellaneousEntityMapper");
		fieldCurrencyServices.setAccessible(true);
		fieldCurrencyServices.set(miscellaneousRetrieval, miscellaneousEntityMapper);
	}

	/**
	 * Miscellaneous exists
	 */
	@Test
	public void testFindByTaxonomyIdExists() throws SolrServerException {

		int id = 6;
		SolrMiscellaneous solrMiscellaneous = new SolrMiscellaneous();
		solrMiscellaneous.setTaxonomyId(id);
		final List<SolrMiscellaneous> solrTerms = Arrays
				.asList(solrMiscellaneous);
		final List<SolrDocumentType> termType = SolrMiscellaneousDocumentType
				.getAsInterfaces();

		final Miscellaneous miscellaneous = new Miscellaneous();
		miscellaneous.setTaxonomyId(id);

		context.checking(new Expectations() {
			{
				allowing(solRServerProcessor).findByQuery(
						with(any(SolrQuery.class)), with(any(Class.class)),
						with(any(Integer.class)));
				will(returnValue(solrTerms));

				allowing(miscellaneousEntityMapper).toEntityObject(solrTerms,
						termType);
				will(returnValue(miscellaneous));
			}
		});

		Miscellaneous foundMiscellaneous = (Miscellaneous) miscellaneousRetrieval
				.findById(String.valueOf(id));
		context.assertIsSatisfied();
		assertTrue(foundMiscellaneous.getTaxonomyId() == miscellaneous.getTaxonomyId());
	}

	/**
	 * Find sequence by query
	 */
	@Test
	public void testFindSequenceByQuery() throws SolrServerException {

		final String accession = "A01234";
		SolrMiscellaneous solrMiscellaneous = new SolrMiscellaneous();
		solrMiscellaneous.setDbObjectID(accession);
		final List<SolrMiscellaneous> solrTerms = Arrays.asList(solrMiscellaneous);
		final Miscellaneous miscellaneous = new Miscellaneous();
		miscellaneous.setDbObjectID(accession);

		context.checking(new Expectations() {
			{
				allowing(solRServerProcessor).findByQuery(
						with(any(SolrQuery.class)), with(any(Class.class)),
						with(any(Integer.class)));
				will(returnValue(solrTerms));

				allowing(miscellaneousEntityMapper).toEntityObject(
						with(any(List.class)));
				will(returnValue(miscellaneous));
			}
		});

		List<Miscellaneous> foundMiscellaneous = (List<Miscellaneous>) miscellaneousRetrieval
				.findByQuery(MiscellaneousField.DBOBJECTID.getValue() + ":"
						+ accession, -1);
		context.assertIsSatisfied();
		Miscellaneous firstMiscellaneous = foundMiscellaneous.get(0);
		assertTrue(solrMiscellaneous.getDbObjectID().equals(firstMiscellaneous.getDbObjectID()));
	}

	/**
	 * Find publication by id
	 */
	@Test
	public void testFindPublicationgetDbObjectIDById()
			throws SolrServerException {

		final String publicationID = "12345";
		SolrMiscellaneous solrMiscellaneous = new SolrMiscellaneous();
		solrMiscellaneous.setPublicationID(Integer.valueOf(publicationID));
		final List<SolrMiscellaneous> solrTerms = Arrays
				.asList(solrMiscellaneous);
		final Miscellaneous miscellaneous = new Miscellaneous();
		miscellaneous.setPublicationID(Integer.valueOf(publicationID));

		context.checking(new Expectations() {
			{
				allowing(solRServerProcessor).findByQuery(
						with(any(SolrQuery.class)), with(any(Class.class)),
						with(any(Integer.class)));
				will(returnValue(solrTerms));

				allowing(miscellaneousEntityMapper).toEntityObject(
						with(any(List.class)), with(any(List.class)));
				will(returnValue(miscellaneous));
			}
		});

		Miscellaneous foundMiscellaneous = miscellaneousRetrieval
				.findByMiscellaneousId(publicationID,
						MiscellaneousField.PUBLICATIONID.getValue());
		context.assertIsSatisfied();
		assertTrue(foundMiscellaneous.getPublicationID() == (solrMiscellaneous.getPublicationID()));
	}

	/**
	 * Find annotation blacklist by term
	 */
	@Test
	public void testFindAnnotationBlacklistByGOID() throws SolrServerException {

		final String term = "GO:00001";
		SolrMiscellaneous solrMiscellaneous = new SolrMiscellaneous();
		solrMiscellaneous.setTerm(term);
		final List<SolrMiscellaneous> solrTerms = Arrays
				.asList(solrMiscellaneous);
		final Miscellaneous miscellaneous = new Miscellaneous();
		miscellaneous.setTerm(term);

		context.checking(new Expectations() {
			{
				allowing(solRServerProcessor).findByQuery(
						with(any(SolrQuery.class)), with(any(Class.class)),
						with(any(Integer.class)));
				will(returnValue(solrTerms));

				allowing(miscellaneousEntityMapper).toEntityObject(
						with(any(List.class)));
				will(returnValue(miscellaneous));
			}
		});

		List<Miscellaneous> foundMiscellaneous = (List<Miscellaneous>) miscellaneousRetrieval
				.findByQuery(MiscellaneousField.TYPE.getValue() + ":"
						+ SolrMiscellaneousDocumentType.BLACKLIST.getValue()
						+ " AND " + MiscellaneousField.TERM.getValue() + ":"
						+ term, -1);
		context.assertIsSatisfied();
		Miscellaneous firstMiscellaneous = foundMiscellaneous.get(0);
		assertTrue(solrMiscellaneous.getTerm().equals(firstMiscellaneous.getTerm()));
	}

	/**
	 * Find annotation guideline by term
	 */
	@Test
	public void testFindAnnotationGuidelineByGOID() throws SolrServerException {

		final String term = "GO:00001";
		SolrMiscellaneous solrMiscellaneous = new SolrMiscellaneous();
		solrMiscellaneous.setTerm(term);
		final List<SolrMiscellaneous> solrTerms = Arrays.asList(solrMiscellaneous);
		final Miscellaneous miscellaneous = new Miscellaneous();
		miscellaneous.setTerm(term);

		context.checking(new Expectations() {
			{
				allowing(solRServerProcessor).findByQuery(
						with(any(SolrQuery.class)), with(any(Class.class)),
						with(any(Integer.class)));
				will(returnValue(solrTerms));

				allowing(miscellaneousEntityMapper).toEntityObject(
						with(any(List.class)));
				will(returnValue(miscellaneous));
			}
		});

		List<Miscellaneous> foundMiscellaneous = (List<Miscellaneous>) miscellaneousRetrieval
				.findByQuery(MiscellaneousField.TYPE.getValue() + ":"
						+ SolrMiscellaneousDocumentType.GUIDELINE.getValue()
						+ " AND " + MiscellaneousField.TERM.getValue() + ":"
						+ term, -1);
		context.assertIsSatisfied();
		Miscellaneous firstMiscellaneous = foundMiscellaneous.get(0);
		assertTrue(solrMiscellaneous.getTerm().equals(firstMiscellaneous.getTerm()));
	}
	
	/**
	 * Find annotation guideline by term does not exist
	 */
	@Test
	public void testFindAnnotationGuidelineNotExist() throws SolrServerException {

		final String term = "GO:00001";
		SolrMiscellaneous solrMiscellaneous = new SolrMiscellaneous();
		solrMiscellaneous.setTerm(term);		
		final Miscellaneous miscellaneous = new Miscellaneous();
		miscellaneous.setTerm(term);

		context.checking(new Expectations() {
			{
				allowing(solRServerProcessor).findByQuery(
						with(any(SolrQuery.class)), with(any(Class.class)),
						with(any(Integer.class)));
				will(returnValue(new ArrayList<>()));

				allowing(miscellaneousEntityMapper).toEntityObject(
						with(any(List.class)));
				will(returnValue(miscellaneous));
			}
		});

		List<Miscellaneous> foundMiscellaneous = (List<Miscellaneous>) miscellaneousRetrieval
				.findByQuery(MiscellaneousField.TYPE.getValue() + ":"
						+ SolrMiscellaneousDocumentType.GUIDELINE.getValue()
						+ " AND " + MiscellaneousField.TERM.getValue() + ":"
						+ term, -1);
		context.assertIsSatisfied();
		assertTrue(foundMiscellaneous.size() == 0);
	}
}