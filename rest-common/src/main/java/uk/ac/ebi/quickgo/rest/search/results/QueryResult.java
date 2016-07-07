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
    private final List<Aggregation> aggregations;

    private QueryResult(long numberOfHits, List<T> results, PageInfo pageInfo, Facet facet,
            List<DocHighlight> highlighting, List<Aggregation> aggregations) {
        Preconditions.checkArgument(numberOfHits >= 0, "Total number of hits can not be negative: " + numberOfHits);
        Preconditions.checkArgument(results != null, "Results list can not be null");
        Preconditions.checkArgument(results.size() <= numberOfHits,
                "Total number of results is less than number of results in list: [total: " + numberOfHits + ", " +
                        "results: " + results.size() + "]");

        this.numberOfHits = numberOfHits;
        this.results = Collections.unmodifiableList(results);
        this.pageInfo = pageInfo;
        this.facet = facet;

        this.highlighting = (highlighting != null) ?
                Collections.unmodifiableList(highlighting) : null;

        this.aggregations = (aggregations != null) ?
                Collections.unmodifiableList(aggregations) : null;
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
     * Returns an unmodifiable list of aggregations.
     *
     * @return list of aggregation results
     */
    public List<Aggregation> getAggregations() {
        return Collections.unmodifiableList(aggregations);
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
        if (!results.equals(that.results)) {
            return false;
        }
        if (pageInfo != null ? !pageInfo.equals(that.pageInfo) : that.pageInfo != null) {
            return false;
        }
        if (facet != null ? !facet.equals(that.facet) : that.facet != null) {
            return false;
        }
        if (!highlighting.equals(that.highlighting)) {
            return false;
        }
        return aggregations.equals(that.aggregations);
    }

    @Override public int hashCode() {
        int result = (int) (numberOfHits ^ (numberOfHits >>> 32));
        result = 31 * result + results.hashCode();
        result = 31 * result + (pageInfo != null ? pageInfo.hashCode() : 0);
        result = 31 * result + (facet != null ? facet.hashCode() : 0);
        result = 31 * result + highlighting.hashCode();
        result = 31 * result + aggregations.hashCode();
        return result;
    }

    @Override public String toString() {
        return "QueryResult{" +
                "numberOfHits=" + numberOfHits +
                ", results=" + results +
                ", pageInfo=" + pageInfo +
                ", facet=" + facet +
                ", highlighting=" + highlighting +
                ", aggregations=" + aggregations +
                '}';
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
        private Set<Aggregation> aggregations;

        public Builder(long hits, List<T> results) {
            this.numberOfHits = hits;
            this.results = results;

            this.highlights = new LinkedHashSet<>();
            this.aggregations = new LinkedHashSet<>();
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

        public Builder<T> appendAggregations(Aggregation... aggregation) {
            if (aggregation != null) {
                aggregations.addAll(Arrays.asList(aggregation));
            }
            return this;
        }

        public QueryResult<T> build() {
            return new QueryResult<>(numberOfHits,
                    results,
                    pageInfo,
                    facets,
                    new ArrayList<>(highlights),
                    new ArrayList<>(aggregations));
        }
    }
}