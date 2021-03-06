package uk.ac.ebi.quickgo.index.annotation.coterms;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

/**
 * Aggregation class for co-occurring terms.
 * Add a succession of terms that annotate the same gene product(s) as a single (unspecified in this class) term.
 *
 * @author Tony Wardell
 * Date: 13/11/2015
 * Time: 14:24
 * Created with IntelliJ IDEA.
 */
class CoTermsForSelectedTerm {

    private final List<CoTerm> sortedView;

    /**
     * Create an instance of this class, initializing it with the list of co-occurring terms for a term.
     * @param sortedView The list of co-occurring terms for a term, sorted by similarity ratio
     */
    private CoTermsForSelectedTerm(List<CoTerm> sortedView) {
        this.sortedView = sortedView;
    }

    /**
     * @return an immutable list of co-occurring terms, in descending order of similarity.
     */
    List<CoTerm> highestSimilarity() {
        return sortedView;
    }

    public static class Builder {
        private final List<CoTerm> coTerms = new ArrayList<>();
        private long totalNumberGeneProducts;
        private long selected;

        /**
         * @return an empty instance.
         */
        static CoTermsForSelectedTerm empty() {
            return new CoTermsForSelectedTerm(Collections.emptyList());
        }

        /**
         *
         * @param totalNumberGeneProducts The total count of unique gene products for all terms encountered during
         * processing.
         */
        Builder setTotalNumberOfGeneProducts(long totalNumberGeneProducts) {
            Preconditions
                    .checkArgument(totalNumberGeneProducts > 0, "totalNumberGeneProducts" +
                            " should not be zero");
            this.totalNumberGeneProducts = totalNumberGeneProducts;
            return this;
        }

        /**
         *
         * @param selected The count of unique gene products annotated to selected term.
         */
        Builder setSelected(long selected) {
            Preconditions.checkArgument(selected != 0, "'setSelected' should not be zero");
            this.selected = selected;
            return this;
        }

        /**
         * Add this term to the list of terms that annotate the same gene products as the target term.
         * In this method the probability and similarity ratios are calculated on the passed in CoTerm, using
         * information
         * held by this class.
         * @param coTerm has all the required information to all the co-occurrence statistics to be calculated.
         */
        Builder addCoTerm(CoTerm coTerm) {
            Preconditions.checkArgument(coTerm != null, "addCoTerm was passed a coTerm which was null");
            coTerms.add(coTerm);
            return this;
        }

        /**
         * Create CoTermsForSelectedTerm instance populated with CoTerms
         * @return CoTermsForSelectedTerm instance
         */
        CoTermsForSelectedTerm build() {
            Preconditions
                    .checkState(totalNumberGeneProducts != 0, "totalNumberGeneProducts" +
                            " should not be zero");
            Preconditions.checkArgument(selected != 0, "selected should not be zero");
            return new CoTermsForSelectedTerm(Collections.unmodifiableList(sortCoTermsBySimilarityDescending()));
        }

        /**
         * @return an immutable list of co-occurring terms, in descending order of similarity.
         */
        private List<CoTerm> sortCoTermsBySimilarityDescending() {
            return coTerms.stream()
                          .sorted(comparing(CoTerm::getSimilarityRatio).reversed())
                          .collect(Collectors.toList());
        }
    }
}
