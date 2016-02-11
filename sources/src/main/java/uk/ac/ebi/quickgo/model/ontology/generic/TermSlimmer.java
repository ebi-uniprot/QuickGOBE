package uk.ac.ebi.quickgo.model.ontology.generic;

import java.util.*;

/**
 * given a set of term IDs that are to be used as a slim set, calculate the term(s) from that set to which each term in the ontology slims up
 * 
 * based on the original uk.ac.ebi.quickgo.web.servlets.annotation.Slimmer class, which is itself based on the Perl map2slim script
 * (http://search.cpan.org/~cmungall/go-perl/scripts/map2slim)
 * 
 * @author tonys
 */
public class TermSlimmer {
    private HashMap<String, List<GenericTerm>> slimTranslate;
    
    /**
     * @param ontology - the ontology of which the slim terms are a subset
     * @param slimTermSet - (possibly empty) set of terms from the ontology that comprise the slim 
     * @param relationTypes - (optional) the set of relation types to slim over (cf {@link RelationType} for details)
     * @throws Exception - if slim terms not from the same ontology as the background set
     */
    public TermSlimmer(GenericOntology ontology, ITermContainer slimTermSet, EnumSet<RelationType> relationTypes) throws Exception {
        // if no slim terms have been supplied, there's nothing to do
        if (slimTermSet == null || slimTermSet.getTermCount() == 0) {
	        return;
        }
        
        if(ontology == null){
        	return;
        }
        
        // the slim terms must be from the same ontology as the background set
        if (!ontology.getNamespace().equals(slimTermSet.getNamespace())) {
        	throw new Exception("Slim terms from different ontology - expecting " + ontology.getNamespace() + ", found " + slimTermSet.getNamespace());
        }

        // have we been given a set of relation types to slim over - if not, use the defaults (is_a, part_of and occurs_in)
    	if (relationTypes == null) {
    		relationTypes = TermRelation.defaultRelationTypes();
    	}

    	// convert the list of term IDs into an array for easier indexing
        GenericTerm[] slimTerms = slimTermSet.toArray();

        // slim terms which exclude other slim terms
        BitSet[] exclude = new BitSet[slimTerms.length];

        for (int i = 0; i < slimTerms.length; i++) {
            // a term excludes all its ancestors from being used as slim terms
            exclude[i] = slimTerms[i].getAncestors(slimTerms, relationTypes);
            // a term does not exclude itself from a slim
            exclude[i].clear(i);
        }

        // map that translates terms to their slimmed equivalent(s)
        slimTranslate = new HashMap<>();

        for (String id : ontology.getTermIds()) {
        	GenericTerm t = ontology.terms.get(id);

        	// determine which of the slim terms (if any) are in the ancestry of this term
        	// if bit i is set in the BitSet, that means that slimTerms[i] is an ancestor of the term
            BitSet bsAncestors = t.getAncestors(slimTerms, relationTypes);
            // if none of this term's ancestors are in the slim set, there's nothing more to be done 
            

            if (bsAncestors.cardinality() > 0) {
                // modify the ancestry to exclude any of the slim terms that are hidden by more specific ones
                BitSet bsSlimTerms = (BitSet)bsAncestors.clone();
                for (int p = 0; (p = bsAncestors.nextSetBit(p)) >= 0; p++) {
                    bsSlimTerms.andNot(exclude[p]);
                }

                // create a map entry that translates this term to slim term(s)   
                List<GenericTerm> mappedTerms = new ArrayList<>(bsSlimTerms.cardinality());
                slimTranslate.put(id, mappedTerms);
                for (int p = 0; (p = bsSlimTerms.nextSetBit(p)) >= 0; p++) {
                    mappedTerms.add(slimTerms[p]);
                }
			} else if (slimTermSet.getTerm(t.getId()) != null) {// Term is in the slim set but it has no ancestors for the specified relation
				slimTranslate.put(id, Arrays.asList(t));
			}
        }
    }
    
    /**
     * map the supplied term to term(s) from the slim set
     * 
     * @param id - id of the term to be mapped
     * @return a list of terms from the slim set to which this term is mapped, or null if no mapping exists 
     */
    public List<GenericTerm> map(String id) {
    	return slimTranslate.get(id);
    }

    /**
     * map the supplied term to term(s) from the slim set
     * 
     * @param t - term to be mapped
     * @return a list of terms from the slim set to which this term is mapped, or null if no mapping exists 
     */
    public List<GenericTerm> map(GenericTerm t) {
    	return map(t.getId());
    }

	public HashMap<String, List<GenericTerm>> getSlimTranslate() {
		return slimTranslate;
	}

	public void setSlimTranslate(HashMap<String, List<GenericTerm>> slimTranslate) {
		this.slimTranslate = slimTranslate;
	}
}
