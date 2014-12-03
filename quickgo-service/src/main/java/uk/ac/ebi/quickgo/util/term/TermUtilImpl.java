package uk.ac.ebi.quickgo.util.term;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import uk.ac.ebi.quickgo.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.ontology.generic.TermRelation;
import uk.ac.ebi.quickgo.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.service.term.TermService;

/**
 * Useful term operations not included in the Term Service 
 * @author cbonill
 *
 */

@Service
public class TermUtilImpl implements TermUtil {

	@Autowired
	TermService goTermService;
	
	/**
	 * See #{@link TermUtil#calculateChildTerms(String)} 
	 */
	@Cacheable(value="childTerms")
	public List<TermRelation> calculateChildTerms(String goTermId){
		
		GOTerm goTerm = goTermService.retrieveTerm(goTermId);
		List<TermRelation> childTermsRelations = new ArrayList<>();
		
		for (TermRelation childRelation : goTerm.getChildren()) {
			childTermsRelations.add(new TermRelation((GenericTerm)goTermService.retrieveTerm(childRelation.getChild().getId()), goTerm, childRelation.getTypeof()));
		}
		return childTermsRelations;
	}
	
	/**
	 * See #{@link TermUtil#calculateReplacesTermsNames(GOTerm)} 
	 */
	@Cacheable(value="replacesTerms")
	public void calculateReplacesTermsNames(GOTerm goTerm){		
		
		// Replaces
		for(TermRelation replaces : goTerm.getReplaces()){
			GOTerm replacedTerm = goTermService.retrieveTerm(replaces.getChild().getId());
			replaces.setChild(replacedTerm);
		}
		// Replacements
		for(TermRelation replacedby : goTerm.getReplacements()){
			GOTerm replacedByTerm = goTermService.retrieveTerm(replacedby.getParent().getId());
			replacedby.setParent(replacedByTerm);
		}
	}
}