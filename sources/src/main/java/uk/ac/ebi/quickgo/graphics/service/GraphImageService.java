package uk.ac.ebi.quickgo.graphics.service;

import uk.ac.ebi.quickgo.graphics.ontology.GraphImageResult;
import uk.ac.ebi.quickgo.graphics.ontology.GraphPresentation;
import uk.ac.ebi.quickgo.graphics.ontology.RenderingGraphException;

import java.util.List;

/**
 * Responsible for creating a {@link GraphImageResult} for a given list of
 * {@code ids}.
 *
 * Created 26/09/16
 * @author Edd
 */
public interface GraphImageService {
    /**
     * Creates a {@link GraphImageResult} for a given list of {@code ids} and for
     * a given {@code scope}.
     *
     * @param ids the term ids whose graphical representation is required
     * @param scope the scope in which the graph is being drawn. Currently this is either "ECO" or "GO".
     * @param graphPresentation defines the look and attributes of the rendered graph
     * @return the corresponding graphical image representing the {@code ids}
     * @throws RenderingGraphException if there was a problem rendering the graph
     */
    GraphImageResult createChart(List<String> ids, String scope, GraphPresentation graphPresentation);

    /**
     * Provide a supplier of GraphPresentation.Builder instances.
     * @return a builder of GraphPresentation instances
     */
    GraphPresentation.Builder graphPresentationBuilder();
}
