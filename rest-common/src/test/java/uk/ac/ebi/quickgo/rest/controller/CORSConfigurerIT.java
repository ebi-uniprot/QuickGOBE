package uk.ac.ebi.quickgo.rest.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.ac.ebi.quickgo.rest.controller.FakeCORSFilteringRESTApp.RESOURCE_1_URL;

/**
 * Created 21/12/16
 * @author Edd
 */
@ActiveProfiles("test-cors-filter")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {FakeCORSFilteringRESTApp.class})
@WebAppConfiguration
public class CORSConfigurerIT {
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private FakeCORSFilteringRESTApp.FakeController corsFilteringController;
    private MockMvc mockMvc;
    @Autowired
    CORSConfigurer corsConfigurer;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(corsFilteringController)
//                .webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    public void canFetchResult() throws Exception {
        String origin = "http://localhost:9090";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setOrigin(origin);

        MvcResult result = mockMvc.perform(
                get(RESOURCE_1_URL)
                        .headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        result.getResponse().getHeaderNames();
    }

}
