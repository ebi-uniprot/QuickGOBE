package uk.ac.ebi.quickgo.common.search.solr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper functions associated with {@link SolrRetrievalConfig}.
 *
 * Created 08/02/16
 * @author Edd
 */
public class SolrRetrievalConfigHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(SolrRetrievalConfigHelper.class);

    private static final String COMMA = ",";
    public static final String DEFAULT_HIGHLIGHT_DELIMS = "<em>,</em>";
    public static final int HIGHLIGHT_START_DELIM_INDEX = 0;
    public static final int HIGHLIGHT_END_DELIM_INDEX = 1;

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
