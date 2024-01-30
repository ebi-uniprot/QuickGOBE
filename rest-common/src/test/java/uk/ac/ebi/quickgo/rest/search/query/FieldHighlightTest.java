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
class FieldHighlightTest {
    @Test
    void nullFieldThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new FieldHighlight(null));
    }

    @Test
    void emptyFieldThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new FieldHighlight(""));
    }

    @Test
    void createFieldHighlight() {
        String field = "field";
        FieldHighlight fieldHighlight = new FieldHighlight(field);

        assertThat(fieldHighlight.getField(), Is.is(equalTo(field)));
    }
}