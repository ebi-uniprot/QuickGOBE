package uk.ac.ebi.quickgo.rest.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.filter.CorsFilter;

import static com.google.common.net.HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpHeaders.*;
import static uk.ac.ebi.quickgo.rest.controller.FilterProperties.DEFAULT_ACCESS_CONTROL_ALLOW_HEADERS;
import static uk.ac.ebi.quickgo.rest.controller.FilterProperties.DEFAULT_ACCESS_CONTROL_ALLOW_METHODS;

/**
 * This class tests that the all loaded CORS properties from a YAML file are used by the CORS filter as appropriate.
 *
 * Created 31/10/16
 * @author Edd
 */
@ActiveProfiles("cors-config-filtering-test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CORSFilterPropertiesTest.FakeApplication.class)
public class CORSFilterPropertiesTest {
    private static final String TEST_ORIGIN = "http://any-host";
    private static final String TEST_EXPOSED_HEADERS = "1st-exposed-header, 2nd-exposed-header";

    @Autowired
    private CorsFilter corsFilter;

    @Test
    public void checkAllowOriginIsPermitted() throws IOException, ServletException {
        MockHttpServletRequest request = createStandardRequest("GET");
        MockHttpServletResponse response = createResponse();

        corsFilter.doFilter(request, response, new MockFilterChain());
        assertThat(response.getHeader(ACCESS_CONTROL_ALLOW_ORIGIN), is(TEST_ORIGIN));
    }

    @Test
    public void checkDisallowedOriginIsNotPermitted() throws IOException, ServletException {
        String origin = "THIS-ORIGIN-IS-DISALLOWED";
        MockHttpServletRequest request = createStandardRequest(origin,"GET");
        request.addHeader(ORIGIN, origin);
        MockHttpServletResponse response = createResponse();

        corsFilter.doFilter(request, response, new MockFilterChain());
        assertThat(response.getStatus(), is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    public void checkAllowCredentialsIsShown() throws IOException, ServletException {
        MockHttpServletRequest request = createStandardRequest("GET");
        MockHttpServletResponse response = createResponse();

        corsFilter.doFilter(request, response, new MockFilterChain());
        assertThat(response.getHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS), is(String.valueOf(true)));
    }

    @Test
    public void checkAllAllowHeadersPermitted() throws IOException, ServletException {
        for (String allowHeader : DEFAULT_ACCESS_CONTROL_ALLOW_HEADERS) {
            MockHttpServletRequest request = createPreflightedRequest("GET");
            request.addHeader(ACCESS_CONTROL_REQUEST_HEADERS, allowHeader);
            MockHttpServletResponse response = createResponse();

            corsFilter.doFilter(request, response, new MockFilterChain());
            assertThat(response.getHeader(ACCESS_CONTROL_ALLOW_HEADERS), is(allowHeader));
        }
    }

    @Test
    public void checkDisallowedHeadersIsNotPermitted() throws IOException, ServletException {
        String allowHeader = "WE-DO-NOT-ALLOW-THIS-HEADER";
        MockHttpServletRequest request = createPreflightedRequest("GET");
        request.addHeader(ACCESS_CONTROL_REQUEST_HEADERS, allowHeader);
        MockHttpServletResponse response = createResponse();

        corsFilter.doFilter(request, response, new MockFilterChain());
        assertThat(response.getStatus(), is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    public void checkAllAllowMethodsArePermitted() throws IOException, ServletException {
        for (String allowMethod : DEFAULT_ACCESS_CONTROL_ALLOW_METHODS) {
            MockHttpServletRequest request = createPreflightedRequest(allowMethod);
            MockHttpServletResponse response = createResponse();

            corsFilter.doFilter(request, response, new MockFilterChain());
            assertThat(response.getHeader(ACCESS_CONTROL_ALLOW_METHODS), is(allowMethod));
        }
    }

    @Test
    public void checkDisallowedMethodsAreNotPermitted() throws IOException, ServletException {
        String allowMethod = "THIS-METHOD-IS-NOT-ALLOWED";
        MockHttpServletRequest request = createPreflightedRequest(allowMethod);
        MockHttpServletResponse response = createResponse();

        corsFilter.doFilter(request, response, new MockFilterChain());
        assertThat(response.getStatus(), is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    public void checkAllExposeHeadersAreShown() throws IOException, ServletException {
        MockHttpServletRequest request = createPreflightedRequest("GET");
        MockHttpServletResponse response = createResponse();

        corsFilter.doFilter(request, response, new MockFilterChain());
        assertThat(response.getHeader(ACCESS_CONTROL_EXPOSE_HEADERS), is(TEST_EXPOSED_HEADERS));
    }

    @Test
    public void checkMaxAgeIsShown() throws IOException, ServletException {
        MockHttpServletRequest request = createPreflightedRequest("GET");
        MockHttpServletResponse response = createResponse();

        corsFilter.doFilter(request, response, new MockFilterChain());
        assertThat(response.getHeader(ACCESS_CONTROL_MAX_AGE), is(String.valueOf(3600)));
    }

    private MockHttpServletRequest createStandardRequest(String origin, String method) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(ORIGIN, origin);
        request.setMethod(method);
        return request;
    }

    private MockHttpServletResponse createResponse() {
        return new MockHttpServletResponse();
    }

    private MockHttpServletRequest createPreflightedRequest(String requestMethod) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(ORIGIN, TEST_ORIGIN);
        request.setMethod("OPTIONS");
        request.addHeader(ACCESS_CONTROL_REQUEST_METHOD, requestMethod);
        return request;
    }

    private MockHttpServletRequest createStandardRequest(String method) {
        return createStandardRequest(TEST_ORIGIN, method);
    }

    @Profile("cors-config-filtering-test")
    @Configuration
    @EnableAutoConfiguration
    @Import(CORSConfig.class)
    public static class FakeApplication {}
}