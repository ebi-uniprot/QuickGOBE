package uk.ac.ebi.quickgo.index.annotation.coterms;

import com.google.common.base.Preconditions;
import java.util.Iterator;
import org.springframework.batch.item.ItemReader;

/**
 * @author Tony Wardell
 * Date: 07/09/2016
 * Time: 15:38
 * Created with IntelliJ IDEA.
 */
public class CoStatsForTermItemReader implements ItemReader<String>{

    private final CoStatsPermutations coStatsPermutations;

    public CoStatsForTermItemReader(CoStatsPermutations coStatsPermutations) {
        this.coStatsPermutations = coStatsPermutations;

    }

    @Override public String read() throws Exception {
        Iterator<String> termsIt = coStatsPermutations.getTermToTermOverlapMatrix().keySet().iterator();

        if(termsIt.hasNext()){
            return termsIt.next();
        }

        return null;
    }
}
