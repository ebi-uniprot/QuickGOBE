package uk.ac.ebi.quickgo.ontology.controller;

import uk.ac.ebi.quickgo.ontology.OntologyREST;
import uk.ac.ebi.quickgo.ontology.common.coterms.CoTermSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Tony Wardell
 * Date: 07/10/2016
 * Time: 16:32
 * Created with IntelliJ IDEA.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {OntologyREST.class})
@WebAppConfiguration
public class CoTermControllerIT {

    private static final String RESOURCE_URL = "/ontology/go/coterms";
    private static final int NUMBER_OF_ALL_CO_TERM_RECORDS = 12;
    private static final String GO_0000001 = "GO:0000001";
    private static final String GO_9000001 = "GO:9000001";
    private static final String GO_TERM_INVALID = "GO:ABCDEFG";
    private static final String MANUAL_ONLY_TERM = "GO:8888881";
    private static final String ALL_ONLY_TERM = "GO:7777771";
    private static final String SOURCE_VALUES = Arrays.stream(CoTermSource.values())
            .map(CoTermSource::name)
            .collect(Collectors.joining(", "));
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    public void canRetrieveCoTermsForTerm() throws Exception {
        ResultActions response = mockMvc.perform(get(buildPathToResource(GO_0000001)));
        response.andDo(print());
        expectFieldsInResults(response, Collections.singletonList(GO_0000001))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.results.*", hasSize(NUMBER_OF_ALL_CO_TERM_RECORDS)))
                .andExpect(status().isOk());
    }

    @Test
    public void nothingRetrievedIfTermDoesntExist() throws Exception {
        ResultActions response = mockMvc.perform(get(buildPathToResource(GO_9000001)));
        response.andDo(print())
                .andExpect(jsonPath("$.results.*", hasSize(0)))
                .andExpect(status().isOk());
    }


    @Test
    public void errorReturnedIfTheRequestedGoTermIdIsInvalid() throws Exception {
        ResultActions response = mockMvc.perform(get(buildPathToResource(GO_TERM_INVALID)));
        response.andExpect(status().isBadRequest());
        expectInvalidGoTermErrorMessage(response, GO_TERM_INVALID);
    }

    // Test source parameter

    @Test
    public void retrieveManualCoTermsInformationWhenRequested() throws Exception {
        ResultActions response = mockMvc.perform(get(buildPathToResource(MANUAL_ONLY_TERM, "source=MANUAL")));

        expectFieldsInResults(response, Collections.singletonList(MANUAL_ONLY_TERM))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.results.*", hasSize(1)))
                .andExpect(jsonPath("$.results.*.target").value(MANUAL_ONLY_TERM))
                .andExpect(jsonPath("$.results.*.comparedTerm").value("GO:0004444"))
                .andExpect(jsonPath("$.results.*.probabilityRatio").value(302.4))
                .andExpect(jsonPath("$.results.*.similarityRatio").value(78.28))
                .andExpect(jsonPath("$.results.*.together").value(1933))
                .andExpect(jsonPath("$.results.*.compared").value(5219))
                .andExpect(status().isOk());
    }

    @Test
    public void retrieveAllCoTermsInformationWhenRequested() throws Exception {
        ResultActions response = mockMvc.perform(get(buildPathToResource(ALL_ONLY_TERM, "source=ALL")));

        expectFieldsInResults(response, Collections.singletonList(ALL_ONLY_TERM))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.results.*", hasSize(1)))
                .andExpect(jsonPath("$.results.*.target").value(ALL_ONLY_TERM))
                .andExpect(jsonPath("$.results.*.comparedTerm").value("GO:0003333"))
                .andExpect(jsonPath("$.results.*.probabilityRatio").value(486.4))
                .andExpect(jsonPath("$.results.*.similarityRatio").value(22.28))
                .andExpect(jsonPath("$.results.*.together").value(8632))
                .andExpect(jsonPath("$.results.*.compared").value(5778))
                .andExpect(status().isOk());
    }

