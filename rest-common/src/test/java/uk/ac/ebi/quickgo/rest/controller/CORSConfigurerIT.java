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
import org.springframework.web.filter.CorsFilter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.ac.ebi.quickgo.rest.controller.CORSFilter2.ACCESS_CONTROL_ALLOW_ORIGIN;
import static uk.ac.ebi.quickgo.rest.controller.FakeCORSFilteringRESTApp.RESOURCE_1_URL;
import static uk.ac.ebi.quickgo.rest.controller.FakeCORSFilteringRESTApp.RESOURCE_2_SUB_RESOURCE_URL;
import static uk.ac.ebi.quickgo.rest.controller.FakeCORSFilteringRESTApp.RESOURCE_2_URL;

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
    private MockMvc mockMvc;
    @Autowired private
    CorsFilter corsFilter;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .addFilter(corsFilter)
                .build();
    }

    @Test
    public void firstOriginInPropertiesForFirstResourceIsAccepted() throws Exception {
        String origin = "http://www.ebi.ac.uk";

        MvcResult result = mockMvc.perform(
                get(RESOURCE_1_URL)
                        .headers(originHeader(origin)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getResponse().getHeader(ACCESS_CONTROL_ALLOW_ORIGIN), is(origin));
    }

    @Test
    public void secondOriginInPropertiesForFirstResourceIsAccepted() throws Exception {
        String origin = "http://localhost:9090";

        MvcResult result = mockMvc.perform(
                get(RESOURCE_1_URL)
                        .headers(originHeader(origin)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getResponse().getHeader(ACCESS_CONTROL_ALLOW_ORIGIN), is(origin));
    }

    @Test
    public void originInPropertiesWithWrongPortForFirstResourceIsForbidden() throws Exception {
        String origin = "http://localhost:9999";

        mockMvc.perform(
                get(RESOURCE_1_URL)
                        .headers(originHeader(origin)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void firstOriginInPropertiesForSecondResourceIsAccepted() throws Exception {
        String origin = "http://wwwdev.ebi.ac.uk:1234";

        MvcResult result = mockMvc.perform(
                get(RESOURCE_2_URL)
                        .headers(originHeader(origin)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getResponse().getHeader(ACCESS_CONTROL_ALLOW_ORIGIN), is(origin));
    }

    private HttpHeaders originHeader(String origin) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setOrigin(origin);
        return httpHeaders;
    }

    @Test
    public void firstOriginInPropertiesWithWrongPortForSecondResourceIsAccepted() throws Exception {
        String origin = "http://wwwdev.ebi.ac.uk:9999";

        mockMvc.perform(
                get(RESOURCE_2_URL)
                        .headers(originHeader(origin)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void firstOriginInPropertiesWithNoPortForSecondResourceIsAccepted() throws Exception {
        String origin = "http://wwwdev.ebi.ac.uk";

        mockMvc.perform(
                get(RESOURCE_2_URL)
                        .headers(originHeader(origin)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void firstOriginInPropertiesForSubResourceIsAccepted() throws Exception {
        String origin = "http://wwwdev.ebi.ac.uk:1234";

        MvcResult result = mockMvc.perform(
                get(RESOURCE_2_SUB_RESOURCE_URL)
                        .headers(originHeader(origin)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getResponse().getHeader(ACCESS_CONTROL_ALLOW_ORIGIN), is(origin));
    }
}
