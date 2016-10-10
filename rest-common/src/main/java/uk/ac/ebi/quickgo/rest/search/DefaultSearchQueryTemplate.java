package uk.ac.ebi.quickgo.rest.search;

import uk.ac.ebi.quickgo.rest.search.query.QueryRequest;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static uk.ac.ebi.quickgo.rest.search.SearchDispatcher.isValidFacets;
import static uk.ac.ebi.quickgo.rest.search.SearchDispatcher.isValidFilterQueries;

/**
 * Records common configuration details required to create a {@link QueryRequest} instance,
 * and provides a {@link SearchQueryRequestBuilder} instance that can be used to build
 * specialised forms of the query.
 * <p>
 * The purpose of this class is to encapsulate and simplify the creation of search queries.
 * <p>
 * More specifically, this class wraps {@link uk.ac.ebi.quickgo.rest.search.query.QueryRequest.Builder}
 * and performs boiler-plate configuration tasks which all callers
 * of a search require. These tasks include, setting up field projections,
 * highlighting fields, validating facets and filters, etc.
 *
 * Created 11/04/16
 * @author Edd
 */
public class DefaultSearchQueryTemplate {
    public static final int DEFAULT_PAGE_SIZE = 25;
    public static final int DEFAULT_PAGE_NUMBER = 1;
    private static final boolean NO_HIGHLIGHTING = false;

    private final String highlightStartDelim;
    private final String highlightEndDelim;
    private final Iterable<String> returnedFields;
    private final Iterable<String> highlightedFields;
    private final SearchableField fieldSpec;

    public DefaultSearchQueryTemplate(
            SearchableField fieldSpec,
            Iterable<String> returnedFields,
            Iterable<String> highlightedFields,
            String highlightStartDelim,
            String highlightEndDelim) {
        this.highlightedFields = highlightedFields;
        this.highlightStartDelim = highlightStartDelim;
        this.highlightEndDelim = highlightEndDelim;
        this.returnedFields = returnedFields;
        this.fieldSpec = fieldSpec;
    }

    public Builder newBuilder() {
        return new Builder(
                fieldSpec,
                returnedFields,
                highlightedFields,
                highlightStartDelim,
                highlightEndDelim);
    }

    public static class Builder implements SearchQueryRequestBuilder {
        private final String highlightStartDelim;
        private final String highlightEndDelim;
        private final Iterable<String> highlightedFields;
        private final Iterable<String> returnedFields;
        private final Set<String> filterQueriesText;
        private final Set<QuickGOQuery> filterQueries;

        private final Set<String> facets;

        private QuickGOQuery query;
        private int page = DEFAULT_PAGE_NUMBER;
        private int pageSize = DEFAULT_PAGE_SIZE;
        private SearchableField fieldSpec;
        private boolean highlighting;

        private Builder(
                SearchableField fieldSpec,
                Iterable<String> returnedFields,
                Iterable<String> highlightedFields,
                String highlightStartDelim,
                String highlightEndDelim) {
            this.highlightedFields = highlightedFields;
            this.highlightStartDelim = highlightStartDelim;
            this.highlightEndDelim = highlightEndDelim;
            this.returnedFields = returnedFields;
            this.fieldSpec = fieldSpec;

            this.facets = new HashSet<>();
            this.filterQueriesText = new HashSet<>();
            this.filterQueries = new HashSet<>();
            this.highlighting = NO_HIGHLIGHTING;
        }

        /**
         * Specify a list of facets that should be used.
         * <p>
         * Note that this argument is nullable.
         *
         * @param facets the facets
         * @return this {@link DefaultSearchQueryTemplate.Builder} instance
         */
        public DefaultSearchQueryTemplate.Builder addFacets(List<String> facets) {
            if (facets != null) {
                this.facets.addAll(facets);
            }
            return this;
        }

        /**
         * Specify a list of filter queries that should be used.
         * <p>
         * Note that this argument is nullable.
         *
         * @param filters the filter queries
         * @return this {@link DefaultSearchQueryTemplate.Builder} instance
         */
        public DefaultSearchQueryTemplate.Builder addFilters(List<ConvertedFilter<QuickGOQuery>> filters) {
            if (filters != null) {
                List<QuickGOQuery> queryFilters = filters.stream()
                        .map(ConvertedFilter::getConvertedValue)
                        .collect(Collectors.toList());
                this.filterQueries.addAll(queryFilters);
            }
            return this;
        }

        /**
         * Whether or not to use highlighting.
         *
         * @param highlighting whether or not to use highlighting
         * @return this {@link DefaultSearchQueryTemplate.Builder} instance
         */
        public DefaultSearchQueryTemplate.Builder useHighlighting(boolean highlighting) {
            this.highlighting = highlighting;
            return this;
        }

        /**
         * Specify the search query.
         *
         * @param query the search query.
         * @return this {@link DefaultSearchQueryTemplate.Builder} instance
         */
        public DefaultSearchQueryTemplate.Builder setQuery(QuickGOQuery query) {
            this.query = query;
            return this;
        }

        /**
         * Specify the number of results to be returned per page, i.e., page size.
         *
         * @param pageSize the page size.
         * @return this {@link DefaultSearchQueryTemplate.Builder} instance
         */
        public DefaultSearchQueryTemplate.Builder setPageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        /**
         * Specify which page of results to return.
         *
         * @param page the page of results to return.
         * @return this {@link DefaultSearchQueryTemplate.Builder} instance
         */
        public DefaultSearchQueryTemplate.Builder setPage(int page) {
            this.page = page;
            return this;
        }

        @Override public QueryRequest build() {
            checkFacets(facets);
            checkFilters(filterQueriesText);

            QueryRequest.Builder builder = new QueryRequest.Builder(query);
            builder.setPageParameters(page, pageSize);

            if (!facets.isEmpty()) {
                facets.forEach(builder::addFacetField);
            }

            if(!filterQueries.isEmpty()) {
                filterQueries.forEach(builder::addQueryFilter);
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

        /**
         * Checks the specified facets are all searchable fields.
         *
         * @param facets the facets
         */
        void checkFacets(Iterable<String> facets) {
            if (!isValidFacets(fieldSpec, facets)) {
                throw new IllegalArgumentException("At least one of the provided facets is not searchable: " + facets);
            }
        }

        /**
         * Checks the specified filters all refer to searchable fields.
         *
         * @param filterQueries the filter queries
         */
        void checkFilters(Iterable<String> filterQueries) {
            if (!isValidFilterQueries(fieldSpec, filterQueries)) {
                throw new IllegalArgumentException("At least one of the provided filter queries is not filterable: " +
                        filterQueries);
            }
        }

        @Override public QueryRequest.Builder builder() {
            return null;
        }
    }

}
