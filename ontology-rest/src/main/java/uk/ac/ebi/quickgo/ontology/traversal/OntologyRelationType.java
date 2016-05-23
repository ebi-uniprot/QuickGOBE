package uk.ac.ebi.quickgo.ontology.traversal;

import java.util.HashMap;
import java.util.Map;

/**
 * This {@code enum} defines the ontology relationships that are valid for use in QuickGO's source files.
 * The relationships defined are applicable to all ontologies, including GO and ECO.
 *
 * Created 20/05/16
 * @author Edd
 */
public enum OntologyRelationType {

    UNDEFINED("?", "ancestor"),
    IDENTITY("=", "equals"),
    IS_A("I", "is_a"),
    PART_OF("P", "part_of"),
    REGULATES("R", "regulates"),
    POSITIVE_REGULATES("PR", "positively_regulates"),
    NEGATIVE_REGULATES("NR", "negatively_regulates"),
    REPLACED_BY("replaced_by", "replaced_by"),
    CONSIDER("consider", "consider"),
    HAS_PART("H", "has_part"),
    OCCURS_IN("OI", "occurs_in"),
    USED_IN("UI", "used_in"),
    CAPABLE_OF("CO", "capable_of"),
    CAPABLE_OF_PART_OF("CP", "capable_of_part_of");

    private final String shortName;
    private final String longName;

    OntologyRelationType(String shortName, String longName) {
        this.shortName = shortName;
        this.longName = longName;
    }

    public String getShortName() {
        return shortName;
    }

    public String getLongName() {
        return longName;
    }

    private static final Map<String, OntologyRelationType> nameToValueMap = new HashMap<>();

    static {
        for (OntologyRelationType value : OntologyRelationType.values()) {
            nameToValueMap.put(value.getShortName(), value);
        }
    }

    public static OntologyRelationType getByShortName(String shortName) {
        if (nameToValueMap.containsKey(shortName)) {
            return nameToValueMap.get(shortName);
        } else {
            throw new IllegalArgumentException("Unknown OntologyRelation: " + shortName);
        }
    }

    /**
     * Check whether this type is that of a specified type.
     *
     * @param type the type to test against
     * @return true if {@code type} matches this type, or if certain properties are matched. Return false otherwise.
     */
    boolean hasType(OntologyRelationType type) {
        return (type == UNDEFINED)
                || (this == IDENTITY)
                || (type == this)
                || (type == REGULATES && (this == POSITIVE_REGULATES || this == NEGATIVE_REGULATES));
    }

    /**
     * Checks whether this type has the same type as any of those specified in the vargargs parameter of types.
     *
     * @param types a vararg of types to check against
     * @return whether or not this type matches one of those specified as parameter
     */
    public boolean hasType(OntologyRelationType... types) {
        for (OntologyRelationType type : types) {
            if (this.hasType(type)) {
                return true;
            }
        }
        return false;
    }
}
