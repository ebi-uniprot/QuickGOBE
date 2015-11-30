package uk.ac.ebi.quickgo.rest.controller;

import uk.ac.ebi.quickgo.model.ontology.ECOTerm;
import uk.ac.ebi.quickgo.service.ontology.OntologyService;

import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static uk.ac.ebi.quickgo.rest.controller.ECOController.PathValidator.isValidECOId;

/**
 * REST controller for accessing ECO related information.
 *
 * For complete list of necessary endpoints, and their behaviour:
 *  refer to https://www.ebi.ac.uk/seqdb/confluence/display/GOA/REST+API
 *
 * Created 16/11/15
 * @author Edd
 */
@RestController
@RequestMapping(value = "/QuickGO/services/eco")
public class ECOController extends OBOController<ECOTerm> {

    // retained for use with specialised end-points
    private OntologyService<ECOTerm> ecoOntologyService;

    @Autowired
    public ECOController(OntologyService<ECOTerm> ecoOntologyService) {
        super(ecoOntologyService);
        this.ecoOntologyService = ecoOntologyService;
    }

    @Override
    public boolean isValidId(String id) {
        return isValidECOId(id);
    }

    /**
     * Contains validation logic of GO path components
     */
    protected static class PathValidator {
        private PathValidator(){}

        static final Pattern validECOFormat = Pattern.compile("^ECO:[0-9]{7}$");

        static boolean isValidECOId(String id) {
            return validECOFormat.matcher(id).matches();
        }
    }

}
