package uk.ac.ebi.quickgo.rest.controller;

import uk.ac.ebi.quickgo.model.ontology.GOTerm;
import uk.ac.ebi.quickgo.service.ontology.OntologyService;

import java.util.Optional;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static uk.ac.ebi.quickgo.rest.controller.GOOntologyController.PathValidator.isValidGOId;

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
public class GOOntologyController {

    private static final String GO_REQUEST_MAPPING_BASE = "/QuickGO/services/go";

    @Autowired
    private OntologyService<GOTerm> goOntologyService;

    /**
     * An empty path should result in a bad request
     * @param id
     * @return
     */
    @RequestMapping(value = GO_REQUEST_MAPPING_BASE, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<GOTerm> emptyId() {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**
     * Get a GO term based on its id
     * @param id
     * @return
     */
    @RequestMapping(value = GO_REQUEST_MAPPING_BASE + "/{id}", produces = {MediaType
            .APPLICATION_JSON_VALUE})
    public ResponseEntity<GOTerm> findSingleGOTerm(
            @PathVariable(value = "id") String id) {

        if (!isValidGOId(id)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // use the service to retrieve what user requested
        Optional<GOTerm> optionalGODoc = goOntologyService.findByOntologyId(id);

        if (optionalGODoc.isPresent()) {
            return new ResponseEntity<>(optionalGODoc.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Contains validation logic of GO path components
     */
    protected static class PathValidator {
        static Pattern validGOFormat = Pattern.compile("^GO:[0-9]{7}$");

        static boolean isValidGOId(String id) {
            return validGOFormat.matcher(id).matches();
        }
    }

}
