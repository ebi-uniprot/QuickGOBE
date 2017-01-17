package uk.ac.ebi.quickgo.rest.search.query;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

/**
 * Tests the {@link SortField} implementation
 *
 * Created 16/01/17
 * @author Edd
 */
public class SortFieldTest {
    @Test(expected = IllegalArgumentException.class)
    public void nullFieldThrowsException() throws Exception {
        new SortField(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyFieldThrowsException() throws Exception {
        new SortField("");
    }

    @Test
    public void createSortField() throws Exception {
        String sortField = "field";
        SortField sort = new SortField(sortField);

        assertThat(sort.getField(), is(equalTo(sortField)));
    }
}