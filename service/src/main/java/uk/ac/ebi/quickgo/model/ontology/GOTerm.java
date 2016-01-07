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
    public String usage;

    public enum Aspect {
        BIOLOGICAL_PROCESS("Biological Process", "P"),
        MOLECULAR_FUNCTION("Molecular Function", "F"),
        CELLULAR_COMPONENT("Cellular Component", "C");

        private final String abbreviation;
        private final String fullName;

        @JsonValue
        public String getName() {
            return this.fullName;
        }

        Aspect(String fullName, String abbreviation) {
            this.fullName = fullName;
            this.abbreviation = abbreviation;
        }

        public static Aspect string2Aspect(String abbreviation) {
            for (Aspect aspect : Aspect.values()) {
                if (aspect.abbreviation.equals(abbreviation)) {
                    return aspect;
                }
            }
            throw new IllegalArgumentException("Unrecognized Aspect abbreviation: " + abbreviation);
        }
    }
}
