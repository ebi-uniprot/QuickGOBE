package uk.ac.ebi.quickgo.rest.search.solr;

import uk.ac.ebi.quickgo.rest.search.QueryResultConverter;
import uk.ac.ebi.quickgo.rest.search.query.Page;
import uk.ac.ebi.quickgo.rest.search.query.QueryRequest;
import uk.ac.ebi.quickgo.rest.search.results.*;

import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.solr.client.solrj.SolrResponse;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

/**
 * Abstract class that deals with the conversion of the non type specific aspects of a {@link QueryResult}.
 *
 * @author Ricardo Antunes
 */
public abstract class AbstractSolrQueryResultConverter<T> implements QueryResultConverter<T, QueryResponse> {
    private QueryResultHighlightingConverter<SolrDocumentList, Map<String, Map<String, List<String>>>>
            queryResultHighlightingConverter;

    private AggregationConverter<SolrResponse, AggregateResponse> aggregationConverter;

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

        if (resultHighlights != null && solrResults != null && queryResultHighlightingConverter != null) {
            highlights = queryResultHighlightingConverter.convertResultHighlighting(solrResults, resultHighlights);
        }

        AggregateResponse aggregation = null;

        if (aggregationConverter != null) {
            aggregation = aggregationConverter.convert(toConvert);
        }

        return new QueryResult.Builder<>(totalNumberOfResults, results)
                .withPageInfo(pageInfo)
                .withFacets(facet)
                .withAggregation(aggregation)
                .appendHighlights(highlights)
                .build();
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
        PageInfo pageInfo;

        int resultsPerPage = page.getPageSize();

        if (resultsPerPage > 0) {
            int totalPages = (int) Math.ceil((double) totalNumberOfResults / (double) resultsPerPage);

            totalPages = totalPages == 0 ? 1 : totalPages;

            pageInfo = new PageInfo(totalPages, page.getPageNumber(), resultsPerPage);
        } else {
            pageInfo = new PageInfo(1, 1, resultsPerPage);
        }

        return pageInfo;
    }

    protected abstract List<T> convertResults(SolrDocumentList results);

    protected void setQueryResultHighlightingConverter(
            QueryResultHighlightingConverter<SolrDocumentList, Map<String, Map<String, List<String>>>>
                    queryResultHighlightingConverter) {
        if (queryResultHighlightingConverter != null) {
            this.queryResultHighlightingConverter = queryResultHighlightingConverter;
        }
    }

    protected void setAggregationConverter(AggregationConverter<SolrResponse, AggregateResponse> aggregationConverter) {
        if (aggregationConverter != null) {
            this.aggregationConverter = aggregationConverter;
        }
    }
}