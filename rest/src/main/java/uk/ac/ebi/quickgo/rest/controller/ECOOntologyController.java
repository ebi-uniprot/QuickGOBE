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
public class ECOOntologyController {

    private static final String ECO_REQUEST_MAPPING_BASE = "/QuickGO/services/eco";

    @Autowired
    private OntologyService<ECOTerm> ecoOntologyService;

    /**
     * An empty path should result in a bad request
     * @param id
     * @return
     */
    @RequestMapping(value = ECO_REQUEST_MAPPING_BASE, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ECOTerm> emptyId() {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**
     * Get a GO term based on its id
     * @param id
     * @return
     */
    @RequestMapping(value = ECO_REQUEST_MAPPING_BASE + "/{id}", produces = {MediaType
            .APPLICATION_JSON_VALUE})
    public ResponseEntity<ECOTerm> findCoreGOTerm(@PathVariable(value = "id") String id) {

        if (!isValidECOId(id)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // use the service to retrieve what user requested
        return getEcoTermResponse(ecoOntologyService.findCoreInfoByOntologyId(id));
    }

    /**
     * Get a GO term based on its id
     * @param id
     * @return
     */
    @RequestMapping(value = ECO_REQUEST_MAPPING_BASE + "/{id}/complete", produces = {MediaType
            .APPLICATION_JSON_VALUE})
    public ResponseEntity<ECOTerm> findCompleteGOTerm(@PathVariable(value = "id") String id) {

        if (!isValidECOId(id)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // use the service to retrieve what user requested
        return getEcoTermResponse(ecoOntologyService.findCompleteInfoByOntologyId(id));
    }

    /**
     * Get a GO term based on its id
     * @param id
     * @return
     */
    @RequestMapping(value = ECO_REQUEST_MAPPING_BASE + "/{id}/history", produces = {MediaType
            .APPLICATION_JSON_VALUE})
    public ResponseEntity<ECOTerm> findGOTermHistory(@PathVariable(value = "id") String id) {

        if (!isValidECOId(id)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // use the service to retrieve what user requested
        return getEcoTermResponse(ecoOntologyService.findHistoryInfoByOntologyId(id));
    }

    /**
     * Get a GO term based on its id
     * @param id
     * @return
     */
    @RequestMapping(value = ECO_REQUEST_MAPPING_BASE + "/{id}/xrefs", produces = {MediaType
            .APPLICATION_JSON_VALUE})
    public ResponseEntity<ECOTerm> findGOTermXRefs(@PathVariable(value = "id") String id) {

        if (!isValidECOId(id)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // use the service to retrieve what user requested
        return getEcoTermResponse(ecoOntologyService.findXRefsInfoByOntologyId(id));
    }

    private ResponseEntity<ECOTerm> getEcoTermResponse(Optional<ECOTerm> optionalECODoc) {
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
        final static Pattern validECOFormat = Pattern.compile("^ECO:[0-9]{7}$");

        static boolean isValidECOId(String id) {
            return validECOFormat.matcher(id).matches();
        }
    }

}
