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

    private List<CoTerm> sortedView;

    /**
     * Create an instance of this class, initializing it the list of co-occurring terms for a term.
     * @param sortedView The list of co-occurring terms for a term, sorted by similarity ratio
     */
    public CoTermsForSelectedTerm(List<CoTerm> sortedView) {
        this.sortedView = sortedView;
    }

    /**
     * @return an immutable list of co-occurring terms, in descending order of similarity.
     */
    public List<CoTerm> highestSimilarity() {
        return sortedView;
    }

    public static class Builder{
        private long totalNumberGeneProducts;
        private long selected;
        private final List<CoTerm> coTerms = new ArrayList<>();

        /**
         *
         * @param totalNumberGeneProducts The total number of unique gene products processed
         */
        Builder setTotalNumberOfGeneProducts(long totalNumberGeneProducts){
            Preconditions
                    .checkArgument(totalNumberGeneProducts != 0, "totalNumberGeneProducts" +
                            " should not be zero");
            this.totalNumberGeneProducts = totalNumberGeneProducts;
            return this;
        }

        /**
         *
         * @param selected Total count of proteins annotated to selected term
         */
        Builder setSelected(long selected){
            Preconditions.checkArgument(selected != 0, "term should not be zero");
            this.selected = selected;
            return this;
        }

        /**
         * Add this term to the list of terms that annotate the same gene products as the target term.
         * In this method the probability and similarity ratios are calculated on the passed in CoTerm, using information
         * held by this class.
         * @param coTerm has all the required information to all the co-occurrence statistics to be calculated.
         */
        Builder addCoTerm(CoTerm coTerm) {
            Preconditions.checkArgument(coTerm != null, "addCoTerm was passed a coTerm which was null");
            coTerm.calculateProbabilityRatio(this.selected, this.totalNumberGeneProducts);
            coTerm.calculateProbabilitySimilarityRatio(this.selected);
            coTerms.add(coTerm);
            return this;
        }

        /**
         * @return an immutable list of co-occurring terms, in descending order of similarity.
         */
        CoTermsForSelectedTerm build(){
            Preconditions
                    .checkArgument(totalNumberGeneProducts != 0, "totalNumberGeneProducts" +
                            " should not be zero");
            Preconditions.checkArgument(selected != 0, "term should not be zero");
            coTerms.sort(new SignificanceSorter());
            return new CoTermsForSelectedTerm (Collections.unmodifiableList(coTerms));
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
}
