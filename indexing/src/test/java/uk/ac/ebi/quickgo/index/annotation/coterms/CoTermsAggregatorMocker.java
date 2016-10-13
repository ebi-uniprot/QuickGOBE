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
     * @param count the number that the count for Gene Product will be set to.
     * @param termsLists a list of GO Term identifiers used to seed a map of term::count used in testing
     * @return a map of term::count used in testing
     */
    @SafeVarargs
    static Map<String, AtomicLong> makeGpCountForTerm(int count, List<String>... termsLists ){
        Map<String, AtomicLong> termGpCount = new HashMap<>();

        for (List<String> terms : termsLists) {
            for (String s : terms) {
                termGpCount.put(s, new AtomicLong(count));
            }
        }

        return termGpCount;
    }
}
