package uk.ac.ebi.quickgo.output.json;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

import uk.ac.ebi.quickgo.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.ontology.go.GOTerm.EGOAspect;

/**
 * Tests for EntityToJSONStream class
 * 
 * @author cbonill
 * 
 */
public class EntityToJSONStreamTest {

	@Test	
	public void emptyGoTermEntityToJSON() {
		GOTerm goTerm = new GOTerm();
		EntityToJSONStream<GOTerm> entityToJSONStream = new EntityToJSONStream<GOTerm>();

		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		try {
			entityToJSONStream.convertToJSONStream(goTerm, arrayOutputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String result = new String(arrayOutputStream.toByteArray());
		System.out.println(result);
		assertTrue(result.contains("\"obsolete\":false"));		
	}

	@Test
	public void goTermEntityToJSON() {
		GOTerm goTerm = new GOTerm();
		goTerm.setId("GO:000001");
		goTerm.setName("GO Term Name");
		goTerm.setAspect(EGOAspect.F);
		goTerm.setDefinition("GO Term definition");
		EntityToJSONStream<GOTerm> entityToJSONStream = new EntityToJSONStream<GOTerm>();

		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		try {
			entityToJSONStream.convertToJSONStream(goTerm, arrayOutputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String result = new String(arrayOutputStream.toByteArray());
		System.out.println(result);
		assertNotNull(result);
		assertTrue(result.contains("\"id\"" + ":" + "\"GO:000001\""));
		assertTrue(result.contains("\"definition\"" + ":" + "\"GO Term definition\""));
		assertTrue(result.contains("\"name\"" + ":" + "\"GO Term Name\""));
	}
}
