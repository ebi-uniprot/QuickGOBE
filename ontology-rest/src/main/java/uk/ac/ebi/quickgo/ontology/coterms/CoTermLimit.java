package uk.ac.ebi.quickgo.ontology.coterms;


/**
 * Logic for determining the limit to use for retrieving co-occurring terms
 *
 * @author Tony Wardell
 * Date: 29/09/2016
 * Time: 16:08
 * Created with IntelliJ IDEA.
 */
public class CoTermLimit {

    private final int defaultLimit;

    /**
     *
     * @param defaultLimit The limit to use under some circumstances.
     */
    CoTermLimit(int defaultLimit) {
        this.defaultLimit = defaultLimit;
    }

    /**
     * Determine the limit value to use for retrieving co-occurring terms.
     * @param limit value to be checked.
     * @return maximum number of co-occurring terms to returned to the caller.
     */
    public int workoutLimit(String limit) {
        int limitNumeric;
        if (limit == null) {
            limitNumeric = defaultLimit;
        } else {
            if (LimitValue.ALL.toString().equalsIgnoreCase(limit)) {
                limitNumeric = Integer.MAX_VALUE;
            } else {
                if (limit.trim().length() == 0) {
                    limitNumeric = defaultLimit;
                } else {
                    limitNumeric = processLimitAsANumeric(limit);
                }
            }
        }
        return limitNumeric;
    }

    /**
     * Now that other values for limit have been attempted, treat the argument as a numeric and deal with any
     * problems that occur if it is not.
     * @param limit requested limit value - could represent a numeric or constant identifier.
     * @return a limit value.
     */
    private int processLimitAsANumeric(String limit) {
        int limitNumeric;
        try {
            limitNumeric = Integer.parseInt(limit);

            if(limitNumeric < 0){
                throw new IllegalArgumentException("The value for limit cannot be negative");
            }

            if (limitNumeric == 0) {
                limitNumeric = defaultLimit;
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("The value for co-occurring terms limit is not ALL, or a " +
                    "number");
        }
        return limitNumeric;
    }

    enum LimitValue {
        ALL
    }
}
