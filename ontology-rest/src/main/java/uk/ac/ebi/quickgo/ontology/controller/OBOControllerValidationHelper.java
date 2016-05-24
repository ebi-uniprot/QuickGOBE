package uk.ac.ebi.quickgo.ontology.controller;

import uk.ac.ebi.quickgo.ontology.traversal.OntologyRelationType;
import uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelper;

import java.util.List;

/**
 * Created 24/05/16
 * @author Edd
 */
public interface OBOControllerValidationHelper extends ControllerValidationHelper {
    List<OntologyRelationType> validateRelationTypes(String relationTypes);
}
