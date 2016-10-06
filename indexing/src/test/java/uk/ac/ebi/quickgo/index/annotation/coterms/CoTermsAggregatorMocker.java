package uk.ac.ebi.quickgo.index.annotation.coterms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Tony Wardell
 * Date: 05/10/2016
 * Time: 16:41
 * Created with IntelliJ IDEA.
 */
public class CoTermsAggregatorMocker {

    /**
     * Mocks the {@code CoTermsAggregator#getGeneProductCounts} to product a count of gene products annotated by a
     * single GO Term.
     * @param count
     * @param termsLists
     * @return
     */
    static Map<String, AtomicLong> makeGpCountForTerm(int count, List<String>... termsLists ){
        Map<String, AtomicLong> termGpCount = new HashMap<>();

        for (int i = 0; i < termsLists.length; i++) {
            List<String> terms = termsLists[i];

            for (int j = 0; j < terms.size(); j++) {
                String s =  terms.get(j);
                termGpCount.put(s, new AtomicLong(count));
            }
        }

        return termGpCount;
    }
}
