package uk.ac.ebi.quickgo.index.annotation.coterms;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Aggregation class for co-occurring terms.
 * Create the class with the target GO term id.
 * Then add a succession of terms that annotate the same gene product(s) as the target term.
 * Calculate co-occurring statistics at the time the compared to term is added to the list.
 *
 *
 * @author Tony Wardell
 * Date: 13/11/2015
 * Time: 14:24
 * Created with IntelliJ IDEA.
 */
public class Co_occurringTermsForSelectedTerm {

    private final String target;
    private final float totalNumberGeneProducts;
    private final long selected;

    private final List<Co_occurringTerm> statsTerms = new ArrayList<>();

    /**
     * Create an instance of this class, initializing it the target term (against which all compared terms will be
     * compared.
     * @param target GO Term id which is the term will compare against all the compared too terms(because they are
     * co-occurring)
     * @param totalNumberGeneProducts The total number of unique gene products processed
     * @param selected Total count of proteins annotated to selected term
     */
    public Co_occurringTermsForSelectedTerm(String target, float totalNumberGeneProducts, long selected) {
        Preconditions.checkArgument(target != null, "CoOccurringTermsForSelectedTerm target should not be null");
        Preconditions
                .checkArgument(totalNumberGeneProducts != 0, "CoOccurringTermsForSelectedTerm totalNumberGeneProducts" +
                        " should not be zero");
        Preconditions.checkArgument(selected != 0, "CoOccurringTermsForSelectedTerm target should not be zero");
        this.target = target;
        this.totalNumberGeneProducts = totalNumberGeneProducts;
        this.selected = selected;
    }

    /**
     * Add this term to the list of terms that annotate the same gene products as the target term.
     * @param coOccurringTerm has all the required information to all the co-occurrence statistics to be calculated.
     */
    public void addAndCalculate(Co_occurringTerm coOccurringTerm) {
        Preconditions.checkArgument(coOccurringTerm != null, "CoOccurringTermsForSelectedTerm.addAndCalculate was " +
                "passed a CoOccurringTerm which was null");
        coOccurringTerm.calculateProbabilityRatio(this.selected, this.totalNumberGeneProducts);
        coOccurringTerm.calculateProbabilitySimilarityRatio(this.selected);
        statsTerms.add(coOccurringTerm);
    }

    /**
     * The term id which co-occurs with all the co-occurring terms listed in this instance.
     * @return term id
     */
    public String getTarget() {
        return target;
    }

    /**
     * @return an iterator that makes available the list of co-occurring terms, in descending order of the similarity
     */
    public Iterator<Co_occurringTerm> highestSimilarity() {

        statsTerms.sort(new SignificanceSorter());

        return new Iterator<Co_occurringTerm>() {

            final Iterator<Co_occurringTerm> navIterator = statsTerms.iterator();

            @Override
            public boolean hasNext() {
                return navIterator.hasNext();
            }

            @Override
            public Co_occurringTerm next() {
                return navIterator.next();
            }
        };
    }

    private class SignificanceSorter implements Comparator<Co_occurringTerm> {

        @Override
        public int compare(Co_occurringTerm o1, Co_occurringTerm o2) {
            if (o1.getSimilarityRatio() == o2.getSimilarityRatio()) {
                return 0;
            }
            return o1.getSimilarityRatio() > o2.getSimilarityRatio() ? -1 : 1;
        }
    }
}
