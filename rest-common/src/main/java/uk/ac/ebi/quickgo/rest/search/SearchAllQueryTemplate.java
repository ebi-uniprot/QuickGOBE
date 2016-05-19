package uk.ac.ebi.quickgo.rest.search;

import uk.ac.ebi.quickgo.rest.search.query.FilterProvider;
import uk.ac.ebi.quickgo.rest.search.query.PrototypeFilter;
import uk.ac.ebi.quickgo.rest.search.query.QueryRequest;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Records common configuration details required to create a {@link QueryRequest} instance,
 * and provides a {@link SearchQueryRequestBuilder} instance that can be used to build
 * specialised forms of the query.
 * <p>
 * The purpose of this class is to encapsulate and simplify the creation of search queries.
 * <p>
 * More specifically, this class wraps {@link QueryRequest.Builder}
 * and performs boiler-plate configuration tasks which all callers
 * of a search require. These tasks include, setting up field projections,
 * highlighting fields, validating facets and filters, etc.
 *
 *
 * Created 11/04/16
 * @author Edd
 */
public class SearchAllQueryTemplate {
    public static final int DEFAULT_PAGE_SIZE = 25;
    public static final int DEFAULT_PAGE_NUMBER = 1;
    private static final boolean NO_HIGHLIGHTING = false;

    private final String highlightStartDelim;
    private final String highlightEndDelim;
    private final Iterable<String> returnedFields;
    private Iterable<String> highlightedFields;
    private SearchableField fieldSpec;


    public SearchAllQueryTemplate(Iterable<String> returnedFields) {
        this.highlightedFields = Collections.emptyList();
        this.highlightStartDelim = null;
        this.highlightEndDelim = null;
        this.returnedFields = returnedFields;
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
        private Iterable<String> highlightedFields;
        private final Iterable<String> returnedFields;

        private int page = DEFAULT_PAGE_NUMBER;
        private int pageSize = DEFAULT_PAGE_SIZE;
        private List<String> facets;
        private SearchableField fieldSpec;
        private boolean highlighting;
        private FilterProvider filterProvider;

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

            this.facets = new ArrayList<>();
            this.highlighting = NO_HIGHLIGHTING;
        }



        /**
         * Specify a list of filter queries that should be used.
         * <p>
         * Note that this argument is nullable.
         *
         * @param filterProvider the filter queries
         * @return this {@link DefaultSearchQueryTemplate.Builder} instance
         */
        public SearchAllQueryTemplate.Builder addFilterProvider(FilterProvider filterProvider) {
            this.filterProvider = filterProvider;
            return this;
        }



        /**
         * Specify which page of results to return.
         *
         * @param page the page of results to return.
         * @return this {@link DefaultSearchQueryTemplate.Builder} instance
         */
        public SearchAllQueryTemplate.Builder setPage(int page) {
            this.page = page;
            return this;
        }

        @Override public QueryRequest build() {

            QueryRequest.Builder builder = new QueryRequest.Builder(QuickGOQuery.createAllQuery());
            builder.setPageParameters(page, pageSize);

            if (facets != null) {
                facets.forEach(builder::addFacetField);
            }

            builder.setPageParameters(Integer.valueOf(filterProvider.getPage()), Integer.valueOf(filterProvider
                    .getLimit()));

            filterProvider.stream().forEach(pf -> addFilterToBuilder(builder, pf));
            returnedFields
                    .forEach(builder::addProjectedField);

            return builder.build();
        }


        /**
         * Add a new QuickGOQuery to the builder based on consuming the PrototypeFilter passed to this method.
         * @param builder
         * @param pf
         */
        private void addFilterToBuilder(QueryRequest.Builder builder, PrototypeFilter pf) {

            QuickGOQuery quickGOQuery = pf.provideArgStream()
                    .parallel()
                    .reduce(null, (q, arg) -> QuickGOQuery.createQuery( pf.getFilterField(), arg), QuickGOQuery::or) ;

            if(quickGOQuery!=null) {
                builder.addQueryFilter(quickGOQuery);
            }
        }
    }

}
