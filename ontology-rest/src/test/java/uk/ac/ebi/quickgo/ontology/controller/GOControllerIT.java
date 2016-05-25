package uk.ac.ebi.quickgo.ontology.controller;

import uk.ac.ebi.quickgo.ontology.common.document.OntologyDocMocker;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyDocument;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    private static final String GO_0000002 = "GO:0000002";

    @Test
    public void canRetrieveBlacklistByIds() throws Exception {
        ResultActions response = mockMvc.perform(get(buildTermsURL(GO_0000001 + COMMA + GO_0000002) + "/constraints"));

        expectBasicFieldsInResults(response, Arrays.asList(GO_0000001, GO_0000002))
                .andExpect(jsonPath("$.results.*.blacklist", hasSize(2)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

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

    @Override protected List<OntologyDocument> createNDocs(int n) {
        return IntStream.range(0, n)
                .mapToObj(i -> OntologyDocMocker.createGODoc(createId(i), "go doc name " + i)).collect
                        (Collectors.toList());
    }

    private String createId(int idNum) {
        return String.format("GO:%07d", idNum);
    }

    @Override
    protected List<OntologyDocument> createBasicDocs() {
        return Arrays.asList(
                OntologyDocMocker.createGODoc(GO_0000001, "go name 1"),
                OntologyDocMocker.createGODoc(GO_0000002, "go name 2"));
    }

    @Override
    protected String idMissingInRepository() {
        return "GO:0000003";
    }

    @Override
    protected String invalidId() {
        return "GO|0000001";
    }

    @Override
    protected String getResourceURL() {
        return RESOURCE_URL;
    }
}