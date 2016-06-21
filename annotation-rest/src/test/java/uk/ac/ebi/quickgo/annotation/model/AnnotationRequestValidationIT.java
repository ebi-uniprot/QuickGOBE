package uk.ac.ebi.quickgo.annotation.model;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

/**
 * Tests that the validation added to the {@link AnnotationRequest} class is correct.
 */
public class AnnotationRequestValidationIT {
    private static final String[] VALID_ASSIGNED_BY_PARMS = {"ASPGD", "ASPGD,Agbase", "ASPGD_,Agbase",
            "ASPGD,Agbase_", "ASPGD,Agbase", "BHF-UCL,Agbase", "Roslin_Institute,BHF-UCL,Agbase"};

    private static final String[] INVALID_ASSIGNED_BY_PARMS = {"_ASPGD", "ASPGD,_Agbase",
            "5555,Agbase", "ASPGD,5555,", "4444,5555,"};

    private Validator validator;
    private AnnotationRequest annotationRequest;

    @Before
    public void setUp() throws Exception {
        ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
        validator = vf.getValidator();

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
        String assignedByValues = Arrays.stream(VALID_ASSIGNED_BY_PARMS)
                .collect(Collectors.joining(","));

        annotationRequest.setAssignedBy(assignedByValues);

        assertThat(validator.validate(annotationRequest), hasSize(0));
    }

    @Test
    public void allAssignedByValuesAreInvalid() {
        Arrays.stream(INVALID_ASSIGNED_BY_PARMS).forEach(
                invalidValue -> {
                    AnnotationRequest annotationRequest = new AnnotationRequest();
                    annotationRequest.setAssignedBy(invalidValue);

                    assertThat(validator.validate(annotationRequest), hasSize(greaterThan(0)));
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

        assertThat(validator.validate(annotationRequest), hasSize(greaterThan(0)));
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
        assertThat(violations.iterator().next().getMessage(), is("Invalid taxonomic identifier: " + taxId));
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
                            is("Invalid taxonomic identifier: " + invalidValue));
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
    public void oneValidTaxIdAndOneInvalidTaxIdIsValid() {
        String taxId = "2,-1";

        annotationRequest.setTaxon(taxId);

        assertThat(validator.validate(annotationRequest), hasSize(greaterThan(0)));
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
}