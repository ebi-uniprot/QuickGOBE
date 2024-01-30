package uk.ac.ebi.quickgo.rest.search.query;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

/**
 * Tests the behaviour of the {@link ContainsFieldQuery}.
 */
@ExtendWith(MockitoExtension.class)
class ContainsFieldQueryTest {
    @Mock
    private uk.ac.ebi.quickgo.rest.search.query.QueryVisitor visitor;

    @Test
    void nullFieldThrowsException() {
        String field = null;
        String value = "value";
        assertThrows(IllegalArgumentException.class, () -> new ContainsFieldQuery(field, value));
    }

    @Test
    void emptyFieldThrowsException() {
        String field = "";
        String value = "value";
        assertThrows(IllegalArgumentException.class, () -> new ContainsFieldQuery(field, value));
    }

    @Test
    void nullValueThrowsException() {
        String field = "field";
        String value = null;
        assertThrows(IllegalArgumentException.class, () -> new ContainsFieldQuery(field, value));
    }

    @Test
    void emptyValueThrowsException() {
        String field = "field";
        String value = "";
        assertThrows(IllegalArgumentException.class, () -> new ContainsFieldQuery(field, value));
    }

    @Test
    void createFieldAndValueQuery() {
        String field = "field";
        String value = "myName";

        ContainsFieldQuery query = new ContainsFieldQuery(field, value);

        assertThat(query.field(), is(equalTo(field)));
        assertThat(query.value(), is(equalTo(value)));
    }

    @Test
    void visitorIsCalledCorrectly() {
        ContainsFieldQuery
                query = new ContainsFieldQuery("field1", "value1");
        query.accept(visitor);
        verify(visitor).visit(query);
    }
}
