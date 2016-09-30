package uk.ac.ebi.quickgo.ontology.common.coterm;

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
            if (LimitValue.FULL.toString().equals(limitNumeric)) {
                limitNumeric = Integer.MAX_VALUE;
            } else {
                try {
                    limitNumeric = Integer.parseInt(limit);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("The value for limit co-occurring terms is not ALL, or 0-100");
                }
            }
        }
        return limitNumeric;
    }

    enum LimitValue{
        FULL
    }
}
