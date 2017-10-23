package uk.ac.ebi.quickgo.ontology.traversal;

import uk.ac.ebi.quickgo.ontology.common.OntologyType;
import uk.ac.ebi.quickgo.ontology.model.OntologyRelationType;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.*;
import static uk.ac.ebi.quickgo.ontology.common.OntologyType.GO;
import static uk.ac.ebi.quickgo.ontology.model.OntologyRelationType.DEFAULT_SLIM_TRAVERSAL_TYPES;
import static uk.ac.ebi.quickgo.ontology.model.OntologyRelationType.DEFAULT_TRAVERSAL_TYPES;

/**
 * An instance of this class is used to find, for a given term, the equivalent "slimmed" terms. The slimming algorithm
 * is documented here: https://www.ebi.ac.uk/seqdb/confluence/display/GOA/GO+Slimming+-+an+executive+summary.
 *
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
     * @param ontologyType the ontology type
     * @param ontology the ontology
     * @param slimTerms the slimmed-terms
     * @param requestedRelationTypes the relationships over which slimmed ancestors will be computed
     * @return a {@link TermSlimmer}
     */
    public static TermSlimmer createSlims(
            OntologyType ontologyType,
            OntologyGraphTraversal ontology,
            List<String> slimTerms,
            OntologyRelationType... requestedRelationTypes) {
        // if no slim terms have been supplied, there's nothing to do
        checkArgument(ontologyType != null && ontologyType == GO, "OntologyType cannot be null and must be GO");
        checkArgument(ontology != null, "Ontology cannot be null");
        checkArgument(slimTerms != null && !slimTerms.isEmpty(), "Slim-set cannot be null or empty");
        checkArgument(requestedRelationTypes != null, "Requested relation types cannot be null");

        OntologyRelationType[] relationTypes;
        if (requestedRelationTypes.length == 0) {
            relationTypes = DEFAULT_RELATION_TYPES;
        } else {
            relationTypes = requestedRelationTypes;
        }

        // convert term IDs into an array for faster access
        String[] slimTermsArr = slimTerms.toArray(new String[slimTerms.size()]);

        // slim terms which exclude/hide other slim terms
        BitSet[] exclude = new BitSet[slimTerms.size()];

        for (int i = 0; i < slimTermsArr.length; i++) {
            // a term excludes all its ancestors from being used as slim terms
            exclude[i] = ontology.getAncestorsBitSet(slimTermsArr[i], slimTerms, relationTypes);
            // a term does not exclude itself from a slim
            exclude[i].clear(i);
        }

        Map<String, List<String>> slimTranslationMap = new HashMap<>();

        for (String id : ontology.getVertices(ontologyType)) {
            // determine which slim terms (if any) are in the ancestry of this term
            //      if bit i is set in the BitSet, that means that slimTermsArr[i] is an ancestor of the term
            BitSet ancestorsBitSet = ontology.getAncestorsBitSet(id, slimTerms, requestedRelationTypes);

            if (ancestorsBitSet.cardinality() > 0) {
                // modify the ancestry to exclude any of the slim terms that are hidden by more specific ones
                BitSet slimTermsBitSet = (BitSet) ancestorsBitSet.clone();
                for (int i = 0; (i = ancestorsBitSet.nextSetBit(i)) >= 0; i++) {
                    slimTermsBitSet.andNot(exclude[i]);
                }

                // create a map entry that translates this term to slim term(s)
                List<String> mappedTerms = new ArrayList<>(slimTermsBitSet.cardinality());
                for (int i = 0; (i = slimTermsBitSet.nextSetBit(i)) >= 0; i++) {
                    mappedTerms.add(slimTermsArr[i]);
                }
                slimTranslationMap.put(id, mappedTerms);
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
     * @param id the identifier of the term to map
     * @return a list of terms within the original slim-set to which this term slims up to.
     */
    public List<String> findSlims(String id) {
        return slimTranslate.getOrDefault(id, emptyList());
    }

    /**
     * Get the map of ontology terms to slimmed equivalent terms.
     * @return a map of ontology terms to slimmed equivalent terms.
     */
    public Map<String, List<String>> getSlimmedTermsMap() {
        return slimTranslate;
    }
}
