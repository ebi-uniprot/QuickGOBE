package uk.ac.ebi.quickgo.rest.search.results;

import com.google.common.base.Preconditions;

/**
 * Contains the paging information for a {@link QueryResult}.
 */
public class PageInfo {
    private final int totalPages;
    private final int currentPage;
    private final int resultsPerPage;

    public PageInfo(int totalPages, int currentPage, int resultsPerPage) {
        Preconditions.checkArgument(totalPages >= 0, "Total number of pages can not be negative: " + totalPages);
        Preconditions.checkArgument(currentPage >= 0, "Current page can not be negative: " + currentPage);
        Preconditions.checkArgument(totalPages >= currentPage, "Current page can not be larger than total amount of " +
                "pages: [current: " + currentPage + ", total: " + totalPages + "]");
        Preconditions.checkArgument(resultsPerPage >= 0, "Results per page can not be less than 0: " + resultsPerPage);

        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.resultsPerPage = resultsPerPage;
    }

    /**
     * Total amount of pages with results for the given query
     *
     * @return the number of pages with results
     */
    public int getTotal() {
        return totalPages;
    }

    /**
     * The current page number of the results in the {@link QueryResult}.
     *
     * @return the current page
     */
    public int getCurrent() {
        return currentPage;
    }

    /**
     * The maximum number of query results a page can display
     *
     * @return the maximum number of query results per page
     */
    public int getResultsPerPage() {
        return resultsPerPage;
    }
}
