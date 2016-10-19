package uk.ac.ebi.quickgo.graphics.service;

import uk.ac.ebi.quickgo.ff.loader.ontology.OntologyGraphicsSourceLoader;
import uk.ac.ebi.quickgo.graphics.ontology.*;
import uk.ac.ebi.quickgo.model.ontology.generic.GenericOntology;
import uk.ac.ebi.quickgo.model.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.model.ontology.generic.GenericTermSet;
import uk.ac.ebi.quickgo.model.ontology.generic.RelationType;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

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
    private final OntologyGraphicsSourceLoader sourceLoader;

    public GraphImageServiceImpl(OntologyGraphicsSourceLoader ontologyGraphicsSourceLoader) {
        checkArgument(ontologyGraphicsSourceLoader != null, "OntologyGraphicsSourceLoader cannot be null");

        this.sourceLoader = ontologyGraphicsSourceLoader;
    }

    @Override
    public GraphImageResult createChart(List<String> ids, String scope) {
        if (sourceLoader.isLoaded()) {

            NameSpace nameSpace = NameSpace.getNameSpace(scope);
            GraphImage graphImage = createRenderableImage(ids, nameSpace);

            String description;
            int idsSize = ids.size();
            if (idsSize == 1) {
                description = "Ancestor chart for " + ids.get(0);
            } else {
                description = "Comparison chart for " + String.valueOf(idsSize);
            }

            return new GraphImageResult(description, graphImage);
        } else {
            throw new RenderingGraphException(
                    "Cannot create chart because internal ontologies could not be loaded at application startup");
        }
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

    private GraphImage createRenderableImage(List<String> termsIds, NameSpace nameSpace) {
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
                OntologyGraph.makeGraph(termSet, getRelationTypes(nameSpace), 0, 0, new GraphPresentation());
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
