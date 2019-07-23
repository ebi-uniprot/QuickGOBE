package uk.ac.ebi.quickgo.rest.search.query;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;

/**
 * Tests the behaviour of the {@link FieldQuery}.
 */
@RunWith(MockitoJUnitRunner.class)
public class FieldQueryTest {
    @Mock
    private uk.ac.ebi.quickgo.rest.search.query.QueryVisitor visitor;

    @Test(expected = IllegalArgumentException.class)
    public void nullFieldThrowsException() throws Exception {
        String field = null;
        String value = "value";

        new FieldQuery(field, value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void FieldThrowsException() throws Exception {
        String field = null;
        String value = "value";

        new FieldQuery(field, value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullValueThrowsException() throws Exception {
        String field = "field";
        String value = null;

        new FieldQuery(field, value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyValueThrowsException() throws Exception {
        String field = "field";
        String value = "";

        new FieldQuery(field, value);
    }

    @Test
    public void createFieldAndValueQuery() throws Exception {
        String field = "field";
        String value = "myName";

        FieldQuery
                query = new FieldQuery(field, value);

        assertThat(query.field(), is(equalTo(field)));
        assertThat(query.value(), is(equalTo(value)));
    }

    @Test
    public void visitorIsCalledCorrectly() throws Exception {
        FieldQuery
                query = new FieldQuery("field1", "value1");
        query.accept(visitor);
        verify(visitor).visit(query);
    }
}
