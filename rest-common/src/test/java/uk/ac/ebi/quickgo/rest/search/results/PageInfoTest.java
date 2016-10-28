package uk.ac.ebi.quickgo.rest.search.results;

import uk.ac.ebi.quickgo.rest.QuickGoIndexOutOfBoundsException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
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
        thrown.expectMessage(startsWith("Total number of pages can not be negative"));

        new PageInfo(totalPages, currentPage, resultsPerPage);
    }

    @Test
    public void negativeCurrentPageThrowsException() throws Exception {
        int totalPages = 1;
        int currentPage = -1;
        int resultsPerPage = 1;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(startsWith("Current page can not be negative"));

        new PageInfo(totalPages, currentPage, resultsPerPage);
    }

    @Test
    public void negativeResultsPerPageThrowsException() throws Exception {
        int totalPages = 1;
        int currentPage = 0;
        int resultsPerPage = -1;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(startsWith("Results per page can not be less than 0"));

        new PageInfo(totalPages, currentPage, resultsPerPage);
    }

    @Test
    public void currentPageLargerThanTotalPagesThrowsException() throws Exception {
        int totalPages = 1;
        int currentPage = totalPages + 1;
        int resultsPerPage = 1;

        thrown.expect(QuickGoIndexOutOfBoundsException.class);
        thrown.expectMessage(startsWith("Current page can not be greater than total amount ofTAXON_ID_FILTER"));

        new PageInfo(totalPages, currentPage, resultsPerPage);
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