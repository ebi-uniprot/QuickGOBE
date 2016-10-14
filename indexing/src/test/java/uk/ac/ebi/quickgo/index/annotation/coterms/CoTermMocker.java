package uk.ac.ebi.quickgo.index.annotation.coterms;

import java.text.DecimalFormat;
import java.util.*;

/**
 * @author Tony Wardell
 * Date: 21/07/2016
 * Time: 10:08
 * Created with IntelliJ IDEA.
 */
public class CoTermMocker {

    static final DecimalFormat ID_FORMAT_1 = new DecimalFormat("0000000");
    static final DecimalFormat ID_FORMAT_2 = new DecimalFormat("9000000");


    /**
     * Generate a list of pseudo GO Term ids.
     * @param numberRequired Quantity of ids to generate.
     * @param df The format to use when building each id.
     * @return a list of pseudo GO Term ids.
     */
    static List<String> makeTermList(int numberRequired, DecimalFormat df) {
        List<String> termList = new ArrayList<>();
        for (; numberRequired > 0; numberRequired--) {
            String id = "GO:" + df.format(numberRequired);
            termList.add(id);
        }
        return termList;
    }

}
