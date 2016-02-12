package uk.ac.ebi.quickgo.common.search.query;

import org.hamcrest.core.Is;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Created 11/02/16
 * @author Edd
 */
public class FieldHighlightTest {
    @Test(expected = IllegalArgumentException.class)
    public void nullFieldThrowsException() throws Exception {
        new FieldHighlight(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyFieldThrowsException() throws Exception {
        new FieldHighlight("");
    }

    @Test
    public void createFieldHighlight() throws Exception {
        String field = "field";
        FieldHighlight fieldHighlight = new FieldHighlight(field);

        assertThat(fieldHighlight.getField(), Is.is(equalTo(field)));
    }
}