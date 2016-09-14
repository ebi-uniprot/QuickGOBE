package uk.ac.ebi.quickgo.index.annotation.coterms;

import com.google.common.base.Preconditions;
import java.util.*;
import org.springframework.batch.item.ItemProcessor;

/**
 * @author Tony Wardell
 * Date: 26/11/2015
 * Time: 11:59
 * Created with IntelliJ IDEA.
 *
 * For the contents of the termToTermOverlapMatrix calculate co-occurrence statistics
 * A version of CoStatsSummarizer from Beta
 */
public class Co_occurringTermsStatsCalculator implements ItemProcessor<String, List<Co_occurringTerm>> {

    //This is the count of all gene products for the term. We hold this figure separately as it is used many times.
    private Map<String, HitCount> termGPCount;

    //Holds a termN by termN matrix, each cell of which holds the count of gp this intersection of terms hold
    private Map<String, Map<String, HitCount>> termToTermOverlapMatrix;

    //Total number of unique gene products that have annotations
    private long geneProductCount;
    private AnnotationCo_occurringTermsAggregator annotationCoOccurringTermsAggregator;

    public Co_occurringTermsStatsCalculator(
            AnnotationCo_occurringTermsAggregator annotationCoOccurringTermsAggregator) {
        this.annotationCoOccurringTermsAggregator = annotationCoOccurringTermsAggregator;
        this.geneProductCount = 0;
        this.termGPCount = null;
        this.termToTermOverlapMatrix = null;
    }

    /**
     * Read each line in the term to term matrix for the selected term. For each calculate a CoStat instance.
     *
     */
    public List<Co_occurringTerm> process(String goTerm) {

        //One time operation
        if (termToTermOverlapMatrix == null) {
            this.geneProductCount = annotationCoOccurringTermsAggregator.totalOfAnnotatedGeneProducts();
            this.termGPCount = annotationCoOccurringTermsAggregator.termGPCount();
            this.termToTermOverlapMatrix = annotationCoOccurringTermsAggregator.termToTermOverlapMatrix();
        }

        return resultsForOneGoTerm(calculateCoStatsForTerm(goTerm));
    }

    /**
     * Create a CoStatsForTerm instance for each compared term, and calculate the COOccurrenceStatsTerm based on the
     * values passed in.
     * @param target The GO Term for which the co-occurrence statistics will be calculated.
     * @return Co_occurringTermsForSelectedTerm instance with co-occurring statistics calculated for every
     * co-occurring term.
     */
    private Co_occurringTermsForSelectedTerm calculateCoStatsForTerm(String target) {

        Preconditions.checkArgument(null != target, "Target passed to calculateCoStatsForTerm should not be null");

        Map<String, HitCount> co_occurringTermsForTarget = termToTermOverlapMatrix.get(target);
        Co_occurringTermsForSelectedTerm
                coTerms = new Co_occurringTermsForSelectedTerm(target, geneProductCount, termGPCount.get(target).hits);

        for (String comparedTerm : co_occurringTermsForTarget.keySet()) {

            coTerms.addAndCalculate(new Co_occurringTerm(target, comparedTerm, termGPCount.get(comparedTerm).hits,
                            co_occurringTermsForTarget.get(comparedTerm).hits));
        }
        return coTerms;

    }

    private List<Co_occurringTerm> resultsForOneGoTerm(
            Co_occurringTermsForSelectedTerm coOccurringTermsForSelectedTerm) {

        List<Co_occurringTerm> results = new ArrayList<>();

        //Get iterator of compared terms, ordered by significance ratio descending
        Iterator<Co_occurringTerm> descendingIt = coOccurringTermsForSelectedTerm.highestSimilarity();

        while (descendingIt.hasNext()) {
            results.add(descendingIt.next());
        }

        return results;
    }

}
