package uk.ac.ebi.quickgo.annotation.model;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

/**
 * Tests that the validation added to the {@link AnnotationRequest} class is correct.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={AnnotationRequestConfig.class})
public class AnnotationRequestValidationIT {
    private static final String[] VALID_ASSIGNED_BY_PARMS = {"ASPGD", "ASPGD,Agbase", "ASPGD_,Agbase",
            "ASPGD,Agbase_", "ASPGD,Agbase", "BHF-UCL,Agbase", "Roslin_Institute,BHF-UCL,Agbase"};

    private static final String[] INVALID_ASSIGNED_BY_PARMS = {"_ASPGD", "ASPGD,_Agbase",
            "5555,Agbase", "ASPGD,5555,", "4444,5555,"};

    private static final String[] VALID_GO_EVIDENCE = {"IEA,IBD,IC"};
    private static final String[] INVALID_GO_EVIDENCE = {"9EA,IBDD,I"};

    private static final String[] VALID_GENE_PRODUCT_ID  = {"A0A000","A0A003"};
    private static final String[] INVALID_GENE_PRODUCT_ID = {"99999","&12345"};

    @Autowired
    private Validator validator;

    private AnnotationRequest annotationRequest;

    @Before
    public void setUp() throws Exception {
        annotationRequest = new AnnotationRequest();
    }

    //ASSIGNED BY PARAMETER
    @Test
    public void nullAssignedByIsValid() {
        annotationRequest.setAssignedBy(null);

        assertThat(validator.validate(annotationRequest), hasSize(0));
    }

    @Test
    public void allAssignedByValuesAreValid() {
        String assignedByValues = gimmeCSV(VALID_ASSIGNED_BY_PARMS);
        annotationRequest.setAssignedBy(assignedByValues);
        assertThat(validator.validate(annotationRequest), hasSize(0));
    }

    @Test
    public void allAssignedByValuesAreInvalid() {
        Arrays.stream(INVALID_ASSIGNED_BY_PARMS).forEach(
                invalidValue -> {
                    AnnotationRequest annotationRequest = new AnnotationRequest();
                    annotationRequest.setAssignedBy(invalidValue);

                    Set<ConstraintViolation<AnnotationRequest>> violations = validator.validate(annotationRequest);

                    assertThat(violations, hasSize(1));
                    assertThat(violations.iterator().next().getMessage(),
                            is("At least one 'Assigned By' value is invalid: " + invalidValue));
                }
        );
    }

    //GO EVIDENCE
    @Test
    public void nullGoEvidenceIsValid() {
        annotationRequest.setGoEvidence(null);
        assertThat(validator.validate(annotationRequest), hasSize(0));
    }

    @Test
    public void allGoEvidenceValuesAreValid() {
        for (String valid : VALID_GO_EVIDENCE) {
            annotationRequest.setGoEvidence(valid);
            assertThat(valid + " expected to be a valid value, but has failed validation",
                    validator.validate(annotationRequest), hasSize(0));
        }
    }

    @Test
    public void allGoEvidenceValuesAreInvalid() {
        Arrays.stream(INVALID_GO_EVIDENCE).forEach(
                invalidValue -> {
                    AnnotationRequest annotationRequest = new AnnotationRequest();
                    annotationRequest.setGoEvidence(invalidValue);

                    Set<ConstraintViolation<AnnotationRequest>> violations = validator.validate(annotationRequest);
                    assertThat(violations, hasSize(1));
                    assertThat(violations.iterator().next().getMessage(),
                            is("At least one 'GO Evidence' value is invalid: " + invalidValue));
                }
        );
    }

    //ASPECT PARAMETER
    @Test
    public void nullAspectIsValid() {
        String aspect = null;
        annotationRequest.setAspect(aspect);
        assertThat(validator.validate(annotationRequest), hasSize(0));
    }

    @Test
    public void processAspectIsValid() {
        String aspect = "biological_process";

        annotationRequest.setAspect(aspect);

        assertThat(validator.validate(annotationRequest), hasSize(0));
    }

    @Test
    public void functionAspectIsValid() {
        String aspect = "molecular_function";

        annotationRequest.setAspect(aspect);

        assertThat(validator.validate(annotationRequest), hasSize(0));
    }

    @Test
    public void componentAspectIsValid() {
        String aspect = "cellular_component";

        annotationRequest.setAspect(aspect);

        assertThat(validator.validate(annotationRequest), hasSize(0));
    }

    @Test
    public void unknownAspectIsInvalid() {
        String aspect = "unknown";

        annotationRequest.setAspect(aspect);

        Set<ConstraintViolation<AnnotationRequest>> violations = validator.validate(annotationRequest);

        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getMessage(),
                is("At least one 'Aspect' value is invalid: " + aspect));
    }

    @Test
    public void mixedCaseAspectIsValid() {
        String[] aspects = {"MoLeCuLaR_FuNcTiOn", "BiOlOgIcAl_pRoCeSs", "CelLUlar_CoMpOnEnT"};

        Arrays.stream(aspects).forEach(
                mixedCaseAspect -> {
                    annotationRequest.setAspect(mixedCaseAspect);

                    Set<ConstraintViolation<AnnotationRequest>> violations = validator.validate(annotationRequest);
                    printConstraintViolations(violations);

                    assertThat(violations, hasSize(0));
                }
        );
    }

    //TAXONOMY ID PARAMETER
    @Test
    public void negativeTaxonIdIsInvalid() {
        String taxId = "-1";

        annotationRequest.setTaxon(taxId);

        Set<ConstraintViolation<AnnotationRequest>> violations = validator.validate(annotationRequest);

        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getMessage(),
                is("At least one 'Taxonomic identifier' value is invalid: " + taxId));
    }

    @Test
    public void taxonIdWithNonNumberCharactersIsInvalid() {
        String[] invalidTaxonIdParms = {"1a", "a", "$1"};

        Arrays.stream(invalidTaxonIdParms).forEach(
                invalidValue -> {
                    annotationRequest.setTaxon(invalidValue);

                    Set<ConstraintViolation<AnnotationRequest>> violations = validator.validate(annotationRequest);
                    assertThat(violations, hasSize(is(1)));
                    assertThat(violations.iterator().next().getMessage(),
                            is("At least one 'Taxonomic identifier' value is invalid: " + invalidValue));
                }
        );
    }

    @Test
    public void positiveNumericTaxonIdIsValid() {
        String taxId = "2";

        annotationRequest.setTaxon(taxId);

        assertThat(validator.validate(annotationRequest), hasSize(0));
    }

    @Test
    public void multiplePositiveNumericTaxonIdsIsValid() {
        String taxId = "2,3,4,5";

        annotationRequest.setTaxon(taxId);

        assertThat(validator.validate(annotationRequest), hasSize(0));
    }

    @Test
    public void oneValidTaxIdAndOneInvalidTaxIdIsInvalid() {
        String taxId = "2,-1";

        annotationRequest.setTaxon(taxId);

        Set<ConstraintViolation<AnnotationRequest>> violations = validator.validate(annotationRequest);
        assertThat(violations, hasSize(is(1)));
        assertThat(violations.iterator().next().getMessage(),
                is("At least one 'Taxonomic identifier' value is invalid: " + taxId));
    }


    //GENE PRODUCT ID
    @Test
    public void allGeneProductValuesAreValid() {
        String geneProductIdValues = gimmeCSV(VALID_GENE_PRODUCT_ID);
        annotationRequest.setGpId(geneProductIdValues);
        assertThat(validator.validate(annotationRequest), hasSize(0));
    }

    @Test
    public void geneProductIDValidationIsCaseSensitive() {
        String geneProductIdValues = (gimmeCSV(VALID_GENE_PRODUCT_ID)).toLowerCase();
        annotationRequest.setGpId(geneProductIdValues);
        Set<ConstraintViolation<AnnotationRequest>> violations = validator.validate(annotationRequest);
        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getMessage(),
                is("At least one 'Gene Product ID' value is invalid: " +
                        (Arrays.stream(VALID_GENE_PRODUCT_ID).collect(Collectors.joining(", "))).toLowerCase()));
    }

    @Test
    public void allGeneProductValuesAreInvalid() {
        String geneProductIdValues = gimmeCSV(INVALID_GENE_PRODUCT_ID);
        annotationRequest.setGpId(geneProductIdValues);
        Set<ConstraintViolation<AnnotationRequest>> violations = validator.validate(annotationRequest);
        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getMessage(),
                is("At least one 'Gene Product ID' value is invalid: " + Arrays.stream(INVALID_GENE_PRODUCT_ID)
                        .collect(Collectors.joining(", "))));
    }


    //GO ID PARAMETER

    @Test
    public void goIdIsValid() {
        String[] goIds = {"GO:0003824", "GO:0009999", "GO:0003333"};

        Arrays.stream(goIds).forEach(
                id -> {
                    annotationRequest.setGoId(id);

                    Set<ConstraintViolation<AnnotationRequest>> violations = validator.validate(annotationRequest);
                    printConstraintViolations(violations);

                    assertThat(violations, hasSize(0));
                }
        );
    }


    @Test
    public void mixedCaseGoIdIsValid() {
        String[] goIds = {"GO:0003824", "gO:0003824", "Go:0003824"};

        Arrays.stream(goIds).forEach(
                mixedCaseId -> {
                    annotationRequest.setGoId(mixedCaseId);

                    Set<ConstraintViolation<AnnotationRequest>> violations = validator.validate(annotationRequest);
                    printConstraintViolations(violations);

                    assertThat(violations, hasSize(0));
                }
        );
    }

    @Test
    public void goIdIsInvalid() {
        String[] invalidGoIds = {"GO:4", "xxx:0009999", "-"};

        Arrays.stream(invalidGoIds).forEach(
                id -> {
                    annotationRequest.setGoId(id);

                    Set<ConstraintViolation<AnnotationRequest>> violations = validator.validate(annotationRequest);
                    printConstraintViolations(violations);

                    assertThat(violations, hasSize(1));
                    assertThat(violations.iterator().next().getMessage(),
                            is("At least one 'GO Id' value is invalid: " + id));
                }
        );
    }


    //ECO PARAMETER

    @Test
    public void ecoIdIsValid() {
        String[] ecoIds = {"ECO:0000256", "ECO:0000888", "ECO:0000777"};

        Arrays.stream(ecoIds).forEach(
                validIds -> {
                    annotationRequest.setEcoId(validIds);

                    Set<ConstraintViolation<AnnotationRequest>> violations = validator.validate(annotationRequest);
                    printConstraintViolations(violations);

                    assertThat(violations, hasSize(0));
                }
        );
    }

    @Test
    public void mixedCaseEcoIdIsValid() {
        String[] ecoIds = {"ECO:0000256", "EcO:0000256", "eCO:0000256"};

        Arrays.stream(ecoIds).forEach(
                mixedCaseId -> {
                    annotationRequest.setEcoId(mixedCaseId);

                    Set<ConstraintViolation<AnnotationRequest>> violations = validator.validate(annotationRequest);
                    printConstraintViolations(violations);

                    assertThat(violations, hasSize(0));
                }
        );
    }


    @Test
    public void ecoIdIsInvalid() {
        String[] ecoIds = {"ECO:9", "xxx:0000888", "-"};

        Arrays.stream(ecoIds).forEach(
                validId -> {
                    annotationRequest.setEcoId(validId);

                    Set<ConstraintViolation<AnnotationRequest>> violations = validator.validate(annotationRequest);
                    printConstraintViolations(violations);
                    assertThat(violations.iterator().next().getMessage(),
                            is("At least one 'ECO identifier' value is invalid: " + validId));
                    assertThat(violations, hasSize(1));
                }
        );
    }

    //PAGE PARAMETER
    @Test
    public void negativePageValueIsInvalid() {
        annotationRequest.setPage(-1);

        assertThat(validator.validate(annotationRequest), hasSize(greaterThan(0)));
    }

    @Test
    public void zeroPageValueIsInvalid() {
        annotationRequest.setPage(0);

        assertThat(validator.validate(annotationRequest), hasSize(greaterThan(0)));
    }

    @Test
    public void positivePageValueIsValid() {
        annotationRequest.setPage(1);

        assertThat(validator.validate(annotationRequest), hasSize(0));
    }

    //LIMIT PARAMETER
    @Test
    public void negativeLimitValueIsInvalid() {
        annotationRequest.setLimit(-1);

        assertThat(validator.validate(annotationRequest), hasSize(greaterThan(0)));
    }

    @Test
    public void zeroLimitValueIsValid() {
        annotationRequest.setLimit(0);

        assertThat(validator.validate(annotationRequest), hasSize(0));
    }

    @Test
    public void positiveLimitValueIsValid() {
        annotationRequest.setLimit(1);

        assertThat(validator.validate(annotationRequest), hasSize(0));
    }

    @Test
    public void limitValueEqualToMaxEntriesPerPageIsInvalid() {
        annotationRequest.setLimit(AnnotationRequest.MAX_ENTRIES_PER_PAGE);

        assertThat(validator.validate(annotationRequest), hasSize(0));
    }

    @Test
    public void limitValueAboveMaxEntriesPerPageIsInvalid() {
        annotationRequest.setLimit(AnnotationRequest.MAX_ENTRIES_PER_PAGE + 1);

        assertThat(validator.validate(annotationRequest), hasSize(greaterThan(0)));
    }

    private void printConstraintViolations(Set<ConstraintViolation<AnnotationRequest>> violations) {
        violations.stream().forEach(System.out::println);
    }

    private String gimmeCSV(String... values) {
        return Arrays.stream(values).collect(Collectors.joining(","));
    }
}
