package uk.ac.ebi.quickgo.index.annotation.coterms;

import com.google.common.base.Preconditions;
import java.util.List;
//import java.util.Map;
//import java.util.concurrent.atomic.AtomicLong;
import org.springframework.batch.item.ItemProcessor;

/**
 * For a GO Term retrieve all the co-occurring terms together with the statistics related to that co-occurrence.
 *
 * @author Tony Wardell
 * Date: 26/11/2015
 * Time: 11:59
 * Created with IntelliJ IDEA.
 *
 */
public class CoTermsProcessor implements ItemProcessor<String, List<CoTerm>> {

    private final CoTermsAggregationWriter aggregator;

    /**
     *
     * @param aggregator holds the data for co-occurring terms.
     */
    public CoTermsProcessor(CoTermsAggregationWriter aggregator) {
        Preconditions.checkArgument(aggregator != null, "The aggregator instance passed to the Statistics Calculator " +
                "constructor cannot be null");
        this.aggregator = aggregator;
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
                Preconditions
                        .checkArgument(null != goTerm, "Target GO term id passed to createCoTermsForSelectedTerm should not " +
                                "be null");
        return aggregator.createCoTermsForSelectedTerm(goTerm).highestSimilarity();
    }

}
