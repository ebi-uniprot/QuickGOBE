package uk.ac.ebi.quickgo.rest;

/**
 * Should be used when a value falls beyond the limits of a given range.
 *
 * @author Ricardo Antunes
 */
public class RangeOutOfBoundsException extends RuntimeException {
    static final String RANGE_ERROR_MSG = "Provided value: %d, goes beyond the defined boundary: [%d, %d]";

    public RangeOutOfBoundsException(String message) {
        super(message);
    }

    public RangeOutOfBoundsException(int lowerLimit, int upperLimit, int value) {
        super(String.format(RANGE_ERROR_MSG, value, lowerLimit, upperLimit));
    }
}