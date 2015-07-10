package uk.ac.ebi.quickgo.solr.mapper.term;

import static org.junit.Assert.assertTrue;

import java.util.*;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import uk.ac.ebi.quickgo.ontology.generic.AuditRecord;
import uk.ac.ebi.quickgo.ontology.generic.Synonym;
import uk.ac.ebi.quickgo.ontology.generic.TermOntologyHistory;
import uk.ac.ebi.quickgo.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.ontology.go.GOTerm.EGOAspect;
import uk.ac.ebi.quickgo.ontology.go.GOTerm.ETermUsage;
import uk.ac.ebi.quickgo.ontology.go.GOTerm.NamedURL;
import uk.ac.ebi.quickgo.ontology.go.TaxonConstraint;
import uk.ac.ebi.quickgo.solr.mapper.term.go.SolrGOTermMapper;
import uk.ac.ebi.quickgo.solr.model.ontology.SolrTerm;
import uk.ac.ebi.quickgo.solr.model.SolrDocumentType;
import uk.ac.ebi.quickgo.solr.model.ontology.SolrTerm.SolrTermDocumentType;
import uk.ac.ebi.quickgo.util.NamedXRef;

/**
 * Tests for SolrTermMapper class
 * @author cbonill
 *
 */
public class SolrTermMapperTest {

