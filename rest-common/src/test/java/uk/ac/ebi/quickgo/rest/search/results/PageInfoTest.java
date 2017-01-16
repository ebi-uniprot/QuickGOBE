package uk.ac.ebi.quickgo.rest.search.results;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Tests the {@link PageInfo} implementation.
 */
public class PageInfoTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void negativeTotalNumberThrowsException() throws Exception {
        int totalPages = -1;
        int currentPage = 0;
        int resultsPerPage = 1;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Total number of pages cannot be negative");

        new PageInfo.Builder()
                .withTotalPages(totalPages)
                .withCurrentPage(currentPage)
                .withResultsPerPage(resultsPerPage)
                .build();
    }

    @Test
    public void negativeCurrentPageThrowsException() throws Exception {
        int totalPages = 1;
        int currentPage = -1;
        int resultsPerPage = 1;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Current page cannot be negative");

        new PageInfo.Builder()
                .withTotalPages(totalPages)
                .withCurrentPage(currentPage)
                .withResultsPerPage(resultsPerPage)
                .build();
    }

    @Test
    public void negativeResultsPerPageThrowsException() throws Exception {
        int totalPages = 1;
        int currentPage = 0;
        int resultsPerPage = -1;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Results per page cannot be less than 0");

        new PageInfo.Builder()
                .withTotalPages(totalPages)
                .withCurrentPage(currentPage)
                .withResultsPerPage(resultsPerPage)
                .build();
    }

    @Test
    public void currentPageLargerThanTotalPagesThrowsException() throws Exception {
        int totalPages = 1;
        int currentPage = 2;
        int resultsPerPage = 1;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Current page cannot be larger than total amount of pages");

        new PageInfo.Builder()
                .withTotalPages(totalPages)
                .withCurrentPage(currentPage)
                .withResultsPerPage(resultsPerPage)
                .build();
    }

    @Test
    public void settingNextCursorAndCurrentPageThrowsException() throws Exception {
        int totalPages = 10;
        int currentPage = 1;
        int resultsPerPage = 1;
        String cursor = "fakeCursor";

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Cannot set both next cursor and current page");

        new PageInfo.Builder()
                .withTotalPages(totalPages)
                .withCurrentPage(currentPage)
                .withNextCursor(cursor)
                .withResultsPerPage(resultsPerPage)
                .build();
    }

    @Test
    public void settingCurrentPageAndNextCursorThrowsException() throws Exception {
        int totalPages = 10;
        int currentPage = 1;
        int resultsPerPage = 1;
        String cursor = "fakeCursor";

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Cannot set both current page and next cursor");

        new PageInfo.Builder()
                .withTotalPages(totalPages)
                .withNextCursor(cursor)
                .withCurrentPage(currentPage)
                .withResultsPerPage(resultsPerPage)
                .build();
    }

    @Test
    public void settingNullNextCursorThrowsException() throws Exception {
        int totalPages = 10;
        int currentPage = 1;
        int resultsPerPage = 1;
        String cursor = null;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Next cursor cannot be null or empty");

        new PageInfo.Builder()
                .withTotalPages(totalPages)
                .withNextCursor(cursor)
                .withCurrentPage(currentPage)
                .withResultsPerPage(resultsPerPage)
                .build();
    }

    @Test
    public void validResultsPerRegularPage() throws Exception {
        int totalPages = 1;
        int currentPage = 0;
        int resultsPerPage = 1;

        PageInfo pageInfo = new PageInfo.Builder()
                .withTotalPages(totalPages)
                .withCurrentPage(currentPage)
                .withResultsPerPage(resultsPerPage)
                .build();

        assertThat(pageInfo.getTotal(), is(totalPages));
        assertThat(pageInfo.getCurrent(), is(currentPage));
        assertThat(pageInfo.getNextCursor(), is(nullValue()));
        assertThat(pageInfo.getResultsPerPage(), is(resultsPerPage));
    }

    @Test
    public void validResultsPerCursorPage() throws Exception {
        int totalPages = 1;
        String cursor = "fakeCursor";
        int resultsPerPage = 1;

        PageInfo pageInfo = new PageInfo.Builder()
                .withTotalPages(totalPages)
                .withNextCursor(cursor)
                .withResultsPerPage(resultsPerPage)
                .build();

        assertThat(pageInfo.getTotal(), is(totalPages));
        assertThat(pageInfo.getNextCursor(), is(cursor));
        assertThat(pageInfo.getCurrent(), is(PageInfo.CURSOR_PAGE_NUMBER));
        assertThat(pageInfo.getResultsPerPage(), is(resultsPerPage));
    }
}