package uk.ac.ebi.quickgo.rest.search.query;

import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;

/**
 * Tests the behaviour of the {@link AllNonEmptyFieldQuery}
 * @author Tony Wardell
 * Date: 30/08/2017
 * Time: 13:35
 * Created with IntelliJ IDEA.
 */
@RunWith(MockitoJUnitRunner.class)
public class AllNonEmptyFieldQueryTest {
    @Mock
    private uk.ac.ebi.quickgo.rest.search.query.QueryVisitor visitor;

    @Test(expected = IllegalArgumentException.class)
    public void nullFieldThrowsException() throws Exception {
        String field = null;
        String value = "value";

        new AllNonEmptyFieldQuery(field, value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void FieldThrowsException() throws Exception {
        String field = null;
        String value = "value";

        new AllNonEmptyFieldQuery(field, value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullValueThrowsException() throws Exception {
        String field = "field";
        String value = null;

        new AllNonEmptyFieldQuery(field, value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyValueThrowsException() throws Exception {
        String field = "field";
        String value = "";

        new AllNonEmptyFieldQuery(field, value);
    }

    @Test
    public void createFieldAndValueQuery() throws Exception {
        String field = "field";
        String value = "myName";

        AllNonEmptyFieldQuery query = new AllNonEmptyFieldQuery(field, value);

        MatcherAssert.assertThat(query.field(), is(equalTo(field)));
        MatcherAssert.assertThat(query.value(), is(equalTo(value)));
    }

    @Test
    public void equalsAndHashcodeComparisonBetweenFieldQueryAndAllNonEmptyFieldQueryDoNotMatch() throws Exception {
        String field = "field";
        String value = "myName";

        AllNonEmptyFieldQuery allNonEmptyFieldQuery = new AllNonEmptyFieldQuery(field, value);
        FieldQuery fieldQuery = new FieldQuery(field, value);

        MatcherAssert.assertThat(allNonEmptyFieldQuery, is(not(equalTo(fieldQuery))));
        MatcherAssert.assertThat(fieldQuery, is(not(equalTo(allNonEmptyFieldQuery))));
        MatcherAssert.assertThat(allNonEmptyFieldQuery.hashCode(), is(not(equalTo(fieldQuery.hashCode()))));
    }


    @Test
    public void visitorIsCalledCorrectly() throws Exception {
        AllNonEmptyFieldQuery query = new AllNonEmptyFieldQuery("field1", "value1");
        query.accept(visitor);
        verify(visitor).visit(query);
    }
}
