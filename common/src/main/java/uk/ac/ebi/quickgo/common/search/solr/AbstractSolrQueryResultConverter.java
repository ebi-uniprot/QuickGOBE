package uk.ac.ebi.quickgo.common.search.solr;

import uk.ac.ebi.quickgo.common.search.QueryResultConverter;
import uk.ac.ebi.quickgo.common.search.query.Page;
import uk.ac.ebi.quickgo.common.search.query.QueryRequest;
import uk.ac.ebi.quickgo.common.search.results.*;

import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

/**
 * Abstract class that deals with the conversion of the non type specific aspects of a {@link QueryResult}.
 *
 * @author Ricardo Antunes
 */
public abstract class AbstractSolrQueryResultConverter<T> implements QueryResultConverter<T, QueryResponse> {
    private static final String DOC_ID = "id";
    protected final Map<String, String> fieldNameMap;

    public AbstractSolrQueryResultConverter(Map<String, String> fieldNameMap) {
        Preconditions.checkArgument(fieldNameMap != null, "Map of fields can not be null");

        this.fieldNameMap = fieldNameMap;
    }

    @Override public QueryResult<T> convert(QueryResponse toConvert, QueryRequest request) {
        Preconditions.checkArgument(toConvert != null, "Query response cannot be null");
        Preconditions.checkArgument(request != null, "Query request cannot be null");

        SolrDocumentList solrResults = toConvert.getResults();
        Page page = request.getPage();
        List<FacetField> facetFieldResults = toConvert.getFacetFields();
        Map<String, Map<String, List<String>>> resultHighlights = toConvert.getHighlighting();

        long totalNumberOfResults = 0;

        List<T> results;

        if (solrResults != null) {
            totalNumberOfResults = solrResults.getNumFound();
            results = convertResults(solrResults);
        } else {
            results = Collections.emptyList();
        }

        PageInfo pageInfo = null;

        if (page != null) {
            pageInfo = convertPage(page, totalNumberOfResults);
        }

        Facet facet = null;

        if (facetFieldResults != null && !facetFieldResults.isEmpty()) {
            facet = convertFacet(facetFieldResults);
        }

        List<DocHighlight> highlights = null;

        if (resultHighlights != null && solrResults != null) {
            highlights = convertResultHighlighting(solrResults, resultHighlights);
        }

        return new QueryResult<>(totalNumberOfResults, results, pageInfo, facet, highlights);
    }

    /**
     * Converts a {@link SolrDocumentList} of results, together with an associated list
     * of highlighted fields into a list of domain {@link DocHighlight} instances.
     *
     * @param solrResults the Solr results
     * @param resultHighlights the Solr highlighting information
     * @return the domain highlighting information
     */
    protected List<DocHighlight> convertResultHighlighting(
            SolrDocumentList solrResults,
            Map<String, Map<String, List<String>>> resultHighlights) {

        return solrResults.stream()
                .map(doc ->
                {
                    if (doc.containsKey(DOC_ID)) {
                        return convertToDocHighlight(
                                doc.getFieldValue(DOC_ID).toString(),
                                resultHighlights);
                    } else {
                        return null;
                    }
                })
                .filter(fieldHighlight -> fieldHighlight != null)
                .collect(Collectors.toList());
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
                    if (fieldNameMap.containsKey(fieldName)) {
                        fieldName = fieldNameMap.get(fieldName);
                    }
                    return new FieldHighlight(fieldName, entry.getValue());
                })
                .collect(Collectors.toList());
        return new DocHighlight(id, fieldHighlights);
    }

    /**
     * Creates a {@link Facet} object containing all facet related information.
     *
     * @param fields Solr facet fields to convert
     * @return a domain facet object
     */
    private Facet convertFacet(List<FacetField> fields) {
        Facet facet = new Facet();

        fields.stream()
                .map(this::convertFacetField)
                .forEach(facet::addFacetField);

        return facet;
    }

    /**
     * Converts a Solr {@link FacetField} into a domain {@link FieldFacet} object
     *
     * @param solrField field containing facet information
     * @return a domain facet field
     */
    private FieldFacet convertFacetField(FacetField solrField) {
        String name = solrField.getName();

        final FieldFacet domainFieldFacet = new FieldFacet(name);

        solrField.getValues().stream()
                .forEach(count -> domainFieldFacet.addCategory(count.getName(), count.getCount()));

        return domainFieldFacet;
    }

    private PageInfo convertPage(Page page, long totalNumberOfResults) {
        int resultsPerPage = page.getPageSize();
        int totalPages = (int) Math.ceil((double) totalNumberOfResults / (double) resultsPerPage);

        int currentPage = (totalPages == 0 ? 0 : page.getPageNumber());

        return new PageInfo(totalPages, currentPage, resultsPerPage);
    }

    protected abstract List<T> convertResults(SolrDocumentList results);
}