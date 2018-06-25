package uk.ac.ebi.quickgo.geneproduct.common;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import java.util.Optional;

public enum Proteome {

    REFERENCE("gcrpCan"),
    COMPLETE("Complete"),
    NONE("None"),
    NOT_APPLICABLE(null),
    IS_ISOFORM ("gcrpIso");

    private String value;

    Proteome(String value) {
        this.value = value;
    }

    @JsonValue
    @Override
    public String toString() {
        return value;
    }

    /**
     * Provide a Proteome derived from a string representation.
     * @param value string tested for Proteome equivalence.
     * @return Proteome
     */
    public static Proteome fromString(String value) {
        return Arrays.stream(values())
                .filter(v -> v.toString() != null)
                .filter(v -> v.toString().equalsIgnoreCase(value))
                .findFirst()
                .orElse(NOT_APPLICABLE);
    }

}
