package uk.ac.ebi.quickgo.rest.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static uk.ac.ebi.quickgo.rest.controller.CORSFilter.*;

/**
 * Created 31/10/16
 * @author Edd
 */
@ActiveProfiles("cors-test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CORSFilterReadingPropertiesTest.FakeApplication.class)
public class CORSFilterReadingPropertiesTest {
    @Autowired
    private CORSFilter filter;

    @Test
    public void checkAllowedOriginsWereOverriden() throws IOException, ServletException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(new MockHttpServletRequest(), response, new MockFilterChain());
        assertThat(response.getHeader(ACCESS_CONTROL_ALLOW_ORIGIN), is("my-origin"));
    }

    @Test
    public void checkAllowedCredentialsWereOverriden() throws IOException, ServletException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(new MockHttpServletRequest(), response, new MockFilterChain());
        assertThat(response.getHeader(ACCESS_CONTROL_ALLOW_HEADERS), is("my-headers"));
    }

    @Test
    public void checkAllowedHeadersWereOverriden() throws IOException, ServletException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(new MockHttpServletRequest(), response, new MockFilterChain());
        assertThat(response.getHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS), is("my-credentials"));
    }

    @Test
    public void checkAllowedMethodsWereOverriden() throws IOException, ServletException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(new MockHttpServletRequest(), response, new MockFilterChain());
        assertThat(response.getHeader(ACCESS_CONTROL_ALLOW_METHODS), is("my-methods"));
    }

    @Test
    public void checkExposedHeadersWereOverriden() throws IOException, ServletException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(new MockHttpServletRequest(), response, new MockFilterChain());
        assertThat(response.getHeader(ACCESS_CONTROL_EXPOSE_HEADERS), is("my-exposed-headers"));
    }

    @Test
    public void checkMaxAgeWasOverriden() throws IOException, ServletException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(new MockHttpServletRequest(), response, new MockFilterChain());
        assertThat(response.getHeader(ACCESS_CONTROL_MAX_AGE), is("my-max-age"));
    }

    @Profile("cors-test")
    @Configuration
    @EnableAutoConfiguration
    @PropertySource(value = "cors.test.properties")
    @Import(CORSFilter.class)
    public static class FakeApplication {}
}