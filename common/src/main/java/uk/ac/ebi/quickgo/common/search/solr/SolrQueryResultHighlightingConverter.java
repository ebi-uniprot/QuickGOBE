package uk.ac.ebi.quickgo.common.search.solr;

import uk.ac.ebi.quickgo.common.search.results.DocHighlight;
import uk.ac.ebi.quickgo.common.search.results.FieldHighlight;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.solr.common.SolrDocumentList;

/**
 * A Solr implementation for highlighting results from a search query.
 * <p>
 * Note that it is necessary that each document has a document field,
 * "id", which is used to relate information in the {@link SolrDocumentList}
 * and the map of highlighting information.
 *
 * Created 10/02/16
 * @author Edd
 */
public class SolrQueryResultHighlightingConverter implements QueryResultHighlightingConverter<SolrDocumentList, Map<String, Map<String, List<String>>>> {

    private static final String DOC_ID = "id";

    private final Map<String, String> highlightedFieldsNameMap;

    public SolrQueryResultHighlightingConverter(Map<String, String> highlightedFieldsNameMap) {
        Preconditions.checkArgument(highlightedFieldsNameMap != null, "Map of highlighted fields cannot be null");

        this.highlightedFieldsNameMap = highlightedFieldsNameMap;
    }

    /**
     * Converts a {@link SolrDocumentList} of results, together with an associated list
     * of highlighted fields into a list of domain {@link DocHighlight} instances.
     *
     * @param solrResults the Solr results
     * @param resultHighlights the Solr highlighting information
     * @return the domain highlighting information
     */
    public List<DocHighlight> convertResultHighlighting(
            SolrDocumentList solrResults,
            Map<String, Map<String, List<String>>> resultHighlights) {

        if (resultHighlights != null && !resultHighlights.isEmpty()) {
            return solrResults.stream()
                    .filter(doc -> doc.containsKey(DOC_ID))
                    .map(doc -> convertToDocHighlight(
                            doc.getFieldValue(DOC_ID).toString(),
                            resultHighlights))
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Creates a {@link DocHighlight} instance for a particular document id. This requires use of
     * the original map of highlighting information returned from Solr.
     *
     * @param id the document identifier
     * @param resultHighlights the original map of highlighting information returned from Solr
     * @return domain level highlighting information for the document corresponding to {@code id}
     */
    private DocHighlight convertToDocHighlight(String id, Map<String, Map<String, List<String>>>
            resultHighlights) {
        List<FieldHighlight> fieldHighlights = resultHighlights
                .get(id)
                .entrySet().stream()
                .map(entry -> {
                    // by default, field name is that given by Solr
                    String fieldName = entry.getKey();

                    // if user specified mapping exists, use this as the field name
                    if (highlightedFieldsNameMap.containsKey(fieldName)) {
                        fieldName = highlightedFieldsNameMap.get(fieldName);
                    }
                    return new FieldHighlight(fieldName, entry.getValue());
                })
                .collect(Collectors.toList());
        return new DocHighlight(id, fieldHighlights);
    }
}
