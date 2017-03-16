package uk.ac.ebi.quickgo.annotation.controller;

import uk.ac.ebi.quickgo.annotation.AnnotationREST;

import java.util.Arrays;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Tony Wardell
 * Date: 07/10/2016
 * Time: 16:32
 * Created with IntelliJ IDEA.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {AnnotationREST.class})
@WebAppConfiguration
@TestPropertySource(properties = {"coterm.source.manual=fu", "coterm.source.all=bar"})
public class CoTermControllerFailedLoadIT {

    private static final String RESOURCE_URL = "/annotation/coterms";
    private static final String VALID_GO_TERM = "GO:7777771";

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    public void internalServerErrorIfCoTermFilesNotLoaded() throws Exception {
        ResultActions response = mockMvc.perform(get(buildPathToResource(VALID_GO_TERM)));
        response.andDo(print())
                .andExpect(status().isInternalServerError());
    }

    private String buildPathToResource(String id, String... args) {
        return RESOURCE_URL + "/" + id + Arrays.stream(args)
                .collect(Collectors.joining("&", "?", ""));
    }

}
