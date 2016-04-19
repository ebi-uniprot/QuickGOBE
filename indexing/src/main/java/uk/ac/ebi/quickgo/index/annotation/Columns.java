package uk.ac.ebi.quickgo.index.annotation;

/**
 * Enum defining the columns of annotation data files.
 *
 * Created 19/04/16
 * @author Edd
 */
public enum Columns {
    COLUMN_DB(0, "Database"),
    COLUMN_DB_OBJECT_ID(1, "DB Object ID"),
    COLUMN_QUALIFIER(2, "Qualifier"),
    COLUMN_GO_ID(3, "GO ID"),
    COLUMN_DB_REFERENCES(4, "DB References"),
    COLUMN_ECO(5, "Evidence Code"),
    COLUMN_WITH(6, "With"),
    COLUMN_INTERACTING_TAXON_ID(7, "Interacting Taxonomy ID"),
    COLUMN_DATE(8, "Date"),
    COLUMN_ASSIGNED_BY(9, "Assigned By"),
    COLUMN_ANNOTATION_EXTENSION(10, "Annotation Extension"),
    COLUMN_ANNOTATION_PROPERTIES(11, "Annotation Properties");

    private int position;
    private String columnName;

    Columns(int position, String columnName) {
        this.position = position;
        this.columnName = columnName;
    }

    public int getPosition() {
        return position;
    }

    public String getName() {
        return columnName;
    }

    public static int numColumns() {
        return Columns.values().length;
    }

}
