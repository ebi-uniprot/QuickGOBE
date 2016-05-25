package uk.ac.ebi.quickgo.ontology.controller;

import uk.ac.ebi.quickgo.ontology.traversal.OntologyRelationType;
import uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelperImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created 24/05/16
 * @author Edd
 */
public class OBOControllerValidationHelperImpl
        extends ControllerValidationHelperImpl
        implements OBOControllerValidationHelper {

    public OBOControllerValidationHelperImpl(int maxPageResults, Predicate<String> validIDCondition) {
        super(maxPageResults, validIDCondition);
    }

    private static final Logger LOGGER = getLogger(OBOControllerValidationHelperImpl.class);

    private final static List<OntologyRelationType> VALID_TRAVERSAL_RELATION_TYPES = Arrays.asList(
            OntologyRelationType.IS_A,
            OntologyRelationType.PART_OF,
            OntologyRelationType.OCCURS_IN,
            OntologyRelationType.REGULATES
    );

    final static String VALID_TRAVERSAL_RELATION_TYPES_STRING =
            VALID_TRAVERSAL_RELATION_TYPES.stream()
                    .map(OntologyRelationType::getLongName)
                    .collect(Collectors.joining(", "));

    @Override public List<OntologyRelationType> validateRelationTypes(String relationTypesCSV) {
        if (relationTypesCSV.isEmpty()) {
            return VALID_TRAVERSAL_RELATION_TYPES;
        }

        relationTypesCSV = relationTypesCSV.toLowerCase();

        List<String> relationTypeList = csvToList(relationTypesCSV);

        List<OntologyRelationType> relationTypes = new ArrayList<>();
        for (String relationTypeStr : relationTypeList) {
            OntologyRelationType relationType;
            try {
                relationType = OntologyRelationType.getByName(relationTypeStr);
            } catch (IllegalArgumentException e) {
                LOGGER.error(e.getMessage());
                throw new IllegalArgumentException("Unknown relationship requested");
            }

            if (relationType != null && isValidTraversalRelationType(relationType)) {
                relationTypes.add(relationType);
            }
        }

        return relationTypes;
    }

    boolean isValidTraversalRelationType(OntologyRelationType relationType) {
        if (!VALID_TRAVERSAL_RELATION_TYPES.contains(relationType)) {
            throw new IllegalArgumentException(
                    "Cannot traverse over relation type: " + relationType.getLongName() + ". " +
                            "Can only traverse over: " + VALID_TRAVERSAL_RELATION_TYPES_STRING);
        }
        return true;
    }
}
