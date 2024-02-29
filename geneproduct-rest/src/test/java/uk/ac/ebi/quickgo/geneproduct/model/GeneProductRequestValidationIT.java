package uk.ac.ebi.quickgo.geneproduct.model;

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
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

/**
 * Tests that the validation added to the {@link GeneProductRequest} class is correct.
 */
@SpringBootTest(classes = GeneProductRequestValidationIT.GeneProductRequestValidationConfig.class)
class GeneProductRequestValidationIT {
    private static final String INVALID_FACET_1 = "invalid1";

    @Configuration
    static class GeneProductRequestValidationConfig {
        @Bean
        FacetableField facetableField() {
            return new FacetableField() {
                private final List<String> notFacetable = Collections.singletonList(INVALID_FACET_1);

                @Override public boolean isFacetable(String field) {
                    return !notFacetable.contains(field);
                }

                @Override public Stream<String> facetableFields() {
                    return Stream.empty();
                }
            };
        }

        @Bean
        Validator validator() {
            return new LocalValidatorFactoryBean();
        }
    }

    @Autowired
    private Validator validator;

    private GeneProductRequest geneProductRequest;

    @BeforeEach
    void setUp() {
        geneProductRequest = new GeneProductRequest();
        geneProductRequest.setQuery("query");
    }

    //TYPE PARAMETER
    @Test
    void nullTypeIsValid() {
        geneProductRequest.setType(null);

        assertThat(validator.validate(geneProductRequest), hasSize(0));
    }

    @Test
    void emptyTypeIsInvalid() {
        geneProductRequest.setType("");

        Set<ConstraintViolation<GeneProductRequest>> violations = validator.validate(geneProductRequest);
        assertThat(validator.validate(geneProductRequest), hasSize(1));

        assertThat(violations.iterator().next().getMessage(),
                is(createGenericErrorMessage("type", "")));
    }

    @Test
    void proteinTypeIsValid() {
        String type = "protein";
        geneProductRequest.setType(type);

        assertThat(validator.validate(geneProductRequest), hasSize(0));
    }

    @Test
    void miRNATypeIsValid() {
        String type = "miRNA";
        geneProductRequest.setType(type);

        assertThat(validator.validate(geneProductRequest), hasSize(0));
    }

    @Test
    void caseInsensitiveTypeIsValid() {
        String type = "mirna";
        geneProductRequest.setType(type);

        assertThat(validator.validate(geneProductRequest), hasSize(0));
    }

    @Test
    void incorrectTypeIsInvalid() {
        String type = "Incorrect";
        geneProductRequest.setType(type);

        assertThat(validator.validate(geneProductRequest), hasSize(1));
    }

    @Test
    void nullDbSubsetIsValid() {
        geneProductRequest.setDbSubset(null);

        assertThat(validator.validate(geneProductRequest), hasSize(0));
    }

    @Test
    void emptyDbSubsetIsInvalid() {
        geneProductRequest.setDbSubset("");

        Set<ConstraintViolation<GeneProductRequest>> violations = validator.validate(geneProductRequest);
        assertThat(validator.validate(geneProductRequest), hasSize(1));

        assertThat(violations.iterator().next().getMessage(),
                is(createGenericErrorMessage("dbSubset", "")));
    }

    @Test
    void tremblDbSubsetIsValid() {
        String type = "TrEMBL";
        geneProductRequest.setDbSubset(type);

        assertThat(validator.validate(geneProductRequest), hasSize(0));
    }

    @Test
    void swissprotDbSubsetIsValid() {
        String type = "Swiss-Prot";
        geneProductRequest.setDbSubset(type);

        assertThat(validator.validate(geneProductRequest), hasSize(0));
    }

    @Test
    void caseInsensitiveDbSubsetIsValid() {
        String type = "swiss-prot";
        geneProductRequest.setDbSubset(type);

        assertThat(validator.validate(geneProductRequest), hasSize(0));
    }

    @Test
    void nullTaxonIdIsValid() {
        geneProductRequest.setTaxonId();

        assertThat(validator.validate(geneProductRequest), hasSize(0));
    }

    @Test
    void emptyTaxonIdIsInvalid() {
        geneProductRequest.setTaxonId("");

        Set<ConstraintViolation<GeneProductRequest>> violations = validator.validate(geneProductRequest);
        assertThat(validator.validate(geneProductRequest), hasSize(1));

        assertThat(violations.iterator().next().getMessage(),
                containsString(createTaxonIdErrorMessage()));
    }

