package uk.ac.ebi.quickgo.rest.search.query;

import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Created 11/02/16
 * @author Edd
 */
class FieldProjectionTest {
    @Test
    void nullFieldThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new FieldProjection(null));
    }

    @Test
    void emptyFieldThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new FieldProjection(""));
    }

    @Test
    void createFieldProjection() {
        String field = "field";
        FieldProjection fieldProjection = new FieldProjection(field);

        assertThat(fieldProjection.getField(), Is.is(equalTo(field)));
    }
}