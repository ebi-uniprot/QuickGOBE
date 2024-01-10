package uk.ac.ebi.quickgo.common.validator;

import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.regex.Pattern.CASE_INSENSITIVE;

/**
 * The information for a single Database cross reference.
 * This information is used, for example, to validate gene product ids based on source database and the id type.
 *
 * @author Tony Wardell
 *         Date: 18/04/2016
 *         Time: 13:52
 *         Created with IntelliJ IDEA.
 */
public class DbXRefEntity {

    private static final String COLON = ":";
    private final String database;
    private final String entityType;
    private final String entityTypeName;
    private final Pattern idValidationPattern;
    private final String databaseURL;
    private final boolean validationCaseSensitive;

    public DbXRefEntity(
            String database,
            String entityType,
            String entityTypeName,
            String idValidationPattern,
            String databaseURL,
            boolean validationCaseSensitive) {

        checkArgument(database != null, "The database ID should not be null");
        checkArgument(entityType != null, "The entity type should not be null");
        checkArgument(idValidationPattern != null,
                      "The regex for the validation of ids from " + database + " is null and therefore invalid");
        this.database = database;
        this.entityType = entityType;
        this.validationCaseSensitive = validationCaseSensitive;
        this.idValidationPattern =
                Pattern.compile(idValidationPattern, this.validationCaseSensitive ? 0 : CASE_INSENSITIVE);
        this.entityTypeName = entityTypeName;
        this.databaseURL = databaseURL;
    }

    /**
     * This method checks whether the supplied {@code id} matches the regular expression
     * that determines a valid entity.
     * <p>
     * The id will be structured in one of the following ways.
     * <ul>
     * <li> id e.g. P19712
     * <li> db:id e.g. UniProtKB:P19712
     * <li> id:feature or variation e.g. 'P19712-PRO_0000038050'
     * <li> db:id:feature or variation e.g. 'UniProtKB:P19712-PRO_0000038050'
     * </ul>
     * @param id the gene product id to be checked. This can be either the qualified (e.g., UniProtKB:Q12345),
     *           or unqualified value (e.g., Q12345)
     * @return true if the gene product id is a valid identifier according to this {@link DbXRefEntity}.
     */
    public boolean matches(String id) {
        String[] idComponents = id.split(COLON, 2);

        switch (idComponents.length) {
            case 1:
                //id has the format just id e.g. P19712
                return idValidationPattern.matcher(idComponents[0]).matches();
            case 2:
                if (idIncludesDatabase(idComponents)) {
                    //id has the format db:id e.g. UniProtKB:P19712
                    return idValidationPattern.matcher(idComponents[1]).matches();
                } else {
                    // id has the format id:feature or variation e.g. 'P19712-PRO_0000038050'
                    return idValidationPattern.matcher(id).matches();
                }
            default:
                return false;
        }
    }

    /**
     * @return the name of the database from which gene product ids can be validated, and which provided the
     * validation regular expression. E.g. UniProtKB
     */
    public String getDatabase() {
        return database;
    }

    /**
     * @return A code that uniquely identifies the type of entity that this class instance applies to e.g.
     * PR:000000001 for protein;  SO:0000704 for gene.
     */
    public String getEntityType() {
        return entityType;
    }

    /**
     * @return A string that identifies the type of entity that this class instance applies to e.g. gene or protein.
     */
    public String getEntityTypeName() {
        return entityTypeName;
    }

    /**
     * @return The regular expression used to verify an entity.
     */
    public Pattern getIdValidationPattern() {
        return idValidationPattern;
    }

    /**
     * @return The URL of the source database of the entity. e.g. http://www.uniprot.org/uniprot/[example_id]/
     */
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

    private boolean idIncludesDatabase(String[] idComponents) {
        return validationCaseSensitive ?
                idComponents[0].equals(database) : idComponents[0].equalsIgnoreCase(database);
    }
}
