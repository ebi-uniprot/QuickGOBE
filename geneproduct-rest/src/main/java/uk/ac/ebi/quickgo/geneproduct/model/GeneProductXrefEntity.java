package uk.ac.ebi.quickgo.geneproduct.model;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 *
 * Hold the content of one line of the DB_XREF_ENTITIES.dat file
 *
 * Use this data to validate a Gene Product ID
 *
 * Every entity REQUIRES a regex for validation
 *
 * @author Tony Wardell
 *         Date: 18/04/2016
 *         Time: 13:52
 *         Created with IntelliJ IDEA.
 */
public class   GeneProductXrefEntity {

    public String database;                  //E.g. UniProtKB
    public String entityType;                //E.g. PR:000000001
    public String entityTypeName;            //E.g. protein
    public Pattern localIDSyntax;
            //E.g. regex for validation  ([OPQ][0-9][A-Z0-9]{3}[0-9]|[A-NR-Z]([0-9][A-Z][A-Z0-9]{2}){1,2}[0-9])(
			// (-[0-9]+)|:PRO_[0-9]{10}|:VAR_[0-9]{6}){0,1}
    public String URLSyntax;                //E.g. http://www.uniprot.org/uniprot/[example_id]/
    public GeneProductXrefEntity.Key dbKey;

    public GeneProductXrefEntity(String database, String entityType, String entityTypeName, String localIDSyntax,
            String URLSyntax) {
        this.database = database;
        this.entityType = entityType;
        this.entityTypeName = entityTypeName;
        Objects.requireNonNull(localIDSyntax,
                "The regex for the validation of ids from " + database + " is null and therefore invalid");
        this.localIDSyntax = Pattern.compile(localIDSyntax);
        this.URLSyntax = URLSyntax;
        this.dbKey = new Key(database, entityTypeName);

    }

    /**
     * Does the argument match the regular expression that determines validity of the entity?
     * @param id
     * @return
     */
    public boolean matches(String id) {
        return localIDSyntax.matcher(id).find();
    }

    public String getDatabase() {
        return null;
    }

    public static Key key(String db, String name) {
        return new Key(db, name);
    }

    public GeneProductXrefEntity.Key getDbKey() {
        return dbKey;
    }

    public static class Key {
        String database;
        String entityTypeName;

        public Key(String database, String entityTypeName) {
            this.database = database;
            this.entityTypeName = entityTypeName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Key key = (Key) o;

            if (database != null ? !database.equals(key.database) : key.database != null) {
                return false;
            }
            return entityTypeName != null ? entityTypeName.equals(key.entityTypeName) : key.entityTypeName == null;

        }

        @Override
        public int hashCode() {
            int result = database != null ? database.hashCode() : 0;
            result = 31 * result + (entityTypeName != null ? entityTypeName.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "Key{" +
                    "database='" + database + '\'' +
                    ", entityTypeName='" + entityTypeName + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "GeneProductXrefEntity{" +
                "database='" + database + '\'' +
                ", entityType='" + entityType + '\'' +
                ", entityTypeName='" + entityTypeName + '\'' +
                ", localIDSyntax=" + localIDSyntax +
                ", URLSyntax='" + URLSyntax + '\'' +
                ", dbKey=" + dbKey +
                '}';
    }
}
