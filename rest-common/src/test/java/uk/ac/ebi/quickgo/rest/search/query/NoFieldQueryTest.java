package uk.ac.ebi.quickgo.rest.search.query;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

/**
 * Tests the behaviour of the {@link NoFieldQuery} implementation
 */
@ExtendWith(MockitoExtension.class)
class NoFieldQueryTest {
    @Mock
    private uk.ac.ebi.quickgo.rest.search.query.QueryVisitor visitor;

    @Test
    void nullArgumentValueInConstructorThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new NoFieldQuery(null));
    }

    @Test
    void emptyArgumentValueInConstructorThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new NoFieldQuery(""));
    }

    @Test
    void valueIsSameAsGivenInConstructor() {
        String value = "value1";
        uk.ac.ebi.quickgo.rest.search.query.NoFieldQuery
                query = new uk.ac.ebi.quickgo.rest.search.query.NoFieldQuery(value);

        assertThat(query.getValue(), is(value));
    }

    @Test
    void visitorIsCalledCorrectly() {
        uk.ac.ebi.quickgo.rest.search.query.NoFieldQuery
                query = new uk.ac.ebi.quickgo.rest.search.query.NoFieldQuery("value1");

        query.accept(visitor);

        verify(visitor).visit(query);
    }
}
