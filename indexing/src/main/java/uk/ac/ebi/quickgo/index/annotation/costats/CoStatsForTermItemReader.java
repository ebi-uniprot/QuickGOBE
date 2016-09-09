package uk.ac.ebi.quickgo.index.annotation.costats;

import java.util.Iterator;
import org.springframework.batch.item.ItemReader;

/**
 * @author Tony Wardell
 * Date: 07/09/2016
 * Time: 15:38
 * Created with IntelliJ IDEA.
 */
public class CoStatsForTermItemReader implements ItemReader<String>{

    private final Iterator<String> termsIt;

    public CoStatsForTermItemReader(CoStatsPermutations coStatsPermutations) {
        termsIt = coStatsPermutations.getTermToTermOverlapMatrix().keySet().iterator();
    }

    @Override public String read() throws Exception {

        if(termsIt.hasNext()){
            return termsIt.next();
        }

        return null;
    }
}