    @Test
    void singlePositiveTaxonIdIsValid() {
        geneProductRequest.setTaxonId("1");

        assertThat(validator.validate(geneProductRequest), hasSize(0));
    }

    @Test
    void multiplePositiveTaxonIdIsValid() {
        String[] taxonId = {"1","2","3"};

        geneProductRequest.setTaxonId(taxonId);

        assertThat(validator.validate(geneProductRequest), hasSize(0));
    }

    @Test
    void negativeTaxonIdIsInvalid() {
        String taxonId = "-1";

        geneProductRequest.setTaxonId(taxonId);

        Set<ConstraintViolation<GeneProductRequest>> violations = validator.validate(geneProductRequest);
        assertThat(validator.validate(geneProductRequest), hasSize(1));

        assertThat(violations.iterator().next().getMessage(),
                containsString(createTaxonIdErrorMessage()));
    }

    @Test
    void negativeTaxonIdMixedWithPositiveTaxonIdsIsInvalid() {
        String[] taxonId = {"1","-1","2"};

        geneProductRequest.setTaxonId(taxonId);

        Set<ConstraintViolation<GeneProductRequest>> violations = validator.validate(geneProductRequest);
        assertThat(validator.validate(geneProductRequest), hasSize(1));

        assertThat(violations.iterator().next().getMessage(),
                containsString(createTaxonIdErrorMessage()));
    }

    @Test
    void emptyTaxonIdMixedWithPositiveTaxonIdsIsInvalid() {
        String[] taxonId = {"1","","2"};

        geneProductRequest.setTaxonId(taxonId);

        Set<ConstraintViolation<GeneProductRequest>> violations = validator.validate(geneProductRequest);
        assertThat(validator.validate(geneProductRequest), hasSize(1));

        assertThat(violations.iterator().next().getMessage(),
                containsString(createTaxonIdErrorMessage()));
    }

    @Test
    void successfullyValidateNullForProteome() {
        geneProductRequest.setProteome(null);

        assertThat(validator.validate(geneProductRequest), hasSize(0));
    }

    @Test
    void successfullyValidateIsoFormForProteome() {
        String proteome = "gcrpIso";

        geneProductRequest.setProteome(proteome);

        assertThat(validator.validate(geneProductRequest), hasSize(0));
    }

    @Test
    void successfullyValidateProteomeCaseInsensitive() {
        String proteome = "GcrPiso";

        geneProductRequest.setProteome(proteome);

        assertThat(validator.validate(geneProductRequest), hasSize(0));
    }

    @Test
    void successfullyValidateNoneForProteome() {
        String proteome = "None";

        geneProductRequest.setProteome(proteome);

        assertThat(validator.validate(geneProductRequest), hasSize(0));
    }

    @Test
    void successfullyValidateReferenceForProteome() {
        String proteome = "gcrpCan";

        geneProductRequest.setProteome(proteome);

        assertThat(validator.validate(geneProductRequest), hasSize(0));
    }

    @Test
    void successfullyValidateCompleteForProteome() {
        String proteome = "complete";

        geneProductRequest.setProteome(proteome);

        assertThat(validator.validate(geneProductRequest), hasSize(0));
    }

    @Test
    void emptyProteomeIsInvalid() {
        String proteome = "";

        geneProductRequest.setProteome(proteome);

        Set<ConstraintViolation<GeneProductRequest>> violations = validator.validate(geneProductRequest);
        assertThat(validator.validate(geneProductRequest), hasSize(1));

        assertThat(violations.iterator().next().getMessage(),
                containsString(createGenericErrorMessage("proteome", "")));
    }

    @Test
    void proteomeIsInvalid() {
        String proteome = "asdfasdgfas";

        geneProductRequest.setProteome(proteome);

        Set<ConstraintViolation<GeneProductRequest>> violations = validator.validate(geneProductRequest);
        assertThat(validator.validate(geneProductRequest), hasSize(1));

        assertThat(violations.iterator().next().getMessage(),
                containsString(createGenericErrorMessage("proteome", proteome)));
    }

    private String createGenericErrorMessage(String field, String value) {
        return "Provided " + field + " is invalid: " + value;
    }

    private String createTaxonIdErrorMessage() {
        return "The 'taxonId' parameter contains invalid values: ";
    }

}
