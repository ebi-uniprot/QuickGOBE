package uk.ac.ebi.quickgo.ontology.traversal;

import uk.ac.ebi.quickgo.ontology.common.OntologyType;
import uk.ac.ebi.quickgo.ontology.model.OntologyRelationType;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableMap;
import static uk.ac.ebi.quickgo.ontology.common.OntologyType.GO;
import static uk.ac.ebi.quickgo.ontology.model.OntologyRelationType.DEFAULT_SLIM_TRAVERSAL_TYPES;
import static uk.ac.ebi.quickgo.ontology.model.OntologyRelationType.DEFAULT_TRAVERSAL_TYPES;

/**
 * An instance of this class is used to find, for a given term, the equivalent "slimmed" terms. The slimming algorithm
 * is documented here: https://www.ebi.ac.uk/seqdb/confluence/display/GOA/GO+Slimming+-+an+executive+summary.
 * <p>
 * Created by Edd on 04/10/2017.
 */
public class TermSlimmer {
    static final OntologyRelationType[] DEFAULT_RELATION_TYPES =
            DEFAULT_SLIM_TRAVERSAL_TYPES.toArray(new OntologyRelationType[DEFAULT_TRAVERSAL_TYPES.size()]);
    private final Map<String, List<String>> slimTranslate;
    private final OntologyRelationType[] relationTypes;

    private TermSlimmer(OntologyRelationType[] relationTypes, Map<String, List<String>> slimTranslate) {
        this.relationTypes = relationTypes;
        this.slimTranslate = slimTranslate;
    }

    /**
     * Factory method for creating a {@link TermSlimmer} for a given {@link OntologyType}, {@link OntologyGraph},
     * set of slimmed-terms and where slimmed ancestors will only be computed via given relationships.
     *
     * @param ontologyType           the ontology type
     * @param ontology               the ontology
     * @param requestedSlimTerms     the slimmed-terms
     * @param requestedRelationTypes the relationships over which slimmed ancestors will be computed
     * @return a {@link TermSlimmer}
     */
    public static TermSlimmer createSlims(
            OntologyType ontologyType,
            OntologyGraphTraversal ontology,
            List<String> requestedSlimTerms,
            OntologyRelationType... requestedRelationTypes) {
        // if no slim terms have been supplied, there's nothing to do
        checkArgument(ontologyType != null && ontologyType == GO, "OntologyType cannot be null and must be GO");
        checkArgument(ontology != null, "Ontology cannot be null");
        checkArgument(requestedSlimTerms != null && !requestedSlimTerms.isEmpty(), "Slim-set cannot be null or empty");
        checkArgument(requestedRelationTypes != null && requestedRelationTypes.length != 0, "Requested relation types cannot be null");
        List<String> slimTerms = retainValidSlimTerms(ontology, ontologyType, requestedSlimTerms);
        checkArgument(!slimTerms.isEmpty(), "Requested slim-set contains no valid terms");

        OntologyRelationType[] relationTypes = getOntologyRelationTypes(requestedRelationTypes);

        // convert term IDs into an array for faster access
        String[] slimTermsArr = slimTerms.toArray(new String[slimTerms.size()]);

        // slim terms which exclude/hide other slim terms
        BitSet[] exclude = findExclusionBitSet(ontology, slimTerms, slimTermsArr, relationTypes);

        Map<String, List<String>> slimTranslationMap = new HashMap<>();

        for (String id : ontology.getVertices(ontologyType)) {
            // determine which slim terms (if any) are in the ancestry of this term
            //      if bit i is set in the BitSet, that means that slimTermsArr[i] is an ancestor of the term
            BitSet ancestorsBitSet = ontology.getAncestorsBitSet(id, slimTerms, requestedRelationTypes);

            if (ancestorsBitSet.cardinality() > 0) {
                // modify the ancestry to exclude any of the slim terms that are hidden by more specific ones
                BitSet slimTermsBitSet = excludeHiddenSlimTerms(exclude, ancestorsBitSet);

                // create a map entry that translates this term to slim term(s)
                slimTranslationMap.put(id, transformBitSetSlims(slimTermsArr, slimTermsBitSet));
            } else if (slimTerms.contains(id)) {
                // the term is in the slim-set but it has no ancestors for the specified relation
                slimTranslationMap.put(id, singletonList(id));
            }
            // else: none of this term's ancestors are in the slim-set, so there's nothing more to be done
        }

        return new TermSlimmer(relationTypes, unmodifiableMap(slimTranslationMap));
    }

