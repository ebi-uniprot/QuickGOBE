package uk.ac.ebi.quickgo.graphics.ontology;

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

    public GraphImageResult(String description, GraphImage graphImage) {
        checkArgument(description != null && !description.isEmpty(), "Description must not be null");
        checkArgument(graphImage != null, "GraphImage must not be null");

        this.description = description;
        this.graphImage = graphImage;
    }

    public String getDescription() {
        return description;
    }

    public GraphImage getGraphImage() {
        return graphImage;
    }
}
