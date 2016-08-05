package uk.ac.ebi.quickgo.rest.search.query;

import com.google.common.base.Preconditions;

/**
 * Used by the {@link QueryRequest} to hold the paging information of the request.
 * <p/>
 * The paging information consists of the following elements:
 * <ul>
 *     <li>Page to retrieve</li>
 *     <li>Number of results to display in page</li>
 * </ul>
 */
public class Page {
    private int pageNumber;
    private int pageSize;

    public Page(int pageNumber, int pageSize) {
        Preconditions.checkArgument(pageNumber > 0, "Page number must be greater than 0");
        Preconditions.checkArgument(pageSize >= 0, "Page result size cannot be less than 0");

        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public int getPageNumber() {
        return this.pageNumber;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Page page = (Page) o;

        if (pageNumber != page.pageNumber) {
            return false;
        }
        return pageSize == page.pageSize;

    }

    @Override public int hashCode() {
        int result = pageNumber;
        result = 31 * result + pageSize;
        return result;
    }

    @Override public String toString() {
        return "Page{" +
                "pageNumber=" + pageNumber +
                ", pageSize=" + pageSize +
                '}';
    }
}