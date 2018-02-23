package uk.ac.ebi.quickgo.graphics.ontology;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * The GraphPresentation is a data class, test it's creation, the setting of defaults.
 * @author Tony Wardell
 * Date: 13/02/2018
 * Time: 15:17
 * Created with IntelliJ IDEA.
 */
public class GraphPresentationTest {

    @Test
    public void usingBuilderWithNoArgumentsUsesDefaults() {
        GraphPresentation.Builder builder = new GraphPresentation.Builder();
        GraphPresentation graphPresentation = builder.build();
        assertThat(GraphPresentation.defaultShowKey, is(equalTo(graphPresentation.key)));
        assertThat(GraphPresentation.defaultShowTermIds, is(equalTo(graphPresentation.termIds)));
        assertThat(GraphPresentation.defaultWidth, is(equalTo(graphPresentation.width)));
        assertThat(GraphPresentation.defaultHeight, is(equalTo(graphPresentation.height)));
        assertThat(GraphPresentation.defaultShowSlimColours, is(equalTo(graphPresentation.subsetColours)));
        assertThat(GraphPresentation.defaultShowChildren, is(equalTo(graphPresentation.showChildren)));
    }

    @Test
    public void suppliedArgumentsAreUsed() {
        GraphPresentation.Builder builder = new GraphPresentation.Builder();
        builder.showKey(!GraphPresentation.defaultShowKey)
                .showIDs(!GraphPresentation.defaultShowTermIds)
                .termBoxWidth(GraphPresentation.defaultWidth + 50)
                .termBoxHeight(GraphPresentation.defaultHeight + 30)
                .showSlimColours(!GraphPresentation.defaultShowSlimColours)
                .showChildren(!GraphPresentation.defaultShowChildren);
        GraphPresentation graphPresentation = builder.build();
        assertThat(!GraphPresentation.defaultShowKey, is(equalTo(graphPresentation.key)));
        assertThat(!GraphPresentation.defaultShowTermIds, is(equalTo(graphPresentation.termIds)));
        assertThat(GraphPresentation.defaultWidth + 50, is(equalTo(graphPresentation.width)));
        assertThat(GraphPresentation.defaultHeight + 30, is(equalTo(graphPresentation.height)));
        assertThat(!GraphPresentation.defaultShowSlimColours, is(equalTo(graphPresentation.subsetColours)));
        assertThat(!GraphPresentation.defaultShowChildren, is(equalTo(graphPresentation.showChildren)));
    }

}
