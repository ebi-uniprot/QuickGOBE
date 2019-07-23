package uk.ac.ebi.quickgo.rest.controller.request;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import uk.ac.ebi.quickgo.common.FacetableField;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

/**
 * Tests the behaviour of the {@link AllowableFacetsImpl} class.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AllowableFacetsImplIT.AllowableFacetsConfig.class)
public class AllowableFacetsImplIT {
    private static final String INVALID_FACET_1 = "invalid1";
    private static final String INVALID_FACET_2 = "invalid2";

    @Configuration
    static class AllowableFacetsConfig {
        @Bean
        FacetableField facetableField() {
            return new FacetableField() {
                private final List<String> notFacetable = Arrays.asList(INVALID_FACET_1, INVALID_FACET_2);

                @Override public boolean isFacetable(String field) {
                    return !notFacetable.contains(field);
                }

                @Override public Stream<String> facetableFields() {
                    return Stream.empty();
                }
            };
        }

        @Bean
        AllowableFacetsImpl allowableFacets(FacetableField facetableField) {
            return new AllowableFacetsImpl(facetableField);
        }

        @Bean
        Validator validator() {
            return new LocalValidatorFactoryBean();
        }
    }

    @Autowired
    private Validator validator;

    private StubFacet stubFacet;

    @Before
    public void setUp() throws Exception {
        stubFacet = new StubFacet();
    }

    @Test
    public void nullFacetArrayIsValid() throws Exception {
        stubFacet.facets = null;

        validator.validate(stubFacet);

        Set<ConstraintViolation<StubFacet>> violations = validator.validate(stubFacet);

        assertThat(violations, hasSize(0));
    }

    @Test
    public void emptyFacetArrayIsValid() throws Exception {
        validator.validate(stubFacet);
    }

    @Test
    public void facetArrayWithInvalidFacetsIsInvalid() throws Exception {
        stubFacet.facets = new String[]{INVALID_FACET_1};

        validator.validate(stubFacet);

        Set<ConstraintViolation<StubFacet>> violations = validator.validate(stubFacet);

        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getMessage(), is(createRegexErrorMessage(INVALID_FACET_1)));
    }

    @Test
    public void facetArrayWithJustValidFacetsIsValid() throws Exception {
        stubFacet.facets = new String[]{"valid1", "valid2", "valid3",};

        validator.validate(stubFacet);

        Set<ConstraintViolation<StubFacet>> violations = validator.validate(stubFacet);

        assertThat(violations, hasSize(0));
    }

    @Test
    public void facetArrayWithASeveralValidFacetsAndAnInvalidFacetIsInValid() throws Exception {
        stubFacet.facets = new String[]{"valid1", INVALID_FACET_1, "valid3"};

        validator.validate(stubFacet);

        Set<ConstraintViolation<StubFacet>> violations = validator.validate(stubFacet);

        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getMessage(), is(createRegexErrorMessage(INVALID_FACET_1)));
    }

    @Test
    public void facetArrayWithSeveralInvalidFacetsIsInValid() throws Exception {
        stubFacet.facets = new String[]{"valid1", INVALID_FACET_1, INVALID_FACET_2};

        validator.validate(stubFacet);

        Set<ConstraintViolation<StubFacet>> violations = validator.validate(stubFacet);

        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getMessage(),
                is(createRegexErrorMessage(INVALID_FACET_1, INVALID_FACET_2)));
    }

    private String createRegexErrorMessage(String... invalidItems) {
        String csvInvalidItems = Stream.of(invalidItems).collect(Collectors.joining(", "));
        return AllowableFacets.DEFAULT_ERROR_MESSAGE + ":"+ csvInvalidItems;
    }

    /**
     * Class annotated with {@link AllowableFacets} so that we can test the annotation.
     */
    private class StubFacet {
        @AllowableFacets
        String[] facets;

        public StubFacet() {
            this.facets = new String[]{};
        }
    }
}