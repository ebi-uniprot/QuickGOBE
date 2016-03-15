package uk.ac.ebi.quickgo.index.geneproduct;

/**
 * Enum indicating the columns that exist within a Gene product file.
 *
 * @author Ricardo Antunes
 */
enum Columns {
    COLUMN_DB(0, "Database"),
    COLUMN_ID(1, "Identifier"),
    COLUMN_SYMBOL(2,"Symbol"),
    COLUMN_NAME(3, "Name"),
    COLUMN_SYNONYM(4, "Synonyms"),
    COLUMN_TYPE(5, "Type"),
    COLUMN_TAXON_ID(6, "Taxonmy id"),
    COLUMN_PARENT_ID(7, "Parent id"),
    COLUMN_XREF(8, "Cross-references"),
    COLUMN_PROPERTIES(9, "Properties");

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