    @Test
    public void sourceParameterShouldNotBeCaseSensitive() throws Exception {
        ResultActions response = mockMvc.perform(get(buildPathToResource(MANUAL_ONLY_TERM, "source=MaNuAl")));

        expectFieldsInResults(response, Collections.singletonList(MANUAL_ONLY_TERM))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.results.*", hasSize(1)))
                .andExpect(jsonPath("$.results.*.target").value(MANUAL_ONLY_TERM))
                .andExpect(jsonPath("$.results.*.comparedTerm").value("GO:0004444"))
                .andExpect(status().isOk());
    }

    @Test
    public void retrievesAllCoTermsWhenNoSourceProvided() throws Exception {
        ResultActions response = mockMvc.perform(get(buildPathToResource(GO_0000001, "source=")));
        expectFieldsInResults(response, Collections.singletonList(GO_0000001))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.results.*", hasSize(NUMBER_OF_ALL_CO_TERM_RECORDS)))
                .andExpect(status().isOk());
    }

    @Test
    public void errorIfValueForSourceIsUnknown() throws Exception {
        String requestedSource = "FUBAR";
        ResultActions response = mockMvc.perform(get(buildPathToResource(GO_0000001, "source=" + requestedSource)));
        response.andExpect(status().isBadRequest());
        expectInvalidSourceErrorMessage(response, requestedSource);
    }

    // Tests for limit parameter
    @Test
    public void setNumberOfResponsesBasedOnLimit() throws Exception {
        ResultActions response = mockMvc.perform(get(buildPathToResource(GO_0000001, "limit=4")));
        expectFieldsInResults(response, Collections.singletonList(GO_0000001))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.results.*", hasSize(4)));

        //Now we are going to reduce the requested limit to see if it works OK.
        response = mockMvc.perform(get(buildPathToResource(GO_0000001, "limit=3")));
        expectFieldsInResults(response, Collections.singletonList(GO_0000001))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.results.*", hasSize(3)))
                .andExpect(jsonPath("$.results[0].target").value(GO_0000001))
                .andExpect(jsonPath("$.results[0].comparedTerm").value(GO_0000001))
                .andExpect(jsonPath("$.results[0].probabilityRatio").value(16526.18))
                .andExpect(jsonPath("$.results[1].comparedTerm").value("GO:0034643"))
                .andExpect(jsonPath("$.results[1].probabilityRatio").value(16446.73))
                .andExpect(jsonPath("$.results[2].comparedTerm").value("GO:0090149"))
                .andExpect(jsonPath("$.results[2].probabilityRatio").value(12394.64))
                .andExpect(status().isOk());
    }

    @Test
    public void ifTheLimitIsLeftEmptyThenUserDefaultLimit() throws Exception {
        ResultActions response = mockMvc.perform(get(buildPathToResource(GO_0000001, "limit=")));
        expectFieldsInResults(response, Collections.singletonList(GO_0000001))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.results.*", hasSize(NUMBER_OF_ALL_CO_TERM_RECORDS)))
                .andExpect(status().isOk());
    }

    @Test
    public void ifTheLimitIsSetToZeroThenReturnNoResults() throws Exception {
        ResultActions response = mockMvc.perform(get(buildPathToResource(GO_0000001, "limit=0")));
        response.andExpect(status().isBadRequest());
        expectLimitErrorMessage(response);
    }

    @Test
    public void ifTheLimitIsSetToNegativeNumberThenExpectError() throws Exception {
        String requestedLimit = "-1";
        ResultActions response = mockMvc.perform(get(buildPathToResource(GO_0000001, "limit=" + requestedLimit)));
        response.andExpect(status().isBadRequest());
        expectLimitErrorMessage(response);
    }

    @Test
    public void ifTheLimitIsSetToNonNumberThenExpectError() throws Exception {
        String requestedLimit = "AAA";
        ResultActions response = mockMvc.perform(get(buildPathToResource(GO_0000001, "limit=" + requestedLimit)));
        response.andExpect(status().isBadRequest());
        expectLimitErrorMessage(response);
    }


