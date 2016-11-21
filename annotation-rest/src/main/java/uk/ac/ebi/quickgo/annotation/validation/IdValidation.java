package uk.ac.ebi.quickgo.annotation.validation;

import com.google.common.base.Preconditions;

/**
 * @author Tony Wardell
 * Date: 10/11/2016
 * Time: 13:41
 * Created with IntelliJ IDEA.
 */
class IdValidation {

    private static final String DELIMITER = ":";

    static String db(final String idWithDb) {
        Preconditions.checkArgument(idWithDb.contains(DELIMITER), "The id should contain contain the delimiter %s",
                DELIMITER);
        return idWithDb.substring(0, idWithDb.indexOf(":")).toLowerCase();
    }

    static String id(final String id){
        Preconditions.checkArgument(id.contains(DELIMITER), "The id should contain contain the delimiter %s",
                DELIMITER);
        return id.substring(id.indexOf(":") + 1);
    }
}
