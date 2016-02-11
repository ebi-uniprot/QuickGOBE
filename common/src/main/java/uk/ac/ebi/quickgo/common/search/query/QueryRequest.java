package uk.ac.ebi.quickgo.common.search.query;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Contains all of the information necessary to put in a search request to a searchable data source.
 */
public class QueryRequest {
    private final QuickGOQuery query;
    private final Page page;
    private final List<Facet> facets;
    private final List<QuickGOQuery> filters;
    private final boolean highlighting;

    private QueryRequest(QuickGOQuery query,
            Page page,
            List<Facet> facets,
            List<QuickGOQuery> filters,
            boolean highlighting) {
        this.query = query;
        this.page = page;
        this.facets = Collections.unmodifiableList(facets);
        this.filters = filters;
        this.highlighting = highlighting;
    }

    public QuickGOQuery getQuery() {
        return query;
    }

    public Page getPage() {
        return page;
    }

    public Collection<Facet> getFacets() {
        return facets;
    }

    public List<QuickGOQuery> getFilters() {
        return Collections.unmodifiableList(filters);
    }

    public void addFilter(QuickGOQuery filterQuery) {
        filters.add(filterQuery);
    }

    public boolean usesHighlighting() {
        return highlighting;
    }

    public static class Builder {
        private QuickGOQuery query;
        private Page page;
        private List<Facet> facets;
        private List<QuickGOQuery> filters;
        private boolean highlighting;

        public Builder(QuickGOQuery query) {
            Preconditions.checkArgument(query != null, "Query cannot be null");

            this.query = query;
            facets = new ArrayList<>();
            filters = new ArrayList<>();
            highlighting = false;
        }

        public Builder setPageParameters(int currentPage, int pageSize) {
            this.page = new Page(currentPage, pageSize);

            return this;
        }

        public Builder addFacetField(String facet) {
            facets.add(new Facet(facet));

            return this;
        }

        public Builder addQueryFilter(QuickGOQuery filter) {
            this.filters.add(filter);

            return this;
        }

        public Builder useHighlighting(boolean useHighlighting) {
            this.highlighting = useHighlighting;

            return this;
        }

        public QueryRequest build() {
            return new QueryRequest(query, page, facets, filters, highlighting);
        }
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        QueryRequest that = (QueryRequest) o;

        if (highlighting != that.highlighting) {
            return false;
        }
        if (query != null ? !query.equals(that.query) : that.query != null) {
            return false;
        }
        if (page != null ? !page.equals(that.page) : that.page != null) {
            return false;
        }
        if (facets != null ? !facets.equals(that.facets) : that.facets != null) {
            return false;
        }
        return filters != null ? filters.equals(that.filters) : that.filters == null;

    }

    @Override public int hashCode() {
        int result = query != null ? query.hashCode() : 0;
        result = 31 * result + (page != null ? page.hashCode() : 0);
        result = 31 * result + (facets != null ? facets.hashCode() : 0);
        result = 31 * result + (filters != null ? filters.hashCode() : 0);
        result = 31 * result + (highlighting ? 1 : 0);
        return result;
    }

    @Override public String toString() {
        return "QueryRequest{" +
                "query=" + query +
                ", page=" + page +
                ", facets=" + facets +
                ", filters=" + filters +
                ", highlighting=" + highlighting +
                '}';
    }
}