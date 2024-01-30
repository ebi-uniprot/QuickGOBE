package uk.ac.ebi.quickgo.rest.search.query;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.ac.ebi.quickgo.rest.search.query.CursorPage.*;

/**
 * Created by edd on 15/01/2017.
 */
class CursorPageTest {
    @Test
    void canCreateInitialCursorPage() {
        int pageSize = 4;
        CursorPage page = createFirstCursorPage(pageSize);

        assertThat(page.getCursor(), is(FIRST_CURSOR));
        assertThat(page.getPageSize(), is(pageSize));
    }

    @Test
    void canCreateSubsequentCursorPage() {
        int pageSize = 4;
        String cursor = "fakeCursor";
        CursorPage page = createCursorPage(cursor, pageSize);

        assertThat(page.getCursor(), is(cursor));
        assertThat(page.getPageSize(), is(pageSize));
    }

    @Test
    void nullCursorCausesException() {
        assertThrows(IllegalArgumentException.class, () -> createCursorPage(null, 1));
    }

    @Test
    void emptyCursorCausesException() {
        assertThrows(IllegalArgumentException.class, () -> createCursorPage("", 1));
    }
}