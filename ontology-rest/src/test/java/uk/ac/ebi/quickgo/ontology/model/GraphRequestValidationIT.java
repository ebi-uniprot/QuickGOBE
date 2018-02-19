package uk.ac.ebi.quickgo.ontology.model;

import javax.validation.Validator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

/**
 * Check the operation of the java.validation.constraints for the GraphRepresentation class.
 * @author Tony Wardell
 * Date: 19/02/2018
 * Time: 11:10
 * Created with IntelliJ IDEA.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = GraphRequestValidationIT.GraphRequestValidationConfig.class)
public class GraphRequestValidationIT {

    static class GraphRequestValidationConfig {

        @Bean
        Validator validator() {
            return new LocalValidatorFactoryBean();
        }
    }

    @Autowired
    private Validator validator;

    private GraphRequest graphRequest;

    @Before
    public void setUp() {
        graphRequest = new GraphRequest();
    }

    @Test
    public void termBoxHeightMinimum1() {
        graphRequest.setTermBoxHeight(1);

        assertThat(validator.validate(graphRequest), hasSize(0));
    }

    @Test
    public void termBoxHeightCannotBeZero() {
        graphRequest.setTermBoxHeight(0);

        assertThat(validator.validate(graphRequest), hasSize(1));
    }

    @Test
    public void termBoxHeightCannotBeLessThan1() {
        graphRequest.setTermBoxHeight(-1);

        assertThat(validator.validate(graphRequest), hasSize(1));
    }

    @Test
    public void termBoxWidthMinimum1() {
        graphRequest.setTermBoxWidth(1);

        assertThat(validator.validate(graphRequest), hasSize(0));
    }

    @Test
    public void termBoxWidthCannotBeZero() {
        graphRequest.setTermBoxWidth(0);

        assertThat(validator.validate(graphRequest), hasSize(1));
    }

    @Test
    public void termBoxWidthCannotBeLessThan1() {
        graphRequest.setTermBoxWidth(-1);

        assertThat(validator.validate(graphRequest), hasSize(1));
    }
}
