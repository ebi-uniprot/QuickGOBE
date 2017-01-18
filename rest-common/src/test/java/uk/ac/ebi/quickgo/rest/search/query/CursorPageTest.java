package uk.ac.ebi.quickgo.rest.search.query;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static uk.ac.ebi.quickgo.rest.search.query.CursorPage.*;

/**
 * Created by edd on 15/01/2017.
 */
public class CursorPageTest {
    @Test
    public void canCreateInitialCursorPage() {
        int pageSize = 4;
        CursorPage page = createFirstCursorPage(pageSize);

        assertThat(page.getCursor(), is(FIRST_CURSOR));
        assertThat(page.getPageSize(), is(pageSize));
    }

    @Test
    public void canCreateSubsequentCursorPage() {
        int pageSize = 4;
        String cursor = "fakeCursor";
        CursorPage page = createCursorPage(cursor, pageSize);

        assertThat(page.getCursor(), is(cursor));
        assertThat(page.getPageSize(), is(pageSize));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullCursorCausesException() {
        createCursorPage(null, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyCursorCausesException() {
        createCursorPage("", 1);
    }
}