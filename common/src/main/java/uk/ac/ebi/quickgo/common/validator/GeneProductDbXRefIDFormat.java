package uk.ac.ebi.quickgo.common.validator;

import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * The information for a single Database cross reference.
 * This information is used, for example, to validate gene product ids based on source database and the id type.
 *
 *
 * @author Tony Wardell
 *         Date: 18/04/2016
 *         Time: 13:52
 *         Created with IntelliJ IDEA.
 */
public class GeneProductDbXRefIDFormat {
    //E.g. UniProtKB
    private final String database;

    //E.g. PR:000000001
    private final String entityType;

    //E.g. protein
    private final String entityTypeName;

    //E.g. regex for validation  ([OPQ][0-9][A-Z0-9]{3}[0-9]|[A-NR-Z]([0-9][A-Z][A-Z0-9]{2}){1,2}[0-9])...
    private final Pattern idValidationPattern;

    //E.g. http://www.uniprot.org/uniprot/[example_id]/
    private final String databaseURL;

    public GeneProductDbXRefIDFormat(String database, String entityType, String entityTypeName,
            String idValidationPattern, String databaseURL) {

        checkArgument(database != null, "The database ID should not be null");
        checkArgument(entityType != null, "The entity type should not be null");
        checkArgument(idValidationPattern != null,
                "The regex for the validation of ids from " + database + " is null and therefore invalid");
        this.database = database;
        this.entityType = entityType;
        this.idValidationPattern = Pattern.compile(idValidationPattern);
        this.entityTypeName = entityTypeName;
        this.databaseURL = databaseURL;
    }

    /**
     * Does the argument match the regular expression that determines validity of the entity?
     * @param id
     * @return
     */
    public boolean matches(String id) {
        return idValidationPattern.matcher(id).matches();
    }

    public String getDatabase() {
        return database;
    }

    public String getEntityType() {
        return entityType;
    }

    public String getEntityTypeName() {
        return entityTypeName;
    }

    public Pattern getIdValidationPattern() {
        return idValidationPattern;
    }

    public String getDatabaseURL() {
        return databaseURL;
    }

    @Override
    public String toString() {
        return "GeneProductXrefEntity{" +
                "database='" + database + '\'' +
                ", entityType='" + entityType + '\'' +
                ", entityTypeName='" + entityTypeName + '\'' +
                ", idValidationPattern=" + idValidationPattern +
                ", databaseURL='" + databaseURL + '\'' +
                '}';
    }
}
