package uk.ac.ebi.quickgo.annotation.validation.loader;

/**
 * @author Tony Wardell
 * Date: 07/11/2016
 * Time: 18:16
 * Created with IntelliJ IDEA.
 *
 * Specify the layout of the DB_XREFS_ENTITIES.dat.gz file.
 * DATABASE        ENTITY_TYPE_ID  ENTITY_TYPE_NAME        LOCAL_ID_SYNTAX URL_SYNTAX
 *
 */
public enum DBXrefEntityColumns {

    COLUMN_DB(0),
    COLUMN_ENTITY_TYPE_ID(1),
    COLUMN_ENTITY_TYPE_NAME(2),
    COLUMN_LOCAL_ID_SYNTAX(3),
    COLUMN_URL_SYNTAX(4);

    private int position;

    DBXrefEntityColumns(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public static int numColumns() {
        return DBXrefEntityColumns.values().length;
    }

}
