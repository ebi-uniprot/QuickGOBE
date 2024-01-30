package uk.ac.ebi.quickgo.rest.search.query;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests the {@link SortField} implementation
 *
 * Created 16/01/17
 * @author Edd
 */
class SortFieldTest {
    @Test
    void nullFieldThrowsException()  {
        assertThrows(IllegalArgumentException.class, () -> new SortField(null));
    }

    @Test
    void emptyFieldThrowsException()  {
        assertThrows(IllegalArgumentException.class, () -> new SortField(""));
    }

    @Test
    void createSortField()  {
        String sortField = "field";
        SortField sort = new SortField(sortField);

        assertThat(sort.getField(), is(equalTo(sortField)));
    }
}