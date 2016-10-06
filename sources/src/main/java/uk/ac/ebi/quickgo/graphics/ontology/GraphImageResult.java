package uk.ac.ebi.quickgo.graphics.ontology;

import uk.ac.ebi.quickgo.graphics.model.GraphImageLayout;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A container of a graph image with a description.
 *
 * Created 26/09/16
 * @author Edd
 */
public class GraphImageResult {
    private final String description;
    private final GraphImage graphImage;
    private final GraphImageLayout layout;

    public GraphImageResult(String description, GraphImage graphImage, GraphImageLayout layout) {
        checkArgument(description != null && !description.isEmpty(), "Description must not be null");
        checkArgument(graphImage != null, "GraphImage must not be null");
        checkArgument(layout != null, "GraphImageLayout must not be null");

        this.description = description;
        this.graphImage = graphImage;
        this.layout = layout;
    }

    public String getDescription() {
        return description;
    }

    public GraphImage getGraphImage() {
        return graphImage;
    }

    public GraphImageLayout getLayout() {
        return layout;
    }
}