    @Test
    public void numberOfHitsIsNotLimitedToRequestedLimit() throws Exception {
        ResultActions response = mockMvc.perform(get(buildPathToResource(GO_0000001, "limit=4")));
        expectFieldsInResults(response, Collections.singletonList(GO_0000001))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.results.*", hasSize(4)))
                .andExpect(jsonPath("$.numberOfHits", is(equalTo(12))));
    }


    // Tests for similarity threshold

    @Test
    public void retrieveAllCoTermsUsingSimilarityThresholdBelowThatFoundInTheRecordsForAllCoTerms() throws Exception {
        ResultActions response = mockMvc.perform(get(buildPathToResource(GO_0000001, "similarityThreshold=0.1")));
        expectFieldsInResults(response, Collections.singletonList(GO_0000001))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.results.*", hasSize(NUMBER_OF_ALL_CO_TERM_RECORDS)))
                .andExpect(status().isOk());
    }

    @Test
    public void noCoTermsRetrievedWhenSimilarityThresholdAboveThatFoundInTheRecordsForAllCoTerms() throws Exception {
        ResultActions response = mockMvc.perform(get(buildPathToResource(GO_0000001, "similarityThreshold=101")));
        response.andDo(print())
                .andExpect(jsonPath("$.results.*", hasSize(0)))
                .andExpect(status().isOk());
    }

    @Test
    public void useValueForSimilarityThresholdThatReturnsOnlyOneRecord() throws Exception {
        ResultActions response = mockMvc.perform(get(buildPathToResource(GO_0000001, "similarityThreshold=99.9")));
        response.andDo(print());
        expectFieldsInResults(response, Collections.singletonList(GO_0000001))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.results.*", hasSize(1)))
                .andExpect(jsonPath("$.results.*.target").value(GO_0000001))
                .andExpect(jsonPath("$.results.*.comparedTerm").value(GO_0000001))
                .andExpect(jsonPath("$.results.*.similarityRatio").value(100.0))
                .andExpect(status().isOk());
    }

    @Test
    public void returnsAllCoTermsWhenSimilarityNotFilledIn() throws Exception {
        ResultActions response = mockMvc.perform(get(buildPathToResource(GO_0000001, "similarityThreshold=")));
        expectFieldsInResults(response, Collections.singletonList(GO_0000001))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.results.*", hasSize(NUMBER_OF_ALL_CO_TERM_RECORDS)))
                .andExpect(status().isOk());
    }

    private String buildPathToResource(String id, String... args) {
        return RESOURCE_URL + "/" + id + Arrays.stream(args)
                .collect(Collectors.joining("&", "?", ""));
    }

    private ResultActions expectFieldsInResults(ResultActions result, List<String> ids) throws Exception {
        int index = 0;

        for (String id : ids) {
            expectBasicFields(result, id, "$.results[" + index++ + "].");
        }

        return result;
    }

    private void expectBasicFields(ResultActions result, String id, String path) throws Exception {
        result
                .andDo(print())
                .andExpect(jsonPath(path + "target").value(id))
                .andExpect(jsonPath(path + "comparedTerm").exists())
                .andExpect(jsonPath(path + "probabilityRatio").exists())
                .andExpect(jsonPath(path + "similarityRatio").exists())
                .andExpect(jsonPath(path + "together").exists())
                .andExpect(jsonPath(path + "compared").exists());
    }

    private void expectInvalidGoTermErrorMessage(ResultActions result, String id) throws Exception {
        result
                .andDo(print())
                .andExpect(jsonPath("$.messages", hasItem(containsString("Provided ID: '" + id + "' is invalid"))));
    }


    private void expectInvalidSourceErrorMessage(ResultActions result, String requestedSource) throws Exception {
        result
                .andDo(print())
                .andExpect(jsonPath("$.messages", hasItem(containsString("The value for source should be one of " +
                        SOURCE_VALUES + ". '" + requestedSource + "' is not a valid value for source."))));
    }

    private void expectLimitErrorMessage(ResultActions result) throws Exception {
        result
                .andDo(print())
                .andExpect(jsonPath("$.messages", hasItem(containsString("The value for limit should be a positive integer, or 'ALL'"))));
    }
}
