package uk.ac.ebi.quickgo.rest.controller;

import uk.ac.ebi.quickgo.service.OntologyService;
import uk.ac.ebi.quickgo.service.model.ontology.ECOTerm;

import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    private static final Pattern ECO_ID_FORMAT = Pattern.compile("^ECO:[0-9]{7}$");

    @Autowired
    public ECOController(OntologyService<ECOTerm> ecoOntologyService) {
        super(ecoOntologyService);
    }

    @Override
    public boolean isValidId(String id) {
        return ECO_ID_FORMAT.matcher(id).matches();
    }
}
