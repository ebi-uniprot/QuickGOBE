package uk.ac.ebi.quickgo.rest.search;

import uk.ac.ebi.quickgo.rest.search.query.AggregateRequest;
import uk.ac.ebi.quickgo.rest.search.query.Page;
import uk.ac.ebi.quickgo.rest.search.query.QueryRequest;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;

import java.util.*;

import static uk.ac.ebi.quickgo.rest.search.query.PageFactory.createCursorPage;
import static uk.ac.ebi.quickgo.rest.search.query.PageFactory.createPage;
import static uk.ac.ebi.quickgo.rest.search.query.QueryRequest.FIRST_CURSOR_POSITION;

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

    private String highlightStartDelim;
    private String highlightEndDelim;
    private Iterable<String> returnedFields;
    private Iterable<String> highlightedFields;

    public DefaultSearchQueryTemplate() {
        this.returnedFields = Collections.emptyList();
        this.highlightedFields = Collections.emptyList();
        this.highlightStartDelim = "";
        this.highlightEndDelim = "";
    }

    public void setHighlighting(Collection<String> highlightedFields, String highlightStartDelim,
            String highlightEndDelim) {
        if (highlightedFields != null) {
            this.highlightedFields = highlightedFields;

            if (highlightStartDelim != null) {
                this.highlightStartDelim = highlightStartDelim;
            }

            if (highlightEndDelim != null) {
                this.highlightEndDelim = highlightEndDelim;
            }
        }
    }

    public void setReturnedFields(Collection<String> returnedFields) {
        if (returnedFields != null) {
            this.returnedFields = returnedFields;
        }
    }

    public Builder newBuilder() {
        return new Builder(
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
        private final Set<QuickGOQuery> filterQueries;

        private final Set<String> facets;
        private QuickGOQuery query;

        private int page = DEFAULT_PAGE_NUMBER;
        private int pageSize = DEFAULT_PAGE_SIZE;
        private boolean highlighting;
        private AggregateRequest aggregate;
        private String cursor;

        private Builder(
                Iterable<String> returnedFields,
                Iterable<String> highlightedFields,
                String highlightStartDelim,
                String highlightEndDelim) {
            this.highlightedFields = highlightedFields;
            this.highlightStartDelim = highlightStartDelim;
            this.highlightEndDelim = highlightEndDelim;
            this.returnedFields = returnedFields;

            this.facets = new HashSet<>();
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
        public DefaultSearchQueryTemplate.Builder addFilters(Collection<QuickGOQuery> filters) {
            if (filters != null) {
                this.filterQueries.addAll(filters);
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

        /**
         * Set the collection of aggregates to be calculated.
         *
         * @param aggregate the aggregate to calculate
         * @return this {@link DefaultSearchQueryTemplate.Builder} instance
         */
        public DefaultSearchQueryTemplate.Builder setAggregate(AggregateRequest aggregate) {
            this.aggregate = aggregate;
            return this;
        }

        /**
         * Specifies that the request should return both the current page of results,
         * in addition to a cursor to the next page of results.
         * @return this {@link DefaultSearchQueryTemplate.Builder} instance
         */
        public DefaultSearchQueryTemplate.Builder useCursor() {
            this.cursor = FIRST_CURSOR_POSITION;
            return this;
        }

        /**
         * Specifies that the request should return the next page of results starting from
         * the {@code cursor} position, in addition to a cursor to the next page of results.
         * @return this {@link DefaultSearchQueryTemplate.Builder} instance
         */
        public DefaultSearchQueryTemplate.Builder setCursorPosition(String cursor) {
            this.cursor = cursor;
            return this;
        }

        @Override public QueryRequest build() {
            QueryRequest.Builder builder = new QueryRequest.Builder(query);

            facets.forEach(builder::addFacetField);

            filterQueries.forEach(builder::addQueryFilter);

            if (highlighting) {
                highlightedFields
                        .forEach(builder::addHighlightedField);
                builder.setHighlightStartDelim(highlightStartDelim);
                builder.setHighlightEndDelim(highlightEndDelim);
            }

            returnedFields
                    .forEach(builder::addProjectedField);

            builder.setAggregate(aggregate);

            setStartPositionAndResultsSize(builder);

            return builder.build();
        }

        /**
         * <p>Sets the start position of the results to fetch, in addition to the
         * number of results.
         * <p>If a cursor is being used to indicate the start point, a {@link Page} created via
         * {@link uk.ac.ebi.quickgo.rest.search.query.PageFactory#createCursorPage(int)} is used. Otherwise,
         * a standard page is created via {@link uk.ac.ebi.quickgo.rest.search.query.PageFactory#createPage(int, int)}.
         */
        void setStartPositionAndResultsSize(QueryRequest.Builder builder) {
            Page pageRequest;
            if (isFirstCursorRequest()) {
                builder.useCursor();
                pageRequest = createCursorPage(pageSize);
            } else if (isCursorRequest()) {
                builder.setCursorPosition(cursor);
                pageRequest = createCursorPage(pageSize);
            } else {
                pageRequest = createPage(page, pageSize);
            }

            builder.setPage(pageRequest);
        }

        private boolean isFirstCursorRequest() {
            return cursor != null && cursor.equals(FIRST_CURSOR_POSITION);
        }

        private boolean isCursorRequest() {
            return cursor != null && !cursor.isEmpty();
        }

        @Override public QueryRequest.Builder builder() {
            return null;
        }
    }
}