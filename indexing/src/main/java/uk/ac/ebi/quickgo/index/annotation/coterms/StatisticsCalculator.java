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
 * For a GO Term retrieve all the co-occurring terms together with the statistics related to that co-occurrence.
 *
 * @author Tony Wardell
 * Date: 26/11/2015
 * Time: 11:59
 * Created with IntelliJ IDEA.
 *
 */
public class StatisticsCalculator implements ItemProcessor<String, List<CoTerm>> {

    private final CoTermsAggregator aggregator;

    /**
     *
     * @param aggregator holds the data for co-occurring terms.
     */
    public StatisticsCalculator(ItemWriter<AnnotationDocument> aggregator) {
        Preconditions.checkArgument(aggregator!=null, "The aggregator instance passed to the Statistics Calculator " +
                "constructor cannot be null");
        this.aggregator = (CoTermsAggregator)aggregator;
    }

    /**
     * For the passed in GO Term id, find the list of co-occurring terms and calculate CoTerm instances.
     * @param goTerm the GO Term id for which co-occurring statistics will be calculated, co-occurring term list of
     * objects will be returned containing data and statistics.
     * @return a list of CoTerm objects. Each object represents a single permutation of two GO Terms that are
     * used to annotate the same gene product, and the statistics about that permutation.
     */
    @Override
    public List<CoTerm> process(String goTerm) {
        return createCoTermsForSelectedTerm(goTerm).highestSimilarity();
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

        Map<String, AtomicLong> coTermsForTarget = aggregator.getCoTerms().get(goTerm);
        CoTermsForSelectedTerm.Builder coTermsBuilder =  new CoTermsForSelectedTerm.Builder()
                .setTotalNumberOfGeneProducts(aggregator.getTotalOfAnnotatedGeneProducts()).setSelected(aggregator.getGeneProductCounts().get(goTerm).get());

        for (String comparedTerm : coTermsForTarget.keySet()) {
            coTermsBuilder.addCoTerm(new CoTerm.Builder().setTarget(goTerm).setComparedTerm
                    (comparedTerm)
                    .setCompared(aggregator.getGeneProductCounts().get(comparedTerm).get())
                    .setTogether(coTermsForTarget.get(comparedTerm).get()).createCoTerm());
        }
        return coTermsBuilder.build();

    }
}
