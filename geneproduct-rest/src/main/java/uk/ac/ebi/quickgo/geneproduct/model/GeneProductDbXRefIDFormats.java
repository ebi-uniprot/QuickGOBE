package uk.ac.ebi.quickgo.geneproduct.model;

import com.google.common.base.Preconditions;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.groupingBy;

/**
 *
 * Holds the entries from 'DB_XREFS_ENTITIES.dat.gz'
 * Ask this class if the id is a valid gene product id.
 * Can either use defaults or supply the identifying database and typeName (protein or gene).
 *
 *
 * @author Tony Wardell
 *         Date: 18/04/2016
 *         Time: 13:36
 *         Created with IntelliJ IDEA.
 */
public class GeneProductDbXRefIDFormats {

    private final String defaultTypeName;
    private final Map<GeneProductDbXRefIDFormats.Key, GeneProductDbXRefIDFormat> geneProductXrefEntities;
    private final String[] targetDatabases;

    public GeneProductDbXRefIDFormats(Map<Key, GeneProductDbXRefIDFormat> geneProductXrefEntities,
            String[]targetDBs, String defaultTypeName) {
        this.defaultTypeName = checkNotNull(defaultTypeName);
        this.targetDatabases = checkNotNull(targetDBs);
        this.geneProductXrefEntities = checkNotNull(geneProductXrefEntities);
    }


    /**
     *  Use the default database and type name to test if a gene product id is valid.
     * @param id
     * @return
     */
    public boolean isValidId(String id) {
        return isValidId(id,  this.targetDatabases, defaultTypeName);
    }

    /**
     * Use the supplied database and type name to test if a gene product id is valid.
     * @param id
     * @param targetDBs
     * @param typeName
     * @return
     */
    public boolean isValidId(String id, String[] targetDBs, String typeName) {

        //If we haven't managed to load the validation regular expressions then pass everything
        if (geneProductXrefEntities.size()==0) {
            return true;
        }

        //for each target database, attempt to validate the id
        for(String targetDB : targetDBs){

            final GeneProductDbXRefIDFormat Xref = this.geneProductXrefEntities.get(new Key(targetDB, typeName));
            if(Xref.matches(id)) return true;

            //otherwise continue in loop to try next database if available

        }

        //no matches
        return false;

    }

    /**
     * Create an instance of this class
     * @param entities
     * @param allowedDBs
     * @param defaultTypeName
     * @return
     */
    public static GeneProductDbXRefIDFormats createWithData(List<GeneProductDbXRefIDFormat> entities, String[]
            allowedDBs, String defaultTypeName) {

        Preconditions.checkNotNull(entities, "The list of GeneProductDbXRefIDFormat entities passed to createWithData" +
                " was null, which is illegal");

        Map<Key, GeneProductDbXRefIDFormat> mappedEntities = new LinkedHashMap<>();

        for(GeneProductDbXRefIDFormat entity : entities) {
            GeneProductDbXRefIDFormats.Key key = new GeneProductDbXRefIDFormats.Key(entity.getDatabase(), entity
                    .getEntityTypeName());
            mappedEntities.put(key, entity);
        }


        GeneProductDbXRefIDFormats formats = new GeneProductDbXRefIDFormats(mappedEntities, allowedDBs, defaultTypeName);
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
