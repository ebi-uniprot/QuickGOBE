package uk.ac.ebi.quickgo.common.search.solr;

import java.util.List;

/**
 * Configuration information relating to Solr data retrieval.
 *
 * Created 08/02/16
 * @author Edd
 */
public interface SolrRetrievalConfig {
    /**
     * Retrieves the fields to be returned from Solr.
     * @return a list of field names
     */
    List<String> getSearchReturnedFields();

    /**
     * The start delimiter used in highlighting.
     * @return the start delimiter
     */
    String getHighlightStartDelim();

    /**
     * The end delimiter used in highlighting.
     * @return the end delimiter
     */
    String getHighlightEndDelim();
}
