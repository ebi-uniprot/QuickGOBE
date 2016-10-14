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
     * @param comparedList a list of real or imagined GO Terms.
     * @param hits co-occurring count to be added to each member of the compared list.
     * @return a map of the contents of comparedList together with the hits value passed in as an argument.
     */
    static Map<String, AtomicLong> createCoOccurringTermValues(List<String> comparedList, int hits) {
        Map<String, AtomicLong> coOccurringTerms = new HashMap<>();
        for (String comparedTerm : comparedList) {
            coOccurringTerms.put(comparedTerm, new AtomicLong(hits));
        }
        return coOccurringTerms;
    }
}
