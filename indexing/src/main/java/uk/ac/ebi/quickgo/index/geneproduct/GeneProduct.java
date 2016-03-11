package uk.ac.ebi.quickgo.index.geneproduct;

/**
 * An intermediate object used to store the data retrieved from a row in a gene product file.
 *
 * This object can be later transformed into a more fine grained domain object.
 *
 * @author Ricardo Antunes
 */
class GeneProduct {
    String database;

    String id;

    String symbol;

    String name;

    String synonym;

    String type;

    String taxonId;

    String parentId;

    String xref;

    String properties;

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GeneProduct that = (GeneProduct) o;

        if (database != null ? !database.equals(that.database) : that.database != null) {
            return false;
        }
        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (symbol != null ? !symbol.equals(that.symbol) : that.symbol != null) {
            return false;
        }
        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        if (synonym != null ? !synonym.equals(that.synonym) : that.synonym != null) {
            return false;
        }
        if (type != null ? !type.equals(that.type) : that.type != null) {
            return false;
        }
        if (taxonId != null ? !taxonId.equals(that.taxonId) : that.taxonId != null) {
            return false;
        }
        if (parentId != null ? !parentId.equals(that.parentId) : that.parentId != null) {
            return false;
        }
        if (xref != null ? !xref.equals(that.xref) : that.xref != null) {
            return false;
        }
        return properties != null ? properties.equals(that.properties) : that.properties == null;

    }

    @Override public int hashCode() {
        int result = database != null ? database.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (symbol != null ? symbol.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (synonym != null ? synonym.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (taxonId != null ? taxonId.hashCode() : 0);
        result = 31 * result + (parentId != null ? parentId.hashCode() : 0);
        result = 31 * result + (xref != null ? xref.hashCode() : 0);
        result = 31 * result + (properties != null ? properties.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return "GeneProduct{" +
                "database='" + database + '\'' +
                ", id='" + id + '\'' +
                ", symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                ", synonym='" + synonym + '\'' +
                ", type='" + type + '\'' +
                ", taxonId='" + taxonId + '\'' +
                ", parentId='" + parentId + '\'' +
                ", xref='" + xref + '\'' +
                ", properties='" + properties + '\'' +
                '}';
    }
}
