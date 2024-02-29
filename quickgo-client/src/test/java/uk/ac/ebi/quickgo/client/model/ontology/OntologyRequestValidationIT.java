package uk.ac.ebi.quickgo.client.model.ontology;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import uk.ac.ebi.quickgo.common.FacetableField;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelperImpl.MAX_ENTRIES_PER_PAGE;
import static uk.ac.ebi.quickgo.rest.controller.request.AllowableFacets.DEFAULT_ERROR_MESSAGE;
import static uk.ac.ebi.quickgo.rest.controller.request.ArrayPattern.DEFAULT_ERROR_MSG;

/**
 * Tests that the validation added to the {@link OntologyRequest} class is correct.
 */
@SpringBootTest(classes = OntologyRequestValidationIT.OntologyRequestValidationConfig.class)
class OntologyRequestValidationIT {
    private static final String VALID_FACET = "valid";

    @Configuration
    static class OntologyRequestValidationConfig {
        @Bean
        public Validator validator() {
            return new LocalValidatorFactoryBean();
        }

        @Bean
        public FacetableField ontologyFacetableField() {
            return new FacetableField() {
                @Override public boolean isFacetable(String field) {
                    return VALID_FACET.equals(field);
                }

                @Override public Stream<String> facetableFields() {
                    return Stream.empty();
                }
            };
        }
    }

    @Autowired
    private Validator validator;

    private OntologyRequest ontologyRequest;

    @BeforeEach
    void setUp() {
        ontologyRequest = new OntologyRequest();
        ontologyRequest.setQuery("query");
    }

