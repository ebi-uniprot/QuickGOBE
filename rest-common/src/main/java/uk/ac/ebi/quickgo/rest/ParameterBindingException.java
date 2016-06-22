package uk.ac.ebi.quickgo.rest;

import com.google.common.base.Preconditions;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

/**
 * Interprets the binding errors detected by a {@link BindingResult}, and converts these into readable error messages.
 *
 * @author Ricardo Antunes
 */
public class ParameterBindingException extends ParameterException {
    public ParameterBindingException(BindingResult bindingResult) {
        Preconditions.checkArgument(bindingResult != null, "Binding result cannot be null");

        errorMessages = getBindingResultToMessages(bindingResult);
    }

    private List<String> getBindingResultToMessages(BindingResult bindingResult) {
        return bindingResult.getAllErrors().stream()
                .map(this::convertBindingErrorError)
                .collect(Collectors.toList());
    }

    private String convertBindingErrorError(ObjectError error) {
        return error.getDefaultMessage();
    }

    @Override public String toString() {
        return "ParameterBindingException{} " + super.toString();
    }
}