package uk.ac.ebi.quickgo.rest.search.results;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the {@link PageInfo} implementation.
 */
class PageInfoTest {

    @Test
    void negativeTotalNumberThrowsException() {
        int totalPages = -1;
        int currentPage = 0;
        int resultsPerPage = 1;
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new PageInfo.Builder()
                .withTotalPages(totalPages)
                .withCurrentPage(currentPage)
                .withResultsPerPage(resultsPerPage)
                .build());
        assertTrue(exception.getMessage().contains("Total number of pages cannot be negative"));
    }

    @Test
    void negativeCurrentPageThrowsException() {
        int totalPages = 1;
        int currentPage = -1;
        int resultsPerPage = 1;
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new PageInfo.Builder()
                .withTotalPages(totalPages)
                .withCurrentPage(currentPage)
                .withResultsPerPage(resultsPerPage)
                .build());
        assertTrue(exception.getMessage().contains("Current page cannot be negative"));
    }

    @Test
    void negativeResultsPerPageThrowsException() {
        int totalPages = 1;
        int currentPage = 0;
        int resultsPerPage = -1;
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new PageInfo.Builder()
                .withTotalPages(totalPages)
                .withCurrentPage(currentPage)
                .withResultsPerPage(resultsPerPage)
                .build());
        assertTrue(exception.getMessage().contains("Results per page cannot be less than 0"));
    }

    @Test
    void currentPageLargerThanTotalPagesThrowsException() {
        int totalPages = 1;
        int currentPage = 2;
        int resultsPerPage = 1;
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new PageInfo.Builder()
                .withTotalPages(totalPages)
                .withCurrentPage(currentPage)
                .withResultsPerPage(resultsPerPage)
                .build());
        assertTrue(exception.getMessage().contains("Current page cannot be larger than total amount of pages"));
    }

    @Test
    void settingNextCursorAndCurrentPageThrowsException() {
        int totalPages = 10;
        int currentPage = 1;
        int resultsPerPage = 1;
        String cursor = "fakeCursor";
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new PageInfo.Builder()
                .withTotalPages(totalPages)
                .withCurrentPage(currentPage)
                .withNextCursor(cursor)
                .withResultsPerPage(resultsPerPage)
                .build());
        assertTrue(exception.getMessage().contains("Cannot set both next cursor and current page"));
    }

    @Test
    void settingCurrentPageAndNextCursorThrowsException() {
        int totalPages = 10;
        int currentPage = 1;
        int resultsPerPage = 1;
        String cursor = "fakeCursor";
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new PageInfo.Builder()
                .withTotalPages(totalPages)
                .withNextCursor(cursor)
                .withCurrentPage(currentPage)
                .withResultsPerPage(resultsPerPage)
                .build());
        assertTrue(exception.getMessage().contains("Cannot set both current page and next cursor"));
    }

    @Test
    void settingNullNextCursorThrowsException() {
        int totalPages = 10;
        int currentPage = 1;
        int resultsPerPage = 1;
        String cursor = null;
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new PageInfo.Builder()
                .withTotalPages(totalPages)
                .withNextCursor(cursor)
                .withCurrentPage(currentPage)
                .withResultsPerPage(resultsPerPage)
                .build());
        assertTrue(exception.getMessage().contains("Next cursor cannot be null or empty"));
    }

    @Test
    void validResultsPerRegularPage() {
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
    void validResultsPerCursorPage() {
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