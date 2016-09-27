package uk.ac.ebi.quickgo.graphics.service;

import uk.ac.ebi.quickgo.graphics.ontology.*;
import uk.ac.ebi.quickgo.model.ontology.eco.EvidenceCodeOntology;
import uk.ac.ebi.quickgo.model.ontology.generic.GenericOntology;
import uk.ac.ebi.quickgo.model.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.model.ontology.generic.GenericTermSet;
import uk.ac.ebi.quickgo.model.ontology.generic.RelationType;
import uk.ac.ebi.quickgo.model.ontology.go.GeneOntology;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static uk.ac.ebi.quickgo.graphics.service.GraphImageServiceImpl.NameSpace.ECO;
import static uk.ac.ebi.quickgo.graphics.service.GraphImageServiceImpl.NameSpace.GO;

/**
 * Created 26/09/16
 * @author Edd
 */
public class GraphImageServiceImpl implements GraphImageService {

    private static final String GO_NAMESPACE = "GO";
    private static final Pattern GO_ID_FORMAT = Pattern.compile(GO_NAMESPACE + ":\\d{7}", Pattern.CASE_INSENSITIVE);
    private static final String ECO_NAMESPACE = "ECO";
    private static final Pattern ECO_ID_FORMAT = Pattern.compile(ECO_NAMESPACE + ":\\d{7}", Pattern.CASE_INSENSITIVE);
    private static final EnumSet<RelationType> ECO_RELATIONS_SET = EnumSet.of(RelationType.USEDIN, RelationType.ISA);
    private static final EnumSet<RelationType> GO_RELATIONS_SET = EnumSet.of(RelationType.ISA, RelationType.PARTOF,
            RelationType.REGULATES, RelationType.POSITIVEREGULATES, RelationType.NEGATIVEREGULATES,
            RelationType.OCCURSIN, RelationType.CAPABLEOF, RelationType.CAPABLEOFPARTOF);
    private final GeneOntology geneOntology;
    private final EvidenceCodeOntology evidenceCodeOntology;

    public GraphImageServiceImpl(GeneOntology geneOntology, EvidenceCodeOntology evidenceCodeOntology) {
        checkArgument(geneOntology != null, "GeneOntology cannot be null");
        checkArgument(evidenceCodeOntology != null, "EvidenceCodeOntology cannot be null");

        this.geneOntology = geneOntology;
        this.evidenceCodeOntology = evidenceCodeOntology;
    }

    @Override
    public GraphImageResult createChart(List<String> ids, String scope) {
        NameSpace nameSpace = NameSpace.getNameSpace(scope);
        List<String> termsIdsList = validateIds(ids, nameSpace);

        GraphImage graphImage = createRenderableImage(ids, nameSpace);

        String description;
        if (termsIdsList.size() == 1) {
            description = "Ancestor chart for " + termsIdsList.get(0);
        } else {
            description = "Comparison chart for " + String.valueOf(termsIdsList.size());
        }

        return new GraphImageResult(description, graphImage);
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

    private List<String> validateIds(List<String> ids, NameSpace nameSpace) {
        List<String> validIds = ids.stream()
                .filter(id -> nameSpace == GO && GO_ID_FORMAT.matcher(id).matches()
                        || (nameSpace == ECO && ECO_ID_FORMAT.matcher(id).matches()))
                .collect(Collectors.toList());

        checkArgument(validIds != null && validIds.size() > 0,
                "No valid term IDs [" + ids + "] found for given namespace [" + nameSpace + "].");

        return validIds;
    }

    private GraphImage createRenderableImage(List<String> termsIds, NameSpace nameSpace) {
        // Check if the selected terms exist
        List<GenericTerm> terms = new ArrayList<>();

        termsIds.stream()
                .map(this::retrieveTerm)
                .forEach(term -> {
                    term.ifPresent(terms::add);
                });

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
                return geneOntology;
            case ECO:
                return evidenceCodeOntology;
            default:
                throw new IllegalArgumentException("Unknown term namespace used: " + nameSpace.name());
        }
    }

    private Optional<GenericTerm> retrieveTerm(String id) {
        if (hasGONamespace(id)) {
            return Optional.of(geneOntology.getTerm(id));
        } else if (hasECONamespace(id)) {
            return Optional.of(evidenceCodeOntology.getTerm(id));
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

    enum NameSpace {
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
