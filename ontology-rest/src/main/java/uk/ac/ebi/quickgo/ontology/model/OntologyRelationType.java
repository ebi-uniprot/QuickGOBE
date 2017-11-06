package uk.ac.ebi.quickgo.ontology.model;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

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

    private static final Map<String, OntologyRelationType> shortNameToValueMap = new HashMap<>();
    private static final Map<String, OntologyRelationType> longNameToValueMap = new HashMap<>();
    static {
        for (OntologyRelationType value : OntologyRelationType.values()) {
            shortNameToValueMap.put(value.getShortName(), value);
            longNameToValueMap.put(value.getLongName(), value);
        }
    }

    // keep these variables below the above 'static' block
    public static final String DEFAULT_TRAVERSAL_TYPES_CSV = "is_a,part_of,occurs_in,regulates";
    public static final List<OntologyRelationType> DEFAULT_TRAVERSAL_TYPES =
            stream(DEFAULT_TRAVERSAL_TYPES_CSV.split(","))
                    .map(OntologyRelationType::getByLongName)
                    .collect(Collectors.toList());

    public static final String DEFAULT_SLIM_TRAVERSAL_TYPES_CSV = "is_a,part_of,occurs_in";
    public static final List<OntologyRelationType> DEFAULT_SLIM_TRAVERSAL_TYPES =
            stream(DEFAULT_SLIM_TRAVERSAL_TYPES_CSV.split(","))
                    .map(OntologyRelationType::getByLongName)
                    .collect(Collectors.toList());

    OntologyRelationType(String shortName, String longName) {
        this.shortName = shortName;
        this.longName = longName;
    }

    public String getShortName() {
        return shortName;
    }

    @JsonValue
    public String getLongName() {
        return longName;
    }

    public static OntologyRelationType getByShortName(String name) {
        if (shortNameToValueMap.containsKey(name)) {
            return shortNameToValueMap.get(name);
        } else {
            throw new IllegalArgumentException("Unknown OntologyRelationType: " + name);
        }
    }

    public static OntologyRelationType getByLongName(String name) {
        if (longNameToValueMap.containsKey(name)) {
            return longNameToValueMap.get(name);
        } else {
            throw new IllegalArgumentException("Unknown OntologyRelationType: " + name);
        }
    }

    /**
     * This checks whether an {@link OntologyRelationType} has
     * any of the types specified in the vargargs parameter. This
     * is used when computing transitive relationships to ancestor
     * ontology terms.
     *
     * @param types a vararg of types to check against
     * @return whether or not this type matches one of those specified as parameter
     */
    public boolean hasTransitiveType(OntologyRelationType... types) {
        for (OntologyRelationType type : types) {
            if (this.hasTransitiveType(type)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether this type <i>is</i> that of a specified type. This method is used
     * when computing the transitive ancestors of this ontology term. We say that
     * a provided relationship type matches if:
     *
     * <ul>
     *     <li>{@code UNDEFINED} => we do not know enough about it and so target vertices of the relationship
     *         should not be ignored</li>
     *     <li>{@code IDENTITY} => it is the special identity relationship, and so is itself</li>
     *     <li>{@code REGULATES} (or its children relationships, {@code POSITIVE_REGULATES} / {@code
     *         NEGATIVE_REGULATES}) => we state that all ancestors are reachable over the regulation relationships</li>
     * </ul>
     *
     * @param type the type to test against
     * @return true if {@code type} matches this type, or if certain properties are matched. Return false otherwise.
     */
    private boolean hasTransitiveType(OntologyRelationType type) {
        return (type == UNDEFINED)
                || (this == IDENTITY)
                || (type == this)
                || (type == REGULATES && (this == POSITIVE_REGULATES || this == NEGATIVE_REGULATES));
    }
}