	private Mockery context = new Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};
	
	SolrGOTermMapper solrMapper = new SolrGOTermMapper();
	
	/**
	 * Test for basic information mapping
	 */
	@Test
	public void testMapBasicInformation(){
		// Mock
		final GOTerm term = context.mock(GOTerm.class);		 
		
		context.checking(new Expectations() {
			{
				allowing(term).getId();
				will(returnValue("GO:00000001"));

				allowing(term).getName();
				will(returnValue("apoptotic process"));
				
				allowing(term).getOntologyText();
				will(returnValue(EGOAspect.P.text));
				
				allowing(term).getComment();				
				will(returnValue("Comment"));
				
				allowing(term).getDefinition();
				will(returnValue("Definition"));
			
				allowing(term).getUsage();
				will(returnValue(ETermUsage.E));
				
				allowing(term).getSubsetsNames();
				will(returnValue(new ArrayList<>()));
				
				allowing(term).isObsolete();
				will(returnValue(true));
				
				allowing(term).getDefinitionXrefs();
				will(returnValue(new ArrayList<>()));
				
				allowing(term).getCredits();
				will(returnValue(new ArrayList<>()));
			}
		});
		
		Collection<SolrTerm> solrTerms = solrMapper.toSolrObject(term, Collections.singletonList((SolrDocumentType)SolrTermDocumentType.TERM));
		context.assertIsSatisfied();
		List<SolrTerm> terms = new ArrayList<>(solrTerms);
		assertTrue(terms.size() == 1);
		assertTrue(terms.get(0).getId().equals("GO:00000001"));
		assertTrue(terms.get(0).getName().equals("apoptotic process"));
		assertTrue(terms.get(0).getOntology().equals("Process"));
		assertTrue(terms.get(0).isObsolete());
	}
	
	/**
	 * Test for taxonomy constraints mapping 
	 */
	@Test
	public void testMapTaxonConstraints(){
		// Mock
		final GOTerm term = context.mock(GOTerm.class);
		TaxonConstraint taxonConstraint = new TaxonConstraint("rule001", "GO:0000002", "name", "Relation 1", "TaxId Types", "1234", "Taxon name", "");
		term.taxonConstraints = Collections.singletonList(taxonConstraint);
		
		context.checking(new Expectations() {
			{
				allowing(term).getId();
				will(returnValue("GO:0000002"));
			}
		});
		
		Collection<SolrTerm> solrTerms = solrMapper.toSolrObject(term, Collections.singletonList((SolrDocumentType)SolrTermDocumentType.CONSTRAINT));
		context.assertIsSatisfied();
		List<SolrTerm> terms = new ArrayList<>(solrTerms);
		assertTrue(terms.size() == 1);
	}
	
	/**
	 * Test for annotation guidelines mapping 
	 */
	@Test
	public void testMapAnnotationGuidelines(){
		// Mock
		final GOTerm term = context.mock(GOTerm.class);
		NamedURL namedURL1 = new NamedURL("Title1", "Url1");
		NamedURL namedURL2 = new NamedURL("Title2", "Url2");
		term.guidelines = Arrays.asList(namedURL1,namedURL2);
		
		context.checking(new Expectations() {
			{
				allowing(term).getId();
				will(returnValue("GO:0000002"));
			}
		});
		
		Collection<SolrTerm> solrTerms = solrMapper.toSolrObject(term, Collections.singletonList((SolrDocumentType)SolrTermDocumentType.GUIDELINE));
		context.assertIsSatisfied();
		List<SolrTerm> terms = new ArrayList<>(solrTerms);
		assertTrue(terms.size() == 2);
	}
	
	/**
	 * Test for change logs mapping 
	 */
	@Test
	public void testMapHistory(){
		// Mock
		final GOTerm term = context.mock(GOTerm.class);
		AuditRecord auditRecord1 = new AuditRecord("GO:0000001", "Term", "12-12-2012", "removed", "category1", "text1");
		AuditRecord auditRecord2 = new AuditRecord("GO:0000001", "Term", "12-12-2012", "added", "category2", "text2");		
		term.history = new TermOntologyHistory();
		term.history.auditRecords = new ArrayList<>();
		term.history.auditRecords.addAll(Arrays.asList(auditRecord1,auditRecord2));
		
		context.checking(new Expectations() {
			{
				allowing(term).getId();
				will(returnValue("GO:0000002"));
			}
		});
		
		Collection<SolrTerm> solrTerms = solrMapper.toSolrObject(term, Collections.singletonList((SolrDocumentType)SolrTermDocumentType.HISTORY));
		context.assertIsSatisfied();
		List<SolrTerm> terms = new ArrayList<>(solrTerms);
		assertTrue(terms.size() == 2);
	}
	
	/**
	 * Test for synonyms mapping 
	 */
	@Test
	public void testSynonyms(){
		// Mock
		final GOTerm term = context.mock(GOTerm.class);
		Synonym synonym1 = new Synonym("Type1", "Name1");
		Synonym synonym2 = new Synonym("Type2", "Name2");		
		term.synonyms = Arrays.asList(synonym1,synonym2);
		
		context.checking(new Expectations() {
			{
				allowing(term).getId();
				will(returnValue("GO:0000002"));
			}
		});
		
		Collection<SolrTerm> solrTerms = solrMapper.toSolrObject(term, Collections.singletonList((SolrDocumentType)SolrTermDocumentType.SYNONYM));
		context.assertIsSatisfied();
		List<SolrTerm> terms = new ArrayList<>(solrTerms);
		assertTrue(terms.size() == 2);
	}
	
	
	/**
	 * Test for xrefs mapping 
	 */
	@Test
	public void testXref(){
		// Mock
		final GOTerm term = context.mock(GOTerm.class);
		NamedXRef xref1 = new NamedXRef("DB", "A00001", "");
		NamedXRef xref2 = new NamedXRef("DB", "A00002", "");		
		NamedXRef xref3 = new NamedXRef("DB", "A00003", "");
		term.xrefs = Arrays.asList(xref1, xref2, xref3);
		
		context.checking(new Expectations() {
			{
				allowing(term).getId();
				will(returnValue("GO:0000002"));
			}
		});
		
		Collection<SolrTerm> solrTerms = solrMapper.toSolrObject(term, Collections.singletonList((SolrDocumentType)SolrTermDocumentType.XREF));
		context.assertIsSatisfied();
		List<SolrTerm> terms = new ArrayList<>(solrTerms);
		assertTrue(terms.size() == 3);
	}
}
