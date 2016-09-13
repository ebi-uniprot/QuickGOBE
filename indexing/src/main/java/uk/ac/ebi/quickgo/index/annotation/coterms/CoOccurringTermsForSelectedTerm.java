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
 * @Author Tony Wardell
 * Date: 13/11/2015
 * Time: 14:24
 * Created with IntelliJ IDEA.
 */
public class CoOccurringTermsForSelectedTerm {

    private final String target;
    private final float totalNumberGeneProducts;
    private long selected;

    private List<CoOccurringTerm> statsTerms = new ArrayList<>();

    /**
     * Create an instance of this class, initializing it the target term (against which all compared terms will be
     * compared.
     * @param target GO Term id which is the term will compare against all the compared too terms(because they are
     * co-occurring)
     * @param totalNumberGeneProducts
     * @param selected
     */
    public CoOccurringTermsForSelectedTerm(String target, float totalNumberGeneProducts, long selected) {
        Preconditions.checkArgument(target!=null, "CoOccurringTermsForSelectedTerm target should not be null");
        Preconditions.checkArgument(totalNumberGeneProducts!=0, "CoOccurringTermsForSelectedTerm totalNumberGeneProducts" +
                " should not be zero");
        Preconditions.checkArgument(selected!=0, "CoOccurringTermsForSelectedTerm target should not be zero");
        this.target = target;
        this.totalNumberGeneProducts = totalNumberGeneProducts;
        this.selected = selected;
    }

    /**
     * Add this term to the list of terms that annotate the same gene products as the target term.
     * @param coOccurringTerm has all the required information to all the co-occurrence statistics to be calculated.
     */
    public void addAndCalculate(CoOccurringTerm coOccurringTerm) {
        Preconditions.checkArgument(coOccurringTerm!=null, "CoOccurringTermsForSelectedTerm.addAndCalculate was " +
                "passed a CoOccurringTerm which was null");
        coOccurringTerm.calculateProbabilityRatio(this.selected, this.totalNumberGeneProducts);
        coOccurringTerm.calculateProbabilitySimilarityRatio(this.selected);
        statsTerms.add(coOccurringTerm);
    }

    /**
     * The term id which co-occurs with all the co-occurring terms listed in this instance.
     * @return
     */
    public String getTarget() {
        return target;
    }

    /**
     * @return an iterator that makes available the list of co-occurring terms, in descending order of the similarity
     */
    public Iterator<CoOccurringTerm> highestSimilarity() {

        statsTerms.sort(new SignificanceSorter());

        return new Iterator<CoOccurringTerm>() {

            Iterator<CoOccurringTerm> navIterator = statsTerms.iterator();

            @Override
            public boolean hasNext() {
                return navIterator.hasNext();
            }

            @Override
            public CoOccurringTerm next() {
                return navIterator.next();
            }
        };
    }


    private class SignificanceSorter implements Comparator<CoOccurringTerm> {

        @Override
        public int compare(CoOccurringTerm o1, CoOccurringTerm o2) {
            if (o1.getSimilarityRatio() == o2.getSimilarityRatio()) {
                return 0;
            }
            return o1.getSimilarityRatio() > o2.getSimilarityRatio() ? -1 : 1;
        }
    }
}
