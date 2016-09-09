package uk.ac.ebi.quickgo.index.annotation.costats;

import uk.ac.ebi.quickgo.common.costats.HitCount;

import java.text.DecimalFormat;
import java.util.*;

/**
 * @author Tony Wardell
 * Date: 21/07/2016
 * Time: 10:08
 * Created with IntelliJ IDEA.
 */
public class CoStatsMocker {

    public static DecimalFormat df1 = new DecimalFormat("0000000");
    public static DecimalFormat df2 = new DecimalFormat("9000000");

    static Map<String, Map<String, HitCount>> singleEntry(){
        Map<String, Map<String, HitCount>> matrix = new HashMap<>();
        List<String> comparedList = Arrays.asList("GO:0003824");
        matrix.put("GO:0003824", makeSingleCostat(comparedList, 2));
        return  matrix;
    }

    /**
     * Suggests 2 annotations for GO:0003824, with different gene products
     * @return
     */
     static Map<String, HitCount> makeSingleCostat(List<String> comparedList, int hits) {
        Map<String, HitCount> coStat = new HashMap<>();
        for(String comparedTerm : comparedList) {
            coStat.put(comparedTerm, new HitCount(hits));
        }
        return coStat;
    }


    static Map<String, Map<String, HitCount>> createMatrix(List<String> selectedList, List<String> comparedList, int
            hits){
        Map<String, Map<String, HitCount>> matrix = new HashMap<>();

        for(String selectedTerm : selectedList) {
            matrix.put(selectedTerm, makeSingleCostat(comparedList, hits));
        }
        return  matrix;
    }

    static List<String> makeTermList(int numberRequired, DecimalFormat df ){
        List<String> termList = new ArrayList<>();
        for(;numberRequired>0; numberRequired--) {
            String id = "GO:" + df.format(numberRequired);
            termList.add(id);
        }
        return termList;
    }

    static Map<String, HitCount> makeGpHitCountForTerm(int count, List<String>... termsLists ){
        Map<String, HitCount> termGpCount = new HashMap<>();

        for (int i = 0; i < termsLists.length; i++) {
            List<String> terms = termsLists[i];

            for (int j = 0; j < terms.size(); j++) {
                String s =  terms.get(j);
                termGpCount.put(s, new HitCount(count));
            }
        }

        return termGpCount;
    }

}
