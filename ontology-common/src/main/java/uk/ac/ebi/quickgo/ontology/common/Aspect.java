package uk.ac.ebi.quickgo.ontology.common;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumeration representing the various Aspects that a GO term can posses.
 */
public enum Aspect {
    BIOLOGICAL_PROCESS("Biological Process", "Process", "biological_process"),
    MOLECULAR_FUNCTION("Molecular Function", "Function", "molecular_function"),
    CELLULAR_COMPONENT("Cellular Component", "Component", "cellular_component");

    private final String shortName;
    private final String fullName;
    private final String scientificName;

    Aspect(String fullName, String shortName, String scientificName) {
        this.fullName = fullName;
        this.shortName = shortName;
        this.scientificName = scientificName;
    }

    public static Aspect fromShortName(String shortName) {
        for (Aspect aspect : Aspect.values()) {
            if (aspect.shortName.equals(shortName)) {
                return aspect;
            }
        }
        throw new IllegalArgumentException("Unrecognized Aspect shortName: " + shortName);
    }

    public String getShortName() {
        return shortName;
    }

    public String getScientificName() {
        return scientificName;
    }

    @JsonValue
    public String getName() {
        return getScientificName();
    }

    public String getFullName() {
        return fullName;
    }
}
