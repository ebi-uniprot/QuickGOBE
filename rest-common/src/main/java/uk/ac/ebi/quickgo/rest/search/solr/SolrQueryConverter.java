package uk.ac.ebi.quickgo.rest.search.solr;

import uk.ac.ebi.quickgo.rest.search.query.*;

import com.google.common.base.Preconditions;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.solr.client.solrj.SolrQuery;

/**
 * Converts a {@link QueryRequest} into a {@link SolrQuery} object.
 */
public class SolrQueryConverter implements QueryRequestConverter<SolrQuery> {
    public static final String SOLR_FIELD_SEPARATOR = ":";
    public static final String CROSS_CORE_JOIN_SYNTAX = "{!join from=%s to=%s fromIndex=%s} %s";

    static final String FACET_ANALYTICS_ID = "json.facet";

    private static final int MIN_COUNT_TO_DISPLAY_FACET = 1;

    private final String requestHandler;
    private final QueryVisitor<String> solrQuerySerializer;
    private final AggregateConverter<String> aggregateConverter;

    public SolrQueryConverter(String requestHandler) {
        this(requestHandler, new SortedSolrQuerySerializer());
    }

    public SolrQueryConverter(String requestHandler, QueryVisitor<String> solrQuerySerializer) {
        Preconditions.checkArgument(requestHandler != null && !requestHandler.trim().isEmpty(),
                "Request handler name cannot be null or empty");
        Preconditions.checkArgument(solrQuerySerializer != null, "The Solr query serializer (QueryVisitor) cannot be " +
                "null");

        this.requestHandler = requestHandler;
        this.solrQuerySerializer = solrQuerySerializer;
        this.aggregateConverter = new AggregateToStringConverter();
    }

    @Override public SolrQuery convert(QueryRequest request) {
        Preconditions.checkArgument(request != null, "Cannot convert null query request");

        final SolrQuery solrQuery = new SolrQuery();

        solrQuery.setQuery(request.getQuery().accept(solrQuerySerializer));
        solrQuery.setRequestHandler(requestHandler);

        Page page = request.getPage();

        if (page != null) {
            solrQuery.setStart(calculateRowsFromPage(page.getPageNumber(), page.getPageSize()));
            solrQuery.setRows(page.getPageSize());
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

        return solrQuery;
    }

    private int calculateRowsFromPage(int page, int numRows) {
        return (page - 1) * numRows;
    }
}