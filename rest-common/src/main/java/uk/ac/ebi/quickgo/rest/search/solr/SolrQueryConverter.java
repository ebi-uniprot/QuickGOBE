package uk.ac.ebi.quickgo.rest.search.solr;

import uk.ac.ebi.quickgo.rest.search.query.*;

import com.google.common.base.Preconditions;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.params.CursorMarkParams;

/**
 * Converts a {@link QueryRequest} into a {@link SolrQuery} object.
 */
public class SolrQueryConverter implements QueryRequestConverter<SolrQuery> {
    public static final String SOLR_FIELD_SEPARATOR = ":";
    public static final String SOLR_FIELD_STAR = "*";
    public static final String CROSS_CORE_JOIN_SYNTAX = "{!join from=%s to=%s fromIndex=%s} %s";

    static final String FACET_ANALYTICS_ID = "json.facet";

    private static final int MIN_COUNT_TO_DISPLAY_FACET = 1;

    private final String requestHandler;
    private final QueryVisitor<String> solrQuerySerializer;
    private final AggregateConverter<String> aggregateConverter;
    private final SolrPageVisitor solrPageVistor;

    public SolrQueryConverter(String requestHandler, QueryVisitor<String> solrQuerySerializer) {
        Preconditions.checkArgument(requestHandler != null && !requestHandler.trim().isEmpty(),
                "Request handler name cannot be null or empty");
        Preconditions.checkArgument(solrQuerySerializer != null, "The Solr query serializer (QueryVisitor) cannot be " +
                "null");

        this.requestHandler = requestHandler;
        this.solrQuerySerializer = solrQuerySerializer;
        this.aggregateConverter = new AggregateToStringConverter();
        this.solrPageVistor = new SolrPageVisitor();
    }

    public static SolrQueryConverter createWithWildCardSupport(String requestHandler, Set<String>
            wildCardCompatibleFields) {
        Preconditions.checkArgument(wildCardCompatibleFields != null,
                                    "Wildcard compatible field list cannot be null");
        return new SolrQueryConverter(requestHandler, new SortedSolrQuerySerializer(wildCardCompatibleFields));
    }

    public static SolrQueryConverter create(String requestHandler) {
        return new SolrQueryConverter(requestHandler, new SortedSolrQuerySerializer());
    }

    @Override public SolrQuery convert(QueryRequest request) {
        Preconditions.checkArgument(request != null, "Cannot convert null query request");

        final SolrQuery solrQuery = new SolrQuery();

        solrQuery.setQuery(request.getQuery().accept(solrQuerySerializer));
        solrQuery.setRequestHandler(requestHandler);

        Page page = request.getPage();
        if (page != null) {
            setPageData(page, solrQuery);
        }

        List<QuickGOQuery> filterQueries = request.getFilters();

        if (!filterQueries.isEmpty()) {
            List<String> solrFilters = filterQueries.stream()
                    .map(fq -> fq.accept(solrQuerySerializer))
                    .collect(Collectors.toList());

            solrQuery.setFilterQueries(solrFilters.toArray(new String[solrFilters.size()]));
        }

        if (!request.getFacets().isEmpty()) {
            request.getFacets().forEach(facet -> solrQuery.addFacetField(facet.getField()));
            solrQuery.setFacetMinCount(MIN_COUNT_TO_DISPLAY_FACET);
        }

        if (!request.getHighlightedFields().isEmpty()) {
            solrQuery.setHighlight(true);
            request.getHighlightedFields().forEach(field -> solrQuery.addHighlightField(field.getField()));
            solrQuery.setHighlightSimplePre(request.getHighlightStartDelim());
            solrQuery.setHighlightSimplePost(request.getHighlightEndDelim());
        }

        if (!request.getProjectedFields().isEmpty()) {
            request.getProjectedFields().forEach(field -> solrQuery.addField(field.getField()));
        }

        if (aggregateConverter != null && request.getAggregate() != null) {
            solrQuery.setParam(FACET_ANALYTICS_ID, aggregateConverter.convert(request.getAggregate()));
        }

        if (!request.getSortCriteria().isEmpty()) {
            request.getSortCriteria().forEach(criterion ->
                    solrQuery.addSort(criterion.getSortField().getField(), sortOrderOf(criterion.getSortOrder())));
        }

        return solrQuery;
    }

    private SolrQuery.ORDER sortOrderOf(SortCriterion.SortOrder sortOrder) {
        switch (sortOrder) {
            case ASC:
                return SolrQuery.ORDER.asc;
            case DESC:
                return SolrQuery.ORDER.desc;
            default:
                throw new IllegalStateException("Unknown sort order specified: " + sortOrder.name());
        }
    }

    /**
     * Uses the {@link SolrPageVisitor} to visit the {@link Page} structure, and set the appropriate
     * attributes of {@code solrQuery} accordingly.
     *
     * @param page the page instance
     * @param solrQuery the query whose attributes are to be set
     */
    private void setPageData(Page page, SolrQuery solrQuery) {
        page.accept(solrPageVistor, solrQuery);
    }

    /**
     * A {@link PageVisitor} implementation whose role is to set {@link SolrQuery}
     * attributes accordingly, based upon values within the {@link Page} structure.
     */
    private static class SolrPageVisitor implements PageVisitor<SolrQuery> {

        @Override public void visit(RegularPage page, SolrQuery subject) {
            subject.setRows(page.getPageSize());
            subject.setStart(calculateRowsFromPage(page.getPageNumber(), page.getPageSize()));
        }

        @Override public void visit(CursorPage page, SolrQuery subject) {
            subject.setRows(page.getPageSize());
            subject.set(CursorMarkParams.CURSOR_MARK_PARAM, page.getCursor());
        }

        private int calculateRowsFromPage(int page, int numRows) {
            return page == 0 ? 0 : (page - 1) * numRows;
        }
    }
}
