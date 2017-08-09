package uk.ac.ebi.quickgo.annotation.download.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tony Wardell
 * Date: 09/08/2017
 * Time: 16:45
 * Created with IntelliJ IDEA.
 */
public enum Aspect {

    BIOLOGICAL_PROCESS("biological_process", "P"),
    MOLECULAR_FUNCTION("molecular_function", "F"),
    CELLULAR_COMPONENT("cellular_component", "C");

    private static final Logger LOGGER = LoggerFactory.getLogger(Aspect.class);
    private final String scientificName;
    final String character;

    Aspect(String scientificName, String character) {
        this.scientificName = scientificName;
        this.character = character;
    }

    public static Aspect fromScientificName(String scientificName) {
        for (Aspect aspect : Aspect.values()) {
            if (aspect.scientificName.equals(scientificName)) {
                return aspect;
            }
        }
        throw new IllegalArgumentException("Unrecognized Aspect scientificName: " + scientificName);
    }
}
