package uk.ac.ebi.quickgo.common;

import org.apache.solr.client.solrj.util.ClientUtils;

/**
 * Helper class that performs transformations on queries.
 *
 * @author Ricardo Antunes
 */
public final class QueryUtils {
    private QueryUtils(){}

    /**
     * Escapes the elements within the query so that it can be correctly interpreted by the Solr data source.
     *
     * @param query query to escape
     * @return the escape version of the query
     */
    public static String solrEscape(String query) {
        return ClientUtils.escapeQueryChars(query);
    }
}
