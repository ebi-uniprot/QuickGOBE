package uk.ac.ebi.quickgo.graphics.ontology;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.ebi.quickgo.graphics.model.GraphImageLayout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Created 04/10/16
 * @author Edd
 */
class GraphImageResultTest {

    private String validDescription;
    private GraphImage validGraphImage;
    private GraphImageLayout validGraphImageLayout;

    @BeforeEach
    void setUp() {
        validDescription = "Valid description";
        validGraphImage = new GraphImage("stub graph image", null);
        validGraphImageLayout = new GraphImageLayout();
    }

    @Test
    void nullDescriptionCausesException() {
        assertThrows(IllegalArgumentException.class, () -> new GraphImageResult(null, validGraphImage, validGraphImageLayout));
    }

    @Test
    void emptyDescriptionCausesException() {
        assertThrows(IllegalArgumentException.class, () -> new GraphImageResult("", validGraphImage, validGraphImageLayout));
    }

    @Test
    void nullGraphImageCausesException() {
        assertThrows(IllegalArgumentException.class, () -> new GraphImageResult(validDescription, null, validGraphImageLayout));
    }

    @Test
    void nullGraphImageLayoutCausesException() {
        assertThrows(IllegalArgumentException.class, () -> new GraphImageResult(validDescription, validGraphImage, null));
    }

    @Test
    void canCreateValidGraphImageResult() {
        GraphImageResult graphImageResult =
                new GraphImageResult(validDescription, validGraphImage, validGraphImageLayout);
        assertThat(graphImageResult, is(notNullValue()));
    }
}