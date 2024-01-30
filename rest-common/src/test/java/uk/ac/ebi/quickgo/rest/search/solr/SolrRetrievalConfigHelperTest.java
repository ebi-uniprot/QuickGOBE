package uk.ac.ebi.quickgo.rest.search.solr;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static uk.ac.ebi.quickgo.rest.search.solr.SolrRetrievalConfigHelper.HIGHLIGHT_END_DELIM_INDEX;
import static uk.ac.ebi.quickgo.rest.search.solr.SolrRetrievalConfigHelper.HIGHLIGHT_START_DELIM_INDEX;
import static uk.ac.ebi.quickgo.rest.search.solr.SolrRetrievalConfigHelper.convertHighlightDelims;

/**
 * Created 09/02/16
 * @author Edd
 */
class SolrRetrievalConfigHelperTest {
    private static final String COMMA = ",";

    @Test
    void settingZeroHighlightFieldsResultsInUsingDefaultHighlighting() {
        String highlightDelims = "";

        String[] convertedHighlightDelims = convertHighlightDelims(highlightDelims, COMMA);

        assertThat(convertedHighlightDelims[HIGHLIGHT_START_DELIM_INDEX], is(not(isEmptyOrNullString())));
        assertThat(convertedHighlightDelims[HIGHLIGHT_END_DELIM_INDEX], is(not(isEmptyOrNullString())));
    }

    @Test
    void settingExplicitValidHighlightFieldsResultsInTheseFieldsBeingUsed() {
        String highlightDelimStart = "<b>";
        String highlightDelimEnd = "</b>";
        String highlightDelims = highlightDelimStart + COMMA + highlightDelimEnd;

        String[] convertedHighlightDelims = convertHighlightDelims(highlightDelims, COMMA);

        assertThat(convertedHighlightDelims[HIGHLIGHT_START_DELIM_INDEX], is(highlightDelimStart));
        assertThat(convertedHighlightDelims[HIGHLIGHT_END_DELIM_INDEX], is(highlightDelimEnd));
    }

    @Test
    void settingExplicitInvalidHighlightFieldsResultsInUsingDefaultHighlighting() {
        String highlightDelimStart = "<b>";
        String highlightDelimEnd = "</b>";
        String highlightDelims = highlightDelimStart + "-" + highlightDelimEnd;

        String[] convertedHighlightDelims = convertHighlightDelims(highlightDelims, COMMA);

        assertThat(convertedHighlightDelims[HIGHLIGHT_START_DELIM_INDEX], is(not(isEmptyOrNullString())));
        assertThat(convertedHighlightDelims[HIGHLIGHT_END_DELIM_INDEX], is(not(isEmptyOrNullString())));
    }
}