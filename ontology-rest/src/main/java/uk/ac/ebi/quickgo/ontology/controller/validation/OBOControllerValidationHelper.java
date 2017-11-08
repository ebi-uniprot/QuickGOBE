package uk.ac.ebi.quickgo.ontology.controller.validation;

import uk.ac.ebi.quickgo.ontology.model.OntologyRelationType;
import uk.ac.ebi.quickgo.rest.ParameterException;
import uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelper;

import java.util.List;

/**
 * Extends the {@link ControllerValidationHelper} contract with specific
 * validation methods relevant to the OBO domain.
 *
 * Created 24/05/16
 * @author Edd
 */
public interface OBOControllerValidationHelper extends ControllerValidationHelper {
    /**
     * Validates whether a {@link String} representation of a list of ontology relations,
     * in CSV format, correspond to known {@link OntologyRelationType} values.
     *
     * @param relationTypesCSV a list of relation types, specified as a {@link String} in CSV format
     * @param validTypes a list of acceptable relation types.
     * @return the {@link List} of {@link OntologyRelationType} which correspond to the original {@code
     * relationTypesCSV}
     *
     * @throws ParameterException if {@code relationTypesCSV} contains a value that does not correspond
     * to a valid {@link OntologyRelationType}
     */
    List<OntologyRelationType> validateRelationTypes(String relationTypesCSV, List<OntologyRelationType> validTypes);
}
