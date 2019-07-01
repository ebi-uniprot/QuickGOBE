package uk.ac.ebi.quickgo.rest.search.query;

import com.google.common.base.Preconditions;

import java.util.*;

/**
 * Contains all of the information necessary to put in a search request to a searchable data source.
 */
public class QueryRequest {
    private final QuickGOQuery query;
    private final Page page;
    private final List<Facet> facets;
    private final List<QuickGOQuery> filters;
    private final List<FieldProjection> projectedFields;
    private final List<FieldHighlight> highlightedFields;
    private final AggregateRequest aggregate;
    private final String highlightStartDelim;
    private final String highlightEndDelim;
    private final List<SortCriterion> sortCriteria;
    private final String collection;

    private QueryRequest(Builder builder) {
        this.query = builder.query;
        this.page = builder.page;
        this.facets = Collections.unmodifiableList(new ArrayList<>(builder.facets));
        this.filters = new ArrayList<>(builder.filters);
        this.projectedFields = new ArrayList<>(builder.projectedFields);
        this.highlightedFields = new ArrayList<>(builder.highlightedFields);
        this.aggregate = builder.aggregate;
        this.highlightStartDelim = builder.highlightStartDelim;
        this.highlightEndDelim = builder.highlightEndDelim;
        this.sortCriteria = new ArrayList<>(builder.sortCriteria);
        this.collection = builder.collection;
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

    public List<FieldHighlight> getHighlightedFields() {
        return highlightedFields;
    }

    public List<FieldProjection> getProjectedFields() {
        return projectedFields;
    }

    public AggregateRequest getAggregate() {
        return aggregate;
    }

    public String getHighlightStartDelim() {
        return highlightStartDelim;
    }

    public String getHighlightEndDelim() {
        return highlightEndDelim;
    }

    public List<SortCriterion> getSortCriteria() {
        return sortCriteria;
    }

    public String getCollection() {
        return collection;
    }

    public static class Builder {
        private QuickGOQuery query;
        private Page page;
        private Set<Facet> facets;
        private Set<QuickGOQuery> filters;
        private Set<FieldProjection> projectedFields;
        private Set<FieldHighlight> highlightedFields;
        private AggregateRequest aggregate;
        private String highlightStartDelim;
        private String highlightEndDelim;
        private Set<SortCriterion> sortCriteria;
        private String collection;

        public Builder(QuickGOQuery query, String collection) {
            Preconditions.checkArgument(query != null, "Query cannot be null");
            Preconditions.checkArgument(collection != null && !collection.isEmpty(), "Collection cannot be null");

            this.query = query;
            this.collection = collection;
            facets = new LinkedHashSet<>();
            filters = new LinkedHashSet<>();
            sortCriteria = new LinkedHashSet<>();
            projectedFields = new LinkedHashSet<>();
            highlightedFields = new LinkedHashSet<>();
        }

        public Builder setPage(Page page) {
            this.page = page;

            return this;
        }

        public Builder addSortCriterion(String sortField, SortCriterion.SortOrder sortOrder) {
            this.sortCriteria.add(new SortCriterion(sortField, sortOrder));

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

        public Builder addHighlightedField(String field) {
            this.highlightedFields.add(new FieldHighlight(field));

            return this;
        }

        public Builder addProjectedField(String field) {
            this.projectedFields.add(new FieldProjection(field));
            return this;
        }

        public Builder setAggregate(AggregateRequest aggregate) {
            this.aggregate = aggregate;

            return this;
        }

        public Builder setHighlightStartDelim(String highlightStartDelim) {
            this.highlightStartDelim = highlightStartDelim;
            return this;
        }

        public Builder setHighlightEndDelim(String highlightEndDelim) {
            this.highlightEndDelim = highlightEndDelim;
            return this;
        }

        public QueryRequest build() {
            return new QueryRequest(this);
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

        if (query != null ? !query.equals(that.query) : that.query != null) {
            return false;
        }
        if (page != null ? !page.equals(that.page) : that.page != null) {
            return false;
        }
        if (facets != null ? !facets.equals(that.facets) : that.facets != null) {
            return false;
        }
        if (filters != null ? !filters.equals(that.filters) : that.filters != null) {
            return false;
        }
        if (projectedFields != null ? !projectedFields.equals(that.projectedFields) : that.projectedFields != null) {
            return false;
        }
        if (highlightedFields != null ? !highlightedFields.equals(that.highlightedFields) :
                that.highlightedFields != null) {
            return false;
        }
        if (aggregate != null ? !aggregate.equals(that.aggregate) : that.aggregate != null) {
            return false;
        }
        if (highlightStartDelim != null ? !highlightStartDelim.equals(that.highlightStartDelim) :
                that.highlightStartDelim != null) {
            return false;
        }
        if (highlightEndDelim != null ? !highlightEndDelim.equals(that.highlightEndDelim) :
                that.highlightEndDelim != null) {
            return false;
        }
        if (collection != null ? !collection.equals(that.collection) : that.collection != null) {
            return false;
        }
        return sortCriteria != null ? sortCriteria.equals(that.sortCriteria) : that.sortCriteria == null;
    }

    @Override public int hashCode() {
        int result = query != null ? query.hashCode() : 0;
        result = 31 * result + (page != null ? page.hashCode() : 0);
        result = 31 * result + (facets != null ? facets.hashCode() : 0);
        result = 31 * result + (filters != null ? filters.hashCode() : 0);
        result = 31 * result + (projectedFields != null ? projectedFields.hashCode() : 0);
        result = 31 * result + (highlightedFields != null ? highlightedFields.hashCode() : 0);
        result = 31 * result + (aggregate != null ? aggregate.hashCode() : 0);
        result = 31 * result + (highlightStartDelim != null ? highlightStartDelim.hashCode() : 0);
        result = 31 * result + (highlightEndDelim != null ? highlightEndDelim.hashCode() : 0);
        result = 31 * result + (sortCriteria != null ? sortCriteria.hashCode() : 0);
        result = 31 * result + (collection != null ? collection.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return "QueryRequest{" +
                "query=" + query +
                ", page=" + page +
                ", facets=" + facets +
                ", filters=" + filters +
                ", projectedFields=" + projectedFields +
                ", highlightedFields=" + highlightedFields +
                ", aggregate=" + aggregate +
                ", highlightStartDelim='" + highlightStartDelim + '\'' +
                ", highlightEndDelim='" + highlightEndDelim + '\'' +
                ", sortCriteria=" + sortCriteria +
                ", collection='" + collection + '\'' +
                '}';
    }

}