package uk.ac.ebi.quickgo.common.validator;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Validate DB Xref id. The id is validated against one or more entity definitions, each of which is defined by a
 * database - ids are valid in terms of the database they are sourced from. An id is confirmed to be valid by
 * successfully matching a regular expression defined for the combination of database and entity type.
 *
 * @author Tony Wardell
 *         Date: 18/04/2016
 *         Time: 13:36
 *         Created with IntelliJ IDEA.
 */
public class DbXRefEntityValidation implements Predicate<String> {

    // Default list of databases and entity types to validate against.
    private static final Key[] targetDBs = new Key[]{
            new Key("UniProtKB", "PR:000000001"),
            new Key("ComplexPortal", "GO:0032991"),
            new Key("RNAcentral", "CHEBI:33697"),
            new Key("IntAct", "GO:0032991")};

    // A list of entries loaded from 'DB_XREFS_ENTITIES.dat.gz' keyed by database and entity type id.
    private final Map<DbXRefEntityValidation.Key, DbXRefEntity> entityList;

    private DbXRefEntityValidation(Map<Key, DbXRefEntity> entityList) {
        checkArgument(entityList != null, "Gene product xref entities map cannot be null");

        this.entityList = entityList;
    }

    /**
     * Create an instance of this class
     * @param entities A list of regex expressions specified by database and usage.
     * @return an instance EntryValidation populated with the entities passed to the method.
     */
    public static DbXRefEntityValidation createWithData(List<DbXRefEntity> entities) {
        Preconditions.checkArgument(entities != null, "The list of GeneProductDbXRefIDFormat entities is null, which " +
                "is illegal");

        Map<Key, DbXRefEntity> mappedEntities = new HashMap<>();

        for (DbXRefEntity entity : entities) {
            DbXRefEntityValidation.Key key = new DbXRefEntityValidation.Key(entity.getDatabase(), entity
                    .getEntityType());
            mappedEntities.put(key, entity);
        }

        return new DbXRefEntityValidation(mappedEntities);
    }

    /**
     * Test if a gene product id is valid.
     * @param id The gene product ID passed in from the client
     * @return true if the id is valid, false otherwise
     */
    @Override
    public boolean test(String id) {
        // If we haven't managed to load the validation regular expressions, then pass everything
        if (entityList.size() == 0) {
            return true;
        }

        for (Key dbKey : targetDBs) {
            DbXRefEntity entity = this.entityList.get(dbKey);
            if (entity == null) {
                continue;
            }
            if (entity.matches(id)) {
                return true;
            }
        }

        // no matches
        return false;
    }

    /**
     * Key class holds the variables we need to lookup the validating regex
     */
    private static class Key {
        final String database;
        final String entityType;

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
