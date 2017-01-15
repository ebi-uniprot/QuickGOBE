package uk.ac.ebi.quickgo.rest.search.query;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by edd on 15/01/2017.
 */
public class RegularPageTest {
    @Test
    public void canCreateRegularPage() {
        int currentPage = 1;
        int pageSize = 2;
        RegularPage page = new RegularPage(currentPage, pageSize);

        assertThat(page.getPageNumber(), is(currentPage));
        assertThat(page.getPageSize(), is(pageSize));
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativePageNumberCausesException() {
        new RegularPage(-1, 10);
    }
}