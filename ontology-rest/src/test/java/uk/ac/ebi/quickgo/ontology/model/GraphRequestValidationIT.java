package uk.ac.ebi.quickgo.ontology.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.Validator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

/**
 * Check the operation of the java.validation.constraints for the GraphRepresentation class.
 * @author Tony Wardell
 * Date: 19/02/2018
 * Time: 11:10
 * Created with IntelliJ IDEA.
 */
@SpringBootTest(classes = GraphRequestValidationIT.GraphRequestValidationConfig.class)
class GraphRequestValidationIT {

    @Configuration
    static class GraphRequestValidationConfig {

        @Bean
        Validator validator() {
            return new LocalValidatorFactoryBean();
        }
    }

    @Autowired
    private Validator validator;

    private GraphRequest graphRequest;

    @BeforeEach
    void setUp() {
        graphRequest = new GraphRequest();
    }

    @Test
    void termBoxHeightMinimum1() {
        graphRequest.setTermBoxHeight(1);

        assertThat(validator.validate(graphRequest), hasSize(0));
    }

    @Test
    void termBoxHeightCannotBeZero() {
        graphRequest.setTermBoxHeight(0);

        assertThat(validator.validate(graphRequest), hasSize(1));
    }

    @Test
    void termBoxHeightCannotBeLessThan1() {
        graphRequest.setTermBoxHeight(-1);

        assertThat(validator.validate(graphRequest), hasSize(1));
    }

    @Test
    void fontSizeMinimum1() {
        graphRequest.setFontSize(1);

        assertThat(validator.validate(graphRequest), hasSize(0));
    }

    @Test
    void fontSizeCannotBeZero() {
        graphRequest.setFontSize(0);

        assertThat(validator.validate(graphRequest), hasSize(1));
    }

    @Test
    void fontSizeCannotBeLessThan1() {
        graphRequest.setFontSize(-1);

        assertThat(validator.validate(graphRequest), hasSize(1));
    }

    @Test
    void termBoxWidthMinimum1() {
        graphRequest.setTermBoxWidth(1);

        assertThat(validator.validate(graphRequest), hasSize(0));
    }

    @Test
    void termBoxWidthCannotBeZero() {
        graphRequest.setTermBoxWidth(0);

        assertThat(validator.validate(graphRequest), hasSize(1));
    }

    @Test
    void termBoxWidthCannotBeLessThan1() {
        graphRequest.setTermBoxWidth(-1);

        assertThat(validator.validate(graphRequest), hasSize(1));
    }
}
