package uk.ac.ebi.quickgo.ontology.common.coterm;

import com.google.common.base.Preconditions;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author Tony Wardell
 * Date: 29/09/2016
 * Time: 13:48
 * Created with IntelliJ IDEA.
 */
@Component
public class CoTermRepositorySimpleMap  implements CoTermRepository{

    private static final Logger logger = LoggerFactory.getLogger(CoTermRepositorySimpleMap.class);
    private Map<String, List<CoTerm>> coTermsAll;
    private Map<String, List<CoTerm>> coTermsManual;

    public CoTermRepositorySimpleMap(Map<String, List<CoTerm>> costatsAll, Map<String, List<CoTerm>> costatsManual) {
        this.coTermsAll = costatsAll;
        this.coTermsManual = costatsManual;
    }

    /**
     * Get all co-occurring terms for the requested term up to the supplied limit
     * @param id
     * @param limit
     * @return
     */
    public List<CoTerm> findCoTerms(String id, CoTermType type, int limit, int similarityThreshold) {
        return type==CoTermType.MANUAL?findCoTermsFromMap(coTermsManual, id, limit, similarityThreshold)
                :findCoTermsFromMap(coTermsAll, id, limit, similarityThreshold);
    }


    /**
     * Get all co-occurring terms for the requested term up to the supplied limit
     * @param id
     * @param limit
     * @return
     */
    private List<CoTerm> findCoTermsFromMap(Map<String, List<CoTerm>> map, String id, int limit, int
            similarityThreshold) {

        List<CoTerm> results = map.get(id);

        if(results==null){
            return new ArrayList<>();
        }

        if(similarityThreshold >0){
            results = results.stream()
                    .filter(t -> t.getSignificance() >= similarityThreshold)    //todo requires sorting too?
                    .collect(Collectors.toList());
        }

        if(results.size()<=limit){
            return results.subList(0,results.size()-1);
        }

        return results.subList(0, limit);
    }

    /**
     * Show some statistics for the Costats
     * @param out
     */
    public void report(PrintStream out) {

        out.println("Manual Report");
        out.println("==============");
        out.println("Number of Terms: " + coTermsManual.keySet().size());
        out.println("");
        out.println("ALL Report");
        out.println("==========");
        out.println("Number of Terms: " + coTermsAll.keySet().size());
        out.println("");

    }
}
