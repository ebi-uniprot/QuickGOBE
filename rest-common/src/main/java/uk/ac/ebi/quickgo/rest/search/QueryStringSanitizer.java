package uk.ac.ebi.quickgo.rest.search;

/**
 * Class responsible for processing a query string
 * before it is interpreted by the underlying data-store.
 *
 * Created 29/02/16
 * @author Edd
 */
public interface QueryStringSanitizer {

    /**
     * Sanitizes a supplied query by escaping any
     * necessary characters.
     *
     * @param query the query to be sanitized
     * @return the sanitized query
     */
    String sanitize(String query);
}
