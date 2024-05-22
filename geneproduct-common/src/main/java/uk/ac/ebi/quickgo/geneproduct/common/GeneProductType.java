package uk.ac.ebi.quickgo.geneproduct.common;

/**
 * Enumeration that expresses the possible types of gene products.
 *
 * @author Ricardo Antunes
 */
public enum GeneProductType {
    COMPLEX("complex"),
    PROTEIN("protein"),
    RNA("RNA");

    private String name;

    GeneProductType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static GeneProductType typeOf(String name) {
        for (GeneProductType type : GeneProductType.values()) {
            if (type.getName().equalsIgnoreCase(name)) {
                return type;
            }
        }

        throw new IllegalArgumentException("No type maps to provided name: " + name);
    }
}
