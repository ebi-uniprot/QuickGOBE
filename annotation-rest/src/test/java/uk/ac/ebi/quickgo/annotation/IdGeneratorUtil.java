package uk.ac.ebi.quickgo.annotation;

import java.util.function.Function;

/**
 * A helper class containing a set of utility methods to help generate ids for different fields of the annotation.
 *
 * @author Ricardo Antunes
 */
public class IdGeneratorUtil {
    /**
     * Generates a gene product identifier based on the number provided as an argument
     *
     * @param idNum a number to base the id generation on
     * @return a gene product identifier
     */
    public static String createGPId(int idNum) {
        return "A0A%03d".formatted(idNum);
    }

    /**
     * Generate an evidence code based on the number provided as an argument
     *
     * @param idNum a number to base the evidence code generation on
     * @return an evidence code identifier
     */
    public static String createEvidenceCode(int idNum) {
        return "ECO:%07d".formatted(idNum);
    }

    /**
     * Generate a GO_REF code based on the number provided as an argument
     *
     * @param idNum a number to base the GO_REF generation on
     * @return an GO_REF identifier
     */
    public static String createGoRef(int idNum) {
        return "GO_REF:%07d".formatted(idNum);
    }

    /**
     * Generate a GO term identifier based on the number provided as an argument
     *
     * @param idNum a number to base the Go term identifier generation on
     * @return a GO identifier
     */
    public static String createGoId(int idNum) {
        return "GO:%07d".formatted(idNum);
    }

    /**
     * Method that takes in the number of values to generate, and a value generator and returns an array of generated
     * values.
     *
     * @param valueGenerator contains the logic to generate a specific value
     * @param numberOfValues number of values to generate
     * @return an array of generated values
     */
    public static String[] generateValues(Function<Integer, String> valueGenerator, int numberOfValues) {
        String[] values = new String[numberOfValues];

        for (int i = 0; i < numberOfValues; i++) {
            values[i] = valueGenerator.apply(i);
        }

        return values;
    }
}