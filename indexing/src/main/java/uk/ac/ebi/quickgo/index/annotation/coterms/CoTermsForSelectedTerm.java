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
 * @author Tony Wardell
 * Date: 13/11/2015
 * Time: 14:24
 * Created with IntelliJ IDEA.
 */
public class CoTermsForSelectedTerm {

    private final String target;
    private final float totalNumberGeneProducts;
    private final long selected;

    private final List<CoTerm> coTerms = new ArrayList<>();

    /**
     * Create an instance of this class, initializing it the term term (against which all compared terms will be
     * compared.
     * @param term GO Term id which is the term will compare against all the compared too terms(because they are
     * co-occurring)
     * @param totalNumberGeneProducts The total number of unique gene products processed
     * @param selected Total count of proteins annotated to selected term
     */
    public CoTermsForSelectedTerm(String term, float totalNumberGeneProducts, long selected) {
        Preconditions.checkArgument(term != null, "term should not be null");
        Preconditions
                .checkArgument(totalNumberGeneProducts != 0, "totalNumberGeneProducts" +
                        " should not be zero");
        Preconditions.checkArgument(selected != 0, "term should not be zero");
        this.target = term;
        this.totalNumberGeneProducts = totalNumberGeneProducts;
        this.selected = selected;
    }

    /**
     * Add this term to the list of terms that annotate the same gene products as the target term.
     * @param coTerm has all the required information to all the co-occurrence statistics to be calculated.
     */
    public void addAndCalculate(CoTerm coTerm) {
        Preconditions.checkArgument(coTerm != null, "addAndCalculate was passed a coTerm which was null");
        coTerm.calculateProbabilityRatio(this.selected, this.totalNumberGeneProducts);
        coTerm.calculateProbabilitySimilarityRatio(this.selected);
        coTerms.add(coTerm);
    }


    /**
     * @return an iterator that makes available the list of co-occurring terms, in descending order of the similarity
     */
    public Iterator<CoTerm> highestSimilarity() {

        coTerms.sort(new SignificanceSorter());

        return new Iterator<CoTerm>() {

            final Iterator<CoTerm> navIterator = coTerms.iterator();

            @Override
            public boolean hasNext() {
                return navIterator.hasNext();
            }

            @Override
            public CoTerm next() {
                return navIterator.next();
            }
        };
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
