package uk.ac.ebi.quickgo.repo.solr.query;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;

/**
 * Tests the behaviour of the {@link NoFieldQuery} implementation
 */
@RunWith(MockitoJUnitRunner.class)
public class NoFieldQueryTest {
    @Mock
    private QueryVisitor visitor;

    @Test(expected = IllegalArgumentException.class)
    public void nullArgumentValueInConstructorThrowsException() throws Exception {
        new NoFieldQuery(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyArgumentValueInConstructorThrowsException() throws Exception {
        new NoFieldQuery("");
    }

    @Test
    public void valueIsSameAsGivenInConstructor() throws Exception {
        String value = "value1";
        NoFieldQuery query = new NoFieldQuery(value);

        assertThat(query.getValue(), is(value));
    }

    @Test
    public void visitorIsCalledCorrectly() throws Exception {
        NoFieldQuery query = new NoFieldQuery("value1");

        query.accept(visitor);

        verify(visitor).visit(query);
    }
}
