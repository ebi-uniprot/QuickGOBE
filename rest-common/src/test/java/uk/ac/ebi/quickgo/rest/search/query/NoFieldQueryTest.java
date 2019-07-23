package uk.ac.ebi.quickgo.rest.search.query;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;

/**
 * Tests the behaviour of the {@link NoFieldQuery} implementation
 */
@RunWith(MockitoJUnitRunner.class)
public class NoFieldQueryTest {
    @Mock
    private uk.ac.ebi.quickgo.rest.search.query.QueryVisitor visitor;

    @Test(expected = IllegalArgumentException.class)
    public void nullArgumentValueInConstructorThrowsException() throws Exception {
        new uk.ac.ebi.quickgo.rest.search.query.NoFieldQuery(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyArgumentValueInConstructorThrowsException() throws Exception {
        new uk.ac.ebi.quickgo.rest.search.query.NoFieldQuery("");
    }

    @Test
    public void valueIsSameAsGivenInConstructor() throws Exception {
        String value = "value1";
        uk.ac.ebi.quickgo.rest.search.query.NoFieldQuery
                query = new uk.ac.ebi.quickgo.rest.search.query.NoFieldQuery(value);

        assertThat(query.getValue(), is(value));
    }

    @Test
    public void visitorIsCalledCorrectly() throws Exception {
        uk.ac.ebi.quickgo.rest.search.query.NoFieldQuery
                query = new uk.ac.ebi.quickgo.rest.search.query.NoFieldQuery("value1");

        query.accept(visitor);

        verify(visitor).visit(query);
    }
}
