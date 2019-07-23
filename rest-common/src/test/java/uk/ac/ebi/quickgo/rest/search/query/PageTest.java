package uk.ac.ebi.quickgo.rest.search.query;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Tests the {@link Page} implementation
 */
@RunWith(MockitoJUnitRunner.class)
public class PageTest {

    @Test
    public void canCreateConcretePage() {
        int pageSize = 12;
        ConcretePage page = new ConcretePage(pageSize);

        assertThat(page.getPageSize(), is(pageSize));
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativePageSizeCausesException() {
        new ConcretePage(-1);
    }

    private static class ConcretePage extends Page {
        ConcretePage(int pageSize) {
            super(pageSize); // defer constructor behaviour to parent class
        }

        @Override
        public <V> void accept(PageVisitor<V> visitor, V subject) {
            // no-op for test
        }
    }
}
