package uk.ac.ebi.quickgo.rest.search.results;

import com.google.common.base.Preconditions;
import java.util.*;

/**
 * Contains the all the information pertaining to a search result submitted to a data source.
 */
public class QueryResult<T> {
    private final long numberOfHits;
    private final List<T> results;
    private final PageInfo pageInfo;
    private final Facet facet;
    private final List<DocHighlight> highlighting;
    private final AggregateResponse aggregation;
    private final String cursor;

    private QueryResult(Builder<T> builder) {
        Preconditions.checkArgument(builder.numberOfHits >= 0,
                "Total number of hits can not be negative: " + builder.numberOfHits);
        Preconditions.checkArgument(builder.results != null, "Results list can not be null");
        Preconditions.checkArgument(builder.results.size() <= builder.numberOfHits,
                "Total number of results is less than number of results in list: [total: "
                        + builder.numberOfHits + ", results: " + builder.results.size() + "]");

        this.numberOfHits = builder.numberOfHits;
        this.results = Collections.unmodifiableList(builder.results);
        this.pageInfo = builder.pageInfo;
        this.facet = builder.facets;
        this.cursor = builder.nextCursor;

        this.highlighting = (builder.highlights != null) ?
                Collections.unmodifiableList(new ArrayList<>(builder.highlights)) : null;

        this.aggregation = builder.aggregation;
    }

    /**
     * Returns an unmodifiable list of highlights.
     *
     * @return list of highlights
     */
    public List<DocHighlight> getHighlighting() {
        return Collections.unmodifiableList(highlighting);
    }

    public long getNumberOfHits() {
        return numberOfHits;
    }

    public List<T> getResults() {
        return results;
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public Facet getFacet() {
        return facet;
    }

    /**
     * Container that represents the results of all the aggregation calculations don on the result set.
     *
     * @return the aggregation result
     */
    public AggregateResponse getAggregation() {
        return aggregation;
    }

    public String getCursor() {
        return cursor;
    }

    @Override public String toString() {
        return "QueryResult{" +
                "numberOfHits=" + numberOfHits +
                ", results=" + results +
                ", pageInfo=" + pageInfo +
                ", facet=" + facet +
                ", highlighting=" + highlighting +
                ", aggregation=" + aggregation +
                ", nextCursor='" + cursor + '\'' +
                '}';
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        QueryResult<?> that = (QueryResult<?>) o;

        if (numberOfHits != that.numberOfHits) {
            return false;
        }
        if (results != null ? !results.equals(that.results) : that.results != null) {
            return false;
        }
        if (pageInfo != null ? !pageInfo.equals(that.pageInfo) : that.pageInfo != null) {
            return false;
        }
        if (facet != null ? !facet.equals(that.facet) : that.facet != null) {
            return false;
        }
        if (highlighting != null ? !highlighting.equals(that.highlighting) : that.highlighting != null) {
            return false;
        }
        if (aggregation != null ? !aggregation.equals(that.aggregation) : that.aggregation != null) {
            return false;
        }
        return cursor != null ? cursor.equals(that.cursor) : that.cursor == null;
    }

    @Override public int hashCode() {
        int result = (int) (numberOfHits ^ (numberOfHits >>> 32));
        result = 31 * result + (results != null ? results.hashCode() : 0);
        result = 31 * result + (pageInfo != null ? pageInfo.hashCode() : 0);
        result = 31 * result + (facet != null ? facet.hashCode() : 0);
        result = 31 * result + (highlighting != null ? highlighting.hashCode() : 0);
        result = 31 * result + (aggregation != null ? aggregation.hashCode() : 0);
        result = 31 * result + (cursor != null ? cursor.hashCode() : 0);
        return result;
    }

    /**
     * Builder used to facilitate the creation of {@link QueryResult} instances.
     *
     * @author Ricardo Antunes
     */
    public static class Builder<T> {
        private final long numberOfHits;
        private final List<T> results;

        private PageInfo pageInfo;
        private Facet facets;
        private Set<DocHighlight> highlights;
        private AggregateResponse aggregation;
        private String nextCursor;

        public Builder(long hits, List<T> results) {
            this.numberOfHits = hits;
            this.results = results;

            this.highlights = new LinkedHashSet<>();
        }

        public Builder<T> withPageInfo(PageInfo pageInfo) {
            this.pageInfo = pageInfo;
            return this;
        }

        public Builder<T> withFacets(Facet facets) {
            this.facets = facets;
            return this;
        }

        public Builder<T> appendHighlights(DocHighlight... highlight) {
            if (highlight != null) {
                highlights.addAll(Arrays.asList(highlight));
            }
            return this;
        }

        public Builder<T> appendHighlights(Collection<DocHighlight> highlightCol) {
            if (highlightCol != null) {
                highlights.addAll(highlightCol);
            }
            return this;
        }

        public Builder<T> withAggregation(AggregateResponse aggregation) {
            this.aggregation = aggregation;

            return this;
        }

        public Builder<T> withNextCursor(String nextCursor) {
            this.nextCursor = nextCursor;
            return this;
        }

        public QueryResult<T> build() {
            return new QueryResult<>(this);
        }
    }
}