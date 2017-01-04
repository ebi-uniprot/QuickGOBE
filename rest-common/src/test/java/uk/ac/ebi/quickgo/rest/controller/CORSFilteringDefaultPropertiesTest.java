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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.filter.CorsFilter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

/**
 * This class demonstrates that the absence of CORS properties specified in a YAML file, does not cause
 * the application to fail at start up.
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

    @Test
    public void corsFilterInAbsenceOfCORSPropertiesIsNotNull() throws IOException, ServletException {
        assertThat(corsFilter, is(not(nullValue())));
    }

    @Profile("cors-test")
    @Configuration
    @EnableAutoConfiguration
    @Import(CORSConfig.class)
    public static class FakeApplication {}
}