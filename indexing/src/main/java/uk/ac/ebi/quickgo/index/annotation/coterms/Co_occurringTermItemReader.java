package uk.ac.ebi.quickgo.index.annotation.coterms;

import java.util.Iterator;
import org.springframework.batch.item.ItemReader;

/**
 * Provide a list of all GO Terms for which co-occurrence has been determined ( which is all of them that have been
 * processed, since at worst a GO Term will have a statistic for co-occurring with itself).
 *
 * @author Tony Wardell
 * Date: 07/09/2016
 * Time: 15:38
 * Created with IntelliJ IDEA.
 */
class Co_occurringTermItemReader implements ItemReader<String> {

    private final AnnotationCoOccurringTermsAggregator aggregator;
    private Iterator<String> termsIt;

    public Co_occurringTermItemReader(AnnotationCoOccurringTermsAggregator annotationCoOccurringTermsAggregator) {
        this.aggregator = annotationCoOccurringTermsAggregator;

    }

    @Override public String read() throws Exception {

        //Delay providing full list until aggregator has fully processed all records.
        if (termsIt == null) {
            termsIt = aggregator.getCoTerms().keySet().iterator();
        }

        if (termsIt.hasNext()) {
            return termsIt.next();
        }

        return null;
    }
}
