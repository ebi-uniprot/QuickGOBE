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
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static uk.ac.ebi.quickgo.rest.controller.CORSFilter2.*;

/**
 * Created 31/10/16
 * @author Edd
 */
@ActiveProfiles("cors-test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CORSFilterDefaultPropertiesTest.FakeApplication.class)
public class CORSFilterDefaultPropertiesTest {
    @Autowired
    private CORSFilter2 filter;
    private MockHttpServletResponse response;

    @Before
    public void setup() {
        response = new MockHttpServletResponse();
    }

    @Test
    public void checkAllowOriginUsesDefault() throws IOException, ServletException {
        filter.doFilter(new MockHttpServletRequest(), response, new MockFilterChain());
        assertThat(response.getHeader(ACCESS_CONTROL_ALLOW_ORIGIN), is(DEFAULT_ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    @Test
    public void checkAllowCredentialsUsesDefault() throws IOException, ServletException {
        filter.doFilter(new MockHttpServletRequest(), response, new MockFilterChain());
        assertThat(response.getHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS), is(DEFAULT_ACCESS_CONTROL_ALLOW_CREDENTIALS));
    }

    @Test
    public void checkAllowHeadersUsesDefault() throws IOException, ServletException {
        filter.doFilter(new MockHttpServletRequest(), response, new MockFilterChain());
        assertThat(response.getHeader(ACCESS_CONTROL_ALLOW_HEADERS), is(DEFAULT_ACCESS_CONTROL_ALLOW_HEADERS));
    }

    @Test
    public void checkAllowMethodsUsesDefault() throws IOException, ServletException {
        filter.doFilter(new MockHttpServletRequest(), response, new MockFilterChain());
        assertThat(response.getHeader(ACCESS_CONTROL_ALLOW_METHODS), is(DEFAULT_ACCESS_CONTROL_ALLOW_METHODS));
    }

    @Test
    public void checkExposeHeadersHasNoDefault() throws IOException, ServletException {
        filter.doFilter(new MockHttpServletRequest(), response, new MockFilterChain());
        assertThat(response.getHeader(ACCESS_CONTROL_EXPOSE_HEADERS), is(nullValue()));
    }

    @Test
    public void checkMaxAgeUsesDefault() throws IOException, ServletException {
        filter.doFilter(new MockHttpServletRequest(), response, new MockFilterChain());
        assertThat(response.getHeader(ACCESS_CONTROL_MAX_AGE), is(DEFAULT_ACCESS_CONTROL_MAX_AGE));
    }

    @Profile("cors-test")
    @Configuration
    @EnableAutoConfiguration
    @Import(CORSFilter2.class)
    public static class FakeApplication {}
}