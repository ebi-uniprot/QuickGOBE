package uk.ac.ebi.quickgo.rest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Tests the behaviour of the {@link RangeOutOfBoundsException} class.
 */
public class RangeOutOfBoundsExceptionTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void exceptionReturnsCustomMessage() throws Exception {
        String errorMessage = "This is an error message";

        RangeOutOfBoundsException exception = new RangeOutOfBoundsException(errorMessage);

        assertThat(exception.getMessage(), is(errorMessage));
    }

    @Test
    public void exceptionReturnsPredefinedMessageUsingRangeAndValue() throws Exception {
        int lowerLimit = 0;
        int upperLimit = 3;

        int value = upperLimit + 1;

        RangeOutOfBoundsException exception = new RangeOutOfBoundsException(lowerLimit, upperLimit, value);

        assertThat(exception.getMessage(), is(createErrorMessage(value, lowerLimit, upperLimit)));
    }

    private String createErrorMessage(int value, int lowerLimit, int upperLimit) {
        return String.format(RangeOutOfBoundsException.RANGE_ERROR_MSG, value, lowerLimit, upperLimit);
    }
}