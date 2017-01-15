package uk.ac.ebi.quickgo.rest.search.results;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

/**
 * Contains the paging information for a {@link QueryResult}.
 */
public class PageInfo {
    public static final int CURSOR_PAGE_NUMBER = 0;

    private final int totalPages;
    private final int currentPage;
    private final String nextCursor;
    private final int resultsPerPage;

    private PageInfo(Builder builder) {
        this.currentPage = builder.currentPage;
        this.nextCursor = builder.nextCursor;
        this.resultsPerPage = builder.resultsPerPage;
        this.totalPages = builder.totalPages;
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
     * The cursor position from which one can receive the next page of results.
     *
     * @return the next cursor position
     */
    public String getNextCursor() {
        return nextCursor;
    }

    /**
     * The maximum number of query results a page can display
     *
     * @return the maximum number of query results per page
     */
    public int getResultsPerPage() {
        return resultsPerPage;
    }

    public static class Builder {
        private static final int UNINITIALISED_PAGE_NUMBER = -1;
        private int totalPages;
        private int resultsPerPage;
        private int currentPage = UNINITIALISED_PAGE_NUMBER;
        private String nextCursor;

        public Builder withTotalPages(int totalPages) {
            checkArgument(totalPages >= 0, "Total number of pages cannot be negative: " + totalPages);
            this.totalPages = totalPages;
            return this;
        }

        public Builder withResultsPerPage(int resultsPerPage) {
            checkArgument(resultsPerPage >= 0, "Results per page cannot be less than 0: " + resultsPerPage);
            this.resultsPerPage = resultsPerPage;
            return this;
        }

        public Builder withCurrentPage(int currentPage) {
            checkArgument(currentPage >= 0, "Current page cannot be negative: " + currentPage);
            checkState(nextCursor == null, "Cannot set both current page and next cursor");
            this.currentPage = currentPage;
            return this;
        }

        public Builder withNextCursor(String cursor) {
            checkArgument(cursor != null && !cursor.isEmpty(), "Next cursor cannot be null or empty: " + cursor);
            checkState(currentPage == UNINITIALISED_PAGE_NUMBER, "Cannot set both next cursor and current page");
            this.nextCursor = cursor;
            this.currentPage = CURSOR_PAGE_NUMBER;
            return this;
        }

        private void validateState() {
            checkState(totalPages >= currentPage, "Current page cannot be larger than total amount of pages: " +
                    "[current: " + currentPage + ", total: " + totalPages + "]");
        }

        public PageInfo build() {
            validateState();
            return new PageInfo(this);
        }
    }
}
