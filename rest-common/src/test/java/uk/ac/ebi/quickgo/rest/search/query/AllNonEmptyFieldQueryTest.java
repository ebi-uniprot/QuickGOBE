package uk.ac.ebi.quickgo.rest.search.query;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

/**
 * Tests the behaviour of the {@link AllNonEmptyFieldQuery}
 * @author Tony Wardell
 * Date: 30/08/2017
 * Time: 13:35
 * Created with IntelliJ IDEA.
 */
@ExtendWith(MockitoExtension.class)
class AllNonEmptyFieldQueryTest {
    @Mock
    private uk.ac.ebi.quickgo.rest.search.query.QueryVisitor visitor;

    @Test
    void nullFieldThrowsException() {
        String field = null;
        String value = "value";
        assertThrows(IllegalArgumentException.class, () -> new AllNonEmptyFieldQuery(field, value));
    }

    @Test
    void FieldThrowsException() {
        String field = null;
        String value = "value";
        assertThrows(IllegalArgumentException.class, () -> new AllNonEmptyFieldQuery(field, value));
    }

    @Test
    void nullValueThrowsException() {
        String field = "field";
        String value = null;
        assertThrows(IllegalArgumentException.class, () -> new AllNonEmptyFieldQuery(field, value));
    }

    @Test
    void emptyValueThrowsException() {
        String field = "field";
        String value = "";
        assertThrows(IllegalArgumentException.class, () -> new AllNonEmptyFieldQuery(field, value));
    }

    @Test
    void createFieldAndValueQuery() {
        String field = "field";
        String value = "myName";

        AllNonEmptyFieldQuery query = new AllNonEmptyFieldQuery(field, value);

        assertThat(query.field(), is(equalTo(field)));
        assertThat(query.value(), is(equalTo(value)));
    }

    @Test
    void equalsAndHashcodeComparisonBetweenFieldQueryAndAllNonEmptyFieldQueryDoNotMatch() {
        String field = "field";
        String value = "myName";

        AllNonEmptyFieldQuery allNonEmptyFieldQuery = new AllNonEmptyFieldQuery(field, value);
        FieldQuery fieldQuery = new FieldQuery(field, value);

        assertThat(allNonEmptyFieldQuery, is(not(equalTo(fieldQuery))));
        assertThat(fieldQuery, is(not(equalTo(allNonEmptyFieldQuery))));
        assertThat(allNonEmptyFieldQuery.hashCode(), is(not(equalTo(fieldQuery.hashCode()))));
    }


    @Test
    void visitorIsCalledCorrectly() {
        AllNonEmptyFieldQuery query = new AllNonEmptyFieldQuery("field1", "value1");
        query.accept(visitor);
        verify(visitor).visit(query);
    }
}
