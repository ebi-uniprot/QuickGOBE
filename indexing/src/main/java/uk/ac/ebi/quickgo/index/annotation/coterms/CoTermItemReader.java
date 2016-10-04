package uk.ac.ebi.quickgo.index.annotation.coterms;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocument;

import java.util.Iterator;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;

/**
 * Provide a list of all GO Terms for which co-occurrence has been determined ( which is all of them that have been
 * processed, since at worst a GO Term will have a statistic for co-occurring with itself).
 *
 * @author Tony Wardell
 * Date: 07/09/2016
 * Time: 15:38
 * Created with IntelliJ IDEA.
 */
class CoTermItemReader implements ItemReader<String> {

    private final CoTermsAggregator aggregator;
    private Iterator<String> termsIt;

    public CoTermItemReader(ItemWriter<AnnotationDocument> annotationCoOccurringTermsAggregator) {
        this.aggregator = (CoTermsAggregator)annotationCoOccurringTermsAggregator;

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
