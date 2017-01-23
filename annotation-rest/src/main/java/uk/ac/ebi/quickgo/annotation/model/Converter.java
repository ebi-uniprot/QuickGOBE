package uk.ac.ebi.quickgo.annotation.model;

/**
 * Defines methods for Annotation conversion to GAF, GPAD etc.
 * @author Tony Wardell
 * Date: 20/01/2017
 * Time: 17:09
 * Created with IntelliJ IDEA.
 */
public interface Converter {

    /**
     * Convert an annotation to a String based representation. The implementing class determines the format.
     * @param annotation instance
     * @return String representation.
     */
    String convert(Annotation annotation);
}
