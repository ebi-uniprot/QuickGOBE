package uk.ac.ebi.quickgo.rest.controller;

/**
 * Created 21/12/16
 * @author Edd
 */

import uk.ac.ebi.quickgo.common.SearchableField;

import java.util.stream.Stream;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * A fake RESTful application that returns a single resource, which is configurable via the
 * constructor.
 *
 * Created 15/12/16
 * @author Edd
 */
@Profile("test-cors-filter")
@SpringBootApplication
@ComponentScan({"uk.ac.ebi.quickgo.rest"})
@Import({FakeCORSFilteringRESTApp.FakeController.class})
public class FakeCORSFilteringRESTApp {
    static final String RESOURCE_1_URL = "/resource1";
    static final String RESOURCE_2_URL = "/resource2";

    public static void main(String[] args) {
        SpringApplication.run(FakeCORSFilteringRESTApp.class, args);
    }

    @Profile("test-cors-filter")
    @RestController static class FakeController {
        private String value;
        private final static String DEFAULT_VALUE = "value";

        FakeController() {
            this.value = DEFAULT_VALUE;
        }

        void setValue(String value) {
            this.value = value;
        }

        String getValue() {
            return value;
        }

        @RequestMapping(value = RESOURCE_1_URL, method = {RequestMethod.GET},
                produces = {MediaType.APPLICATION_JSON_VALUE})
        public String getResource1() {
            return "{ resource1Attribute : " + this.value+" }";
        }

        @RequestMapping(value = RESOURCE_2_URL, method = {RequestMethod.GET},
                produces = {MediaType.APPLICATION_JSON_VALUE})
        public String getResource2() {
            return "{ resource2Attribute : " + this.value+" }";
        }
    }

    @Profile("test-cors-filter")
    @Configuration static class FakeAppConfig {
        @Bean SearchableField searchableField() {
            return new SearchableField() {
                @Override public boolean isSearchable(String field) {
                    return false;
                }

                @Override public Stream<String> searchableFields() {
                    return Stream.empty();
                }
            };
        }
    }
}