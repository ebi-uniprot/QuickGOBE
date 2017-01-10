package uk.ac.ebi.quickgo.rest.search.query;

import com.google.common.base.Preconditions;

/**
 * Creates instances of {@link Page}.
 *
 * Created 09/01/17
 * @author Edd
 */
public class PageFactory {

    public static final int CURSOR_PAGE_NUMBER = 0;

    private PageFactory() {}

    /**
     * Creates a {@link Page} for use in a standard request.
     * @param pageNumber the page number being requested
     * @param pageSize the maximum number of results to include within a page request
     * @return a {@link Page} instance
     */
    public static Page createPage(int pageNumber, int pageSize) {
        Preconditions.checkArgument(pageNumber > 0, "Page number must be greater than 0");

        return new Page(pageNumber, pageSize);
    }

    /**
     * Creates a {@link Page} for use in a request that uses a cursor.
     * @param pageSize the maximum number of results to include within a page request
     * @return a {@link Page} instance
     */
    public static Page createCursorPage(int pageSize) {
        return new Page(CURSOR_PAGE_NUMBER, pageSize);
    }
}
