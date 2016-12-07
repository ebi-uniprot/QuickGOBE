package uk.ac.ebi.quickgo.annotation.validation.service;

import com.google.common.base.Preconditions;
import java.util.Objects;

/**
 * Utility methods related to Database Cross Reference ids.
 *
 * @author Tony Wardell
 * Date: 10/11/2016
 * Time: 13:41
 * Created with IntelliJ IDEA.
 */
class DbCrossReferenceId {

    private static final String DELIMITER = ":";

    private DbCrossReferenceId() {}

    /**
     * Parse a target id that is in the format DB:ID e.g IntAct:EBI-10043081, and return the DB portion of the id.
     * @param idWithDb
     * @return The DB portion of the id, or null if there is no DB part.
     */
    public static String db(final String idWithDb) {
        checkPreconditions(idWithDb);
        if(!idWithDb.contains(DELIMITER)){
            return null;
        }
        return idWithDb.substring(0, idWithDb.indexOf(":")).trim();
    }

    /**
     * Parse a target id that is in the format DB:ID e.g IntAct:EBI-10043081, and return the ID portion of the id.
     * @param idWithDb
     * @return The ID portion of the id, or null if there is no ID part.
     */
    public static String id(final String idWithDb){
        checkPreconditions(idWithDb);
        if(!idWithDb.contains(DELIMITER)){
            return null;
        }
        return idWithDb.substring(idWithDb.indexOf(":") + 1).trim();
    }

    /**
     * Check to see if the String is contains a delimiter, so hopefully is in the DB:ID format.
     * @param value a DB:ID candidate.
     * @return true if contains delimiter, else false.
     */
    public static boolean isFullId(String value){
        return (value.contains(DELIMITER));
    }

    private static void checkPreconditions(String idWithDb) {
        Preconditions.checkArgument(Objects.nonNull(idWithDb), "The id should not be null");
    }
}