    //QUERY PARAMETER
    @Test
    void nullQueryIsInvalid() {
        ontologyRequest.setQuery(null);

        Set<ConstraintViolation<OntologyRequest>> violations = validator.validate(ontologyRequest);

        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getMessage(), is("Query cannot be null"));
    }

    @Test
    void emptyQueryIsInvalid() {
        ontologyRequest.setQuery("");

        Set<ConstraintViolation<OntologyRequest>> violations = validator.validate(ontologyRequest);

        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getMessage(), is("Query cannot be empty"));
    }

    @Test
    void populatedQueryIsInvalid() {
        ontologyRequest.setQuery("query");

        assertThat(validator.validate(ontologyRequest), hasSize(0));
    }

    //PAGE PARAMETER
    @Test
    void negativePageValueIsInvalid() {
        ontologyRequest.setPage(-1);

        Set<ConstraintViolation<OntologyRequest>> violations = validator.validate(ontologyRequest);

        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getMessage(), is("Page number cannot be less than 1"));

    }

    @Test
    void zeroPageValueIsInvalid() {
        ontologyRequest.setPage(0);

        Set<ConstraintViolation<OntologyRequest>> violations = validator.validate(ontologyRequest);

        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getMessage(), is("Page number cannot be less than 1"));
    }

    @Test
    void positivePageValueIsValid() {
        ontologyRequest.setPage(1);

        assertThat(validator.validate(ontologyRequest), hasSize(0));
    }

    //LIMIT PARAMETER
    @Test
    void negativeLimitValueIsInvalid() {
        ontologyRequest.setLimit(-1);

        Set<ConstraintViolation<OntologyRequest>> violations = validator.validate(ontologyRequest);

        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getMessage(), is("Number of results per page cannot be less than 0"));
    }

    @Test
    void zeroLimitValueIsValid() {
        ontologyRequest.setLimit(0);

        assertThat(validator.validate(ontologyRequest), hasSize(0));
    }

    @Test
    void positiveLimitValueIsValid() {
        ontologyRequest.setLimit(1);

        assertThat(validator.validate(ontologyRequest), hasSize(0));
    }

    @Test
    void limitValueEqualToMaxEntriesPerPageIsInvalid() {
        ontologyRequest.setLimit(MAX_ENTRIES_PER_PAGE);

        assertThat(validator.validate(ontologyRequest), hasSize(0));
    }

    @Test
    void limitValueAboveMaxEntriesPerPageIsInvalid() {
        ontologyRequest.setLimit(MAX_ENTRIES_PER_PAGE + 1);

        Set<ConstraintViolation<OntologyRequest>> violations = validator.validate(ontologyRequest);

        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getMessage(), is("Number of results per page cannot be greater than " +
                MAX_ENTRIES_PER_PAGE));
    }

    //ASPECT FILTER
    @Test
    void unrecognizedFilterByAspectValueIsInvalid() {
        String incorrectAspect = "unrecognized";
        ontologyRequest.setAspect(incorrectAspect);

        Set<ConstraintViolation<OntologyRequest>> violations = validator.validate(ontologyRequest);

        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getMessage(),
                is(DEFAULT_ERROR_MSG.formatted("aspect", incorrectAspect)));
    }

    @Test
    void providedFilterByAspectValuesAreAllValid() {
        Arrays.asList("Process", "PRoceSs", "Function", "funCtioN", "Component", "compONent")
                .forEach(aspect -> {
                            ontologyRequest.setAspect(aspect);
                            assertThat("Aspect value: " + aspect + " is invalid",
                                    validator.validate(ontologyRequest), hasSize(0));
                        }
                );
    }

    @Test
    void providedFilterByAspectValuesAreAllInvalid() {
        Arrays.asList("biological_process", "molecular_function", "cellular_component")
                .forEach(aspect -> {
                            ontologyRequest.setAspect(aspect);
                            assertThat("Aspect value: " + aspect + " is valid, but should be invalid",
                                    validator.validate(ontologyRequest), hasSize(1));
                        }
                );
    }

    //TYPE FILTER
    @Test
    void unrecognizedFilterByTypeValueIsInvalid() {
        String incorrectType = "incorrect";
        ontologyRequest.setOntologyType(incorrectType);

        Set<ConstraintViolation<OntologyRequest>> violations = validator.validate(ontologyRequest);

        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getMessage(),
                is("Provided ontology type is invalid: " + incorrectType));
    }

    @Test
    void typeWithMixedCasingIsValid() {
        String mixedCaseType = "EcO";
        ontologyRequest.setOntologyType(mixedCaseType);

        assertThat(validator.validate(ontologyRequest), hasSize(0));
    }

    @Test
    void providedFilterByTypeValuesAreAllValid() {
        Arrays.asList("go", "eco")
                .forEach(type -> {
                            ontologyRequest.setOntologyType(type);
                            assertThat("Type value: " + type + " is invalid",
                                    validator.validate(ontologyRequest), hasSize(0));
                        }
                );
    }

    //isObsolete FILTER
    @Test
    void unrecognizedFilterByTypeObsoleteIsInvalid() {
        String incorrectVal = "incorrect";
        ontologyRequest.setIsObsolete(incorrectVal);

        Set<ConstraintViolation<OntologyRequest>> violations = validator.validate(ontologyRequest);

        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getMessage(),
          is("Provided isObsolete is invalid: " + incorrectVal));
    }

    @Test
    void obsoleteWithMixedCasingIsInvalid() {
        String mixedCaseBool = "TruE";
        ontologyRequest.setIsObsolete(mixedCaseBool);

        Set<ConstraintViolation<OntologyRequest>> violations = validator.validate(ontologyRequest);

        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getMessage(),
          is("Provided isObsolete is invalid: " + mixedCaseBool));
    }

    @Test
    void providedFilterByObsoleteValuesAreAllValid() {
        Arrays.asList("true", "false")
          .forEach(type -> {
                ontologyRequest.setIsObsolete(type);
                assertThat("Type value: " + type + " is invalid",
                  validator.validate(ontologyRequest), hasSize(0));
            }
          );
    }

    @Test
    void unrecognizedFacetIsInvalid() {
        ontologyRequest.setFacet(new String[]{"invalidFacet"});

        Set<ConstraintViolation<OntologyRequest>> violations = validator.validate(ontologyRequest);

        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getMessage(),
                startsWith(DEFAULT_ERROR_MESSAGE));
    }

    @Test
    void recognizedFacetIsValid() {
        ontologyRequest.setFacet(new String[]{VALID_FACET});

        Set<ConstraintViolation<OntologyRequest>> violations = validator.validate(ontologyRequest);

        assertThat(violations, hasSize(0));
    }
}