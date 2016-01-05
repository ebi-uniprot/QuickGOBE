package uk.ac.ebi.quickgo.rest.controller;

import uk.ac.ebi.quickgo.service.ontology.OntologyService;

import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Abstract controller defining common end-points of an OBO related
 * REST API.
 *
 * Created 27/11/15
 * @author Edd
 */
public abstract class OBOController<T> {
    private final OntologyService<T> ontologyService;

    public abstract boolean isValidId(String id);

    public OBOController(OntologyService<T> ontologyService) {
        this.ontologyService = ontologyService;
    }

    /**
     * An empty or unknown path should result in a bad request
     * @return
     */
    @RequestMapping(value = "/*", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<T> emptyId() {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**
     * Get core information about a term based on its id
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<T> findCoreTerm(@PathVariable(value = "id") String id) {
        if (!isValidId(id)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // use the service to retrieve what user requested
        return getTermResponse(ontologyService.findCoreInfoByOntologyId(id));
    }

    /**
     * Get complete information about a term based on its id
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}/complete", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<T> findCompleteTerm(@PathVariable(value = "id") String id) {
        if (!isValidId(id)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // use the service to retrieve what user requested
        return getTermResponse(ontologyService.findCompleteInfoByOntologyId(id));
    }

    /**
     * Get history information about a term based on its id
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}/history", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<T> findTermHistory(@PathVariable(value = "id") String id) {
        if (!isValidId(id)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // use the service to retrieve what user requested
        return getTermResponse(ontologyService.findHistoryInfoByOntologyId(id));
    }

    /**
     * Get cross-reference information about a term based on its id
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}/xrefs", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<T> findTermXRefs(@PathVariable(value = "id") String id) {
        if (!isValidId(id)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // use the service to retrieve what user requested
        return getTermResponse(ontologyService.findXRefsInfoByOntologyId(id));
    }

    /**
     * Get taxonomy constraint and blacklist information about a term based on its id
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}/constraints", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<T> findTermTaxonConstraints(@PathVariable(value = "id") String id) {
        if (!isValidId(id)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // use the service to retrieve what user requested
        return getTermResponse(ontologyService.findTaxonConstraintsInfoByOntologyId(id));
    }

    /**
     * Get cross-ontology relationship information about a term based on its id
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}/xorels", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<T> findTermXOntologyRelations(@PathVariable(value = "id") String id) {
        if (!isValidId(id)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // use the service to retrieve what user requested
        return getTermResponse(ontologyService.findXORelationsInfoByOntologyId(id));
    }

    /**
     * Get annotation guideline information about a term based on its id
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}/guidelines", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<T> findTermAnnotationGuideLines(@PathVariable(value = "id") String id) {
        if (!isValidId(id)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // use the service to retrieve what user requested
        return getTermResponse(ontologyService.findAnnotationGuideLinesInfoByOntologyId(id));
    }

    private ResponseEntity<T> getTermResponse(Optional<T> optionalECODoc) {
        if (optionalECODoc.isPresent()) {
            return new ResponseEntity<>(optionalECODoc.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}