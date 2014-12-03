package uk.ac.ebi.quickgo.util.term;

import java.util.List;

import uk.ac.ebi.quickgo.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.ontology.generic.TermRelation;
import uk.ac.ebi.quickgo.ontology.go.GOTerm;

/**
 * Useful term operations not included in the Term Service 
 * @author cbonill
 *
 */

public interface TermUtil {

	/**
	 * Given a term, calculate list of children relations
	 * @param goTermId Term id
	 * @return List of children relations
	 */
	public List<TermRelation> calculateChildTerms(String goTermId);

	/**
	 * Populate replaced and replacement terms
	 * @param goTerm
	 */
	public void calculateReplacesTermsNames(GOTerm goTerm);

}
