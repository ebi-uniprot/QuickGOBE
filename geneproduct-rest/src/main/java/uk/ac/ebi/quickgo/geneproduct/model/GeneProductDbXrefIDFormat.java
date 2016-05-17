package uk.ac.ebi.quickgo.geneproduct.model;

import com.google.common.base.Preconditions;
import java.util.regex.Pattern;

/**
 * The information for a single Database cross reference.
 * This information is used, for example, to validate gene product ids based on source database and the id type.
 *
 * @author Tony Wardell
 *         Date: 18/04/2016
 *         Time: 13:52
 *         Created with IntelliJ IDEA.
 */
public class GeneProductDbXRefIDFormat {

    //E.g. UniProtKB
    private String database;

    //E.g. PR:000000001
    private String entityType;

    //E.g. protein
    private String entityTypeName;

    //E.g. regex for validation  ([OPQ][0-9][A-Z0-9]{3}[0-9]|[A-NR-Z]([0-9][A-Z][A-Z0-9]{2}){1,2}[0-9])...
    private Pattern localIDSyntax;

    //E.g. http://www.uniprot.org/uniprot/[example_id]/
    private String URLSyntax;

    public GeneProductDbXRefIDFormat(String database, String entityType, String entityTypeName, String localIDSyntax,
            String URLSyntax) {
        Preconditions.checkNotNull(localIDSyntax,
                "The regex for the validation of ids from " + database + " is null and therefore invalid");
        this.database = database;
        this.entityType = entityType;
        this.entityTypeName = entityTypeName;
        this.localIDSyntax = Pattern.compile(localIDSyntax);
        this.URLSyntax = URLSyntax;
    }

    /**
     * Does the argument match the regular expression that determines validity of the entity?
     * @param id
     * @return
     */
    public boolean matches(String id) {
        return localIDSyntax.matcher(id).matches();
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

    public Pattern getLocalIDSyntax() {
        return localIDSyntax;
    }

    public String getURLSyntax() {
        return URLSyntax;
    }

    @Override
    public String toString() {
        return "GeneProductXrefEntity{" +
                "database='" + database + '\'' +
                ", entityType='" + entityType + '\'' +
                ", entityTypeName='" + entityTypeName + '\'' +
                ", localIDSyntax=" + localIDSyntax +
                ", URLSyntax='" + URLSyntax + '\'' +
                '}';
    }
}
