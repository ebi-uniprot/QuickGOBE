package uk.ac.ebi.quickgo.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import uk.ac.ebi.quickgo.rest.search.RetrievalException;
import uk.ac.ebi.quickgo.rest.service.ServiceConfigException;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handler responsible for returning error responses with meaningful messages to the REST client.
 *
 * @author Ricardo Antunes
 */
@ControllerAdvice
public class ResponseExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseExceptionHandler.class);

    @ExceptionHandler({IllegalStateException.class, RetrievalException.class, ServiceConfigException.class})
    protected ResponseEntity<ErrorInfo> handleInternalServer(RuntimeException ex, HttpServletRequest request) {
        ErrorInfo error = new ErrorInfo(request.getRequestURL().toString(), ex.getMessage());
        LOGGER.error("Internal Server Error: ", ex);
        return new ResponseEntity<>(error, headers(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ErrorInfo> handleBadRequest(RuntimeException ex, HttpServletRequest request) {
        ErrorInfo error = new ErrorInfo(request.getRequestURL().toString(), ex.getMessage());
        return new ResponseEntity<>(error, headers(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ParameterException.class)
    protected ResponseEntity<ErrorInfo> handleParameterErrorRequest(ParameterException ex, HttpServletRequest request) {
        ErrorInfo error = new ErrorInfo(request.getRequestURL().toString(),
                ex.getMessages().collect(Collectors.toList()));
        return new ResponseEntity<>(error, headers(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    protected ResponseEntity<ErrorInfo> handleNotFoundRequest(RuntimeException ex, HttpServletRequest request) {
        ErrorInfo error = new ErrorInfo(request.getRequestURL().toString(), ex.getMessage());
        return new ResponseEntity<>(error, headers(), HttpStatus.NOT_FOUND);
    }

    private HttpHeaders headers(){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        return headers;
    }

    public static class ErrorInfo {
        private final String url;
        private final List<String> messages;

        public ErrorInfo(String url, String message) {
            assert url != null : "Error URL can't be null";
            assert message != null : "Error messages can't be null";

            this.url = url;
            this.messages = Collections.singletonList(message);
        }

        public ErrorInfo(String url, List<String> messages) {
            assert url != null : "Error URL can't be null";
            assert messages != null : "Error messages can't be null";

            this.url = url;
            this.messages = messages;
        }

        public String getUrl() {
            return url;
        }

        public List<String> getMessages() {
            return messages;
        }
    }
}
