package uk.ac.ebi.quickgo.rest.search.query;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static uk.ac.ebi.quickgo.rest.search.query.PageFactory.CURSOR_PAGE_NUMBER;
import static uk.ac.ebi.quickgo.rest.search.query.PageFactory.createCursorPage;
import static uk.ac.ebi.quickgo.rest.search.query.PageFactory.createPage;

/**
 * Tests the creation of instances of {@link Page} through the {@link PageFactory} class.
 *
 * Created 09/01/17
 * @author Edd
 */
@RunWith(HierarchicalContextRunner.class)
public class PageFactoryTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    public class StandardPageTests {
        @Test
        public void negativePageNumberThrowsException() throws Exception {
            int pageNumber = -1;
            int pageSize = 1;

            thrown.expect(IllegalArgumentException.class);
            thrown.expectMessage("Page number must be greater than 0");

            createPage(pageNumber, pageSize);
        }

        @Test
        public void zeroPageNumberThrowsException() throws Exception {
            int pageNumber = 0;
            int pageSize = 1;

            thrown.expect(IllegalArgumentException.class);
            thrown.expectMessage("Page number must be greater than 0");

            createPage(pageNumber, pageSize);
        }

        @Test
        public void negativePageResultSizeThrowsException() throws Exception {
            int pageNumber = 1;
            int pageSize = -1;

            thrown.expect(IllegalArgumentException.class);
            thrown.expectMessage("Page result size cannot be less than 0");

            createPage(pageNumber, pageSize);
        }

        @Test
        public void zeroPageResultSizeIsAccepted() throws Exception {
            int pageNumber = 1;
            int pageSize = 0;

            Page page = createPage(pageNumber, pageSize);

            assertThat(page.getPageNumber(), is(pageNumber));
            assertThat(page.getPageSize(), is(pageSize));
        }

        @Test
        public void canCreatePage() throws Exception {
            int pageNumber = 3;
            int pageSize = 5;

            Page page = createPage(pageNumber, pageSize);

            assertThat(page.getPageNumber(), is(pageNumber));
            assertThat(page.getPageSize(), is(pageSize));
        }
    }

    public class CursorPageTests {
        @Test
        public void canCreatePage() throws Exception {
            int pageSize = 5;

            Page page = createCursorPage(pageSize);

            assertThat(page.getPageNumber(), is(CURSOR_PAGE_NUMBER));
            assertThat(page.getPageSize(), is(pageSize));
        }

        @Test
        public void zeroPageResultSizeIsAccepted() throws Exception {
            int pageSize = 0;

            Page page = createCursorPage(pageSize);

            assertThat(page.getPageNumber(), is(CURSOR_PAGE_NUMBER));
            assertThat(page.getPageSize(), is(pageSize));
        }

        @Test
        public void negativePageResultSizeThrowsException() throws Exception {
            int pageSize = -1;

            thrown.expect(IllegalArgumentException.class);
            thrown.expectMessage("Page result size cannot be less than 0");

            createCursorPage(pageSize);
        }
    }
}