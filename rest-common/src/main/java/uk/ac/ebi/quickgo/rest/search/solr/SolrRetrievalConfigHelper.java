package uk.ac.ebi.quickgo.rest.search.solr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper functions associated with {@link SolrRetrievalConfig}.
 *
 * Created 08/02/16
 * @author Edd
 */
public final class SolrRetrievalConfigHelper {
    private static final String COMMA = ",";

    public static final String DEFAULT_HIGHLIGHT_DELIMS = "<em>" + COMMA + "</em>";
    public static final int HIGHLIGHT_START_DELIM_INDEX = 0;
    public static final int HIGHLIGHT_END_DELIM_INDEX = 1;

    private static final Logger LOGGER = LoggerFactory.getLogger(SolrRetrievalConfigHelper.class);

    private SolrRetrievalConfigHelper() {}

    public static String[] convertHighlightDelims(String highlightDelims, String delim) {
        String[] delims = highlightDelims.split(delim);

        if (delims.length != 2) {
            LOGGER.warn("Invalid highlighting delimiters specified: " + highlightDelims + ". Using defaults: " +
                    DEFAULT_HIGHLIGHT_DELIMS);
            delims = DEFAULT_HIGHLIGHT_DELIMS.split(COMMA);
        }

        return delims;
    }
}