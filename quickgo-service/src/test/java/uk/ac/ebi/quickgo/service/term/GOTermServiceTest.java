package uk.ac.ebi.quickgo.service.term;

import static org.junit.Assert.assertTrue;

import org.apache.solr.client.solrj.SolrServerException;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import uk.ac.ebi.quickgo.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.solr.exception.NotFoundException;
import uk.ac.ebi.quickgo.solr.query.service.ontology.TermRetrieval;

/**
 * Tests for GOTermService class
 * @author cbonill
 *
 */
public class GOTermServiceTest {

	final TermServiceImpl goTermService = new TermServiceImpl();
	
	private Mockery context = new Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	/**
	 * Not found term
	 * @throws NotFoundException
	 * @throws SolrServerException 
	 */
	@Test
	public void testRetrieveNotFoundGoTerm() throws NotFoundException, SolrServerException {
		
		final TermRetrieval termRetrieval = context.mock(TermRetrieval.class);
		goTermService.goTermRetrieval = termRetrieval;
		
		context.checking(new Expectations() {
			{
				allowing(termRetrieval).findById("GO:0001");
				will(throwException(new SolrServerException("Entry not found")));	
			}
		});
		
		GenericTerm goTerm = goTermService.retrieveTerm("GO:0001");
		assertTrue(goTerm.getId() == null);
		context.assertIsSatisfied();
	}
	
	/**
	 * Found term
	 * @throws NotFoundException
	 * @throws SolrServerException 
	 */
	@Test
	public void testRetrieveFoundGoTerm() throws NotFoundException, SolrServerException {
		
		final TermRetrieval termRetrieval = context.mock(TermRetrieval.class);
		goTermService.goTermRetrieval = termRetrieval;
		
		context.checking(new Expectations() {
			{
				allowing(termRetrieval).findById("GO:0001");
				will(returnValue(new GOTerm("GO:0001", "name", "P", "N")));	
			}
		});
		
		GenericTerm goTerm = goTermService.retrieveTerm("GO:0001");
		assertTrue(goTerm.getName().equals("name"));
		context.assertIsSatisfied();
	}
	
	/**
	 * Convert a GO Term into JSON
	 * @throws IOException
	 */
	/*@Test
	public void convertToJSON() throws IOException{
		
		final GOTerm goTerm = context.mock(GOTerm.class);		
		final ByteArrayOutputStream arrayOutputStream = context.mock(ByteArrayOutputStream.class);
		EntityToStream<GOTerm> entityToStream = context.mock(EntityToStream.class);
		goTermService.goTermEntityToStream = entityToStream;
		context.checking(new Expectations() {
			{	
				allowing(goTerm.isGOTerm());
				will(returnValue(true));
				
				allowing(goTermService.goTermEntityToStream).convertToJSONStream(goTerm, arrayOutputStream);					
			}
		});		
		goTermService.convertToJSON(goTerm, arrayOutputStream);
		context.assertIsSatisfied();
	}*/	
}