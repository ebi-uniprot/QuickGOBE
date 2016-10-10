package uk.ac.ebi.quickgo.ontology.controller;

import uk.ac.ebi.quickgo.ontology.OntologyREST;

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


    private static final String RESOURCE_URL = "/QuickGO/services/go/coterms";

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private static final String GO_0000001 = "GO:0000001";
    private static final String GO_9000001 = "GO:9000001";
    private static final String MANUAL_ONLY_TERM = "GO:8888881";

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
                .andExpect(jsonPath("$.results.*", hasSize(11)))
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
    public void retrieveManualCoTermsInformationWhenRequested() throws Exception {
        ResultActions response = mockMvc.perform(get(buildPathToResource(MANUAL_ONLY_TERM, "source=MANUAL")));

        expectFieldsInResults(response, Arrays.asList(MANUAL_ONLY_TERM))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.results.*", hasSize(1)))
                .andExpect(jsonPath("$.results.*.compare").value("GO:0004444"))
                .andExpect(jsonPath("$.results.*.probabilityRatio").value(302.4))
                .andExpect(jsonPath("$.results.*.significance").value(78.28))
                .andExpect(jsonPath("$.results.*.together").value(1933))
                .andExpect(jsonPath("$.results.*.compared").value(5219))
                .andExpect(status().isOk());
    }

    private String buildPathToResource(String id) {
        return RESOURCE_URL + "/" + id;
    }

    private String buildPathToResource(String id, String... args) {
        return RESOURCE_URL + "/" + id + Arrays.stream(args)
                .collect(Collectors.joining("&","?",""));
    }

    protected ResultActions expectFieldsInResults(ResultActions result, List<String> ids) throws Exception {
        int index = 0;

        for (String id : ids) {
            expectBasicFields(result, id, "$.results[" + index++ + "].");
        }

        return result;
    }

    protected ResultActions expectBasicFields(ResultActions result, String id, String path) throws Exception {
        return result
                .andDo(print())
                .andExpect(jsonPath(path + "id").value(id))
                .andExpect(jsonPath(path + "compare").exists())
                .andExpect(jsonPath(path + "probabilityRatio").exists())
                .andExpect(jsonPath(path + "significance").exists())
                .andExpect(jsonPath(path + "together").exists())
                .andExpect(jsonPath(path + "compared").exists());
    }
}
