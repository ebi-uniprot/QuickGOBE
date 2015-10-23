package uk.ac.ebi.quickgo.service.term;

import java.io.ByteArrayOutputStream;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import uk.ac.ebi.quickgo.ontology.generic.GenericTerm;

/**
 * Integration test for GOTermService class
 * @author cbonill
 *
 */
public class GOTermServiceIntegrationTest {

	static ApplicationContext appContext;
	static TermService goTermService;
	static GenericTerm goTerm;
	
	
	public static void main(String[] args) {

		appContext = new ClassPathXmlApplicationContext("service-beans.xml", "common-beans.xml",
				"query-beans.xml");
		goTermService = (TermService) appContext.getBean("termService");
	
		// Retrieve term
		goTerm = retrieveTerm("GO:0006915");
		
		// JSON minimal representation		
		jsonMinimal(goTerm, new ByteArrayOutputStream());		
	}
	
	/***
	 * Retrieve term
	 * @param id Term id
	 * @return Term
	 */
	private static GenericTerm retrieveTerm(String id) {
		try {
			goTerm = goTermService.retrieveTerm(id);
		} catch (Exception e) {			
			e.printStackTrace();
		}
		System.out.println("GOTerm name: " + goTerm.getName());
		System.out.println("GOTerm #change logs: " + goTerm.getHistory().auditRecords.size());
		System.out.println("===================");
		return goTerm;
	}
	
	/**
	 * JSON minimal representation
	 * @param goTerm GO term to represent
	 * @param format Output format 
	 * @param outputStream Stream to write the values to
	 */
	private static void jsonMinimal(GenericTerm goTerm, ByteArrayOutputStream outputStream){
		goTermService.convertToJSON(goTerm, outputStream);
		String result = new String(outputStream.toByteArray());
		System.out.println("JSON minimal: \n" + result);
		System.out.println("===================");
	}
}