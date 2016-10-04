package uk.ac.ebi.quickgo.index.annotation.coterms;

import com.google.common.base.Preconditions;
import java.util.*;

/**
 * Aggregation class for co-occurring terms.
 * Add a succession of terms that annotate the same gene product(s) as a single (unspecified in this class) term.
 * Calculate co-occurring statistics at the time the compared to term is added to the list.
 *
 * @author Tony Wardell
 * Date: 13/11/2015
 * Time: 14:24
 * Created with IntelliJ IDEA.
 */
public class CoTermsForSelectedTerm {

    private final float totalNumberGeneProducts;
    private final long selected;
    private final List<CoTerm> coTerms = new ArrayList<>();

    /**
     * Create an instance of this class, initializing it the term term (against which all compared terms will be
     * compared.
     * @param totalNumberGeneProducts The total number of unique gene products processed
     * @param selected Total count of proteins annotated to selected term
     */
    public CoTermsForSelectedTerm(float totalNumberGeneProducts, long selected) {
        Preconditions
                .checkArgument(totalNumberGeneProducts != 0, "totalNumberGeneProducts" +
                        " should not be zero");
        Preconditions.checkArgument(selected != 0, "term should not be zero");
        this.totalNumberGeneProducts = totalNumberGeneProducts;
        this.selected = selected;
    }

    /**
     * Add this term to the list of terms that annotate the same gene products as the target term.
     * In this method the probability and similarity ratios are calculated on the passed in CoTerm, using information
     * held by this class.
     * @param coTerm has all the required information to all the co-occurrence statistics to be calculated.
     */
    public void addCoTerm(CoTerm coTerm) {
        Preconditions.checkArgument(coTerm != null, "addCoTerm was passed a coTerm which was null");
        coTerm.calculateProbabilityRatio(this.selected, this.totalNumberGeneProducts);
        coTerm.calculateProbabilitySimilarityRatio(this.selected);
        coTerms.add(coTerm);
    }


    /**
     * @return an immutable list of co-occurring terms, in descending order of similarity.
     */
    public List<CoTerm> highestSimilarity() {
        coTerms.sort(new SignificanceSorter());
        return Collections.unmodifiableList(coTerms);

    }

    private class SignificanceSorter implements Comparator<CoTerm> {

        @Override
        public int compare(CoTerm o1, CoTerm o2) {
            if (o1.getSimilarityRatio() == o2.getSimilarityRatio()) {
                return 0;
            }
            return o1.getSimilarityRatio() > o2.getSimilarityRatio() ? -1 : 1;
        }
    }
}
