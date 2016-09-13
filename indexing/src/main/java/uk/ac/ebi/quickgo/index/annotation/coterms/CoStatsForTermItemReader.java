package uk.ac.ebi.quickgo.index.annotation.coterms;

import java.util.Iterator;
import org.springframework.batch.item.ItemReader;

/**
 * @author Tony Wardell
 * Date: 07/09/2016
 * Time: 15:38
 * Created with IntelliJ IDEA.
 */
public class CoStatsForTermItemReader implements ItemReader<String>{

    private final AnnotationCoTermsAggregator annotationCoTermsAggregator;
    Iterator<String> termsIt;

    public CoStatsForTermItemReader(AnnotationCoTermsAggregator annotationCoTermsAggregator) {
        this.annotationCoTermsAggregator = annotationCoTermsAggregator;

    }

    @Override public String read() throws Exception {
        if(termsIt==null) {
            termsIt = annotationCoTermsAggregator.getTermToTermOverlapMatrix().keySet().iterator();
        }

        if(termsIt.hasNext()){
            return termsIt.next();
        }

        return null;
    }
}
