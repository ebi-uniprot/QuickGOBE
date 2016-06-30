package uk.ac.ebi.quickgo.rest;

import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Is used when a client request fails validation in any of its parameters.
 *
 * @author Ricardo Antunes
 */
public class ParameterException extends RuntimeException {
    private static final String JOINING_CHAR = ";";

    protected List<String> errorMessages;

    protected ParameterException(){}

    public ParameterException(String errorMessage) {
        errorMessages = Collections.singletonList(errorMessage);
    }

    public ParameterException(List<String> errorMessages) {
        Preconditions.checkArgument(errorMessages != null && !errorMessages.isEmpty(), "Error messages cannot be null" +
                " or empty");
        this.errorMessages = errorMessages;
    }

    @Override public String getMessage() {
        return errorMessages.stream()
                .collect(Collectors.joining(JOINING_CHAR));
    }

    public Stream<String> getMessages() {
        return errorMessages.stream();
    }

    @Override public String toString() {
        return "ParameterException{" +
                "errorMessages=" + errorMessages +
                "} " + super.toString();
    }
}