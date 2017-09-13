package uk.ac.ebi.quickgo.rest.search.solr;

import uk.ac.ebi.quickgo.rest.search.results.DocHighlight;
import uk.ac.ebi.quickgo.rest.search.results.FieldHighlight;

import com.google.common.base.Preconditions;
import java.util.*;
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
public class SolrQueryResultHighlightingConverter implements
                                                  QueryResultHighlightingConverter<SolrDocumentList, Map<String, Map<String, List<String>>>> {

    private static final String DOC_ID = "id";

    private final Map<String, String> highlightedFieldsNameMap;
    private final Map<String, List<String>> transformationsToSameFieldMap;

    public SolrQueryResultHighlightingConverter(Map<String, String> highlightedFieldsNameMap) {
        Preconditions.checkArgument(highlightedFieldsNameMap != null, "Map of highlighted fields cannot be null");

        this.highlightedFieldsNameMap = highlightedFieldsNameMap;
        this.transformationsToSameFieldMap = extractSameFields(this.highlightedFieldsNameMap);
    }

    private Map<String, List<String>> extractSameFields(Map<String, String> highlightedFieldsNameMap) {
        HashMap<String, List<String>> sameFieldsMap = new HashMap<>();

        for (Map.Entry<String, String> fieldAndTransformation : highlightedFieldsNameMap.entrySet()) {
            String value = fieldAndTransformation.getValue();
            if (!sameFieldsMap.containsKey(value)) {
                sameFieldsMap.put(value, new ArrayList<>());
            }
            sameFieldsMap.get(value).add(fieldAndTransformation.getKey());
        }
        
        return sameFieldsMap;
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
        Map<String, List<String>> originalEntryHighlights = resultHighlights.get(id);
        Map<String, List<String>> entryHighlights = removeDuplicateMappings(originalEntryHighlights);

        List<FieldHighlight> fieldHighlights = entryHighlights
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

    private Map<String, List<String>> removeDuplicateMappings(Map<String, List<String>> entryHighlights) {
        HashMap<String, List<String>> highlights = new HashMap<>(entryHighlights);

        for (Map.Entry<String, List<String>> sameFields : transformationsToSameFieldMap.entrySet()) {
            List<String> fields = sameFields.getValue();
            if (fields.size() > 1 && entryHighlights.keySet().containsAll(fields)) {
                for (int i = 1; i < fields.size(); i++) {
                    highlights.remove(fields.get(i));
                }
            }
        }

        return highlights;
    }
}
