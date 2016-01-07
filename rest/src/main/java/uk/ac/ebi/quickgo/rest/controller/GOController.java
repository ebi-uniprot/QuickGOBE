package uk.ac.ebi.quickgo.rest.controller;

import uk.ac.ebi.quickgo.service.OntologyService;
import uk.ac.ebi.quickgo.service.model.ontology.GOTerm;

import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static uk.ac.ebi.quickgo.rest.controller.GOController.PathValidator.isValidGOId;

/**
 * REST controller for accessing GO related information.
 *
 * For complete list of necessary endpoints, and their behaviour:
 *  refer to https://www.ebi.ac.uk/seqdb/confluence/display/GOA/REST+API
 *
 * Created 16/11/15
 * @author Edd
 */
@RestController
@RequestMapping(value = "/QuickGO/services/go")
public class GOController extends OBOController<GOTerm> {

    // retained for use with specialised end-points
    private OntologyService<GOTerm> goOntologyService;

    @Autowired
    public GOController(OntologyService<GOTerm> goOntologyService) {
        super(goOntologyService);
        this.goOntologyService = goOntologyService;
    }

    @Override
    public boolean isValidId(String id) {
        return isValidGOId(id);
    }

    /**
     * Contains validation logic of GO path components
     */
    protected static class PathValidator {
        private PathValidator(){}

        static final Pattern validGOFormat = Pattern.compile("^GO:[0-9]{7}$");

        static boolean isValidGOId(String id) {
            return validGOFormat.matcher(id).matches();
        }
    }

}
