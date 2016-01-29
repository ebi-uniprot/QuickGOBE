package uk.ac.ebi.quickgo.ontology.controller;

import uk.ac.ebi.quickgo.ontology.model.GOTerm;
import uk.ac.ebi.quickgo.ontology.service.OntologyService;

import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    private static final Pattern GO_ID_FORMAT = Pattern.compile("^GO:[0-9]{7}$");

    @Autowired
    public GOController(OntologyService<GOTerm> goOntologyService) {
        super(goOntologyService);
    }

    @Override
    public boolean isValidId(String id) {
        return GO_ID_FORMAT.matcher(id).matches();
    }
}
