package uk.ac.ebi.quickgo.geneproduct.model;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

/**
 *
 * Holds the entries from 'DB_XREFS_ENTITIES.dat.gz'
 * Ask this class if the id is a valid gene product id.
 * Can either use defaults or supply the identifying database and typeName (protein or gene).
 *
 * @author Tony Wardell
 *         Date: 18/04/2016
 *         Time: 13:36
 *         Created with IntelliJ IDEA.
 */
public class GeneProductDbXRefIDFormats {

    private final String defaultTypeName;
    private Map<GeneProductDbXRefIDFormats.Key, List<GeneProductDbXRefIDFormat>> geneProductXrefEntities;
    private final String defaultDatabase;

    private GeneProductDbXRefIDFormats(Map<GeneProductDbXRefIDFormats.Key, List<GeneProductDbXRefIDFormat>>
            geneProductXrefEntities,
            String defaultDb, String defaultTypeName) {
        this.geneProductXrefEntities = geneProductXrefEntities;
        this.defaultDatabase = defaultDb;
        this.defaultTypeName = defaultTypeName;
    }

    /**
     *  Use the default database and type name to test if a gene product id is valid.
     * @param id
     * @return
     */
    public boolean isValidId(String id) {
        return isValidId(id, defaultDatabase, defaultTypeName);
    }

    /**
     * Use the supplied database and type name to test if a gene product id is valid.
     * @param id
     * @param database
     * @param typeName
     * @return
     */
    public boolean isValidId(String id, String database, String typeName) {

        //If we haven't managed to createWithData the validation regular expression then pass everything
        if (geneProductXrefEntities == null) {
            return true;
        }

        //find the ID entity that matches
        final List<GeneProductDbXRefIDFormat> geneProductXrefEntities
                = this.geneProductXrefEntities.get(new Key(database, typeName));

        //If there is no entity for this combination then the id cannot be correct..
        if (geneProductXrefEntities == null) {
            return false;
        }

        //..otherwise look up the id
        return geneProductXrefEntities.get(0).matches(id);

    }

    /**
     * Create an instance of this class
     * @param entities
     * @param defaultDb
     * @param defaultTypeName
     * @return
     */
    public static GeneProductDbXRefIDFormats createWithData(List<GeneProductDbXRefIDFormat> entities, String defaultDb,
            String defaultTypeName) {

        Map<Key, List<GeneProductDbXRefIDFormat>> mappedEntities =
                entities.stream().collect(groupingBy(e -> new GeneProductDbXRefIDFormats.Key(e
                        .getDatabase(), e.getEntityTypeName())));

        GeneProductDbXRefIDFormats formats = new GeneProductDbXRefIDFormats(mappedEntities, defaultDb, defaultTypeName);
        return formats;
    }

    /**
     * Key class holds the variables we need to lookup up the validating regex
     */

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
}
