package uk.ac.ebi.quickgo.client.presets.read.assignedby;

/**
 * Created 30/08/16
 * @author Edd
 */
public enum Columns {
    COLUMN_DB(0, "database");

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
