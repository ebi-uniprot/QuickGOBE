package uk.ac.ebi.quickgo.model.ontology;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * GO term DTO used by the service layer.
 *
 * Created 13/11/15
 * @author Edd
 */
public class GOTerm extends OBOTerm {
    // refers to the namespace/ontology to which the GO ID belongs
    public Aspect aspect;

    // describes where this term can be used
    public Usage usage;

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

    public enum Usage {
        UNRESTRICTED("Unrestricted", "U"),
        ELECTRONIC("Electronic", "E"),
        NONE("None", "N");

        private final String fullName;
        private final String abbreviation;

        Usage(String fullName, String abbreviation) {
            this.fullName = fullName;
            this.abbreviation = abbreviation;
        }

        public static Usage fromFullName(String fullName) {
            for (Usage usage : Usage.values()) {
                if (usage.fullName.equals(fullName)) {
                    return usage;
                }
            }
            throw new IllegalArgumentException("Unrecognized Usage fullName: " + fullName);
        }

        public String getFullName() {
            return fullName;
        }

        public String getAbbreviation() {
            return abbreviation;
        }

        @JsonValue
        public String getName() {
            return getFullName();
        }
    }
}
