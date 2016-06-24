package uk.ac.ebi.quickgo.annotation.model;

import uk.ac.ebi.quickgo.rest.ParameterException;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;

import org.hibernate.validator.HibernateValidator;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static uk.ac.ebi.quickgo.annotation.model.AnnotationRequest.USAGE_FIELD;
import static uk.ac.ebi.quickgo.annotation.model.AnnotationRequest.USAGE_IDS;
import static uk.ac.ebi.quickgo.annotation.model.AnnotationRequest.USAGE_RELATIONSHIPS;

/**
 * Check filter storage of an {@link AnnotationRequest} and validate
 * values specified in its {@link javax.validation} annotations.
 */
public class AnnotationRequestTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private AnnotationRequest annotationRequest;
    private LocalValidatorFactoryBean validator;

    @Before
    public void setUp() {
        annotationRequest = new AnnotationRequest();
        validator = new LocalValidatorFactoryBean();
        validator.setProviderClass(HibernateValidator.class);
        validator.afterPropertiesSet();
    }

    @Test
    public void defaultPageAndLimitValuesAreCorrect() {
        assertThat(annotationRequest.getPage(), equalTo(1));
        assertThat(annotationRequest.getLimit(), equalTo(25));
    }

    @Test
    public void successfullySetAndGetPageAndLimitValues() {
        annotationRequest.setPage(4);
        annotationRequest.setLimit(15);

        assertThat(annotationRequest.getPage(), equalTo(4));
        assertThat(annotationRequest.getLimit(), equalTo(15));
        expectedNumberOfValidationErrors(0);
    }

    @Test
    public void setAndGetAssignedBy() {
        String assignedBy = "UniProt";
        annotationRequest.setAssignedBy(assignedBy);

        assertThat(annotationRequest.getAssignedBy(), is(assignedBy));
        expectedNumberOfValidationErrors(0);
    }

    @Test
    public void setAndGetOntologyAspect() {
        String aspect = "molecular_function";

        annotationRequest.setAspect(aspect);

        assertThat(annotationRequest.getAspect(), is(aspect));
        expectedNumberOfValidationErrors(0);
    }

    @Test
    public void setAndGetEvidence() {
        String EVIDENCE_IEA = "IEA";
        annotationRequest.setGoEvidence(EVIDENCE_IEA);
        assertThat(annotationRequest.getGoEvidence(), is(EVIDENCE_IEA));
        expectedNumberOfValidationErrors(0);
    }

    @Test
    public void setAndGetEvidenceMulti() {
        String EVIDENCE_MULTI = "IEA,IBD";
        annotationRequest.setGoEvidence(EVIDENCE_MULTI);
        assertThat(annotationRequest.getGoEvidence(), is(EVIDENCE_MULTI));
        expectedNumberOfValidationErrors(0);
    }

    @Test
    public void setAndGetEvidenceMultiInLowerCase() {
        String EVIDENCE_MULTI = "iea,ibd";
        annotationRequest.setGoEvidence(EVIDENCE_MULTI);
        assertThat(annotationRequest.getGoEvidence(), is(EVIDENCE_MULTI));
        expectedNumberOfValidationErrors(0);
    }

    @Test
    public void setAndGetTaxon() {
        String taxonId = "1";

        annotationRequest.setTaxon(taxonId);

        assertThat(annotationRequest.getTaxon(), is(taxonId));
        expectedNumberOfValidationErrors(0);
    }

    @Test
    public void setAndGetUsage() {
        String usage = "exact";

        annotationRequest.setUsage(usage);

        assertThat(annotationRequest.getUsage(), is(usage));
        expectedNumberOfValidationErrors(0);
    }

    @Test
    public void setAndGetUsageIds() {
        String usageIds = "GO:0000001,GO:0000002";

        annotationRequest.setUsageIds(usageIds);

        assertThat(annotationRequest.getUsageIds(), is(usageIds.toLowerCase()));
        expectedNumberOfValidationErrors(0);
    }

    @Test
    public void createsFilterWithUsageAndUsageIds() {
        annotationRequest.setUsage("descEndants");
        annotationRequest.setUsageIds("GO:0000001");

        FilterRequest request = FilterRequest.newBuilder()
                .addProperty(USAGE_FIELD, "descendants")
                .addProperty(USAGE_IDS, "go:0000001")
                .addProperty(USAGE_RELATIONSHIPS)
                .build();
        assertThat(annotationRequest.createFilterRequests(),
                contains(request));
        expectedNumberOfValidationErrors(0);
    }

    @Test
    public void createsFilterWithCaseInsensitiveUsageAndUsageIdsAndUsageRelationships() {
        annotationRequest.setUsage("deSCendants");
        annotationRequest.setUsageIds("GO:0000001");
        annotationRequest.setUsageRelationships("is_A");

        assertThat(annotationRequest.createFilterRequests(),
                contains(FilterRequest.newBuilder()
                        .addProperty(USAGE_FIELD, "descendants")
                        .addProperty(USAGE_IDS, "go:0000001")
                        .addProperty(USAGE_RELATIONSHIPS, "is_a")
                        .build()));
        expectedNumberOfValidationErrors(0);
    }

    @Test
    public void cannotCreatesFilterInvalidUsageRelationship() {
        annotationRequest.setUsage("descendants");
        annotationRequest.setUsageIds("GO:0000001");
        annotationRequest.setUsageRelationships("this_is_not_allowed");

        annotationRequest.createFilterRequests();

        expectedNumberOfValidationErrors(1);
    }

    @Test(expected = ParameterException.class)
    public void cannotCreateFilterWithUsageAndNoUsageIds() {
        annotationRequest.setUsage("descendants");

        annotationRequest.createFilterRequests();
    }

    private void expectedNumberOfValidationErrors(int expectedErrorCount) {
        assertThat(validator.validate(annotationRequest).size(), is(expectedErrorCount));
    }
}
