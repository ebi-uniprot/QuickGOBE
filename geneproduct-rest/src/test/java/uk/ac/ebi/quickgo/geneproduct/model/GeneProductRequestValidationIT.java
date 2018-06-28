package uk.ac.ebi.quickgo.geneproduct.model;

import uk.ac.ebi.quickgo.common.FacetableField;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

/**
 * Tests that the validation added to the {@link GeneProductRequest} class is correct.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = GeneProductRequestValidationIT.GeneProductRequestValidationConfig.class)
public class GeneProductRequestValidationIT {
    private static final String INVALID_FACET_1 = "invalid1";

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

    @Before
    public void setUp() {
        geneProductRequest = new GeneProductRequest();
        geneProductRequest.setQuery("query");
    }

    //TYPE PARAMETER
    @Test
    public void nullTypeIsValid() {
        geneProductRequest.setType(null);

        assertThat(validator.validate(geneProductRequest), hasSize(0));
    }

    @Test
    public void emptyTypeIsInvalid() {
        geneProductRequest.setType("");

        Set<ConstraintViolation<GeneProductRequest>> violations = validator.validate(geneProductRequest);
        assertThat(validator.validate(geneProductRequest), hasSize(1));

        assertThat(violations.iterator().next().getMessage(),
                is(createGenericErrorMessage("type", "")));
    }

    @Test
    public void proteinTypeIsValid() {
        String type = "protein";
        geneProductRequest.setType(type);

        assertThat(validator.validate(geneProductRequest), hasSize(0));
    }

    @Test
    public void miRNATypeIsValid() {
        String type = "miRNA";
        geneProductRequest.setType(type);

        assertThat(validator.validate(geneProductRequest), hasSize(0));
    }

    @Test
    public void caseInsensitiveTypeIsValid() {
        String type = "mirna";
        geneProductRequest.setType(type);

        assertThat(validator.validate(geneProductRequest), hasSize(0));
    }

    @Test
    public void incorrectTypeIsInvalid() {
        String type = "Incorrect";
        geneProductRequest.setType(type);

        assertThat(validator.validate(geneProductRequest), hasSize(1));
    }

    @Test
    public void nullDbSubsetIsValid() {
        geneProductRequest.setDbSubset(null);

        assertThat(validator.validate(geneProductRequest), hasSize(0));
    }

    @Test
    public void emptyDbSubsetIsInvalid() {
        geneProductRequest.setDbSubset("");

        Set<ConstraintViolation<GeneProductRequest>> violations = validator.validate(geneProductRequest);
        assertThat(validator.validate(geneProductRequest), hasSize(1));

        assertThat(violations.iterator().next().getMessage(),
                is(createGenericErrorMessage("dbSubset", "")));
    }

    @Test
    public void tremblDbSubsetIsValid() {
        String type = "TrEMBL";
        geneProductRequest.setDbSubset(type);

        assertThat(validator.validate(geneProductRequest), hasSize(0));
    }

    @Test
    public void swissprotDbSubsetIsValid() {
        String type = "Swiss-Prot";
        geneProductRequest.setDbSubset(type);

        assertThat(validator.validate(geneProductRequest), hasSize(0));
    }

    @Test
    public void caseInsensitiveDbSubsetIsValid() {
        String type = "swiss-prot";
        geneProductRequest.setDbSubset(type);

        assertThat(validator.validate(geneProductRequest), hasSize(0));
    }

    @Test
    public void nullTaxonIdIsValid() {
        geneProductRequest.setTaxonId(null);

        assertThat(validator.validate(geneProductRequest), hasSize(0));
    }

    @Test
    public void emptyTaxonIdIsInvalid() {
        geneProductRequest.setTaxonId("");

        Set<ConstraintViolation<GeneProductRequest>> violations = validator.validate(geneProductRequest);
        assertThat(validator.validate(geneProductRequest), hasSize(1));

        assertThat(violations.iterator().next().getMessage(),
                containsString(createTaxonIdErrorMessage()));
    }

    @Test
    public void singlePositiveTaxonIdIsValid() {
        geneProductRequest.setTaxonId("1");

        assertThat(validator.validate(geneProductRequest), hasSize(0));
    }

    @Test
    public void multiplePositiveTaxonIdIsValid() {
        String[] taxonId = {"1","2","3"};

        geneProductRequest.setTaxonId(taxonId);

        assertThat(validator.validate(geneProductRequest), hasSize(0));
    }

    @Test
    public void negativeTaxonIdIsInvalid() {
        String taxonId = "-1";

        geneProductRequest.setTaxonId(taxonId);

        Set<ConstraintViolation<GeneProductRequest>> violations = validator.validate(geneProductRequest);
        assertThat(validator.validate(geneProductRequest), hasSize(1));

        assertThat(violations.iterator().next().getMessage(),
                containsString(createTaxonIdErrorMessage()));
    }

    @Test
    public void negativeTaxonIdMixedWithPositiveTaxonIdsIsInvalid() {
        String[] taxonId = {"1","-1","2"};

        geneProductRequest.setTaxonId(taxonId);

        Set<ConstraintViolation<GeneProductRequest>> violations = validator.validate(geneProductRequest);
        assertThat(validator.validate(geneProductRequest), hasSize(1));

        assertThat(violations.iterator().next().getMessage(),
                containsString(createTaxonIdErrorMessage()));
    }

    @Test
    public void emptyTaxonIdMixedWithPositiveTaxonIdsIsInvalid() {
        String[] taxonId = {"1","","2"};

        geneProductRequest.setTaxonId(taxonId);

        Set<ConstraintViolation<GeneProductRequest>> violations = validator.validate(geneProductRequest);
        assertThat(validator.validate(geneProductRequest), hasSize(1));

        assertThat(violations.iterator().next().getMessage(),
                containsString(createTaxonIdErrorMessage()));
    }

    @Test
    public void successfullyValidateNullForProteome() {
        geneProductRequest.setProteome(null);

        assertThat(validator.validate(geneProductRequest), hasSize(0));
    }

    @Test
    public void successfullyValidateIsoFormForProteome() {
        String proteome = "gcrpIso";

        geneProductRequest.setProteome(proteome);

        assertThat(validator.validate(geneProductRequest), hasSize(0));
    }

    @Test
    public void successfullyValidateNoneForProteome() {
        String proteome = "None";

        geneProductRequest.setProteome(proteome);

        assertThat(validator.validate(geneProductRequest), hasSize(0));
    }

    @Test
    public void successfullyValidateReferenceForProteome() {
        String proteome = "gcrpCan";

        geneProductRequest.setProteome(proteome);

        assertThat(validator.validate(geneProductRequest), hasSize(0));
    }

    @Test
    public void successfullyValidateCompleteForProteome() {
        String proteome = "complete";

        geneProductRequest.setProteome(proteome);

        assertThat(validator.validate(geneProductRequest), hasSize(0));
    }

    @Test
    public void emptyProteomeIsInvalid() {
        String proteome = "";

        geneProductRequest.setProteome(proteome);

        Set<ConstraintViolation<GeneProductRequest>> violations = validator.validate(geneProductRequest);
        assertThat(validator.validate(geneProductRequest), hasSize(1));

        assertThat(violations.iterator().next().getMessage(),
                containsString(createGenericErrorMessage("proteome", "")));
    }

    @Test
    public void proteomeIsInvalid() {
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
