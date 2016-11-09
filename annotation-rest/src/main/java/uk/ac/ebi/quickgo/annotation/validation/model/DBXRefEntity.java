package uk.ac.ebi.quickgo.annotation.validation.model;

import java.util.regex.Pattern;

/**
 * @author Tony Wardell
 * Date: 07/11/2016
 * Time: 18:07
 * Created with IntelliJ IDEA.
 */
public class DBXRefEntity {

    public String database;
    public String entityType;
    public String entityTypeName;
    public Pattern idValidationPattern;
    public String databaseURL;

    public boolean test(String value) {
        return idValidationPattern.matcher(value).matches();
    }
}
