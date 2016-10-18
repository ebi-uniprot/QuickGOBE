package uk.ac.ebi.quickgo.ontology.controller;

import uk.ac.ebi.quickgo.ontology.OntologyREST;
import uk.ac.ebi.quickgo.ontology.common.coterms.CoTermSource;

import java.util.Arrays;
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
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
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
@SpringApplicationConfiguration(classes = {OntologyREST.class}) //holds coterm config via service config
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

        expectFieldsInResults(response, Arrays.asList(GO_0000001))
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

        expectFieldsInResults(response, Arrays.asList(MANUAL_ONLY_TERM))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.results.*", hasSize(1)))
                .andExpect(jsonPath("$.results.*.id").value(MANUAL_ONLY_TERM))
                .andExpect(jsonPath("$.results.*.compare").value("GO:0004444"))
                .andExpect(jsonPath("$.results.*.probabilityRatio").value(302.4))
                .andExpect(jsonPath("$.results.*.significance").value(78.28))
                .andExpect(jsonPath("$.results.*.together").value(1933))
                .andExpect(jsonPath("$.results.*.compared").value(5219))
                .andExpect(status().isOk());
    }

    @Test
    public void retrieveAllCoTermsInformationWhenRequested() throws Exception {
        ResultActions response = mockMvc.perform(get(buildPathToResource(ALL_ONLY_TERM, "source=ALL")));

        expectFieldsInResults(response, Arrays.asList(ALL_ONLY_TERM))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.results.*", hasSize(1)))
                .andExpect(jsonPath("$.results.*.id").value(ALL_ONLY_TERM))
                .andExpect(jsonPath("$.results.*.compare").value("GO:0003333"))
                .andExpect(jsonPath("$.results.*.probabilityRatio").value(486.4))
                .andExpect(jsonPath("$.results.*.significance").value(22.28))
                .andExpect(jsonPath("$.results.*.together").value(8632))
                .andExpect(jsonPath("$.results.*.compared").value(5778))
                .andExpect(status().isOk());
    }

    @Test
    public void sourceParameterShouldNotBeCaseSensitive() throws Exception {
        ResultActions response = mockMvc.perform(get(buildPathToResource(MANUAL_ONLY_TERM, "source=MaNuAl")));

        expectFieldsInResults(response, Arrays.asList(MANUAL_ONLY_TERM))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.results.*", hasSize(1)))
                .andExpect(jsonPath("$.results.*.id").value(MANUAL_ONLY_TERM))
                .andExpect(jsonPath("$.results.*.compare").value("GO:0004444"))
                .andExpect(status().isOk());
    }

    @Test
    public void doNotExpectErrorIfValueForSourceIsLeftBlank() throws Exception {
        ResultActions response = mockMvc.perform(get(buildPathToResource(GO_0000001, "source=")));
        expectFieldsInResults(response, Arrays.asList(GO_0000001))
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

        ResultActions response = mockMvc.perform(get(buildPathToResource(GO_0000001, "limit=3")));

        expectFieldsInResults(response, Arrays.asList(GO_0000001))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.results.*", hasSize(3)))
                .andExpect(jsonPath("$.results[0].id").value(GO_0000001))
                .andExpect(jsonPath("$.results[0].compare").value(GO_0000001))
                .andExpect(jsonPath("$.results[0].probabilityRatio").value(16526.18))
                .andExpect(jsonPath("$.results[1].compare").value("GO:0034643"))
                .andExpect(jsonPath("$.results[1].probabilityRatio").value(16446.73))
                .andExpect(jsonPath("$.results[2].compare").value("GO:0090149"))
                .andExpect(jsonPath("$.results[2].probabilityRatio").value(12394.64))
                .andExpect(status().isOk());
    }

    @Test
    public void ifTheLimitIsLeftEmptyTheError() throws Exception {
        ResultActions response = mockMvc.perform(get(buildPathToResource(GO_0000001, "limit=")));
        response.andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void ifTheLimitIsSetToZeroThenExpectError() throws Exception {
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
    }

    // Tests for similarity threshold

    @Test
    public void retrieveAllCoTermsUsingSimilarityThresholdBelowThatFoundInTheRecordsForAllCoTerms() throws Exception {
        ResultActions response = mockMvc.perform(get(buildPathToResource(GO_0000001, "similarityThreshold=0.1")));
        expectFieldsInResults(response, Arrays.asList(GO_0000001))
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
    public void useValueForSimiliaryThresholdThatReturnsOnlyOneRecord() throws Exception {
        ResultActions response = mockMvc.perform(get(buildPathToResource(GO_0000001, "similarityThreshold=99.9")));
        expectFieldsInResults(response, Arrays.asList(GO_0000001))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.results.*", hasSize(1)))
                .andExpect(jsonPath("$.results.*.id").value(GO_0000001))
                .andExpect(jsonPath("$.results.*.compare").value(GO_0000001))
                .andExpect(jsonPath("$.results.*.significance").value(100.0))
                .andExpect(status().isOk());
    }

    @Test
    public void doNotGetErrorIfValueForThresholdIsLeftBlank() throws Exception {
        ResultActions response = mockMvc.perform(get(buildPathToResource(GO_0000001, "similarityThreshold=")));
        expectFieldsInResults(response, Arrays.asList(GO_0000001))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.results.*", hasSize(NUMBER_OF_ALL_CO_TERM_RECORDS)))
                .andExpect(status().isOk());
    }

    private String buildPathToResource(String id) {
        return RESOURCE_URL + "/" + id;
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

    private ResultActions expectBasicFields(ResultActions result, String id, String path) throws Exception {
        return result
                .andDo(print())
                .andExpect(jsonPath(path + "id").value(id))
                .andExpect(jsonPath(path + "compare").exists())
                .andExpect(jsonPath(path + "probabilityRatio").exists())
                .andExpect(jsonPath(path + "significance").exists())
                .andExpect(jsonPath(path + "together").exists())
                .andExpect(jsonPath(path + "compared").exists());
    }

    private ResultActions expectInvalidGoTermErrorMessage(ResultActions result, String id) throws Exception {
        return result
                .andDo(print())
                .andExpect(jsonPath("$.messages", hasItem(containsString("Provided ID: '" + id + "' is invalid"))));
    }


    private ResultActions expectInvalidSourceErrorMessage(ResultActions result, String requestedSource) throws
                                                                                                        Exception {
        return result
                .andDo(print())
                .andExpect(jsonPath("$.messages", hasItem(containsString("The value for source should be one of " +
                        SOURCE_VALUES + " and not " + requestedSource))));
    }

    private ResultActions expectInvalidLimitErrorMessage(ResultActions result) throws
                                                                               Exception {
        return result
                .andDo(print())
                .andExpect(jsonPath("$.messages",
                        hasItem(containsString("The value for co-occurring terms limit is not ALL, or a number"))));
    }

    private ResultActions expectLimitErrorMessage(ResultActions result) throws Exception {
        return result
                .andDo(print())
                .andExpect(jsonPath("$.messages", hasItem(containsString("The findCoTerms limit should not be less than 1."))));
    }
}
