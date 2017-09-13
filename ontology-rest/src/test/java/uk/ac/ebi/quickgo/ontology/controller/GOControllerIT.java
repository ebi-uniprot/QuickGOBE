package uk.ac.ebi.quickgo.ontology.controller;

import uk.ac.ebi.quickgo.ontology.common.OntologyDocument;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyDocMocker;

import java.util.Arrays;
import java.util.Collections;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static uk.ac.ebi.quickgo.common.converter.HelpfulConverter.toCSV;
import static uk.ac.ebi.quickgo.ontology.controller.OBOController.COMPLETE_SUB_RESOURCE;
import static uk.ac.ebi.quickgo.ontology.controller.OBOController.CONSTRAINTS_SUB_RESOURCE;

/**
 * Tests the {@link GOController} class. All tests for GO
 * are covered by the tests in the parent class, {@link OBOControllerIT}.
 *
 * Created 16/11/15
 * @author Edd
 */
public class GOControllerIT extends OBOControllerIT {

    private static final String RESOURCE_URL = "/ontology/go";
    private static final String GO_0000001 = "GO:0000001";
    private static final String GO_0000002 = "GO:0000002";
    private static final String GO_0000003 = "GO:0000003";
    private static final String GO_0000004 = "GO:0000004";

    @Test
    public void canRetrieveBlacklistByIds() throws Exception {
        ResultActions response = mockMvc.perform(get(
                buildTermsURLWithSubResource(toCSV(GO_0000001, GO_0000002), CONSTRAINTS_SUB_RESOURCE)));

        expectBasicFieldsInResults(response, Arrays.asList(GO_0000001, GO_0000002))
                .andExpect(jsonPath("$.results.*.blacklist", hasSize(2)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveGoDiscussions() throws Exception {
        ResultActions response = mockMvc.perform(get(buildTermsURLWithSubResource(GO_0000001, COMPLETE_SUB_RESOURCE)));

        expectBasicFieldsInResults(response, Collections.singletonList(GO_0000001))
                .andExpect(jsonPath("$.results.*.goDiscussions", hasSize(1)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveProteinComplexes() throws Exception {
        ResultActions response = mockMvc.perform(get(buildTermsURLWithSubResource(GO_0000001, COMPLETE_SUB_RESOURCE)));

        expectBasicFieldsInResults(response, Collections.singletonList(GO_0000001))
                .andExpect(jsonPath("$.results.*.proteinComplexes", hasSize(1)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void about() throws Exception {
        ResultActions response = mockMvc.perform(get(getResourceURL() + "/about"));
        final String expectedVersion = "http://purl.obolibrary.org/obo/go/releases/2017-01-12/go.owl";
        final String expectedTimestamp = "2017-01-13 02:19";
        response.andDo(print())
                .andExpect(jsonPath("$.go.version").value(expectedVersion))
                .andExpect(jsonPath("$.go.timestamp").value(expectedTimestamp));
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

    @Override protected OntologyDocument createBasicDoc(String id, String name) {
        return OntologyDocMocker.createGODoc(id, name);
    }

    @Override protected List<OntologyDocument> createNDocs(int n) {
        return IntStream.range(1, n + 1)
                .mapToObj(i -> OntologyDocMocker.createGODoc(createId(i), "go doc name " + i)).collect
                        (Collectors.toList());
    }

    @Override
    protected String createId(int idNum) {
        return String.format("GO:%07d", idNum);
    }

    @Override
    protected List<OntologyDocument> createBasicDocs() {
        return Arrays.asList(
                OntologyDocMocker.createGODoc(GO_0000001, "doc name 1"),
                OntologyDocMocker.createGODoc(GO_0000002, "doc name 2"),
                OntologyDocMocker.createGODoc(GO_0000003, "doc name 3"),
                OntologyDocMocker.createGODoc(GO_0000004, "doc name 4"));
    }

    @Override
    protected String idMissingInRepository() {
        return "GO:0000399";
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
