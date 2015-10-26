package uk.ac.ebi.quickgo.output.xml.term.model;

import static org.junit.Assert.assertTrue;

import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import uk.ac.ebi.quickgo.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.xml.term.Lookupdeftype;

/**
 * Tests for the GOTermXML class 
 * @author cbonill
 *
 */
public class GOTermXMLTest {

	private Mockery context = new Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	@Test
	public void testName() {
		GOTerm goTerm = new GOTerm();
		goTerm.setName("name");
		GOTermXML goTermXML = new GOTermXML(goTerm);
		Lookupdeftype termdeftype = (Lookupdeftype) goTermXML.getXmlRepresentation();
		assertTrue(termdeftype.getName().equals("name"));
	}
	
	@Test
	public void testId() {
		GOTerm goTerm = new GOTerm();
		goTerm.setId("GO:1234");
		GOTermXML goTermXML = new GOTermXML(goTerm);
		Lookupdeftype termdeftype = (Lookupdeftype) goTermXML.getXmlRepresentation();
		assertTrue(termdeftype.getId().equals("GO:1234"));
	}
	
	@Test
	public void testComment() {
		GOTerm goTerm = new GOTerm();
		goTerm.setComment("comment");
		GOTermXML goTermXML = new GOTermXML(goTerm);
		Lookupdeftype termdeftype = (Lookupdeftype) goTermXML.getXmlRepresentation();
		assertTrue(termdeftype.getComment().equals("comment"));
	}
	
	@Test
	public void testDefinition() {
		GOTerm goTerm = new GOTerm();
		goTerm.setDefinition("definition");
		GOTermXML goTermXML = new GOTermXML(goTerm);
		Lookupdeftype termdeftype = (Lookupdeftype) goTermXML.getXmlRepresentation();
		assertTrue(termdeftype.getDefinition().equals("definition"));		
	}
}