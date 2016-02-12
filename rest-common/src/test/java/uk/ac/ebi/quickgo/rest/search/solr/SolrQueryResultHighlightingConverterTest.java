package uk.ac.ebi.quickgo.rest.search.solr;

import uk.ac.ebi.quickgo.rest.search.results.DocHighlight;
import uk.ac.ebi.quickgo.rest.search.results.FieldHighlight;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.when;

/**
 * Validate expected creation of {@link DocHighlight} instances.
 *
 * Created 10/02/16
 * @author Edd
 */
@RunWith(MockitoJUnitRunner.class)
public class SolrQueryResultHighlightingConverterTest {

    private Map<String, String> highlightedFieldsNameMap;

    private Map<String, Map<String, List<String>>> resultHighlights;

    @Mock
    private SolrDocumentList results;

    @Before
    public void setUp() {
        highlightedFieldsNameMap = new HashMap<>();

        createResultHighlights();
    }

    private void createResultHighlights() {
        resultHighlights = new HashMap<>();
        Map<String, List<String>> id1Mappings = new HashMap<>();
        id1Mappings.put("a", Collections.singletonList("highlighted_valueA"));
        resultHighlights.put("id1", id1Mappings);

        Map<String, List<String>> id2Mappings = new HashMap<>();
        id2Mappings.put("a", Collections.singletonList("highlighted_valueA"));
        id2Mappings.put("b", Collections.singletonList("highlighted_valueB"));
        resultHighlights.put("id2", id2Mappings);
    }

    @Test
    public void highlightsOneFieldInOneDocumentContainingOneField() {
        highlightedFieldsNameMap.put("a", "newA");
        highlightedFieldsNameMap.put("b", "newB");

        SolrQueryResultHighlightingConverter converter = createConverter();

        SolrDocument doc1 = new SolrDocument();
        doc1.put("id", "id1");
        doc1.put("a", "valueA");

        when(results.stream()).thenReturn(Stream.of(doc1));

        List<DocHighlight> docHighlights = converter.convertResultHighlighting(results, resultHighlights);

        checkValidFieldHighlights(docHighlights, "id1", "newA");
    }

    @Test
    public void highlightsOneFieldInOneDocumentContainingTwoFields() {
        highlightedFieldsNameMap.put("a", "newA");
        highlightedFieldsNameMap.put("b", "newB");

        SolrQueryResultHighlightingConverter converter = createConverter();

        SolrDocument doc1 = new SolrDocument();
        doc1.put("id", "id1");
        doc1.put("a", "valueA");
        doc1.put("x", "valueX");

        when(results.stream()).thenReturn(Stream.of(doc1));

        List<DocHighlight> docHighlights = converter.convertResultHighlighting(results, resultHighlights);

        checkValidFieldHighlights(docHighlights, "id1", "newA");
    }

    @Test
    public void highlightsTwoFieldsInOneDocumentContainingTwoFields() {
        highlightedFieldsNameMap.put("a", "newA");
        highlightedFieldsNameMap.put("b", "newB");

        SolrQueryResultHighlightingConverter converter = createConverter();

        SolrDocument doc2 = new SolrDocument();
        doc2.put("id", "id2");
        doc2.put("a", "valueA");
        doc2.put("b", "valueB");

        when(results.stream()).thenReturn(Stream.of(doc2));

        List<DocHighlight> docHighlights = converter.convertResultHighlighting(results, resultHighlights);

        checkValidFieldHighlights(docHighlights, "id2", "newA", "newB");
    }

    @Test
    public void highlightsOneFieldInOneDocAndTwoInAnother() {
        highlightedFieldsNameMap.put("a", "newA");
        highlightedFieldsNameMap.put("b", "newB");
        highlightedFieldsNameMap.put("c", "newC");

        SolrQueryResultHighlightingConverter converter = createConverter();

        SolrDocument doc1 = new SolrDocument();
        doc1.put("id", "id1");
        doc1.put("a", "valueA");

        SolrDocument doc2 = new SolrDocument();
        doc2.put("id", "id2");
        doc2.put("a", "valueA");
        doc2.put("b", "valueB");

        when(results.stream()).thenReturn(Stream.of(doc1, doc2));

        List<DocHighlight> docHighlights = converter.convertResultHighlighting(results, resultHighlights);

        checkValidFieldHighlights(docHighlights, "id1", "newA");
        checkValidFieldHighlights(docHighlights, "id2", "newA", "newB");
    }

    @Test
    public void highlightsNothing() {
        SolrQueryResultHighlightingConverter converter = createConverter();

        SolrDocument doc1 = new SolrDocument();
        doc1.put("id", "id1");

        when(results.stream()).thenReturn(Stream.of(doc1));

        List<DocHighlight> docHighlights = converter.convertResultHighlighting(results, new HashMap<>());

        checkValidFieldHighlights(docHighlights, "id1");
    }

    private static void checkValidFieldHighlights(List<DocHighlight> docHighlights, String docId, String... fields) {
        List<String> highlightedFields = docHighlights.stream()
                .filter(doc ->
                        doc.getId().equals(docId))
                .map(hl -> hl.getMatches().stream()
                        .map(FieldHighlight::getField)
                        .collect(Collectors.toList()))
                        .flatMap(Collection::stream)
                .collect(Collectors.toList());
        assertThat(highlightedFields, containsInAnyOrder(fields));
    }

    private SolrQueryResultHighlightingConverter createConverter() {
        return new SolrQueryResultHighlightingConverter(highlightedFieldsNameMap);
    }

}