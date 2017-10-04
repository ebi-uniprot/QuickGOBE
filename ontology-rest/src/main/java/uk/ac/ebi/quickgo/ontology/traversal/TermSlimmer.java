package uk.ac.ebi.quickgo.ontology.traversal;

import uk.ac.ebi.quickgo.ontology.model.OntologyRelationType;

import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.emptyList;

/**
 * Created by edd on 04/10/2017.
 */
public class TermSlimmer {
    private final Map<String, List<String>> slimTranslate;
    private final static OntologyRelationType[] DEFAULT_RELATION_TYPES =
            new OntologyRelationType[] {
                    OntologyRelationType.IS_A, OntologyRelationType.PART_OF, OntologyRelationType.OCCURS_IN };
}

    private TermSlimmer() {
        slimTranslate = new HashMap<>();
    }

    public static TermSlimmer createSlims(OntologyGraphTraversal ontology, List<String> slimTerms, OntologyRelationType... requestedRelationTypes) {
//        // if no slim terms have been supplied, there's nothing to do
//        if (slimTermSet == null || slimTermSet.getTermCount() == 0) {
//            return;
//        }
//
//        if(ontology == null){
//            return;
//        }
        // TODO: 04/10/2017 test
        checkArgument(ontology != null, "Ontology cannot be null");
        checkArgument(slimTerms != null && !slimTerms.isEmpty(), "Slim-set cannot be null or empty");
//
//        // the slim terms must be from the same ontology as the background set
//        if (!ontology.getNamespace().equals(slimTermSet.getNamespace())) {
//            throw new Exception("Slim terms from different ontology - expecting " + ontology.getNamespace() + ", found " + slimTermSet.getNamespace());
//        }
//
//        // have we been given a set of relation types to slim over - if not, use the defaults (is_a, part_of and occurs_in)
//        if (relationTypes == null) {
//            relationTypes = TermRelation.defaultRelationTypes();
//        }
        OntologyRelationType[] relationTypes;
        if (requestedRelationTypes.length == 0) {
            relationTypes = DEFAULT_RELATION_TYPES;
        } else {
            relationTypes = requestedRelationTypes;
        }
//
//        // convert the list of term IDs into an array for easier indexing
//        GenericTerm[] slimTerms = slimTermSet.toArray();
        String[] slimTermsArr = slimTerms.toArray(new String[slimTerms.size()]);
//
//        // slim terms which exclude other slim terms
//        BitSet[] exclude = new BitSet[slimTerms.length];
        BitSet[] exclude = new BitSet[slimTerms.size()];
//
//        for (int i = 0; i < slimTerms.length; i++) {
//            // a term excludes all its ancestors from being used as slim terms
//            exclude[i] = slimTerms[i].getAncestors(slimTerms, relationTypes);
//            // a term does not exclude itself from a slim
//            exclude[i].clear(i);
//        }
        for(int i = 0; i < slimTermsArr.length; i++) {
            exclude[i] = ((OntologyGraph)ontology).getAncestorsBitSet(slimTermsArr[i], slimTerms, relationTypes);
            exclude[i].clear(i);
        }
//
//        // map that translates terms to their slimmed equivalent(s)
//        slimTranslate = new HashMap<>();
//

//        for (String id : ontology.getTermIds()) {
//            GenericTerm t = ontology.terms.get(id);
//
//            // determine which of the slim terms (if any) are in the ancestry of this term
//            // if bit i is set in the BitSet, that means that slimTerms[i] is an ancestor of the term
//            BitSet bsAncestors = t.getAncestors(slimTerms, relationTypes);
//            // if none of this term's ancestors are in the slim set, there's nothing more to be done
//
//
//            if (bsAncestors.cardinality() > 0) {
//                // modify the ancestry to exclude any of the slim terms that are hidden by more specific ones
//                BitSet bsSlimTerms = (BitSet)bsAncestors.clone();
//                for (int p = 0; (p = bsAncestors.nextSetBit(p)) >= 0; p++) {
//                    bsSlimTerms.andNot(exclude[p]);
//                }
//
//                // create a map entry that translates this term to slim term(s)
//                List<GenericTerm> mappedTerms = new ArrayList<>(bsSlimTerms.cardinality());
//                slimTranslate.put(id, mappedTerms);
//                for (int p = 0; (p = bsSlimTerms.nextSetBit(p)) >= 0; p++) {
//                    mappedTerms.add(slimTerms[p]);
//                }
//            } else if (slimTermSet.getTerm(t.getId()) != null) {// Term is in the slim set but it has no ancestors for the specified relation
//                slimTranslate.put(id, Arrays.asList(t));
//            }
//        }

        return null;
    }

    //    /**
//     * map the supplied term to term(s) from the slim set
//     *
//     * @param id - id of the term to be mapped
//     * @return a list of terms from the slim set to which this term is mapped, or null if no mapping exists
//     */
//    public List<GenericTerm> map(String id) {
//        return slimTranslate.get(id);
//    }
//

    /**
     * Finds the terms within the original slim-set to which this term slims up to.
     * @param id the identifier of the term to map
     * @return a list of terms within the original slim-set to which this term slims up to.
     */
    public List<String> findSlims(String id) {
        if (slimTranslate.containsKey(id)) {
            return slimTranslate.get(id);
        } else {
            return emptyList();
        }
    }

//    /**
//     * map the supplied term to term(s) from the slim set
//     *
//     * @param t - term to be mapped
//     * @return a list of terms from the slim set to which this term is mapped, or null if no mapping exists
//     */
//    public List<GenericTerm> map(GenericTerm t) {
//        return map(t.getId());
//    }
//
//    public HashMap<String, List<GenericTerm>> getSlimTranslate() {
//        return slimTranslate;
//    }
//
//    public void setSlimTranslate(HashMap<String, List<GenericTerm>> slimTranslate) {
//        this.slimTranslate = slimTranslate;
//    }
}
