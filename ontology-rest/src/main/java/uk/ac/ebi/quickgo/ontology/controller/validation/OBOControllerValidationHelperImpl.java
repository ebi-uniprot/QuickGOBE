package uk.ac.ebi.quickgo.ontology.controller.validation;

import uk.ac.ebi.quickgo.ontology.model.OntologyRelationType;
import uk.ac.ebi.quickgo.rest.ParameterException;
import uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelperImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;
import static uk.ac.ebi.quickgo.ontology.model.OntologyRelationType.DEFAULT_TRAVERSAL_TYPES;
import static uk.ac.ebi.quickgo.ontology.model.OntologyRelationType.DEFAULT_TRAVERSAL_TYPES_CSV;

/**
 * Created 24/05/16
 * @author Edd
 */
public class OBOControllerValidationHelperImpl
        extends ControllerValidationHelperImpl
        implements OBOControllerValidationHelper {

    private static final Logger LOGGER = getLogger(OBOControllerValidationHelperImpl.class);

    public OBOControllerValidationHelperImpl(int maxPageResults, Predicate<String> validIDCondition) {
        super(maxPageResults, validIDCondition);
    }

    @Override public List<OntologyRelationType> validateRelationTypes(String relationTypesCSV) {
        List<String> relationTypeStrList = csvToList(relationTypesCSV);

        List<OntologyRelationType> relationTypes = new ArrayList<>();
        for (String relationTypeStr : relationTypeStrList) {
            OntologyRelationType relationType;
            try {
                String relationTypeStrLowerCase = relationTypeStr.toLowerCase();
                relationType = OntologyRelationType.getByLongName(relationTypeStrLowerCase);
            } catch (IllegalArgumentException e) {
                LOGGER.error(e.getMessage());
                throw new ParameterException("Unknown relationship requested: '" + relationTypeStr + "'");
            }

            if (relationType != null) {
                checkValidTraversalRelationType(relationType);
                relationTypes.add(relationType);
            }
        }

        return relationTypes;
    }

    void checkValidTraversalRelationType(OntologyRelationType relationType) {
        if (!DEFAULT_TRAVERSAL_TYPES.contains(relationType)) {
            String errorMessage = "Cannot traverse over relation type: " + relationType.getLongName() + ". " +
                    "Can only traverse over: " + DEFAULT_TRAVERSAL_TYPES_CSV;
            LOGGER.error(errorMessage);
            throw new ParameterException(errorMessage);
        }
    }
}
