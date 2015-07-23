package uk.ac.ebi.quickgo.solr.mapper.term;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import uk.ac.ebi.quickgo.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.ontology.go.GOTerm.EGOAspect;
import uk.ac.ebi.quickgo.ontology.go.GOTerm.ETermUsage;
import uk.ac.ebi.quickgo.solr.mapper.EntityMapper;
import uk.ac.ebi.quickgo.solr.mapper.term.go.EntityGOTermMapper;
import uk.ac.ebi.quickgo.solr.model.SolrDocumentType;
import uk.ac.ebi.quickgo.solr.model.ontology.SolrTerm;
import uk.ac.ebi.quickgo.solr.model.ontology.SolrTerm.SolrTermDocumentType;

/**
 * Tests for the EntityTermMapper class
 * @author cbonill
 *
 */
public class EntityTermMapperTest {

	private Mockery context = new Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};
	
	EntityMapper<SolrTerm, GOTerm> entityMapper = new EntityGOTermMapper();
	
	/**
	 * Test for basic information mapping
	 */
	@Test
	public void testMapBasicInformation(){
		// Mock
		final SolrTerm solrTerm = context.mock(SolrTerm.class);		 
		
		context.checking(new Expectations() {
			{
				allowing(solrTerm).getDocType();
				will(returnValue(SolrTerm.SolrTermDocumentType.TERM.getValue()));
				
				allowing(solrTerm).getId();
				will(returnValue("GO:00000001"));

/*
				allowing(solrTerm).getComment();
				will(returnValue(Arrays.asList("Comment")));
				
				allowing(solrTerm).getDefinition();
				will(returnValue(Arrays.asList("Definition")));
*/

				allowing(solrTerm).getSecondaryIds();
				will(returnValue(Arrays.asList("GO:000034")));
				
				allowing(solrTerm).getName();
				will(returnValue("apoptotic process"));
				
				allowing(solrTerm).getUsage();
				will(returnValue(ETermUsage.E.text));
				
				allowing(solrTerm).getOntology();
				will(returnValue(EGOAspect.P.text));
				
				allowing(solrTerm).isObsolete();
				will(returnValue(true));				
				
				allowing(solrTerm).getSubsets();
				will(returnValue(new ArrayList<>()));
				
				allowing(solrTerm).getDefinitionXref();
				will(returnValue(new ArrayList<>()));
				
				allowing(solrTerm).getCredits();
				will(returnValue(new ArrayList<>()));
			}
		});
		
		GOTerm goTerm = entityMapper.toEntityObject(Collections.singletonList(solrTerm), Collections.singletonList((SolrDocumentType)SolrTermDocumentType.TERM));
		context.assertIsSatisfied();
		assertTrue(goTerm.getId().equals("GO:00000001"));
		assertTrue(goTerm.getName().equals("apoptotic process"));
		assertTrue(goTerm.getOntologyText().equals(EGOAspect.P.text));
		assertTrue(goTerm.getComment().contains("Comment"));
		assertTrue(goTerm.getDefinition().contains("Definition"));
		assertTrue(goTerm.isObsolete());
	}
	
	/**
	 * Test for Annotation Guideline mapping
	 */
	@Test
	public void testAnnotationGuideline(){
		// Mock
		final SolrTerm solrTerm = context.mock(SolrTerm.class);		 
		
		context.checking(new Expectations() {
			{
				allowing(solrTerm).getDocType();
				will(returnValue(SolrTerm.SolrTermDocumentType.GUIDELINE.getValue()));
				
				allowing(solrTerm).getAnnotationGuidelineTitle();
				will(returnValue("Annotation title"));

				allowing(solrTerm).getAnnotationGuidelineUrl();
				will(returnValue("Annotation URL"));
				
			}
		});
		
		GOTerm goTerm = entityMapper.toEntityObject(Collections.singletonList(solrTerm), Collections.singletonList((SolrDocumentType)SolrTermDocumentType.GUIDELINE));
		context.assertIsSatisfied();	
		assertTrue(goTerm.getGuidelines().size() == 1);
		assertTrue(goTerm.getGuidelines().get(0).title.equals("Annotation title"));
		assertTrue(goTerm.getGuidelines().get(0).url.equals("Annotation URL"));
	}
	
	/**
	 * Test for Synonyms mapping
	 */
	@Test
	public void testSynonyms(){
		// Mock
		final SolrTerm solrTerm = context.mock(SolrTerm.class);		 
		
		context.checking(new Expectations() {
			{
				allowing(solrTerm).getDocType();
				will(returnValue(SolrTerm.SolrTermDocumentType.SYNONYM.getValue()));
				
				allowing(solrTerm).getId();
				will(returnValue("GO:00032"));
				
				allowing(solrTerm).getSynonymName();
				will(returnValue("GO:0003"));

				allowing(solrTerm).getSynonymType();
				will(returnValue("Synonym type"));
				
			}
		});
		
		GOTerm goTerm = entityMapper.toEntityObject(Collections.singletonList(solrTerm), Collections.singletonList((SolrDocumentType)SolrTermDocumentType.SYNONYM));
		context.assertIsSatisfied();	
		assertTrue(goTerm.getSynonyms().size() == 1);
		assertTrue(goTerm.getSynonyms().get(0).name.equals("GO:0003"));
		assertTrue(goTerm.getSynonyms().get(0).type.equals("Synonym type"));
	}
	
	/**
	 * Test for Xrefs mapping
	 */
	@Test
	public void testXrefs(){
		// Mock
		final SolrTerm solrTerm = context.mock(SolrTerm.class);		 
		
		context.checking(new Expectations() {
			{
				allowing(solrTerm).getDocType();
				will(returnValue(SolrTerm.SolrTermDocumentType.XREF.getValue()));
				
				allowing(solrTerm).getXrefDbCode();
				will(returnValue("A01245"));

				allowing(solrTerm).getXrefDbId();
				will(returnValue("UniProt-KB"));
				
				allowing(solrTerm).getXrefName();
				will(returnValue("Protein"));				
			}
		});
		
		GOTerm goTerm = entityMapper.toEntityObject(Collections.singletonList(solrTerm), Collections.singletonList((SolrDocumentType)SolrTermDocumentType.XREF));
		context.assertIsSatisfied();	
		assertTrue(goTerm.getXrefs().size() == 1);
		assertTrue(goTerm.getXrefs().get(0).getDb().equals("A01245"));
		assertTrue(goTerm.getXrefs().get(0).getId().equals("UniProt-KB"));
	}	
}
