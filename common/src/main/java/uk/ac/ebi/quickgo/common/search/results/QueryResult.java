package uk.ac.ebi.quickgo.common.search.results;

import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.List;

/**
 * Contains the all the information pertaining to a search result submitted to a data source.
 */
public class QueryResult<T> {
    private final long numberOfHits;
    private final List<T> results;
    private final PageInfo pageInfo;
    private final Facet facet;
    private final List<DocHighlight> highlighting;

    public QueryResult(long numberOfHits, List<T> results, PageInfo pageInfo, Facet facet, List<DocHighlight>
            highlighting) {
        Preconditions.checkArgument(numberOfHits >= 0, "Total number of hits can not be negative: " + numberOfHits);
        Preconditions.checkArgument(results != null, "Results list can not be null");
        Preconditions.checkArgument(results.size() <= numberOfHits,
                "Total number of results is less than number of results in list: [total: " + numberOfHits + ", " +
                        "results: " + results.size() + "]");

        this.numberOfHits = numberOfHits;
        this.results = Collections.unmodifiableList(results);
        this.pageInfo = pageInfo;
        this.facet = facet;
        this.highlighting = highlighting;
    }

    public List<DocHighlight> getHighlighting() {
        return highlighting;
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
        return highlighting != null ? highlighting.equals(that.highlighting) : that.highlighting == null;

    }

    @Override public int hashCode() {
        int result = (int) (numberOfHits ^ (numberOfHits >>> 32));
        result = 31 * result + (results != null ? results.hashCode() : 0);
        result = 31 * result + (pageInfo != null ? pageInfo.hashCode() : 0);
        result = 31 * result + (facet != null ? facet.hashCode() : 0);
        result = 31 * result + (highlighting != null ? highlighting.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return "QueryResult{" +
                "numberOfHits=" + numberOfHits +
                ", results=" + results +
                ", pageInfo=" + pageInfo +
                ", facet=" + facet +
                ", highlighting=" + highlighting +
                '}';
    }
}