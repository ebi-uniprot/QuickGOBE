package uk.ac.ebi.quickgo.graphics.ontology;

/**
 * Represents a problem that has occurred during the rendering of
 * a term graph.
 *
 * Created 26/09/16
 * @author Edd
 */
public class RenderingGraphException extends RuntimeException {
    public RenderingGraphException(Throwable cause) {
        super(cause);
    }

    public RenderingGraphException(String message) {
        super(message);
    }

    public RenderingGraphException(String message, Throwable cause) {
        super(message, cause);
    }
}
