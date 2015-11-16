package uk.ac.ebi.quickgo.rest.controller;

import uk.ac.ebi.quickgo.document.ontology.OntologyDocument;
import uk.ac.ebi.quickgo.model.ontology.ECOTerm;
import uk.ac.ebi.quickgo.model.ontology.GOTerm;
import uk.ac.ebi.quickgo.service.ontology.OntologyService;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for accessing Ontology related information.
 *
 * Created 16/11/15
 * @author Edd
 */
@RestController
public class OntologyCtrl {

    @Autowired
    private OntologyService ontologyService;

    /**
     * Get a GO term based on its id
     * @param id
     * @return
     */
    @RequestMapping(value = "/go/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<GOTerm> findSingleGOTerm(
            @PathVariable(value = "id") String id) {

        // use the service to retrieve what user requested
        List<OntologyDocument> goIds = ontologyService.findByGoId(id, new PageRequest(0, 1));

        // build response and reply
        GOTerm goTerm = new GOTerm();

        if (goIds.size() == 1) {
            OntologyDocument goDocument = goIds.get(0);
            goTerm.setId(goDocument.id);
            goTerm.setName(goDocument.name);

            return new ResponseEntity<>(goTerm, HttpStatus.OK);
        }

        return new ResponseEntity<>(goTerm, HttpStatus.NOT_FOUND);
    }

    /**
     * Get an ECO term based on its id
     * @param id
     * @return
     */
    @RequestMapping(value = "/eco/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ECOTerm> findSingleECOTerm(
            @PathVariable(value = "id") String id) {

        // use the service to retrieve what user requested
        List<OntologyDocument> ecoIds = ontologyService.findByEcoId(id, new PageRequest(0, 1));

        // build response and reply
        ECOTerm ecoTerm = new ECOTerm();

        if (ecoIds.size() == 1) {
            OntologyDocument goDocument = ecoIds.get(0);
            ecoTerm.setId(goDocument.id);
            ecoTerm.setName(goDocument.name);

            return new ResponseEntity<>(ecoTerm, HttpStatus.OK);
        }

        return new ResponseEntity<>(ecoTerm, HttpStatus.NOT_FOUND);
    }

}
