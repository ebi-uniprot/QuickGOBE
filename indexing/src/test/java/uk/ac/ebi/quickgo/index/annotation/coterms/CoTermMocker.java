package uk.ac.ebi.quickgo.index.annotation.coterms;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Tony Wardell
 * Date: 21/07/2016
 * Time: 10:08
 * Created with IntelliJ IDEA.
 */
public class CoTermMocker {

    public static final DecimalFormat idFormat1 = new DecimalFormat("0000000");
    public static final DecimalFormat idFormat2 = new DecimalFormat("9000000");
    public static final String EXAMPLE_GO_TERM1 = "GO:0003824";

    static Map<String, Map<String, AtomicLong>> singleEntry(){
        Map<String, Map<String, AtomicLong>> matrix = new HashMap<>();
        List<String> comparedList = Collections.singletonList(EXAMPLE_GO_TERM1);
        matrix.put(EXAMPLE_GO_TERM1, createCoOccurringTermValues(comparedList, 2));
        return  matrix;
    }

    /**
     * @param comparedList a list of real or imagined GO Terms.
     * @param hits co-occurring count to be added to each member of the compared list.
     * @return a map of the contents of comparedList together with the hits value passed in as an argument.
     * @return
     */
     static Map<String, AtomicLong> createCoOccurringTermValues(List<String> comparedList, int hits) {
        Map<String, AtomicLong> coOccurringTerms = new HashMap<>();
        for(String comparedTerm : comparedList) {
            coOccurringTerms.put(comparedTerm, new AtomicLong(hits));
        }
        return coOccurringTerms;
    }

    /**
     * Create a representation of term-to-term intersections (as a matrix of maps) using the arguments as test data.
     * @param selectedList a list of real or imagined GO Terms, used as the compared 'from' values for the
     * term-to-term matrix this method creates.
     * @param comparedList a list of real or imagined GO Terms used as the compared 'to' values for the matrixx
     * @param hits co-occurring count to be added to each member of the compared list.
     * @return a representation of term-to-term intersections
     */
    static Map<String, Map<String, AtomicLong>> createMatrix(List<String> selectedList, List<String> comparedList, int
            hits){
        Map<String, Map<String, AtomicLong>> matrix = new HashMap<>();

        for(String selectedTerm : selectedList) {
            matrix.put(selectedTerm, createCoOccurringTermValues(comparedList, hits));
        }
        return  matrix;
    }

    /**
     * Generate a list of pseudo GO Term ids.
     * @param numberRequired Quantity of ids to generate.
     * @param df The format to use when building each id.
     * @return a list of pseudo GO Term ids.
     */
    static List<String> makeTermList(int numberRequired, DecimalFormat df ){
        List<String> termList = new ArrayList<>();
        for(;numberRequired>0; numberRequired--) {
            String id = "GO:" + df.format(numberRequired);
            termList.add(id);
        }
        return termList;
    }

}
