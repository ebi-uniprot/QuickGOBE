package uk.ac.ebi.quickgo.rest.search.query;

import com.google.common.base.Preconditions;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static uk.ac.ebi.quickgo.rest.search.query.PageFactory.CURSOR_PAGE_NUMBER;

/**
 * Contains all of the information necessary to put in a search request to a searchable data source.
 */
public class QueryRequest {

    public static final String FIRST_CURSOR_POSITION = "*";

    private final QuickGOQuery query;
    private final Page page;
    private final List<Facet> facets;
    private final List<QuickGOQuery> filters;
    private final List<FieldProjection> projectedFields;
    private final List<FieldHighlight> highlightedFields;
    private final AggregateRequest aggregate;
    private final String highlightStartDelim;
    private final String highlightEndDelim;
    private final String cursor;

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
        this.cursor = builder.cursor;
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

    public String getCursor() {
        return cursor;
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
        private String cursor;

        public Builder(QuickGOQuery query) {
            Preconditions.checkArgument(query != null, "Query cannot be null");

            this.query = query;
            facets = new LinkedHashSet<>();
            filters = new LinkedHashSet<>();
            projectedFields = new LinkedHashSet<>();
            highlightedFields = new LinkedHashSet<>();
        }

        public Builder setPage(Page page) {
            checkState(this.cursor == null || page.getPageNumber() == CURSOR_PAGE_NUMBER,
                    "Cannot use a non-zero page number when using a cursor");

            checkArgument(page != null, "Page cannot be null");
            this.page = page;

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

        public Builder useCursor() {
            checkState(this.page == null || this.page.getPageNumber() == CURSOR_PAGE_NUMBER,
                    "Cannot use a cursor when a page number is non-zero");
            this.cursor = FIRST_CURSOR_POSITION;
            return this;
        }

        public Builder setCursorPosition(String cursor) {
            checkState(this.page == null || this.page.getPageNumber() == CURSOR_PAGE_NUMBER,
                    "Cannot use a cursor when a page number is non-zero");
            this.cursor = cursor;
            return this;
        }

        public QueryRequest build() {
            return new QueryRequest(this);
        }
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
                ", cursor='" + cursor + '\'' +
                '}';
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
        return cursor != null ? cursor.equals(that.cursor) : that.cursor == null;
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
        result = 31 * result + (cursor != null ? cursor.hashCode() : 0);
        return result;
    }

}