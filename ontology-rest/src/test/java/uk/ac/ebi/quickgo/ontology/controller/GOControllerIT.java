package uk.ac.ebi.quickgo.ontology.controller;

import uk.ac.ebi.quickgo.ontology.common.document.OntologyDocMocker;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyDocument;

import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Tests the {@link GOController} class. All tests for GO
 * are covered by the tests in the parent class, {@link OBOControllerIT}.
 *
 * Created 16/11/15
 * @author Edd
 */
public class GOControllerIT extends OBOControllerIT {

    private static final String RESOURCE_URL = "/QuickGO/services/go";
    private static final String GO_0000001 = "GO:0000001";

    /*
     * GO produces two more attributes in its response (aspect and usage), when compared
     * to the standard OBO response.
     */
    @Override
    protected ResultActions expectCoreFields(ResultActions result, String id) throws Exception {
        return super
                .expectCoreFields(result, id)
                .andExpect(jsonPath("$.aspect").value("Biological Process"))
                .andExpect(jsonPath("$.usage").value("Unrestricted"));
    }

    @Override
    protected OntologyDocument createBasicDoc() {
        return OntologyDocMocker.createGODoc(GO_0000001, "go name");
    }

    @Override
    protected String idMissingInRepository() {
        return "GO:0000002";
    }

    @Override
    protected String invalidId() {
        return "GO;0000001";
    }

    @Override
    protected String getResourceURL() {
        return RESOURCE_URL;
    }
}