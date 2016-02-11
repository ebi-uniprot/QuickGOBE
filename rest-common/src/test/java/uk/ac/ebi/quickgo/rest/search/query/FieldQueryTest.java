package uk.ac.ebi.quickgo.rest.search.query;

import uk.ac.ebi.quickgo.rest.search.query.FieldQuery;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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

        new uk.ac.ebi.quickgo.rest.search.query.FieldQuery(field, value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void FieldThrowsException() throws Exception {
        String field = null;
        String value = "value";

        new uk.ac.ebi.quickgo.rest.search.query.FieldQuery(field, value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullValueThrowsException() throws Exception {
        String field = "field";
        String value = null;

        new uk.ac.ebi.quickgo.rest.search.query.FieldQuery(field, value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyValueThrowsException() throws Exception {
        String field = "field";
        String value = "";

        new uk.ac.ebi.quickgo.rest.search.query.FieldQuery(field, value);
    }

    @Test
    public void createFieldAndValueQuery() throws Exception {
        String field = "field";
        String value = "myName";

        uk.ac.ebi.quickgo.rest.search.query.FieldQuery
                query = new uk.ac.ebi.quickgo.rest.search.query.FieldQuery(field, value);

        assertThat(query.field(), is(equalTo(field)));
        assertThat(query.value(), is(equalTo(value)));
    }

    @Test
    public void visitorIsCalledCorrectly() throws Exception {
        uk.ac.ebi.quickgo.rest.search.query.FieldQuery
                query = new uk.ac.ebi.quickgo.rest.search.query.FieldQuery("field1", "value1");
        query.accept(visitor);
        verify(visitor).visit(query);
    }
}
