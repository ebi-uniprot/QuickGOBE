package uk.ac.ebi.quickgo.rest.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static uk.ac.ebi.quickgo.rest.controller.CORSFilter2.*;

/**
 * Created 31/10/16
 * @author Edd
 */
@ActiveProfiles("cors-test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CORSFilterReadingPropertiesTest.FakeApplication.class)
public class CORSFilterReadingPropertiesTest {
    @Autowired
    private CORSFilter2 filter;
    private MockHttpServletResponse response;

    @Before
    public void setup() {
        response = new MockHttpServletResponse();
    }

    @Test
    public void checkAllowedOriginsWereOverridden() throws IOException, ServletException {
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        String requestHost = "www.this.is.okay.com";
        String requestOrigin = "http://" + requestHost;
        servletRequest.addHeader(ACCESS_CONTROL_ALLOW_ORIGIN, requestOrigin);
        filter.doFilter(servletRequest, response, new MockFilterChain());
        assertThat(response.getHeader(ACCESS_CONTROL_ALLOW_ORIGIN), is(requestOrigin));
    }

    @Test
    public void checkAllowedCredentialsWereOverridden() throws IOException, ServletException {
        filter.doFilter(new MockHttpServletRequest(), response, new MockFilterChain());
        assertThat(response.getHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS), is("my-credentials"));
    }

    @Test
    public void checkAllowedHeadersWereOverridden() throws IOException, ServletException {
        filter.doFilter(new MockHttpServletRequest(), response, new MockFilterChain());
        assertThat(response.getHeader(ACCESS_CONTROL_ALLOW_HEADERS), is("my-headers"));
    }

    @Test
    public void checkAllowedMethodsWereOverridden() throws IOException, ServletException {
        filter.doFilter(new MockHttpServletRequest(), response, new MockFilterChain());
        assertThat(response.getHeader(ACCESS_CONTROL_ALLOW_METHODS), is("my-methods"));
    }

    @Test
    public void checkExposedHeadersWereOverridden() throws IOException, ServletException {
        filter.doFilter(new MockHttpServletRequest(), response, new MockFilterChain());
        assertThat(response.getHeader(ACCESS_CONTROL_EXPOSE_HEADERS), is("my-exposed-headers"));
    }

    @Test
    public void checkMaxAgeWasOverridden() throws IOException, ServletException {
        filter.doFilter(new MockHttpServletRequest(), response, new MockFilterChain());
        assertThat(response.getHeader(ACCESS_CONTROL_MAX_AGE), is("my-max-age"));
    }

    @Profile("cors-test")
    @Configuration
    @EnableAutoConfiguration
    @PropertySource(value = "cors.test.properties")
    @Import(CORSFilter2.class)
    public static class FakeApplication {}
}