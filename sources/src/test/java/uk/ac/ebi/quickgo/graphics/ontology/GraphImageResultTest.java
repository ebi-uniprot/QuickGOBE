package uk.ac.ebi.quickgo.graphics.ontology;

import uk.ac.ebi.quickgo.graphics.model.GraphImageLayout;

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
    private GraphImageLayout validGraphImageLayout;

    @Before
    public void setUp() {
        validDescription = "Valid description";
        validGraphImage = new GraphImage("stub graph image", null);
        validGraphImageLayout = new GraphImageLayout();
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullDescriptionCausesException() {
        new GraphImageResult(null, validGraphImage, validGraphImageLayout);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyDescriptionCausesException() {
        new GraphImageResult("", validGraphImage, validGraphImageLayout);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullGraphImageCausesException() {
        new GraphImageResult(validDescription, null, validGraphImageLayout);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullGraphImageLayoutCausesException() {
        new GraphImageResult(validDescription, validGraphImage, null);
    }

    @Test
    public void canCreateValidGraphImageResult() {
        GraphImageResult graphImageResult =
                new GraphImageResult(validDescription, validGraphImage, validGraphImageLayout);
        assertThat(graphImageResult, is(notNullValue()));
    }
}