package uk.ac.ebi.quickgo.rest.search.query;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Tests the {@link Page} implementation
 */
@RunWith(MockitoJUnitRunner.class)
public class PageTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void negativePageResultSizeThrowsException() throws Exception {
        int pageNumber = 1;
        int pageSize = -1;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Page result size cannot be less than 0");

//        new Page(pageNumber, pageSize);
    }

    @Test
    public void zeroPageResultSizeIsAccepted() throws Exception {
        int pageNumber = 1;
        int pageSize = 0;

//        Page page = new Page(pageNumber, pageSize);
//
//        assertThat(page.getPageNumber(), is(pageNumber));
//        assertThat(page.getPageSize(), is(pageSize));
    }

    @Test
    public void createPage() throws Exception {
        int pageNumber = 3;
        int pageSize = 5;

//        Page page = new Page(pageNumber, pageSize);
//
//        assertThat(page.getPageNumber(), is(pageNumber));
//        assertThat(page.getPageSize(), is(pageSize));
    }
}
