package uk.ac.ebi.quickgo.index.annotation.coterms;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocument;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;

/**
 * For the contents of the co-occurring terms matrix calculate co-occurrence statistics.
 *
 * @author Tony Wardell
 * Date: 26/11/2015
 * Time: 11:59
 * Created with IntelliJ IDEA.
 *
 */
public class StatisticsCalculator implements ItemProcessor<String, List<CoTerm>> {

    //This is the count of all gene products for the term. We hold this figure separately as it is used many times.
    private Map<String, AtomicLong> termGPCount;

    //Holds a termN by termN matrix, each cell of which holds the count of gp this intersection of terms hold
    private Map<String, Map<String, AtomicLong>> coTermMatrix;

    //Total number of unique gene products that have annotations
    private long geneProductCount;
    private final CoTermsAggregator aggregator;

    public StatisticsCalculator(ItemWriter<AnnotationDocument> aggregator) {
        Preconditions.checkArgument(aggregator!=null, "The aggregator instance passed to the Statistics Calculator " +
                "constructor cannot be null");
        this.aggregator = (CoTermsAggregator)aggregator;
    }

    /**
     * Read each line in the term to term matrix for the selected term. For each calculate a CoTerm instance.
     *
     */
    public List<CoTerm> process(String goTerm) {
        return createCoTermsForSelectedTerm(goTerm).highestSimilarity();
    }

    public void initialize() {
        this.geneProductCount = aggregator.getTotalOfAnnotatedGeneProducts();
        this.termGPCount = aggregator.getGeneProductCounts();
        this.coTermMatrix = aggregator.getCoTerms();
    }

    /**
     * Create a CoTermsForSelectedTerm instance for each compared term.
     * @param goTerm The GO Term for which the co-occurrence statistics will be calculated.
     * @return CoTermsForSelectedTerm instance with co-occurring statistics calculated for every
     * co-occurring term.
     */
    private CoTermsForSelectedTerm createCoTermsForSelectedTerm(String goTerm) {

        Preconditions.checkArgument(null != goTerm, "Target GO term id passed to createCoTermsForSelectedTerm should not " +
                "be null");

        Map<String, AtomicLong> coTermsForTarget = coTermMatrix.get(goTerm);
//        CoTermsForSelectedTerm
        //                coTerms = new CoTermsForSelectedTerm(geneProductCount, termGPCount.get(goTerm).get());

        CoTermsForSelectedTerm.Builder coTermsBuilder =  new CoTermsForSelectedTerm.Builder()
                .setTotalNumberOfGeneProducts
        (geneProductCount).setSelected(termGPCount.get(goTerm).get());

        for (String comparedTerm : coTermsForTarget.keySet()) {

            coTermsBuilder.addCoTerm(new CoTerm.Builder().setTarget(goTerm).setComparedTerm
                    (comparedTerm)
                    .setCompared(termGPCount.get(comparedTerm).get())
                    .setTogether(coTermsForTarget.get(comparedTerm).get()).createCoTerm());
        }
        return coTermsBuilder.build();

    }

    /**
     *  Get list of compared terms, ordered by significance ratio descending
     * @param coTerms
     * @return
     */
    private List<CoTerm> resultsForOneGoTerm(CoTermsForSelectedTerm coTerms) {
        return coTerms.highestSimilarity();
    }

}
