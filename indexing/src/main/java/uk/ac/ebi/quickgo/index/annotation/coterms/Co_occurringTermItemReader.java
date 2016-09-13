package uk.ac.ebi.quickgo.index.annotation.coterms;

import java.util.Iterator;
import org.springframework.batch.item.ItemReader;

/**
 * @author Tony Wardell
 * Date: 07/09/2016
 * Time: 15:38
 * Created with IntelliJ IDEA.
 */
class Co_occurringTermItemReader implements ItemReader<String> {

    private final AnnotationCo_occurringTermsAggregator annotationCoOccurringTermsAggregator;
    private Iterator<String> termsIt;

    public Co_occurringTermItemReader(AnnotationCo_occurringTermsAggregator annotationCoOccurringTermsAggregator) {
        this.annotationCoOccurringTermsAggregator = annotationCoOccurringTermsAggregator;

    }

    @Override public String read() throws Exception {
        if (termsIt == null) {
            termsIt = annotationCoOccurringTermsAggregator.getTermToTermOverlapMatrix().keySet().iterator();
        }

        if (termsIt.hasNext()) {
            return termsIt.next();
        }

        return null;
    }
}
