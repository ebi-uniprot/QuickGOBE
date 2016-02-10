package uk.ac.ebi.quickgo.common.search.solr;

import java.util.Map;
import org.junit.Test;
import org.mockito.Mock;

/**
 * Created 10/02/16
 * @author Edd
 */
public class SolrQueryResultHighlightingConverterTest {

    @Mock
    private Map<String, String> highlightedFieldsNameMap;

    @Test
    public void converts() {

    }

    private SolrQueryResultHighlightingConverter createConverter() {
        return new SolrQueryResultHighlightingConverter(highlightedFieldsNameMap);
    }

}