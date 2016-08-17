package uk.ac.ebi.quickgo.rest.search;

import uk.ac.ebi.quickgo.rest.search.query.QueryRequest;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;

import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Reduces the amount of boiler plate code necessary to setup the mandatory elements to configure a
 * {@link QueryRequest}.
 *
 * The builder exposes just enough configuration methods to create a simple {@link QueryRequest}. Which means that
 * the builder will not expose advanced configuration methods such as: faceting; highlighting. To enable these you
 * will need to use more advanced templates such as: {@link FacetedSearchQueryTemplate};
 * {@link HighlightedSearchQueryTemplate} and {@link AggregateSearchQueryTemplate}.
 *
 * @author Ricardo Antunes
 */
public class BasicSearchQueryTemplate {
    static final int DEFAULT_PAGE_SIZE = 25;
    static final int DEFAULT_PAGE_NUMBER = 1;

    private final List<String> returnedFields;

    public BasicSearchQueryTemplate(List<String> returnedFields) {
        Preconditions.checkArgument(returnedFields != null, "Returned fields list cannot be null.");

        this.returnedFields = returnedFields;
    }

    public Builder newBuilder() {
        return new Builder(returnedFields);
    }

    public static class Builder implements SearchQueryRequestBuilder {
        private Set<String> returnedFields;
        private Set<QuickGOQuery> filters;

        private QuickGOQuery query;
        private int page;
        private int pageSize;

        private Builder(List<String> returnedFields) {
            this.returnedFields = new LinkedHashSet<>(returnedFields);

            page = DEFAULT_PAGE_NUMBER;
            pageSize = DEFAULT_PAGE_SIZE;

            this.filters = Collections.emptySet();
        }

        /**
         * Specify a set of filters that should be used.
         * <p>
         * Note that this argument is nullable.
         *
         * @param filters the filter queries
         * @return this {@link Builder} instance
         */
        public Builder setFilters(Set<QuickGOQuery> filters) {
            if (filters != null) {
                this.filters = filters;
            }

            return this;
        }

        /**
         * Specify a set of fields to return with the query response.
         * <p>
         * .
         * @param returnedFields the filter queries
         * @return this {@link Builder} instance
         */
        public Builder setReturnedFields(List<String> returnedFields) {
            if (returnedFields != null) {
                this.returnedFields = new LinkedHashSet<>(returnedFields);
            }

            return this;
        }

        /**
         * Specify the search query.
         *
         * @param query the search query.
         * @return this {@link Builder} instance
         */
        public Builder setQuery(QuickGOQuery query) {
            this.query = query;

            return this;
        }

        /**
         * Specify the number of results to be returned per page, i.e., page size.
         *
         * @param pageSize the page size.
         * @return this {@link Builder} instance
         */
        public Builder setPageSize(int pageSize) {
            this.pageSize = pageSize;

            return this;
        }

        /**
         * Specify which page of results to return.
         *
         * @param page the page of results to return.
         * @return this {@link Builder} instance
         */
        public Builder setPage(int page) {
            this.page = page;

            return this;
        }

        @Override public QueryRequest build() {
            return builder().build();
        }

        @Override public QueryRequest.Builder builder() {
            QueryRequest.Builder builder = new QueryRequest.Builder(query);
            builder.setPageParameters(page, pageSize);

            filters.stream()
                    .forEach(builder::addQueryFilter);

            returnedFields
                    .forEach(builder::addProjectedField);

            return builder;
        }
    }
}