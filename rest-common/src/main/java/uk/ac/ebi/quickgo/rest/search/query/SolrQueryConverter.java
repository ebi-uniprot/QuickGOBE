package uk.ac.ebi.quickgo.rest.search.query;

import uk.ac.ebi.quickgo.rest.search.SolrQueryStringSanitizer;

import com.google.common.base.Preconditions;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.solr.client.solrj.SolrQuery;

/**
 * Converts a {@link QueryRequest} into a {@link SolrQuery} object.
 */
public class SolrQueryConverter implements QueryVisitor<String>, QueryRequestConverter<SolrQuery> {
    public static final String SOLR_FIELD_SEPARATOR = ":";

    private static final int MIN_COUNT_TO_DISPLAY_FACET = 1;

    private final String requestHandler;
    private final SolrQueryStringSanitizer queryStringSanitizer;

    public SolrQueryConverter(String requestHandler) {
        Preconditions.checkArgument(requestHandler != null && !requestHandler.trim().isEmpty(),
                "Request handler name cannot be null or empty");

        this.requestHandler = requestHandler;
        this.queryStringSanitizer = new SolrQueryStringSanitizer();
    }

    @Override public String visit(FieldQuery query) {
        return "(" + query.field() + SOLR_FIELD_SEPARATOR + queryStringSanitizer.sanitize(query.value()) + ")";
    }

    @Override public String visit(CompositeQuery query) {
        CompositeQuery.QueryOp operator = query.queryOperator();
        Set<QuickGOQuery> queries = query.queries();

        String operatorText = " " + operator.name() + " ";

        return queries.stream()
                .map(q -> q.accept(this))
                .collect(Collectors.joining(operatorText, "(", ")"));
    }

    @Override public String visit(NoFieldQuery query) {
        return "(" + queryStringSanitizer.sanitize(query.getValue()) + ")";
    }


    @Override public String visit(AllQuery query) {
        return "*:*";
    }

    @Override public SolrQuery convert(QueryRequest request) {
        Preconditions.checkArgument(request != null, "Cannot convert null query request");

        final SolrQuery solrQuery = new SolrQuery();

        assignQuery(request, solrQuery);
        solrQuery.setRequestHandler(requestHandler);

        Page page = request.getPage();

        if (page != null) {
            solrQuery.setStart(calculateRowsFromPage(page.getPageNumber(), page.getPageSize()));
            solrQuery.setRows(page.getPageSize());
        }

        List<QuickGOQuery> filterQueries = request.getFilters();

        if (!filterQueries.isEmpty()) {
            List<String> solrFilters = filterQueries.stream()
                    .map(fq -> fq.accept(this))
                    .collect(Collectors.toList());

            solrQuery.setFilterQueries(solrFilters.toArray(new String[solrFilters.size()]));
        }

        if (!request.getFacets().isEmpty()) {
            request.getFacets().forEach(facet -> solrQuery.addFacetField(facet.getField()));
            solrQuery.setFacetMinCount(MIN_COUNT_TO_DISPLAY_FACET);
        }

        if (!request.getHighlightedFields().isEmpty()) {
            solrQuery.setHighlight(true);
            request.getHighlightedFields().stream().forEach(field -> solrQuery.addHighlightField(field.getField()));
            solrQuery.setHighlightSimplePre(request.getHighlightStartDelim());
            solrQuery.setHighlightSimplePost(request.getHighlightEndDelim());
        }

        if (!request.getProjectedFields().isEmpty()) {
            request.getProjectedFields().forEach(field -> solrQuery.addField(field.getField()));
        }

        return solrQuery;
    }

    protected void assignQuery(QueryRequest request,
            SolrQuery solrQuery) {
        solrQuery.setQuery(request.getQuery().accept(this));
    }

    private int calculateRowsFromPage(int page, int numRows) {
        return (page - 1) * numRows;
    }
}
