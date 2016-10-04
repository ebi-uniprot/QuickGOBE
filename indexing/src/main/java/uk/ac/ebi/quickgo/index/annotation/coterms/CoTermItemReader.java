package uk.ac.ebi.quickgo.index.annotation.coterms;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocument;

import com.google.common.base.Preconditions;
import java.util.Iterator;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;

/**
 * Iterates (reads) through the list of all GO terms which have a co-occurring go term. Note that, by definition,
 * each GO term co-occurs with itself.
 *
 * @author Tony Wardell
 * Date: 07/09/2016
 * Time: 15:38
 * Created with IntelliJ IDEA.
 */
class CoTermItemReader implements ItemReader<String> {

    private final CoTermsAggregator aggregator;
    private Iterator<String> termsIt;

    public CoTermItemReader(ItemWriter<AnnotationDocument> aggregator) {
        this.aggregator = (CoTermsAggregator)aggregator;
        Preconditions.checkArgument(aggregator!=null, "An instance of CoTermItemReader has been passed a null " +
                "ItemWriter<AnnotationDocument> to it's constructor which is illegal");

    }

    /**
     * Provide the next GO Term id from the list of all co-occurring GO Terms.
     * @return a single GO Term id
     * @throws Exception
     */
    @Override public String read() throws Exception {

        Preconditions.checkArgument(aggregator!=null, "CoTermsAggregator instance is null");

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
