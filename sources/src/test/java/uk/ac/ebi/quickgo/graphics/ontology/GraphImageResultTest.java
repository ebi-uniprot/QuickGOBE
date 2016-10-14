package uk.ac.ebi.quickgo.graphics.ontology;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Created 04/10/16
 * @author Edd
 */
public class GraphImageResultTest {

    private String validDescription;
    private GraphImage validGraphImage;

    @Before
    public void setUp() {
        validDescription = "Valid description";
        validGraphImage = new GraphImage("stub graph image");
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullDescriptionCausesException() {
        new GraphImageResult(null, validGraphImage);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyDescriptionCausesException() {
        new GraphImageResult("", validGraphImage);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullGraphImageCausesException() {
        new GraphImageResult(validDescription, null);
    }

    @Test
    public void canCreateValidGraphImageResult() {
        GraphImageResult graphImageResult = new GraphImageResult(validDescription, validGraphImage);
        assertThat(graphImageResult, is(notNullValue()));
    }
}