package uk.ac.ebi.quickgo.index.annotation;

/**
 * Enum defining the columns of annotation data files.
 *
 * Created 19/04/16
 * @author Edd
 */
public enum Columns {
    /*
     * ==================================================
     * Column specification (as given in goa_uniprot.gpa)
     * ==================================================
     *  !gpa-version: 1.1
     *  !
     *  !Columns:
     *  !
     *  !   name                  required? cardinality   GAF column #
     *  !   DB                    required  1             1
     *  !   DB_Object_ID          required  1             2 / 17
     *  !   Qualifier             required  1 or greater  4
     *  !   GO ID                 required  1             5
     *  !   DB:Reference(s)       required  1 or greater  6
     *  !   ECO evidence code     required  1             7 + 6 (GO evidence code + reference)
     *  !   With                  optional  0 or greater  8
     *  !   Interacting taxon ID  optional  0 or 1        13
     *  !   Date                  required  1             14
     *  !   Assigned_by           required  1             15
     *  !   Annotation Extension  optional  0 or greater  16
     *  !   Annotation Properties optional  0 or 1        n/a
     *
     */

    COLUMN_DB(0, "Database"),
    COLUMN_DB_OBJECT_ID(1, "DB Object ID"),
    COLUMN_QUALIFIER(2, "Qualifier"),
    COLUMN_GO_ID(3, "GO ID"),
    COLUMN_DB_REFERENCES(4, "DB References"),
    COLUMN_EVIDENCE_CODE(5, "Evidence Code"),
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
