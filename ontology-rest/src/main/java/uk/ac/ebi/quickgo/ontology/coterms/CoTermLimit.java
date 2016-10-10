package uk.ac.ebi.quickgo.ontology.coterms;

/**
 * @author Tony Wardell
 * Date: 29/09/2016
 * Time: 16:08
 * Created with IntelliJ IDEA.
 */
public class CoTermLimit {

    int defaultLimit;

    public CoTermLimit(int defaultLimit) {
        this.defaultLimit = defaultLimit;
    }

    public int workoutLimit(String limit) {
        int limitNumeric = 0;
        if (limit == null) {
            limitNumeric = defaultLimit;
        } else {
            if (LimitValue.ALL.toString().equalsIgnoreCase(limit)) {
                limitNumeric = Integer.MAX_VALUE;
            } else {
                if (limit.trim().length() == 0) {
                    limitNumeric = defaultLimit;
                } else {
                    try {
                        limitNumeric = Integer.parseInt(limit);

                        if (limitNumeric == 0) {
                            limitNumeric = defaultLimit;
                        }
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("The value for co-occurring terms limit is not ALL, or a " +
                                "number");
                    }
                }
            }
        }
        return limitNumeric;
    }

    enum LimitValue {
        ALL
    }
}
