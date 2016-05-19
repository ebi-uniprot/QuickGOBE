package uk.ac.ebi.quickgo.ontology.traversal;

/**
 * Created 18/05/16
 * @author Edd
 */
public enum Columns {
    COLUMN_CHILD(0, "child"),
    COLUMN_PARENT(1, "parent"),
    COLUMN_RELATIONSHIP(2,"relationship");

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
