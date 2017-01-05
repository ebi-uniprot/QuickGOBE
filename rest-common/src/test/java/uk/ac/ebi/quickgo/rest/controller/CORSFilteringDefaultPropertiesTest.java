package uk.ac.ebi.quickgo.rest.controller;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import javax.servlet.ServletException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * This class demonstrates that the absence of CORS properties specified in a YAML file, does not cause
 * the application to fail at start up, and shows that default CORS properties were loaded.
 *
 * Created 31/10/16
 * @author Edd
 */
@ActiveProfiles("cors-test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CORSFilteringDefaultPropertiesTest.FakeApplication.class)
public class CORSFilteringDefaultPropertiesTest {
    @Autowired
    private CorsFilter corsFilter;

    @Autowired
    private UrlBasedCorsConfigurationSource corsConfigurationSource;

    @Test
    public void corsFilterInAbsenceOfCORSPropertiesIsNotNull() throws IOException, ServletException {
        assertThat(corsFilter, is(not(nullValue())));
    }

    @Test
    public void ensureDefaultCORSPropertiesWereLoaded() {
        assertThat(corsConfigurationSource, is(notNullValue()));
        Map<String, CorsConfiguration> corsConfigurations = corsConfigurationSource.getCorsConfigurations();
        assertThat(corsConfigurations.keySet(), contains(CORSFilterProperties.DEFAULT_PATH));
        CorsConfiguration corsConfiguration = corsConfigurations.get(CORSFilterProperties.DEFAULT_PATH);

        assertThat(corsConfiguration.getAllowCredentials(), is(CORSFilterProperties.DEFAULT_ACCESS_CONTROL_ALLOW_CREDENTIALS));
        assertThat(corsConfiguration.getMaxAge(), is(CORSFilterProperties.DEFAULT_ACCESS_CONTROL_MAX_AGE));

        compareCollections(CORSFilterProperties.DEFAULT_ACCESS_CONTROL_ALLOW_HEADERS, corsConfiguration.getAllowedHeaders());
        compareCollections(CORSFilterProperties.DEFAULT_ACCESS_CONTROL_ALLOW_METHODS, corsConfiguration.getAllowedMethods());
        compareCollections(CORSFilterProperties.DEFAULT_EXPOSE_HEADERS, corsConfiguration.getExposedHeaders());
        compareCollections(CORSFilterProperties.DEFAULT_ACCESS_CONTROL_ALLOW_ORIGIN, corsConfiguration.getAllowedOrigins());
    }

    private <T> void compareCollections(Collection<T> expected, Collection<T> actual) {
        if (expected.isEmpty()) {
            assertThat(actual, is(nullValue()));
        } else {
            assertThat(actual, contains(expected.toArray()));
        }
    }

    @Profile("cors-test")
    @Configuration
    @EnableAutoConfiguration
    @Import(CORSConfig.class)
    public static class FakeApplication {}
}