package uk.ac.ebi.quickgo.annotation.model;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 *
 * Test methods and structure of AnnotationRequest
 *
 * @author Tony Wardell
 * Date: 29/04/2016
 * Time: 11:25
 * Created with IntelliJ IDEA.
 */
public class AnnotationRequestTest {

    //AssignedBy values
    private static final String UNI_PROT = "UniProt";

    private AnnotationRequest annotationRequest;

    @Before
    public void setUp() {
        annotationRequest = new AnnotationRequest();
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

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
    }

    @Test
    public void setAndGetAssignedBy() {
        String assignedBy = "UniProt";
        annotationRequest.setAssignedBy(assignedBy);

        assertThat(annotationRequest.getAssignedBy(), is(assignedBy));
    }

    @Test
    public void setAndGetWithFrom(){
        String WITH_FROM = "RGD:1623038";
        annotationRequest.setWithFrom(WITH_FROM);
        assertThat(annotationRequest.getWithFrom(), is(WITH_FROM));
    }

    @Test
    public void setAndGetOntologyAspect() {
        String aspect = "function";

        annotationRequest.setAspect(aspect);

        assertThat(annotationRequest.getAspect(), is(aspect));
    }
    @Test
    public void setAndGetEvidence(){
        String EVIDENCE_IEA = "IEA";
        annotationRequest.setGoEvidence(EVIDENCE_IEA);
        assertThat(annotationRequest.getGoEvidence(), is(EVIDENCE_IEA));
    }

    @Test
    public void setAndGetEvidenceMulti(){
        String EVIDENCE_MULTI = "IEA,IBD";
        annotationRequest.setGoEvidence(EVIDENCE_MULTI);
        assertThat(annotationRequest.getGoEvidence(), is(EVIDENCE_MULTI));
    }

    @Test
    public void setAndGetEvidenceMultiInLowerCase(){
        String EVIDENCE_MULTI = "iea,ibd";
        annotationRequest.setGoEvidence(EVIDENCE_MULTI);
        assertThat(annotationRequest.getGoEvidence(), is(EVIDENCE_MULTI));
    }


    @Test
    public void setAndGetTaxon() {
        String taxonId = "1";

        annotationRequest.setTaxon(taxonId);

        assertThat(annotationRequest.getTaxon(), is(taxonId));
    }

    @Test
    public void setAndGetUsage() {
        String usage = "exact";

        annotationRequest.setUsage(usage);

        assertThat(annotationRequest.getUsage(), is(usage));
    }

    @Test
    public void setAndGetUsageIds() {
        String usageIds = "GO:0000001,GO:0000002";

        annotationRequest.setUsageIds(usageIds);

        assertThat(annotationRequest.getUsageIds(), is(usageIds.toLowerCase()));
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
    }

    @Test(expected = ParameterException.class)
    public void cannotCreatesFilterInvalidUsageRelationship() {
        annotationRequest.setUsage("descendants");
        annotationRequest.setUsageIds("GO:0000001");
        annotationRequest.setUsageRelationships("this_is_not_allowed");

        annotationRequest.createFilterRequests();
    }

    @Test(expected = ParameterException.class)
    public void cannotCreateFilterWithUsageAndNoUsageIds() {
        annotationRequest.setUsage("descendants");

        annotationRequest.createFilterRequests();
    }

    @Test
    public void setAndGetQualifier(){
        String qualifier = "NOT";
        annotationRequest.setQualifier((qualifier));
        assertThat(annotationRequest.getQualifter(), is(qualifier));
    }

    @Test
    public void setAndGetReference(){
        String ONE_GOREF = "GO_REF:123456";
        annotationRequest.setReference(ONE_GOREF);
        assertThat(annotationRequest.getReference(), is(ONE_GOREF));
    }
}
