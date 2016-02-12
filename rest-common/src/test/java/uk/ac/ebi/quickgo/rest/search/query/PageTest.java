package uk.ac.ebi.quickgo.rest.search.query;

import uk.ac.ebi.quickgo.rest.search.query.Page;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Tests the {@link Page} implementation
 */
@RunWith(MockitoJUnitRunner.class)
public class PageTest {
    @Mock
    private uk.ac.ebi.quickgo.rest.search.query.QueryVisitor visitorMock;

    @Test(expected = IllegalArgumentException.class)
    public void negativePageNumberThrowsException() throws Exception {
        int pageNumber = -1;
        int pageSize = 1;

        new Page(pageNumber, pageSize);
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativePageResultSizeThrowsException() throws Exception {
        int pageNumber = 0;
        int pageSize = -1;

        new Page(pageNumber, pageSize);
    }

    @Test(expected = IllegalArgumentException.class)
    public void zeroPageResultSizeThrowsException() throws Exception {
        int pageNumber = 0;
        int pageSize = 0;

        new Page(pageNumber, pageSize);
    }

    @Test
    public void createPage() throws Exception {
        int pageNumber = 3;
        int pageSize = 5;

        Page page = new Page(pageNumber, pageSize);

        assertThat(page.getPageNumber(), is(pageNumber));
        assertThat(page.getPageSize(), is(pageSize));
    }
}
