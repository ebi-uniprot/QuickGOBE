package uk.ac.ebi.quickgo.rest.search;

import uk.ac.ebi.quickgo.rest.search.query.*;

import java.util.*;

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
    private List<SortCriterion> sortCriteria;
    private Page page;

    public DefaultSearchQueryTemplate() {
        this.returnedFields = Collections.emptyList();
        this.highlightedFields = Collections.emptyList();
        this.highlightStartDelim = "";
        this.highlightEndDelim = "";
        this.page = new RegularPage(DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE);
        this.sortCriteria = new ArrayList<>();
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

    public void addSortCriterion(String field, SortCriterion.SortOrder order) {
        this.sortCriteria.add(new SortCriterion(field, order));
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public Builder newBuilder() {
        return new Builder(this);
    }

    public static class Builder implements SearchQueryRequestBuilder {
        private final String highlightStartDelim;
        private final String highlightEndDelim;
        private final Iterable<String> highlightedFields;
        private final Iterable<String> returnedFields;
        private final Set<SortCriterion> sortCriteria;

        private final Set<QuickGOQuery> filterQueries;
        private final Set<String> facets;
        private QuickGOQuery query;
        private String collection;

        private Page page;
        private boolean highlighting;
        private AggregateRequest aggregate;

        private Builder(DefaultSearchQueryTemplate template) {
            this.highlightedFields = template.highlightedFields;
            this.highlightStartDelim = template.highlightStartDelim;
            this.highlightEndDelim = template.highlightEndDelim;
            this.returnedFields = template.returnedFields;

            this.facets = new HashSet<>();
            this.filterQueries = new HashSet<>();
            this.highlighting = NO_HIGHLIGHTING;
            this.page = template.page;

            this.sortCriteria = new LinkedHashSet<>();
            template.sortCriteria.forEach(sortCriteria::add);
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
         * Add a sort criteria that should be used.
         *
         * @param field the field to sort on
         * @param sortOrder the sort order
         * @return this {@link DefaultSearchQueryTemplate.Builder} instance
         */
        public DefaultSearchQueryTemplate.Builder addSortCriterion(String field, SortCriterion.SortOrder sortOrder) {
            this.sortCriteria.add(new SortCriterion(field, sortOrder));

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
         * Specify the collection on which you want to query.
         *
         * @param collection the search collection.
         * @return this {@link DefaultSearchQueryTemplate.Builder} instance
         */
        public DefaultSearchQueryTemplate.Builder setCollection(String collection) {
            this.collection = collection;
            return this;
        }

        /**
         * Specify which page of results to return.
         *
         * @param page the page of results to return.
         * @return this {@link DefaultSearchQueryTemplate.Builder} instance
         */
        public DefaultSearchQueryTemplate.Builder setPage(Page page) {
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

        @Override public QueryRequest build() {
            QueryRequest.Builder builder = new QueryRequest.Builder(query, collection);

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

            builder.setPage(page);

            sortCriteria.forEach(criterion ->
                    builder.addSortCriterion(criterion.getSortField().getField(), criterion.getSortOrder()));

            return builder.build();
        }

        @Override public QueryRequest.Builder builder() {
            return null;
        }
    }
}