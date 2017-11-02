package uk.ac.ebi.quickgo.common.model;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Optional;
import java.util.function.Function;

/**
 * The differing representations of Aspect.
 *
 * @author Tony Wardell
 * Date: 10/08/2017
 * Time: 14:22
 * Created with IntelliJ IDEA.
 */
public enum Aspect {
    BIOLOGICAL_PROCESS("Biological Process", "Process", "biological_process", "P"),
    MOLECULAR_FUNCTION("Molecular Function", "Function", "molecular_function", "F"),
    CELLULAR_COMPONENT("Cellular Component", "Component", "cellular_component", "C");

    private final String fullName;
    private final String shortName;
    private final String scientificName;
    private final String character;

    Aspect(String fullName, String shortName, String scientificName, String character) {
        this.fullName = fullName;
        this.shortName = shortName;
        this.scientificName = scientificName;
        this.character = character;
    }

    /**
     * Return Aspect that that has the target short name.
     * @param shortName a shortened version of the Aspect name.
     @return matching Aspect as Optional, or empty Optional if no match.
     */
    public static Optional<Aspect> fromShortName(String shortName) {
        return fromSource(Aspect::getShortName, shortName);
    }

    /**
     * Return Aspect that that has the target character.
     * @param character the Aspect name expressed as one character.
     @return matching Aspect as Optional, or empty Optional if no match.
     */
    public static Optional<Aspect> fromCharacter(String character) {
        return fromSource(Aspect::getCharacter, character);
    }

    /**
     * Return Aspect that that has the target scientific name.
     * @param scientificName the fuller version of the Aspect name.
     * @return matching Aspect as Optional, or empty Optional if no match.
     */
    public static Optional<Aspect> fromScientificName(String scientificName) {
        return fromSource(Aspect::getScientificName, scientificName);
    }

    private static Optional<Aspect> fromSource(Function<Aspect,String> src, String target){
        for (Aspect aspect : Aspect.values()) {
            if (src.apply(aspect).equals(target)) {
                return Optional.of(aspect);
            }
        }
        return Optional.empty();
    }

    public String getShortName() {
        return shortName;
    }

    public String getScientificName() {
        return scientificName;
    }

    @JsonValue public String getName() {
        return getScientificName();
    }

    public String getFullName() {
        return fullName;
    }

    public String getCharacter() {
        return character;
    }
}
