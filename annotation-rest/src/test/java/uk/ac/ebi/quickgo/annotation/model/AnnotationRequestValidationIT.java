package uk.ac.ebi.quickgo.annotation.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.quickgo.annotation.IdGeneratorUtil;
import uk.ac.ebi.quickgo.annotation.validation.loader.ValidationConfig;
import uk.ac.ebi.quickgo.annotation.validation.service.JobTestRunnerConfig;
import uk.ac.ebi.quickgo.rest.ParameterException;
import uk.ac.ebi.quickgo.rest.controller.request.ArrayPattern;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static uk.ac.ebi.quickgo.annotation.IdGeneratorUtil.generateValues;
import static uk.ac.ebi.quickgo.annotation.model.AnnotationRequest.*;
import static uk.ac.ebi.quickgo.annotation.validation.loader.ValidationConfig.LOAD_ANNOTATION_DBX_REF_ENTITIES_STEP_NAME;
import static uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelperImpl.*;

/**
 * Tests that the validation added to the {@link AnnotationRequest} class is correct.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {AnnotationRequestConfig.class, ValidationConfig.class, JobTestRunnerConfig.class})
public class AnnotationRequestValidationIT {
    private static final String[] VALID_GENE_PRODUCT_ID = {"A0A000", "A0A003"};
    private static boolean HAS_RUN = false;

    @Autowired
    private Validator validator;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

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
        String[] validAssignedBy = {"ASPGD", "Agbase", "ASPGD_", "Agbase_", "BHF-UCL", "Roslin_Institute"};
        annotationRequest.setAssignedBy(validAssignedBy);

        assertThat(validator.validate(annotationRequest), hasSize(0));
    }

    @Test
    public void allAssignedByValuesAreInvalid() {
        String[] invalidAssignedBy = {"_ASPGD", "_Agbase", "5555,", "4444"};
        annotationRequest.setAssignedBy(invalidAssignedBy);

        Set<ConstraintViolation<AnnotationRequest>> violations = validator.validate(annotationRequest);

        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getMessage(),
                is(createRegexErrorMessage(ASSIGNED_BY_PARAM, invalidAssignedBy)));
    }

    //GO EVIDENCE PARAMETER
    @Test
    public void nullGoEvidenceIsValid() {
        annotationRequest.setGoIdEvidence(null);

        assertThat(validator.validate(annotationRequest), hasSize(0));
    }

    @Test
    public void allGoEvidenceValuesAreValid() {
        String[] goEvidence = {"IEA", "IBD", "IC"};
        annotationRequest.setGoIdEvidence(goEvidence);

        assertThat(validator.validate(annotationRequest), hasSize(0));
    }

    @Test
    public void allGoEvidenceValuesAreInvalid() {
        String[] invalidGoEvidence = {"9EA", "IBDD", "I"};
        annotationRequest.setGoIdEvidence(invalidGoEvidence);

        Set<ConstraintViolation<AnnotationRequest>> violations = validator.validate(annotationRequest);

        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getMessage(),
                is(createRegexErrorMessage(GO_EVIDENCE_PARAM, invalidGoEvidence)));
    }

    //ASPECT PARAMETER
    @Test
    public void nullAspectIsValid() {
        annotationRequest.setAspect(null);

        assertThat(validator.validate(annotationRequest), hasSize(0));
    }

    @Test
    public void processAspectIsValid() {
        annotationRequest.setAspect("biological_process");

        assertThat(validator.validate(annotationRequest), hasSize(0));
    }

    @Test
    public void functionAspectIsValid() {
        annotationRequest.setAspect("molecular_function");

        assertThat(validator.validate(annotationRequest), hasSize(0));
    }

    @Test
    public void componentAspectIsValid() {
        annotationRequest.setAspect("cellular_component");

        assertThat(validator.validate(annotationRequest), hasSize(0));
    }

    @Test
    public void unknownAspectIsInvalid() {
        String invalidAspect = "unknown";

        annotationRequest.setAspect(invalidAspect);

        Set<ConstraintViolation<AnnotationRequest>> violations = validator.validate(annotationRequest);

        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getMessage(),
                is(createRegexErrorMessage(ASPECT_PARAM, invalidAspect)));
    }

    @Test
    public void mixedCaseAspectIsValid() {
        String[] aspects = {"MoLeCuLaR_FuNcTiOn", "BiOlOgIcAl_pRoCeSs", "CeLlULaR_CoMpOnEnT"};

        annotationRequest.setAspect(aspects);

        Set<ConstraintViolation<AnnotationRequest>> violations = validator.validate(annotationRequest);

        assertThat(violations, hasSize(0));
    }

    //TAXONOMY ID PARAMETER (applicable to all taxonUsage values)
    @Test
    public void negativeTaxonIdIsInvalid() {
        String invalidTaxonId = "-1";

        annotationRequest.setTaxonId(invalidTaxonId);

        Set<ConstraintViolation<AnnotationRequest>> violations = validator.validate(annotationRequest);

        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getMessage(),
                is(createRegexErrorMessage(TAXON_ID_PARAM, invalidTaxonId)));
    }

    @Test
    public void taxonIdWithNonNumberCharactersIsInvalid() {
        String[] invalidTaxonIds = {"1a", "a", "$1"};

        annotationRequest.setTaxonId(invalidTaxonIds);

        Set<ConstraintViolation<AnnotationRequest>> violations = validator.validate(annotationRequest);

        assertThat(violations, hasSize(is(1)));
        assertThat(violations.iterator().next().getMessage(),
                is(createRegexErrorMessage(TAXON_ID_PARAM, invalidTaxonIds)));
    }

    @Test
    public void positiveNumericTaxonIdIsValid() {
        String taxonId = "2";

        annotationRequest.setTaxonId(taxonId);

        assertThat(validator.validate(annotationRequest), hasSize(0));
    }

    @Test
    public void multiplePositiveNumericTaxonIdsIsValid() {
        String[] taxonId = {"2", "3", "4", "5"};

        annotationRequest.setTaxonId(taxonId);

        assertThat(validator.validate(annotationRequest), hasSize(0));
    }

    @Test
    public void oneValidTaxIdAndOneInvalidTaxIdIsInvalid() {
        String invalidTaxonId = "-1";
        String[] taxonIds = {"2", invalidTaxonId};

        annotationRequest.setTaxonId(taxonIds);

        Set<ConstraintViolation<AnnotationRequest>> violations = validator.validate(annotationRequest);

        assertThat(violations, hasSize(is(1)));
        assertThat(violations.iterator().next().getMessage(),
                is(createRegexErrorMessage(TAXON_ID_PARAM, invalidTaxonId)));
    }

    @Test
    public void exceedingMaximumNumberOfTaxonIdentifiesSendsError() {
        int numIds = AnnotationRequest.MAX_TAXON_IDS + 1;

        String[] taxIds = IntStream.rangeClosed(1, numIds)
                .mapToObj(String::valueOf)
                .toArray(String[]::new);

        annotationRequest.setTaxonId(taxIds);

        Set<ConstraintViolation<AnnotationRequest>> violations = validator.validate(annotationRequest);

        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getMessage(),
                is(createMaxSizeErrorMessage(TAXON_ID_PARAM, MAX_TAXON_IDS)));
    }

    //TAXON USAGE
    @Test
    public void exactTaxonUsageIsValid() {
        String usage = "exact";

        annotationRequest.setTaxonUsage(usage);

        assertThat(validator.validate(annotationRequest), hasSize(0));
    }

    @Test
    public void descendantsTaxonUsageIsValid() {
        String usage = "descendants";

        annotationRequest.setTaxonUsage(usage);

        assertThat(validator.validate(annotationRequest), hasSize(0));
    }

    @Test
    public void multipleTaxonUsageIsInvalid() {
        String usage = "descendants,exact";

        annotationRequest.setTaxonUsage(usage);

        Set<ConstraintViolation<AnnotationRequest>> violations = validator.validate(annotationRequest);

        assertThat(violations, hasSize(is(1)));
        assertThat(violations.iterator().next().getMessage(),
                containsString("Invalid " + TAXON_USAGE_FIELD + ": " + usage + ""));
    }

    @Test
    public void taxonUsageIsInvalid() {
        String usage = "thisisnotokay";

        annotationRequest.setTaxonUsage(usage);

        Set<ConstraintViolation<AnnotationRequest>> violations = validator.validate(annotationRequest);

        assertThat(violations, hasSize(is(1)));
        assertThat(violations.iterator().next().getMessage(),
                containsString("Invalid " + TAXON_USAGE_FIELD + ": " + usage + ""));
    }

    // GENE PRODUCT ID
    @Test
    public void allGeneProductValuesAreValid() {
        annotationRequest.setGeneProductId(VALID_GENE_PRODUCT_ID);

        assertThat(validator.validate(annotationRequest), hasSize(0));
    }

    @Test
    public void geneProductIDValidationIsCaseSensitive() {
        String[] geneProductIdValues = Stream.of(VALID_GENE_PRODUCT_ID)
                .map(String::toLowerCase)
                .toArray(String[]::new);

        annotationRequest.setGeneProductId(geneProductIdValues);

        assertThat(validator.validate(annotationRequest), hasSize(0));
    }

    @Test
    public void allGeneProductValuesAreInvalid() {
        String[] invalidGpIds = {"99999", "&12345"};

        annotationRequest.setGeneProductId(invalidGpIds);

        Set<ConstraintViolation<AnnotationRequest>> violations = validator.validate(annotationRequest);

        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getMessage(),
                is("The 'Gene Product ID' parameter contains invalid values: 99999, &12345"));
    }

    @Test
    public void exceedingMaximumNumberOfGeneProductsIdentifiersSendsError() {
        int numIds = AnnotationRequest.MAX_GENE_PRODUCT_IDS + 1;

        String[] gpIds = generateValues(IdGeneratorUtil::createGPId, numIds);

        annotationRequest.setGeneProductId(gpIds);

        Set<ConstraintViolation<AnnotationRequest>> violations = validator.validate(annotationRequest);

        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getMessage(),
                is(createMaxSizeErrorMessage(GENE_PRODUCT_PARAM, MAX_GENE_PRODUCT_IDS)));
    }

    // GO ID PARAMETER
    @Test
    public void goIdIsValid() {
        String[] goIds = {"GO:0003824", "GO:0009999", "GO:0003333"};

        annotationRequest.setGoId(goIds);

        Set<ConstraintViolation<AnnotationRequest>> violations = validator.validate(annotationRequest);

        assertThat(violations, hasSize(0));
    }

    @Test
    public void mixedCaseGoIdIsValid() {
        String[] goIds = {"GO:0003824", "gO:0003824", "Go:0003824"};

        annotationRequest.setGoId(goIds);

        Set<ConstraintViolation<AnnotationRequest>> violations = validator.validate(annotationRequest);

        assertThat(violations, hasSize(0));
    }

    @Test
    public void goIdIsInvalid() {
        String[] invalidGoIds = {"GO:4", "xxx:0009999", "-"};

        annotationRequest.setGoId(invalidGoIds);

        Set<ConstraintViolation<AnnotationRequest>> violations = validator.validate(annotationRequest);

        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getMessage(),
                is(createRegexErrorMessage(GO_ID_PARAM, invalidGoIds)));
    }

    @Test
    public void requestingMaximumNumberOfGOIdentifiersIsValid() {
        String[] goIds = generateValues(IdGeneratorUtil::createGoId, AnnotationRequest.MAX_GO_IDS);
        annotationRequest.setGoId(goIds);
        Set<ConstraintViolation<AnnotationRequest>> violations = validator.validate(annotationRequest);
        assertThat(violations, hasSize(0));
    }

    @Test
    public void exceedingMaximumNumberOfGOIdentifiersSendsError() {
        int numIds = AnnotationRequest.MAX_GO_IDS + 1;

        String[] goIds = generateValues(IdGeneratorUtil::createGoId, numIds);

        annotationRequest.setGoId(goIds);

        Set<ConstraintViolation<AnnotationRequest>> violations = validator.validate(annotationRequest);

        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getMessage(), is(createMaxSizeErrorMessage(GO_ID_PARAM, MAX_GO_IDS)));
    }

    // EVIDENCE CODE PARAMETER
    @Test
    public void evidenceCodeIsValid() {
        String[] ecoIds = {"ECO:0000256", "ECO:0000888", "ECO:0000777"};

        annotationRequest.setEvidenceCode(ecoIds);

        assertThat(validator.validate(annotationRequest), hasSize(0));
    }

    @Test
    public void mixedCaseEvidenceCodeIsValid() {
        String[] ecoIds = {"ECO:0000256", "EcO:0000256", "eCO:0000256"};

        annotationRequest.setEvidenceCode(ecoIds);

        assertThat(validator.validate(annotationRequest), hasSize(0));
    }

    @Test
    public void evidenceCodeIsInvalid() {
        String[] invalidEvCodeIds = {"ECO:9", "xxx:0000888", "-"};

        annotationRequest.setEvidenceCode(invalidEvCodeIds);

        Set<ConstraintViolation<AnnotationRequest>> violations = validator.validate(annotationRequest);

        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getMessage(),
                is(createRegexErrorMessage(EVIDENCE_CODE_PARAM, invalidEvCodeIds)));
    }

    @Test
    public void exceedingMaximumNumberOfEvidenceCodesSendsError() {
        int numIds = AnnotationRequest.MAX_EVIDENCE_CODE + 1;

        String[] evCodes = generateValues(IdGeneratorUtil::createEvidenceCode, numIds);

        annotationRequest.setEvidenceCode(evCodes);

        Set<ConstraintViolation<AnnotationRequest>> violations = validator.validate(annotationRequest);

        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getMessage(),
                is(createMaxSizeErrorMessage(EVIDENCE_CODE_PARAM, MAX_EVIDENCE_CODE)));
    }

    // GENE PRODUCT TYPE PARAMETER
    @Test
    public void validGeneProductTypeValuesDontCauseAnError() {
        String[] gpTypes = {"complex", "miRNA", "protein"};

        annotationRequest.setGeneProductType(gpTypes);

        assertThat(validator.validate(annotationRequest), hasSize(0));
    }

    @Test
    public void setGpTypeNotCaseSensitive() {
        String[] gpTypes = {"comPlex", "mirna", "pRotein"};

        annotationRequest.setGeneProductType(gpTypes);

        assertThat(validator.validate(annotationRequest), hasSize(0));
    }

    @Test
    public void invalidGeneProductTypesCauseError() {
        String[] invalidGPTypes = {"xxx", "000", "..."};

        annotationRequest.setGeneProductType(invalidGPTypes);
        Set<ConstraintViolation<AnnotationRequest>> violations = validator.validate(annotationRequest);

        assertThat(violations, hasSize(is(1)));
        assertThat(violations.iterator().next().getMessage(),
                is(createRegexErrorMessage(GENE_PRODUCT_TYPE_PARAM, invalidGPTypes)));
    }

    // GENE PRODUCT SUBSET PARAMETER
    @Test
    public void setGpSubsetSuccessfully() {
        String[] gpSubsets = {"BHF-UCL", "Exosome", "KRUK", "ParkinsonsUK-UCL", "ReferenceGenome"};

        annotationRequest.setGeneProductSubset(gpSubsets);

        assertThat(validator.validate(annotationRequest), hasSize(0));
    }

    @Test
    public void invalidGpSubsetValuesResultInError() {
        String[] invalidSubsets = {"9999", "Reference:Genome", "*"};

        annotationRequest.setGeneProductSubset(invalidSubsets);
        Set<ConstraintViolation<AnnotationRequest>> violations = validator.validate(annotationRequest);

        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getMessage(),
                is(createRegexErrorMessage(GENE_PRODUCT_SUBSET_PARAM, invalidSubsets)));
    }

    // PAGE PARAMETER
    @Test
    public void negativePageValueIsInvalid() {
        int invalidPage = -1;

        annotationRequest.setPage(invalidPage);

        Set<ConstraintViolation<AnnotationRequest>> violations = validator.validate(annotationRequest);

        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getMessage(),
                is("Page number cannot be less than " + MIN_PAGE_NUMBER + ", but found: " + invalidPage));
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

    // LIMIT PARAMETER
    @Test
    public void negativeLimitValueIsInvalid() {
        int invalidEntriesPerPage = -1;
        annotationRequest.setLimit(invalidEntriesPerPage);

        Set<ConstraintViolation<AnnotationRequest>> violations = validator.validate(annotationRequest);

        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getMessage(),
                is("Number of entries per page cannot be less than " + MIN_ENTRIES_PER_PAGE + " but found: " +
                        invalidEntriesPerPage));
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
        annotationRequest.setLimit(MAX_ENTRIES_PER_PAGE);

        assertThat(validator.validate(annotationRequest), hasSize(0));
    }

    @Test
    public void limitValueAboveMaxEntriesPerPageIsInvalid() {
        int invalidEntriesPerPage = MAX_ENTRIES_PER_PAGE + 1;
        annotationRequest.setLimit(invalidEntriesPerPage);

        Set<ConstraintViolation<AnnotationRequest>> violations = validator.validate(annotationRequest);

        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getMessage(),
                is("Number of entries per page cannot be more than " + MAX_ENTRIES_PER_PAGE + " but found: " +
                        invalidEntriesPerPage));

        assertThat(validator.validate(annotationRequest), hasSize(greaterThan(0)));
    }

    // GO USAGE PARAMETERS
    @Test
    public void descendantsGoUsageIsValid() {
        annotationRequest.setGoUsage("descendants");

        assertThat(validator.validate(annotationRequest), hasSize(0));
    }

    @Test
    public void slimGoUsageIsValid() {
        annotationRequest.setGoUsage("slim");

        assertThat(validator.validate(annotationRequest), hasSize(0));
    }

    @Test
    public void exactGoUsageIsValid() {
        annotationRequest.setGoUsage("exact");

        assertThat(validator.validate(annotationRequest), hasSize(0));
    }

    @Test
    public void goUsageValueIsInvalid() {
        annotationRequest.setGoUsage("thisDoesNotExistAsAValidUsage");

        assertThat(validator.validate(annotationRequest), hasSize(greaterThan(0)));
    }

    @Test
    public void goUsageRelationshipIsValid() {
        annotationRequest.setGoUsageRelationships("is_a");

        assertThat(validator.validate(annotationRequest), hasSize(0));
    }

    @Test
    public void goUsageRelationshipIsInvalid() {
        String invalidRel = "invalid_relationship";

        annotationRequest.setGoUsageRelationships(invalidRel);

        Set<ConstraintViolation<AnnotationRequest>> violations = validator.validate(annotationRequest);

        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getMessage(),
                is(createRegexErrorMessage(USAGE_RELATIONSHIP_PARAM, invalidRel)));
    }

    @Test(expected = ParameterException.class)
    public void cannotCreateFilterWithGoUsageAndNoUsageIds() {
        annotationRequest.setGoUsage("descendants");

        annotationRequest.createFilterRequests();
    }

    //---------------------------------------
    // ECO USAGE PARAMETERS
    @Test
    public void descendantsEcoUsageIsValid() {
        annotationRequest.setEvidenceCodeUsage("descendants");

        assertThat(validator.validate(annotationRequest), hasSize(0));
    }

    @Test
    public void exactEcoUsageIsValid() {
        annotationRequest.setEvidenceCodeUsage("exact");

        assertThat(validator.validate(annotationRequest), hasSize(0));
    }

    @Test
    public void slimEcoUsageIsInvalid() {
        annotationRequest.setEvidenceCodeUsage("slim");

        assertThat(validator.validate(annotationRequest), hasSize(greaterThan(0)));
    }

    @Test
    public void ecoUsageValueIsInvalid() {
        annotationRequest.setEvidenceCodeUsage("thisDoesNotExistAsAValidUsage");

        assertThat(validator.validate(annotationRequest), hasSize(greaterThan(0)));
    }

    @Test
    public void ecoUsageRelationshipIsValid() {
        annotationRequest.setEvidenceCodeUsageRelationships("is_a");

        assertThat(validator.validate(annotationRequest), hasSize(0));
    }

    @Test
    public void ecoUsageRelationshipIsInvalid() {
        String invalidRel = "invalid_relationship";

        annotationRequest.setEvidenceCodeUsageRelationships(invalidRel);

        Set<ConstraintViolation<AnnotationRequest>> violations = validator.validate(annotationRequest);

        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getMessage(),
                is(createRegexErrorMessage(USAGE_RELATIONSHIP_PARAM, invalidRel)));
    }

    @Test(expected = ParameterException.class)
    public void cannotCreateFilterWithEcoUsageAndNoUsageIds() {
        annotationRequest.setEvidenceCodeUsage("descendants");

        annotationRequest.createFilterRequests();
    }

    //---------------------------------------
    // WITH/FROM PARAMETER
    @Test
    public void withFromIsValid() {
        setupDbXrefValidationData();
        String[] refs = new String[]{"PMID:123456"};
        annotationRequest.setWithFrom(refs);
        Set<ConstraintViolation<AnnotationRequest>> violations = validator.validate(annotationRequest);
        assertThat(violations, hasSize(0));
    }

    @Test
    public void withFromIsInvalid() {
        setupDbXrefValidationData();
        String[] refs = new String[]{"PMID:ZZZZZZZZ"};
        annotationRequest.setWithFrom(refs);
        Set<ConstraintViolation<AnnotationRequest>> violations = validator.validate(annotationRequest);
        assertThat(violations, hasSize(1));
    }

    // REFERENCE PARAMETER
    @Test
    public void exceedingMaximumNumberOfReferencesSendsError() {
        setupDbXrefValidationData();

        int numRefs = AnnotationRequest.MAX_REFERENCES + 1;
        List<String> refs = IntStream.range(0, numRefs)
                .mapToObj(i -> "PMID:123456")
                .collect(toList());
        annotationRequest.setReference(refs.toArray(new String[0]));
        Set<ConstraintViolation<AnnotationRequest>> violations = validator.validate(annotationRequest);

        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getMessage(),
                is(createMaxSizeErrorMessage(REFERENCE_PARAM, MAX_REFERENCES)));
    }

    @Test
    public void referenceIsValid() {
        setupDbXrefValidationData();
        String[] refs = new String[]{"PMID:123456"};
        annotationRequest.setReference(refs);
        Set<ConstraintViolation<AnnotationRequest>> violations = validator.validate(annotationRequest);
        assertThat(violations, hasSize(0));
    }

    @Test
    public void referenceIsInvalid() {
        setupDbXrefValidationData();
        String[] refs = new String[]{"PMID:ZZZZZZZZ"};
        annotationRequest.setReference(refs);
        Set<ConstraintViolation<AnnotationRequest>> violations = validator.validate(annotationRequest);
        assertThat(violations, hasSize(1));
    }

    // QUALIFIER
    @Test
    public void qualifierWithUnderscoreNoSpacesOrNumbersIsValid() {
        annotationRequest.setQualifier("foobar", "foo_bar");
        assertThat(validator.validate(annotationRequest), hasSize(0));
    }

    @Test
    public void qualifierWithNoSpacesAroundPipeIsValid() {
        annotationRequest.setQualifier("NOT|enables");
        assertThat(validator.validate(annotationRequest), hasSize(0));
    }

    @Test
    public void qualifierWithSpacesAroundPipeIsInvalid() {
        annotationRequest.setQualifier("NOT | enables");
        assertThat(validator.validate(annotationRequest), hasSize(greaterThan(0)));
        annotationRequest.setQualifier("NOT |enables");
        assertThat(validator.validate(annotationRequest), hasSize(greaterThan(0)));
        annotationRequest.setQualifier("NOT| enables");
        assertThat(validator.validate(annotationRequest), hasSize(greaterThan(0)));
    }

    @Test
    public void qualifierNotValueCaseInsensitiveIsValid() {
        annotationRequest.setQualifier("Not|enable");
        assertThat(validator.validate(annotationRequest), hasSize(0));
        annotationRequest.setQualifier("nOT|enable");
        assertThat(validator.validate(annotationRequest), hasSize(0));
    }

    @Test
    public void qualifierWithNotAndNoPipeIsInvalid() {
        annotationRequest.setQualifier("not boo");
        assertThat(validator.validate(annotationRequest), hasSize(greaterThan(0)));
    }

    @Test
    public void stringWithNumbersIsAnInvalidQualifier() {
        annotationRequest.setQualifier("foo3bar");
        assertThat(validator.validate(annotationRequest), hasSize(greaterThan(0)));
    }

    // DOWNLOAD LIMIT
    @Test
    public void negativeDownloadLimitIsInvalid() {
        annotationRequest.setDownloadLimit(-1);
        assertThat(validator.validate(annotationRequest), hasSize(greaterThan(0)));
    }

    @Test
    public void zeroDownloadLimitIsInvalid() {
        annotationRequest.setDownloadLimit(0);
        assertThat(validator.validate(annotationRequest), hasSize(greaterThan(0)));
    }

    @Test
    public void positiveDownloadLimitLessThanMaxIsValid() {
        annotationRequest.setDownloadLimit(MAX_DOWNLOAD_NUMBER - 1);
        assertThat(validator.validate(annotationRequest), is(empty()));
    }

    @Test
    public void positiveDownloadLimitEqualToMaxIsValid() {
        annotationRequest.setDownloadLimit(MAX_DOWNLOAD_NUMBER);
        assertThat(validator.validate(annotationRequest), is(empty()));
    }

    @Test
    public void positiveDownloadLimitGreaterThanMaxIsInvalid() {
        annotationRequest.setDownloadLimit(MAX_DOWNLOAD_NUMBER + 1);
        assertThat(validator.validate(annotationRequest), hasSize(greaterThan(0)));
    }

    // OPTIONALLY INCLUDED RESPONSE FIELDS
    @Test
    public void goNameAsIncludedFieldIsValid() {
        annotationRequest.setIncludeFields("goName");
        assertThat(validator.validate(annotationRequest), is(empty()));
    }

    @Test
    public void taxonNameAsIncludedFieldIsValid() {
        annotationRequest.setIncludeFields("taxonName");
        assertThat(validator.validate(annotationRequest), is(empty()));
    }

    @Test
    public void multipleValidIncludedFieldsAreValid() {
        annotationRequest.setIncludeFields("goName", "taxonName");
        assertThat(validator.validate(annotationRequest), is(empty()));
    }

    @Test
    public void invalidIncludedFieldProducesValidationError() {
        annotationRequest.setIncludeFields("XXXXXXXXX");
        assertThat(validator.validate(annotationRequest), hasSize(greaterThan(0)));
    }

    // SELECTED FIELDS
    @Test
    public void validSelectedFieldsAreAllValidatedCorrectly() {
        String[] validSelectedFields =
                ("geneProductId|symbol|qualifier|goId|goAspect|goName|evidenceCode|goEvidence|reference" +
                "|withFrom|taxonId|taxonName|assignedBy|extensions|date|name|synonyms|type|interactingTaxonId").split("\\|");

        for (String validSelectedField : validSelectedFields) {
            annotationRequest.setSelectedFields(validSelectedField);
            assertThat(validator.validate(annotationRequest), is(empty()));
        }
    }

    @Test
    public void multipleValidSelectedFieldsAreValid() {
        annotationRequest.setSelectedFields("geneProductId", "symbol");
        assertThat(validator.validate(annotationRequest), is(empty()));
    }

    @Test
    public void invalidSelectedFieldProducesValidationError() {
        annotationRequest.setSelectedFields("XXXXXXXXXX");
        assertThat(validator.validate(annotationRequest), hasSize(greaterThan(0)));
    }

    // Helpers
    private String createRegexErrorMessage(String paramName, String... invalidItems) {
        String csvInvalidItems = Stream.of(invalidItems).collect(Collectors.joining(", "));
        return String.format(ArrayPattern.DEFAULT_ERROR_MSG, paramName, csvInvalidItems);
    }

    private String createMaxSizeErrorMessage(String paramName, int maxSize) {
        return "Number of items in '" + paramName + "' is larger than: " + maxSize;
    }

    private void setupDbXrefValidationData() {
        if (!HAS_RUN) {
            JobExecution jobExecution = jobLauncherTestUtils.launchStep(LOAD_ANNOTATION_DBX_REF_ENTITIES_STEP_NAME);
            assertThat(jobExecution.getStatus(), is(BatchStatus.COMPLETED));
            HAS_RUN = true;
        }

    }
}
