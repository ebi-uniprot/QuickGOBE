package uk.ac.ebi.quickgo.graphics.ontology;

/**
 * Created 26/09/16
 * @author Edd
 */
public class GraphImageResult {
    private final String description;
    private final GraphImage graphImage;

    public GraphImageResult(String description, GraphImage graphImage) {
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
