package uk.ac.ebi.quickgo.client.model.ontology;

import java.util.Arrays;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static uk.ac.ebi.quickgo.ontology.common.OntologyFields.Searchable;
import static uk.ac.ebi.quickgo.rest.controller.request.ArrayPattern.DEFAULT_ERROR_MSG;

/**
 * Tests that the validation added to the {@link OntologyRequest} class is correct.
 */
public class OntologyRequestValidationIT {
    private Validator validator;

    private OntologyRequest ontologyRequest;

    @Before
    public void setUp() throws Exception {
        ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
        validator = vf.getValidator();

        ontologyRequest = new OntologyRequest();
        ontologyRequest.setQuery("query");
    }

    //QUERY PARAMETER
    @Test
    public void nullQueryIsInvalid() {
        ontologyRequest.setQuery(null);

        Set<ConstraintViolation<OntologyRequest>> violations = validator.validate(ontologyRequest);

        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getMessage(), is("Query cannot be null"));
    }

    @Test
    public void emptyQueryIsInvalid() {
        ontologyRequest.setQuery("");

        Set<ConstraintViolation<OntologyRequest>> violations = validator.validate(ontologyRequest);

        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getMessage(), is("Query cannot be empty"));
    }

    @Test
    public void populatedQueryIsInvalid() {
        ontologyRequest.setQuery("query");

        assertThat(validator.validate(ontologyRequest), hasSize(0));
    }

    //PAGE PARAMETER
    @Test
    public void negativePageValueIsInvalid() {
        ontologyRequest.setPage(-1);

        Set<ConstraintViolation<OntologyRequest>> violations = validator.validate(ontologyRequest);

        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getMessage(), is("Page number cannot be less than 1"));

    }

    @Test
    public void zeroPageValueIsInvalid() {
        ontologyRequest.setPage(0);

        Set<ConstraintViolation<OntologyRequest>> violations = validator.validate(ontologyRequest);

        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getMessage(), is("Page number cannot be less than 1"));
    }

    @Test
    public void positivePageValueIsValid() {
        ontologyRequest.setPage(1);

        assertThat(validator.validate(ontologyRequest), hasSize(0));
    }

    //LIMIT PARAMETER
    @Test
    public void negativeLimitValueIsInvalid() {
        ontologyRequest.setLimit(-1);

        Set<ConstraintViolation<OntologyRequest>> violations = validator.validate(ontologyRequest);

        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getMessage(), is("Number of results per page cannot be less than 0"));
    }

    @Test
    public void zeroLimitValueIsValid() {
        ontologyRequest.setLimit(0);

        assertThat(validator.validate(ontologyRequest), hasSize(0));
    }

    @Test
    public void positiveLimitValueIsValid() {
        ontologyRequest.setLimit(1);

        assertThat(validator.validate(ontologyRequest), hasSize(0));
    }

    @Test
    public void limitValueEqualToMaxEntriesPerPageIsInvalid() {
        ontologyRequest.setLimit(ontologyRequest.MAX_ENTRIES_PER_PAGE);

        assertThat(validator.validate(ontologyRequest), hasSize(0));
    }

    @Test
    public void limitValueAboveMaxEntriesPerPageIsInvalid() {
        ontologyRequest.setLimit(ontologyRequest.MAX_ENTRIES_PER_PAGE + 1);

        Set<ConstraintViolation<OntologyRequest>> violations = validator.validate(ontologyRequest);

        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getMessage(), is("Number of results per page cannot be greater than " +
                ontologyRequest.MAX_ENTRIES_PER_PAGE));
    }

    //ASPECT FILTER
    @Test
    public void unrecognizedFilterByAspectValueIsInvalid() {
        String incorrectAspect = "unrecognized";
        ontologyRequest.setAspect(incorrectAspect);

        Set<ConstraintViolation<OntologyRequest>> violations = validator.validate(ontologyRequest);

        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getMessage(),
                is(String.format(DEFAULT_ERROR_MSG, Searchable.ASPECT, incorrectAspect)));
    }

    @Test
    public void aspectWithMixedCasingIsValid() {
        String mixedCaseAspect = "BiOlOgIcAl_PrOcEsS";
        ontologyRequest.setAspect(mixedCaseAspect);

        assertThat(validator.validate(ontologyRequest), hasSize(0));
    }

    @Test
    public void providedFilterByAspectValuesAreAllValid() throws Exception {
        Arrays.asList("biological_process", "molecular_function", "cellular_component")
                .forEach(aspect -> {
                            ontologyRequest.setAspect(aspect);
                            assertThat("Aspect value: " + aspect + " is invalid",
                                    validator.validate(ontologyRequest), hasSize(0));
                        }
                );
    }

    //TYPE FILTER
    @Test
    public void unrecognizedFilterByTypeValueIsInvalid() {
        String incorrectType = "incorrect";
        ontologyRequest.setOntologyType(incorrectType);

        Set<ConstraintViolation<OntologyRequest>> violations = validator.validate(ontologyRequest);

        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getMessage(),
                is("Provided ontology type is invalid: " + incorrectType));
    }

    @Test
    public void typeWithMixedCasingIsValid() {
        String mixedCaseType = "EcO";
        ontologyRequest.setOntologyType(mixedCaseType);

        assertThat(validator.validate(ontologyRequest), hasSize(0));
    }

    @Test
    public void providedFilterByTypeValuesAreAllValid() throws Exception {
        Arrays.asList("go", "eco")
                .forEach(type -> {
                            ontologyRequest.setOntologyType(type);
                            assertThat("Type value: " + type + " is invalid",
                                    validator.validate(ontologyRequest), hasSize(0));
                        }
                );
    }
}