package uk.ac.ebi.quickgo.annotation.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.ac.ebi.quickgo.annotation.AnnotationREST;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test the failure to load the metadata information.
 *
 * @author Tony Wardell
 * Date: 09/03/2017
 * Time: 13:53
 * Created with IntelliJ IDEA.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {AnnotationREST.class})
@WebAppConfiguration
@TestPropertySource(locations="classpath:metadata-source-fails.properties")
public class AnnotationControllerMetaDataSourceFailIT {

    private static final String RESOURCE_URL = "/annotation";
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    // ------------------------------- Check about information -------------------------------
    @Test
    public void about() throws Exception {
        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/about"));
        response.andDo(print())
                .andExpect(status().isInternalServerError());
    }
}
