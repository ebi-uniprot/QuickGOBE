package uk.ac.ebi.quickgo.graphics.service;

import uk.ac.ebi.quickgo.ff.loader.ontology.OntologyGraphicsSourceLoader;
import uk.ac.ebi.quickgo.graphics.model.GraphImageLayout;
import uk.ac.ebi.quickgo.graphics.ontology.*;
import uk.ac.ebi.quickgo.model.ontology.generic.GenericOntology;
import uk.ac.ebi.quickgo.model.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.model.ontology.generic.GenericTermSet;
import uk.ac.ebi.quickgo.model.ontology.generic.RelationType;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.stream.Collectors.joining;

/**
 * Created 26/09/16
 * @author Edd
 */
public class GraphImageServiceImpl implements GraphImageService {

    private static final String GO_NAMESPACE = "GO";
    private static final String ECO_NAMESPACE = "ECO";
    private static final EnumSet<RelationType> ECO_RELATIONS_SET = EnumSet.of(RelationType.USEDIN, RelationType.ISA);
    private static final EnumSet<RelationType> GO_RELATIONS_SET = EnumSet.of(RelationType.ISA, RelationType.PARTOF,
            RelationType.REGULATES, RelationType.POSITIVEREGULATES, RelationType.NEGATIVEREGULATES,
            RelationType.OCCURSIN, RelationType.CAPABLEOF, RelationType.CAPABLEOFPARTOF);
    private static final int TERM_DISPLAY_THRESHOLD = 6;
    private final OntologyGraphicsSourceLoader sourceLoader;

    public GraphImageServiceImpl(OntologyGraphicsSourceLoader ontologyGraphicsSourceLoader) {
        checkArgument(ontologyGraphicsSourceLoader != null, "OntologyGraphicsSourceLoader cannot be null");

        this.sourceLoader = ontologyGraphicsSourceLoader;
    }

    @Override
    public GraphImageResult createChart(List<String> ids, String scope, GraphPresentation graphPresentation) {
        if (sourceLoader.isLoaded()) {

            NameSpace nameSpace = NameSpace.getNameSpace(scope);
            GraphImage graphImage = createRenderableImage(ids, nameSpace, graphPresentation);

            String description;
            int idsSize = ids.size();
            if (idsSize == 1) {
                description = "Ancestor chart for " + ids.get(0);
            } else if (idsSize < TERM_DISPLAY_THRESHOLD) {
                description = "Comparison chart for " + ids.stream().collect(joining(","));
            } else {
                description = "Comparison chart for " + String.valueOf(idsSize) + " terms";
            }

            return new GraphImageResult(
                    description,
                    graphImage,
                    createGraphImageLayout(graphImage, description));
        } else {
            throw new RenderingGraphException(
                    "Cannot create chart because internal ontologies could not be loaded at application startup");
        }
    }

    @Override
    public GraphPresentation.Builder graphPresentationBuilder() {
        return new GraphPresentation.Builder();
    }

    /**
     * Creates the coordinates of the node information stored within the graph image
     * which can be used as an image-map.
     * @param graphImage the image whose layouting information is required
     * @param title the title of the image
     * @return the layouting information for the specified {@link GraphImage}
     */
    private GraphImageLayout createGraphImageLayout(GraphImage graphImage, String title) {
        Collection<TermNode> terms = graphImage.getOntologyTerms();
        GraphImageLayout layout = new GraphImageLayout();

        terms.stream()
                .map(term -> {
                    GraphImageLayout.NodePosition nodePosition = new GraphImageLayout.NodePosition();
                    nodePosition.id = term.getId();
                    nodePosition.bottom = term.bottom();
                    nodePosition.top = term.top();
                    nodePosition.left = term.left();
                    nodePosition.right = term.right();
                    return nodePosition;
                })
                .forEach(layout.nodePositions::add);

        layout.imageHeight = graphImage.height;
        layout.imageWidth = graphImage.width;

        graphImage.legend.stream()
                .map(legendNode -> {
                    GraphImageLayout.LegendPosition legendPosition = new GraphImageLayout.LegendPosition();
                    legendPosition.bottom = legendNode.bottom();
                    legendPosition.top = legendNode.top();
                    legendPosition.height = legendNode.height;
                    legendPosition.width = legendNode.width;
                    legendPosition.left = legendNode.left();
                    legendPosition.right = legendNode.right();
                    legendPosition.xCentre = legendNode.xCentre;
                    legendPosition.yCentre = legendNode.yCentre;
                    return legendPosition;
                })
                .forEach(layout.legendPositions::add);

        layout.title = title;

        return layout;
    }

    private EnumSet<RelationType> getRelationTypes(NameSpace nameSpace) {
        EnumSet<RelationType> targetSet;
        switch (nameSpace) {
            case GO:
                targetSet = GO_RELATIONS_SET;
                break;
            case ECO:
                targetSet = ECO_RELATIONS_SET;
                break;
            default:
                throw new IllegalArgumentException("Unknown term namespace used: " + nameSpace.name());
        }
        return targetSet;
    }

    private GraphImage createRenderableImage(List<String> termsIds, NameSpace nameSpace,
            GraphPresentation graphPresentation) {
        // Check if the selected terms exist
        List<GenericTerm> terms = new ArrayList<>();

        termsIds.stream()
                .map(this::retrieveTerm)
                .forEach(term -> term.ifPresent(terms::add));

        // Build term set
        GenericTermSet termSet = createGenericTermSet(nameSpace);
        terms.forEach(termSet::add);

        // Create ontology graph
        OntologyGraph ontologyGraph =
                OntologyGraph.makeGraph(termSet, getRelationTypes(nameSpace), 0, 0, graphPresentation);
        return ontologyGraph.layout();
    }

    private GenericTermSet createGenericTermSet(NameSpace nameSpace) {
        return new GenericTermSet(getGenericOntology(nameSpace), "Term Set", 0);
    }

    private GenericOntology getGenericOntology(NameSpace nameSpace) {
        switch (nameSpace) {
            case GO:
                return sourceLoader.getGeneOntology();
            case ECO:
                return sourceLoader.getEvidenceCodeOntology();
            default:
                throw new IllegalArgumentException("Unknown term namespace used: " + nameSpace.name());
        }
    }

    private Optional<GenericTerm> retrieveTerm(String id) {
        if (hasGONamespace(id)) {
            return Optional.of(sourceLoader.getGeneOntology().getTerm(id));
        } else if (hasECONamespace(id)) {
            return Optional.of(sourceLoader.getEvidenceCodeOntology().getTerm(id));
        } else {
            throw new RenderingGraphException("Unknown term namespace: " + id);
        }
    }

    private boolean hasGONamespace(String value) {
        return value.contains(GO_NAMESPACE);
    }

    private boolean hasECONamespace(String value) {
        return value.contains(ECO_NAMESPACE);
    }

    private enum NameSpace {
        GO, ECO;

        static NameSpace getNameSpace(String namespace) {
            if (namespace != null && namespace.trim().equalsIgnoreCase(GO.name())) {
                return GO;
            } else if (namespace != null && namespace.trim().equalsIgnoreCase(ECO.name())) {
                return ECO;
            } else {
                throw new RenderingGraphException("Unknown term namespace used: " + namespace);
            }
        }
    }
}
