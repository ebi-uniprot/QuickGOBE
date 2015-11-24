package uk.ac.ebi.quickgo.rest.controller;

import uk.ac.ebi.quickgo.model.ontology.ECOTerm;
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

import static uk.ac.ebi.quickgo.rest.controller.ECOOntologyController.PathValidator.isValidECOId;
import static uk.ac.ebi.quickgo.rest.controller.GOOntologyController.PathValidator.isValidGOId;

/**
 * REST controller for accessing Ontology related information.
 *
 * For complete list of necessary endpoints, and their behaviour:
 *  refer to https://www.ebi.ac.uk/seqdb/confluence/pages/viewpage.action?pageId=32180537
 *
 * Created 16/11/15
 * @author Edd
 */
@RestController
public class ECOOntologyController {

    private static final String GO_REQUEST_MAPPING_BASE = "/QuickGO/services/eco";

    @Autowired
    private OntologyService<ECOTerm> ecoOntologyService;

    /**
     * An empty path should result in a bad request
     * @param id
     * @return
     */
    @RequestMapping(value = GO_REQUEST_MAPPING_BASE, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ECOTerm> emptyId() {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**
     * Get a GO term based on its id
     * @param id
     * @return
     */
    @RequestMapping(value = GO_REQUEST_MAPPING_BASE + "/{id}", produces = {MediaType
            .APPLICATION_JSON_VALUE})
    public ResponseEntity<ECOTerm> findSingleGOTerm(
            @PathVariable(value = "id") String id) {

        if (!isValidECOId(id)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // use the service to retrieve what user requested
        Optional<ECOTerm> optionalECODoc = ecoOntologyService.findByOntologyId(id);

        if (optionalECODoc.isPresent()) {
            return new ResponseEntity<>(optionalECODoc.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Contains validation logic of GO path components
     */
    protected static class PathValidator {
        static Pattern validECOFormat = Pattern.compile("^ECO:[0-9]{7}$");

        static boolean isValidECOId(String id) {
            return validECOFormat.matcher(id).matches();
        }
    }

}
