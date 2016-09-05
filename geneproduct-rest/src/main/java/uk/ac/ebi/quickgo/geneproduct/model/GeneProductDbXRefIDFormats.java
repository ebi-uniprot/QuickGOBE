package uk.ac.ebi.quickgo.geneproduct.model;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

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
@Deprecated
public class GeneProductDbXRefIDFormats {

    private final Map<GeneProductDbXRefIDFormats.Key, GeneProductDbXRefIDFormat> geneProductXrefEntities;
    private static final Key[] TARGET_DBS = new Key[]{new Key("UniProtKB", "PR:000000001"),
            new Key("IntAct", "GO:0043234"),
            new Key("RNAcentral", "CHEBI:33697")};

    private GeneProductDbXRefIDFormats(Map<Key, GeneProductDbXRefIDFormat> geneProductXrefEntities) {
        checkArgument(geneProductXrefEntities != null, "Gene product xref entities map cannot be null");

        this.geneProductXrefEntities = geneProductXrefEntities;
    }

    /**
     * Test if a gene product id is valid.
     * @param id The gene product ID passed in from the client
     * @return true if the id is valid, false otherwise
     */
    public boolean isValidId(String id) {

        //If we haven't managed to load the validation regular expressions then pass everything
        if (geneProductXrefEntities.size() == 0) {
            return true;
        }
        for (Key dbKey : TARGET_DBS) {
            if (this.geneProductXrefEntities.get(dbKey).matches(id)) {
                return true;
            }
        }

        //no matches
        return false;
    }

    /**
     * Create an instance of this class
     * @param entities A list of regex expressions specified by database and usage.
     * @return
     */
    public static GeneProductDbXRefIDFormats createWithData(List<GeneProductDbXRefIDFormat> entities) {
        Preconditions.checkArgument(entities != null, "The list of GeneProductDbXRefIDFormat entities is null, which " +
                "is illegal");

        Map<Key, GeneProductDbXRefIDFormat> mappedEntities = new HashMap<>();

        for (GeneProductDbXRefIDFormat entity : entities) {
            GeneProductDbXRefIDFormats.Key key = new GeneProductDbXRefIDFormats.Key(entity.getDatabase(), entity
                    .getEntityType());
            mappedEntities.put(key, entity);
        }

        return new GeneProductDbXRefIDFormats(mappedEntities);
    }

    /**
     * Key class holds the variables we need to lookup up the validating regex
     */

    public static class Key {
        String database;
        String entityType;

        public Key(String database, String entityType) {
            this.database = database;
            this.entityType = entityType;
        }

        @Override public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Key key = (Key) o;

            if (!database.equals(key.database)) {
                return false;
            }
            return entityType.equals(key.entityType);

        }

        @Override public int hashCode() {
            int result = database.hashCode();
            result = 31 * result + entityType.hashCode();
            return result;
        }

        @Override public String toString() {
            return "Key{" +
                    "database='" + database + '\'' +
                    ", entityType='" + entityType + '\'' +
                    '}';
        }
    }
}
