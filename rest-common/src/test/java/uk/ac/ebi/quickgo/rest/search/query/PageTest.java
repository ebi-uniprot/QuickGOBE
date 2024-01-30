package uk.ac.ebi.quickgo.rest.search.query;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests the {@link Page} implementation
 */
@ExtendWith(MockitoExtension.class)
class PageTest {

    @Test
    void canCreateConcretePage() {
        int pageSize = 12;
        ConcretePage page = new ConcretePage(pageSize);

        assertThat(page.getPageSize(), is(pageSize));
    }

    @Test
    void negativePageSizeCausesException() {
        assertThrows(IllegalArgumentException.class, () -> new ConcretePage(-1));
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
