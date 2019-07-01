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
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.when;

/**
 * Validate expected creation of {@link DocHighlight} instances.
 *
 * Created 10/02/16
 * @author Edd
 */
@RunWith(MockitoJUnitRunner.class)
public class SolrQueryResultHighlightingConverterTest {

    private static final String ID2 = "id2";
    private static final String ID1 = "id1";
    private static final String ID3 = "id3";
    private Map<String, String> highlightedFieldsNameMap;

    private Map<String, Map<String, List<String>>> resultHighlights;

    @Mock
    private SolrDocumentList results;

    @Before
    public void setUp() {
        highlightedFieldsNameMap = new HashMap<>();

        createResultHighlights();
    }

    @Test
    public void highlightsOneFieldInOneDocumentContainingOneField() {
        highlightedFieldsNameMap.put("a", "newA");
        highlightedFieldsNameMap.put("b", "newB");

        SolrQueryResultHighlightingConverter converter = createConverter();

        SolrDocument doc1 = new SolrDocument();
        doc1.put("id", ID1);
        doc1.put("a", "valueA");

        when(results.stream()).thenReturn(Stream.of(doc1));

        List<DocHighlight> docHighlights = converter.convertResultHighlighting(results, resultHighlights);

        checkValidFieldHighlights(docHighlights, ID1, "newA");
    }

    @Test
    public void useFirstOfMultipleMappingsToSameFieldWhenResultsContainBothFields() {
        // field transformation map contains mappings to the same field
        highlightedFieldsNameMap.put("internalA1", "a");
        highlightedFieldsNameMap.put("internalA2", "a");
        highlightedFieldsNameMap.put("b", "newB");

        SolrQueryResultHighlightingConverter converter = createConverter();

        // document contains transformable values to same field
        SolrDocument doc3 = new SolrDocument();
        doc3.put("id", ID3);
        doc3.put("internalA1", "valueA1");
        doc3.put("internalA2", "valueA2");
        doc3.put("b", "valueB");

        when(results.stream()).thenReturn(Stream.of(doc3));

        List<DocHighlight> docHighlights = converter.convertResultHighlighting(results, resultHighlights);

        // use first applicable transformation when multiple are applicable
        checkValidFieldHighlights(docHighlights, ID3, "a", "newB");
        checkFieldHighlightsContain(docHighlights, ID3, "a", "highlighted_internalValueA1");
        checkFieldHighlightsContain(docHighlights, ID3, "newB", "highlighted_valueB");

    }

    @Test
    public void highlightsOneFieldInOneDocumentContainingTwoFields() {
        highlightedFieldsNameMap.put("a", "newA");
        highlightedFieldsNameMap.put("b", "newB");

        SolrQueryResultHighlightingConverter converter = createConverter();

        SolrDocument doc1 = new SolrDocument();
        doc1.put("id", ID1);
        doc1.put("a", "valueA");
        doc1.put("x", "valueX");

        when(results.stream()).thenReturn(Stream.of(doc1));

        List<DocHighlight> docHighlights = converter.convertResultHighlighting(results, resultHighlights);

        checkValidFieldHighlights(docHighlights, ID1, "newA");
    }

    @Test
    public void highlightsTwoFieldsInOneDocumentContainingTwoFields() {
        highlightedFieldsNameMap.put("a", "newA");
        highlightedFieldsNameMap.put("b", "newB");

        SolrQueryResultHighlightingConverter converter = createConverter();

        SolrDocument doc2 = new SolrDocument();
        doc2.put("id", ID2);
        doc2.put("a", "valueA");
        doc2.put("b", "valueB");

        when(results.stream()).thenReturn(Stream.of(doc2));

        List<DocHighlight> docHighlights = converter.convertResultHighlighting(results, resultHighlights);

        checkValidFieldHighlights(docHighlights, ID2, "newA", "newB");
    }

    @Test
    public void highlightsOneFieldInOneDocAndTwoInAnother() {
        highlightedFieldsNameMap.put("a", "newA");
        highlightedFieldsNameMap.put("b", "newB");
        highlightedFieldsNameMap.put("c", "newC");

        SolrQueryResultHighlightingConverter converter = createConverter();

        SolrDocument doc1 = new SolrDocument();
        doc1.put("id", ID1);
        doc1.put("a", "valueA");

        SolrDocument doc2 = new SolrDocument();
        doc2.put("id", ID2);
        doc2.put("a", "valueA");
        doc2.put("b", "valueB");

        when(results.stream()).thenReturn(Stream.of(doc1, doc2));

        List<DocHighlight> docHighlights = converter.convertResultHighlighting(results, resultHighlights);

        checkValidFieldHighlights(docHighlights, ID1, "newA");
        checkValidFieldHighlights(docHighlights, ID2, "newA", "newB");
    }

    @Test
    public void highlightsNothing() {
        SolrQueryResultHighlightingConverter converter = createConverter();

        SolrDocument doc1 = new SolrDocument();
        doc1.put("id", ID1);

        List<DocHighlight> docHighlights = converter.convertResultHighlighting(results, new HashMap<>());

        checkValidFieldHighlights(docHighlights, ID1);
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

    private static void checkFieldHighlightsContain(List<DocHighlight> docHighlights, String docId, String key,
            String... values) {
        List<FieldHighlight> matchingFieldHighlights = docHighlights.stream()
                .filter(doc ->
                        doc.getId().equals(docId))
                .map(DocHighlight::getMatches)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        assertThat(matchingFieldHighlights, hasItem(new FieldHighlight(key, asList(values))));
    }

    private void createResultHighlights() {
        resultHighlights = new HashMap<>();
        Map<String, List<String>> id1Mappings = new HashMap<>();
        id1Mappings.put("a", Collections.singletonList("highlighted_valueA"));
        resultHighlights.put(ID1, id1Mappings);

        Map<String, List<String>> id2Mappings = new HashMap<>();
        id2Mappings.put("a", Collections.singletonList("highlighted_valueA"));
        id2Mappings.put("b", Collections.singletonList("highlighted_valueB"));
        resultHighlights.put(ID2, id2Mappings);

        Map<String, List<String>> id3Mappings = new HashMap<>();
        id3Mappings.put("internalA1", Collections.singletonList("highlighted_internalValueA1"));
        id3Mappings.put("internalA2", Collections.singletonList("highlighted_internalValueA2"));
        id3Mappings.put("b", Collections.singletonList("highlighted_valueB"));
        resultHighlights.put(ID3, id3Mappings);
    }

    private SolrQueryResultHighlightingConverter createConverter() {
        return new SolrQueryResultHighlightingConverter(highlightedFieldsNameMap);
    }

}