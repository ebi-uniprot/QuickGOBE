package uk.ac.ebi.quickgo.rest.search.query;

import org.hamcrest.core.Is;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Created 11/02/16
 * @author Edd
 */
public class FieldProjectionTest {
    @Test(expected = IllegalArgumentException.class)
    public void nullFieldThrowsException() throws Exception {
        new FieldProjection(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyFieldThrowsException() throws Exception {
        new FieldProjection("");
    }

    @Test
    public void createFieldProjection() throws Exception {
        String field = "field";
        FieldProjection fieldProjection = new FieldProjection(field);

        assertThat(fieldProjection.getField(), Is.is(equalTo(field)));
    }
}