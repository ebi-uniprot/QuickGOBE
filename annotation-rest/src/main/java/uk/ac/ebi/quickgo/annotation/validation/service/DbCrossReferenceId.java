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
public class DbCrossReferenceId {

    private static final String DELIMITER = ":";

    public static String db(final String idWithDb) {
        Preconditions.checkArgument(Objects.nonNull(idWithDb), "The id should not be null");
        Preconditions.checkArgument(idWithDb.contains(DELIMITER), "The id should contain the delimiter %s", DELIMITER);
        return idWithDb.substring(0, idWithDb.indexOf(":")).trim();
    }

    public static String id(final String idWithDb){
        Preconditions.checkArgument(Objects.nonNull(idWithDb), "The id should not be null");
        Preconditions.checkArgument(idWithDb.contains(DELIMITER), "The id should contain the delimiter %s", DELIMITER);
        return idWithDb.substring(idWithDb.indexOf(":") + 1).trim();
    }
}
