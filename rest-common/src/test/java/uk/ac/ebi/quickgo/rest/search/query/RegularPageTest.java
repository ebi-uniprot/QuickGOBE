package uk.ac.ebi.quickgo.rest.search.query;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Created by edd on 15/01/2017.
 */
class RegularPageTest {
    @Test
    void canCreateRegularPage() {
        int currentPage = 1;
        int pageSize = 2;
        RegularPage page = new RegularPage(currentPage, pageSize);

        assertThat(page.getPageNumber(), is(currentPage));
        assertThat(page.getPageSize(), is(pageSize));
    }

    @Test
    void negativePageNumberCausesException() {
        assertThrows(IllegalArgumentException.class, () -> new RegularPage(-1, 10));
    }
}