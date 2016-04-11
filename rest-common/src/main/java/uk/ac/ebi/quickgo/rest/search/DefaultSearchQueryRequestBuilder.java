package uk.ac.ebi.quickgo.rest.search;

import uk.ac.ebi.quickgo.rest.search.query.QueryRequest;

import java.util.ArrayList;
import java.util.List;

import static uk.ac.ebi.quickgo.rest.search.SearchDispatcher.isValidFacets;
import static uk.ac.ebi.quickgo.rest.search.SearchDispatcher.isValidFilterQueries;

/**
 * Default implementation of a {@link SearchQueryRequestBuilder}. Its purpose
 * is to encapsulate and simplify the creation of search queries. The built
 * instances are of type, {@link QueryRequest}.
 *
 * Created 11/04/16
 * @author Edd
 */
public class DefaultSearchQueryRequestBuilder implements SearchQueryRequestBuilder {
    public static final int DEFAULT_PAGE_SIZE = 25;
    public static final int DEFAULT_PAGE_NUMBER = 1;
    private static final boolean NO_HIGHLIGHTING = false;

    private final String query;
    private final String highlightStartDelim;
    private final String highlightEndDelim;
    private final Iterable<String> returnedFields;
    private int page = DEFAULT_PAGE_NUMBER;
    private int pageSize = DEFAULT_PAGE_SIZE;
    private List<String> filterQueries;
    private Iterable<String> highlightedFields;
    private List<String> facets;
    private StringToQuickGOQueryConverter converter;
    private SearchableField fieldSpec;
    private boolean highlighting;

    public DefaultSearchQueryRequestBuilder(String query,
            StringToQuickGOQueryConverter converter,
            SearchableField fieldSpec,
            Iterable<String> returnedFields,
            Iterable<String> highlightedFields,
            String highlightStartDelim,
            String highlightEndDelim) {
        this.query = query;
        this.converter = converter;
        this.highlightedFields = highlightedFields;
        this.highlightStartDelim = highlightStartDelim;
        this.highlightEndDelim = highlightEndDelim;
        this.returnedFields = returnedFields;

        this.fieldSpec = fieldSpec;
        this.facets = new ArrayList<>();
        this.filterQueries = new ArrayList<>();
        this.highlighting = NO_HIGHLIGHTING;
    }

    public DefaultSearchQueryRequestBuilder addFacets(List<String> facets) {
        if (facets != null) {
            this.facets.addAll(facets);
        }
        return this;
    }

    public DefaultSearchQueryRequestBuilder addFilters(List<String> filters) {
        if (filters != null) {
            this.filterQueries.addAll(filters);
        }
        return this;
    }

    public DefaultSearchQueryRequestBuilder useHighlighting(boolean highlighting) {
        this.highlighting = highlighting;
        return this;
    }

    public DefaultSearchQueryRequestBuilder setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public DefaultSearchQueryRequestBuilder setPage(int page) {
        this.page = page;
        return this;
    }

    @Override public QueryRequest build() {
        checkFacets(facets);
        checkFilters(filterQueries);

        QueryRequest.Builder builder = new QueryRequest.Builder(converter.convert(query));
        builder.setPageParameters(page, pageSize);

        if (facets != null) {
            facets.forEach(builder::addFacetField);
        }

        if (filterQueries != null) {
            filterQueries.stream()
                    .map(converter::convert)
                    .forEach(builder::addQueryFilter);
        }

        if (highlighting) {
            highlightedFields
                    .forEach(builder::addHighlightedField);
            builder.setHighlightStartDelim(highlightStartDelim);
            builder.setHighlightEndDelim(highlightEndDelim);
        }

        returnedFields
                .forEach(builder::addProjectedField);

        return builder.build();
    }

    void checkFacets(Iterable<String> facets) {
        if (!isValidFacets(fieldSpec, facets)) {
            throw new IllegalArgumentException("At least one of the provided facets is not searchable: " + facets);
        }
    }

    void checkFilters(Iterable<String> filterQueries) {
        if (!isValidFilterQueries(fieldSpec, filterQueries)) {
            throw new IllegalArgumentException("At least one of the provided filter queries is not filterable: " +
                    filterQueries);
        }
    }

}
