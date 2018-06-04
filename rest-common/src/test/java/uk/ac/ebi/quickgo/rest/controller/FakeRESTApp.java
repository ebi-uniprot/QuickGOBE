package uk.ac.ebi.quickgo.rest.controller;

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
 * A fake RESTful application that returns a single configurable resource.
 *
 * Created 15/12/16
 * @author Edd
 */
@Profile("allow-origins-integration-test")
@SpringBootApplication
@ComponentScan({"uk.ac.ebi.quickgo.rest"})
@Import({FakeRESTApp.FakeController.class})
public class FakeRESTApp {
    static final String RESOURCE_1_URL = "/resource1";

    public static void main(String[] args) {
        SpringApplication.run(FakeRESTApp.class, args);
    }

    @Profile("allow-origins-integration-test")
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
    }

    @Profile("allow-origins-integration-test")
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