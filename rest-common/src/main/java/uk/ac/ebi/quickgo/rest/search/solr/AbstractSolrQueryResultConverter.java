package uk.ac.ebi.quickgo.rest.search.solr;

import com.google.common.base.Preconditions;
import org.apache.solr.client.solrj.SolrResponse;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import uk.ac.ebi.quickgo.rest.search.QueryResultConverter;
import uk.ac.ebi.quickgo.rest.search.query.*;
import uk.ac.ebi.quickgo.rest.search.results.*;
import uk.ac.ebi.quickgo.rest.search.results.Facet;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Abstract class that deals with the conversion of the non type specific aspects of a {@link QueryResult}.
 *
 * @author Ricardo Antunes
 */
public abstract class AbstractSolrQueryResultConverter<T> implements QueryResultConverter<T, QueryResponse> {
    private QueryResultHighlightingConverter<SolrDocumentList, Map<String, Map<String, List<String>>>>
            queryResultHighlightingConverter;

    private AggregationConverter<SolrResponse, AggregateResponse> aggregationConverter;

    @Override
    public QueryResult<T> convert(QueryResponse response, QueryRequest request) {
        Preconditions.checkArgument(response != null, "Query response cannot be null");
        Preconditions.checkArgument(request != null, "Query request cannot be null");

        SolrDocumentList solrResults = response.getResults();
        Page requestPage = request.getPage();
        List<FacetField> facetFieldResults = response.getFacetFields();
        Map<String, Map<String, List<String>>> resultHighlights = response.getHighlighting();

        long totalNumberOfResults = 0;

        List<T> results;

        if (solrResults != null) {
            totalNumberOfResults = solrResults.getNumFound();
            results = convertResults(solrResults);
        } else {
            results = Collections.emptyList();
        }

        PageInfo pageInfo = null;

        if (requestPage != null) {
            pageInfo = convertPage(requestPage, response, totalNumberOfResults);
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
            aggregation = aggregationConverter.convert(response);
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

        solrField.getValues()
                .forEach(count -> domainFieldFacet.addCategory(count.getName(), count.getCount()));

        return domainFieldFacet;
    }

    private PageInfo convertPage(Page page, QueryResponse response, long totalNumberOfResults) {
        PageInfo pageInfo;

        int resultsPerPage = page.getPageSize();

        if (resultsPerPage > 0) {
            int totalPages = (int) Math.ceil((double) totalNumberOfResults / (double) resultsPerPage);
            pageInfo = createPageInfo(page, response, totalPages);
        } else {
            pageInfo = new PageInfo.Builder()
                    .withCurrentPage(1)
                    .withTotalPages(1)
                    .withResultsPerPage(resultsPerPage)
                    .build();
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

    /**
     * Creates a {@link uk.ac.ebi.quickgo.rest.search.results.PageInfo} with appropriate
     * values according to the type of {@link Page} initially requested, the response,
     * and the total number of pages.
     *
     * @param page            the page
     * @param response        the response
     * @param totalPages      the total number of pages of results
     */
    private PageInfo createPageInfo(Page page, QueryResponse response, final int totalPages) {
        PageInfo.Builder pageInfoBuilder = new PageInfo.Builder();
        pageInfoBuilder
                .withTotalPages(totalPages)
                .withResultsPerPage(page.getPageSize());

        page.accept(new PageVisitor<PageInfo.Builder>() {
            @Override
            public void visit(RegularPage page, PageInfo.Builder subject) {
                Preconditions.checkArgument((page.getPageNumber() - 1) <= totalPages,
                        "The requested page number should not be greater than the number of pages available.");
                int currentPage = totalPages == 0 ? 0 : page.getPageNumber();
                subject.withCurrentPage(currentPage);
            }

            @Override
            public void visit(CursorPage page, PageInfo.Builder subject) {
                Preconditions.checkArgument((page.getCursor() != null && !page.getCursor().isEmpty()),
                        "The cursor cannot be null or empty.");
                subject.withNextCursor(response.getNextCursorMark());
            }
        }, pageInfoBuilder);

        return pageInfoBuilder.build();
    }
}