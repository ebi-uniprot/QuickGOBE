package uk.ac.ebi.quickgo.webservice.model;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @Author Tony Wardell
 * Date: 21/04/2015
 * Time: 10:51
 * Created with IntelliJ IDEA.
 */
public class OntologyListJson {
	private String ontology;
	private Map<String, String> terms;

	public void setOntology(String ontology) {
		this.ontology = ontology;
	}

	public String getOntology() {
		return ontology;
	}

	public void setTerms(Map<String, String> terms) {
//		List<>
//		Set<Map.Entry<String, String>> termSet = terms.entrySet();
//		for (Iterator<Map.Entry<String, String>> iterator = termSet.iterator();iterator.hasNext();) {
//			Map.Entry<String, String> next = iterator.next();
//
//		}
		this.terms = terms;
	}

	public Map<String, String> getTerms() {
		return terms;
	}
}