    /**
     * Get the {@link OntologyRelationType}s that were used when finding slimmed ancestors. If
     * none were specified at creation time, defaults are used: IS_A, PART_OF, OCCURS_IN.
     *
     * @return the relations used when finding slimmed ancestors.
     */
    public OntologyRelationType[] getRelationTypes() {
        return relationTypes;
    }

    /**
     * Finds the terms within the original slim-set to which this term slims up to.
     *
     * @param id the identifier of the term to map
     * @return a list of terms within the original slim-set to which this term slims up to.
     */
    public List<String> findSlimmedToTerms(String id) {
        return slimTranslate.getOrDefault(id, emptyList());
    }

    /**
     * Get the map of ontology terms to slimmed equivalent terms.
     *
     * @return a map of ontology terms to slimmed equivalent terms.
     */
    public Map<String, List<String>> getSlimmedTermsMap() {
        return slimTranslate;
    }

    private static List<String> retainValidSlimTerms(OntologyGraphTraversal ontology, OntologyType ontologyType,
            List<String> requestedSlimTerms) {
        List<String> validSlimTerms = new ArrayList<>();
        Set<String> allValidTerms = ontology.getVertices(ontologyType);
        for (String requestedTerm : requestedSlimTerms) {
            if (allValidTerms.contains(requestedTerm)) {
                validSlimTerms.add(requestedTerm);
            }
        }
        return validSlimTerms;
    }

    /**
     * Transform a {@link BitSet} of slimming information into a {@link List} of slimmed term ids.
     *
     * @param slimTermsArr    array of term ids
     * @param slimTermsBitSet a bit-set of term id indices, that are part of the slim-set
     * @return a list of term ids corresponding to the bit-set of term id indices
     */
    private static List<String> transformBitSetSlims(String[] slimTermsArr, BitSet slimTermsBitSet) {
        List<String> mappedTerms = new ArrayList<>(slimTermsBitSet.cardinality());
        for (int i = 0; (i = slimTermsBitSet.nextSetBit(i)) >= 0; i++) {
            mappedTerms.add(slimTermsArr[i]);
        }
        return mappedTerms;
    }

    /**
     * Compute a {@link BitSet} array representing a mapping of terms, and the terms they hide.
     *
     * @param ontology      the ontology
     * @param slimTerms     the terms for which to compute slims for
     * @param relationTypes the relations over which to traverse when computing the slims
     * @param slimTermsArr  array of terms for which to compute slims for
     * @return a bit-set array that maps terms to the terms they hide
     */
    private static BitSet[] findExclusionBitSet(OntologyGraphTraversal ontology,
                                                List<String> slimTerms,
                                                String[] slimTermsArr,
                                                OntologyRelationType[] relationTypes) {
        BitSet[] exclude = new BitSet[slimTerms.size()];

        for (int i = 0; i < slimTermsArr.length; i++) {
            // a term excludes all its ancestors from being used as slim terms
            exclude[i] = ontology.getAncestorsBitSet(slimTermsArr[i], slimTerms, relationTypes);
            // a term does not exclude itself from a slim
            exclude[i].clear(i);
        }
        return exclude;
    }

    /**
     * Given a {@link BitSet} of ancestor term ids, create a corresponding {@link BitSet} where
     * terms may be hidden. Hiding/exclusion information is produced by
     * {@link #findExclusionBitSet(OntologyGraphTraversal, List, String[], OntologyRelationType[])}.
     * @param exclude a mapping of term id indices, to the indices of terms that they hide
     * @param ancestorsBitSet a representation of ancestors
     * @return a {@link BitSet} representing slimmed terms
     */
    private static BitSet excludeHiddenSlimTerms(BitSet[] exclude, BitSet ancestorsBitSet) {
        BitSet slimTermsBitSet = (BitSet) ancestorsBitSet.clone();
        for (int i = 0; (i = ancestorsBitSet.nextSetBit(i)) >= 0; i++) {
            slimTermsBitSet.andNot(exclude[i]);
        }
        return slimTermsBitSet;
    }

    /**
     * Given an array of requested relation types, get a corresponding array of relation types. If zero
     * relation types are requested, a default array is used, see {@link #DEFAULT_RELATION_TYPES}.
     * @param requestedRelationTypes the requested relation types
     * @return relation types over which slimming takes place
     */
    private static OntologyRelationType[] getOntologyRelationTypes(OntologyRelationType[] requestedRelationTypes) {
        OntologyRelationType[] relationTypes;
        if (requestedRelationTypes.length == 0) {
            relationTypes = DEFAULT_RELATION_TYPES;
        } else {
            relationTypes = requestedRelationTypes;
        }
        return relationTypes;
    }
}
