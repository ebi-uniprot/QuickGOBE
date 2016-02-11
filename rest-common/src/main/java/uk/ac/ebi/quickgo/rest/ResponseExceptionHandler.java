package uk.ac.ebi.quickgo.rest;

import uk.ac.ebi.quickgo.rest.search.RetrievalException;
import uk.ac.ebi.quickgo.rest.service.ServiceConfigException;

import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Handler responsible for returning error responses with a meaningful message to the REST client.
 *
 * @author Ricardo Antunes
 */
@ControllerAdvice
public class ResponseExceptionHandler {
    @ExceptionHandler({IllegalStateException.class, RetrievalException.class, ServiceConfigException.class})
    protected ResponseEntity<ErrorInfo> handleInternalServer(RuntimeException ex, HttpServletRequest request) {
        ErrorInfo error = new ErrorInfo(request.getRequestURL().toString(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ErrorInfo> handleBadRequest(RuntimeException ex, HttpServletRequest request) {
        ErrorInfo error = new ErrorInfo(request.getRequestURL().toString(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    private static class ErrorInfo {
        private final String url;
        private final String message;

        public ErrorInfo(String url, String message) {
            assert url != null : "Error URL can't be null";
            assert message != null : "Error message can't be null";

            this.url = url;
            this.message = message;
        }

        public String getUrl() {
            return url;
        }

        public String getMessage() {
            return message;
        }
    }
}