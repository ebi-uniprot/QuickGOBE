package uk.ac.ebi.quickgo.common.validator;
import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * This class allows the ids of entities (genes, proteins etc) to be validated. The id is validated against one or
 * more entity definitions, each of which is defined by a database - ids are valid in terms of the database they are
 * sourced from. An id is confirmed to be valid by successfully matching a regular expression defined for the
 * combination of database and entity type.
 *
 * @author Tony Wardell
 *         Date: 18/04/2016
 *         Time: 13:36
 *         Created with IntelliJ IDEA.
 */
public class EntityValidation {

    // A list of entries loaded from 'DB_XREFS_ENTITIES.dat.gz' keyed by database and entity type id.
    private final Map<EntityValidation.Key, DbXRefEntityID> entityList;

    // Default list of databases and entity types to validate against.
    static final Key[] targetDBs = new Key[]{new Key("UniProtKB", "PR:000000001"), new Key("IntAct", "GO:0043234"),
            new Key("RNAcentral", "CHEBI:33697")};

    private EntityValidation(Map<Key, DbXRefEntityID> entityList) {
        checkArgument(entityList != null, "Gene product xref entities map cannot be null");

        this.entityList = entityList;
    }

    /**
     * Test if a gene product id is valid.
     * @param id The gene product ID passed in from the client
     * @return true if the id is valid, false otherwise
     */
    public boolean isValidId(String id) {

        //If we haven't managed to load the validation regular expressions then pass everything
        if (entityList.size() == 0) {
            return true;
        }
        for (Key dbKey : targetDBs) {
            DbXRefEntityID entity = this.entityList.get(dbKey);
            if(null==entity)continue;
            if (entity.matches(id)) {
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
    public static EntityValidation createWithData(List<DbXRefEntityID> entities) {
        Preconditions.checkArgument(entities != null, "The list of GeneProductDbXRefIDFormat entities is null, which " +
                "is illegal");

        Map<Key, DbXRefEntityID> mappedEntities = new HashMap<>();

        for (DbXRefEntityID entity : entities) {
            EntityValidation.Key key = new EntityValidation.Key(entity.getDatabase(), entity
                    .getEntityType());
            mappedEntities.put(key, entity);
        }

        return new EntityValidation(mappedEntities);
    }

    /**
     * Key class holds the variables we need to lookup up the validating regex
     */

    private static class Key {
        String database;
        String entityType;

        private Key(String database, String entityType) {
            this.database = database;
            this.entityType = entityType;
        }

        @Override public int hashCode() {
            int result = database.hashCode();
            result = 31 * result + entityType.hashCode();
            return result;
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

        @Override public String toString() {
            return "Key{" +
                    "database='" + database + '\'' +
                    ", entityType='" + entityType + '\'' +
                    '}';
        }
    }
}
