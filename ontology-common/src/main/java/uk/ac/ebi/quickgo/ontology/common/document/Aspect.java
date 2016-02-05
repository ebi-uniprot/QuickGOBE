package uk.ac.ebi.quickgo.ontology.common.document;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumeration representing the various Aspects that a GO term can posses.
 */
public enum Aspect {
    BIOLOGICAL_PROCESS("Biological Process", "Process", "P"),
    MOLECULAR_FUNCTION("Molecular Function", "Function", "F"),
    CELLULAR_COMPONENT("Cellular Component", "Component", "C");

    private final String shortName;
    private final String fullName;
    private final String abbreviation;

    Aspect(String fullName, String shortName, String abbreviation) {
        this.fullName = fullName;
        this.shortName = shortName;
        this.abbreviation = abbreviation;
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

    public String getAbbreviation() {
        return abbreviation;
    }

    @JsonValue
    public String getName() {
        return getFullName();
    }

    public String getFullName() {
        return fullName;
    }
}
