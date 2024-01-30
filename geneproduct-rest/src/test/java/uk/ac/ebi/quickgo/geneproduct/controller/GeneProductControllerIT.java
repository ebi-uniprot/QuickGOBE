package uk.ac.ebi.quickgo.geneproduct.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.ac.ebi.quickgo.common.store.TemporarySolrDataStore;
import uk.ac.ebi.quickgo.geneproduct.GeneProductREST;
import uk.ac.ebi.quickgo.geneproduct.common.GeneProductDocument;
import uk.ac.ebi.quickgo.geneproduct.common.GeneProductRepository;
import uk.ac.ebi.quickgo.geneproduct.common.common.GeneProductDocMocker;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Performs tests on GeneProduct REST controller.
 * Uses an embedded Solr server that is cleaned up automatically after tests complete.
 *
 * @author Tony Wardell
 * Date: 04/04/2016
 */
// temporary data store for solr's data, which is automatically cleaned on exit
@ExtendWith(TemporarySolrDataStore.class)
@SpringBootTest(classes = {GeneProductREST.class})
@WebAppConfiguration
class GeneProductControllerIT {
    private static final String RESOURCE_URL = "/geneproduct";

    protected static final String COMMA = ",";
    public static final String NON_EXISTANT_ID = "Y0Y000";
    public static final String INVALID_ID = "ZZZZ";

    private static final String VALID_TARGET_SET_NAME = "KRUK";
    private static final String NON_EXISTENT_TARGET_SET_NAME = "BLAH";

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private GeneProductRepository geneProductRepository;

    private MockMvc mockMvc;

    private String validId;
    private String validIdsCSV;
    private List<String> validIdList;

    @BeforeEach
    void setup() {
        geneProductRepository.deleteAll();

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        List<GeneProductDocument> basicDocs = createBasicDocs();
        assertThat(basicDocs.size(), is(greaterThan(1)));

        validId = basicDocs.get(0).id;
        validIdsCSV = basicDocs.stream().map(doc -> doc.id).collect(Collectors.joining(","));
        validIdList = Arrays.asList(validIdsCSV.split(COMMA));

        geneProductRepository.saveAll(basicDocs);
    }

    @Test
    void canRetrieveOneGeneProductById() throws Exception {
        ResultActions response = mockMvc.perform(get(buildGeneProductURL(validId)));

        response.andDo(print())
                .andExpect(jsonPath("$.results.*.id", hasSize(1)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void canRetrieveOneGeneProductByComplexPortalId() throws Exception {
        geneProductRepository.deleteAll();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        List<GeneProductDocument> basicDocs = createBasicComplexPortalDocs();
        geneProductRepository.saveAll(basicDocs);
        ResultActions response = mockMvc.perform(get(buildGeneProductURL(basicDocs.get(0).id)));

        response.andDo(print())
                .andExpect(jsonPath("$.results.*.id", hasSize(1)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void canRetrieveMultiGeneProductById() throws Exception {
        ResultActions result = mockMvc.perform(get(buildGeneProductURL(validIdsCSV)));

        result.andDo(print())
                .andExpect(jsonPath("$.results.*.id", hasSize(3)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        int index = 0;
        for (String id : validIdList) {
            expectFields(result, id, "$.results[" + index++ + "].");
        }
    }

    @Test
    void finds400IfUrlIsEmpty() throws Exception {
        mockMvc.perform(get(RESOURCE_URL + "/"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void finds400IfTermsIdIsEmpty() throws Exception {
        mockMvc.perform(get(buildGeneProductURL("")))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    void finds400IfIdIsInvalid() throws Exception {
        mockMvc.perform(get(buildGeneProductURL(INVALID_ID)))
                .andDo(print())
                .andExpect(jsonPath("$.messages", hasItem(is("Provided ID: '" + INVALID_ID + "' is invalid"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void finds200IfNoResultsBecauseIdsDoNotExist() throws Exception {
        mockMvc.perform(get(buildGeneProductURL(NON_EXISTANT_ID)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfHits").value(0))
                .andExpect(jsonPath("$.results").isArray());
    }

    @Test
    void targetSetLookUpUsingValidValueReturnsMultiGeneProduct() throws Exception {
        ResultActions result = mockMvc.perform(get(buildGeneProductTargetSetURL(VALID_TARGET_SET_NAME)));

        result.andDo(print())
                .andExpect(jsonPath("$.results.*.id", hasSize(3)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        int index = 0;
        for (String id : validIdList) {
            expectFields(result, id, "$.results[" + index++ + "].");
        }
    }

    @Test
    void targetSetLookUpUsingInvalidValueReturnsEmptyResults() throws Exception {
        ResultActions result = mockMvc.perform(get(buildGeneProductTargetSetURL(NON_EXISTENT_TARGET_SET_NAME)));
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfHits").value(0))
                .andExpect(jsonPath("$.results").isArray());
    }

    @Test
    void targetSetLookUpUsingEmptyStringReturnsBadRequest() throws Exception {
        ResultActions result = mockMvc.perform(get(buildGeneProductTargetSetURL("")));
        result.andDo(print())
                .andExpect(jsonPath("$.messages", hasItem(is("Provided ID: 'targetset' is invalid"))))
                .andExpect(status().isBadRequest());
    }

    private ResultActions expectFields(ResultActions result, String id, String path) throws Exception {
        return result
                .andDo(print())
                .andExpect(jsonPath(path + "id").value(id))
                .andExpect(jsonPath(path + "type").value("PROTEIN"))
                .andExpect(jsonPath(path + "taxonId").value(35758))
                .andExpect(jsonPath(path + "symbol").value("Streptomyces ghanaensis - symbol"))
                .andExpect(jsonPath(path + "parentId").value("UniProtKB:OK0206"))
                .andExpect(jsonPath(path + "databaseSubset").value("RRR"))
                .andExpect(jsonPath(path + "name").value("moeA5"))
                .andExpect(jsonPath(path + "synonyms[0]").value("3SSW23"))
                .andExpect(jsonPath(path + "proteome").value("complete"));
    }

    private String buildGeneProductURL(String id) {
        return RESOURCE_URL + "/" + id;
    }

    private String buildGeneProductTargetSetURL(String id) {
        return RESOURCE_URL + "/targetset/" + id;
    }

    private List<GeneProductDocument> createBasicDocs() {
        return Arrays.asList(
                GeneProductDocMocker.createDocWithId("A0A000"),
                GeneProductDocMocker.createDocWithId("A0A001"),
                GeneProductDocMocker.createDocWithId("A0A002"));
    }

    private List<GeneProductDocument> createBasicComplexPortalDocs() {
        return Collections.singletonList(GeneProductDocMocker.createDocWithId("CPX-1004"));
    }
}
