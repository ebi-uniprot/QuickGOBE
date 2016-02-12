package uk.ac.ebi.quickgo.ontology.controller;

import uk.ac.ebi.quickgo.ontology.common.document.OntologyDocMocker;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyDocument;

/**
 * Tests the {@link ECOController} class. All tests for ECO
 * are covered by the tests in the parent class, {@link OBOControllerIT}.
 *
 * Created 24/11/15
 * @author Edd
 */
public class ECOControllerIT extends OBOControllerIT {
    private static final String RESOURCE_URL = "/QuickGO/services/eco";
    private static final String ECO_0000001 = "ECO:0000001";

    @Override
    protected OntologyDocument createBasicDoc() {
        return OntologyDocMocker.createECODoc(ECO_0000001, "eco doc name");
    }

    @Override
    protected String getResourceURL() {
        return RESOURCE_URL;
    }

    @Override
    protected String idMissingInRepository() {
        return "ECO:0000002";
    }

    @Override
    protected String invalidId() {
        return "ECO|0000001";
    }

}