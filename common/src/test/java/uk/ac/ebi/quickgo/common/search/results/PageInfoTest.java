package uk.ac.ebi.quickgo.common.search.results;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Tests the {@link PageInfo} implementation.
 */
public class PageInfoTest {
    @Test
    public void negativeTotalNumberThrowsException() throws Exception {
        int totalPages = -1;
        int currentPage = 0;
        int resultsPerPage = 1;

        try {
            new PageInfo(totalPages, currentPage, resultsPerPage);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), startsWith("Total number of pages can not be negative"));
        }
    }

    @Test
    public void negativeCurrentPageThrowsException() throws Exception {
        int totalPages = 1;
        int currentPage = -1;
        int resultsPerPage = 1;

        try {
            new PageInfo(totalPages, currentPage, resultsPerPage);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), startsWith("Current page can not be negative"));
        }
    }

    @Test
    public void negativeResultsPerPageThrowsException() throws Exception {
        int totalPages = 1;
        int currentPage = 0;
        int resultsPerPage = -1;

        try {
            new PageInfo(totalPages, currentPage, resultsPerPage);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), startsWith("Results per page can not be less than 1"));
        }
    }

    @Test
    public void zeroResultsPerPageThrowsException() throws Exception {
        int totalPages = 1;
        int currentPage = 0;
        int resultsPerPage = -1;

        try {
            new PageInfo(totalPages, currentPage, resultsPerPage);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), startsWith("Results per page can not be less than 1"));
        }
    }

    @Test
    public void currentPageLargerThanTotalPagesThrowsException() throws Exception {
        int totalPages = 1;
        int currentPage = 2;
        int resultsPerPage = 1;

        try {
            new PageInfo(totalPages, currentPage, resultsPerPage);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), startsWith("Current page can not be larger than total amount of pages"));
        }


    }

    @Test
    public void validResultsPerPage() throws Exception {
        int totalPages = 1;
        int currentPage = 0;
        int resultsPerPage = 1;

        PageInfo pageInfo = new PageInfo(totalPages, currentPage, resultsPerPage);

        assertThat(pageInfo.getTotal(), is(totalPages));
        assertThat(pageInfo.getCurrent(), is(currentPage));
        assertThat(pageInfo.getResultsPerPage(), is(resultsPerPage));
    }
}

