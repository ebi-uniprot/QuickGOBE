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
public abstract class Page {
//    private int pageNumber;
    private int pageSize;

    protected Page(int pageSize) {
        Preconditions.checkArgument(pageSize >= 0, "Page result size cannot be less than 0");

        this.pageSize = pageSize;
    }

    public abstract <V> void accept(PageVisitor<V> visitor, V subject);

    public int getPageSize() {
        return this.pageSize;
    }
}