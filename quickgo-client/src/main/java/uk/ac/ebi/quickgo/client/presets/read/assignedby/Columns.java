package uk.ac.ebi.quickgo.client.presets.read.assignedby;

/**
 * Created 30/08/16
 * @author Edd
 */
public enum Columns {
    COLUMN_DATABASE(0, "database"),
    COLUMN_NAME(1, "name"),
    COLUMN_URL(2, "url");

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
