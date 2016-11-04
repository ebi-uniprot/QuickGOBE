package uk.ac.ebi.quickgo.annotation.model;

import uk.ac.ebi.quickgo.rest.ParameterException;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;

import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static uk.ac.ebi.quickgo.annotation.model.AnnotationRequest.USAGE_GO_ID;
import static uk.ac.ebi.quickgo.annotation.model.AnnotationRequest.USAGE_RELATIONSHIPS;

/**
 *
 * Tests methods and structure of AnnotationRequest
 *
 * @author Tony Wardell
 * Date: 29/04/2016
 * Time: 11:25
 * Created with IntelliJ IDEA.
 */
public class AnnotationRequestTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private AnnotationRequest annotationRequest;

    @Before
    public void setUp() {
        annotationRequest = new AnnotationRequest();
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
    }

    @Test
    public void setAndGetAssignedBy() {
        String assignedBy = "UniProt";
        annotationRequest.setAssignedBy(assignedBy);

        assertThat(annotationRequest.getAssignedBy(), arrayContaining(assignedBy));
    }

    @Test
    public void setAndGetWithFrom() {
        String WITH_FROM = "RGD:1623038";
        annotationRequest.setWithFrom(WITH_FROM);
        assertThat(annotationRequest.getWithFrom(), arrayContaining(WITH_FROM));
    }

    @Test
    public void setAndGetOntologyAspect() {
        String aspect = "function";

        annotationRequest.setAspect(aspect);

        assertThat(annotationRequest.getAspect(), arrayContaining(aspect));
    }

    @Test
    public void setAndGetGeneProductID() {
        String geneProductID = "A0A000";
        annotationRequest.setGeneProductId(geneProductID);
        assertThat(annotationRequest.getGeneProductId(), arrayContaining(geneProductID));
    }

    @Test
    public void setAndGetMultipleGeneProductIDs() {
        String[] geneProductID = {"A0A000", "A0A001"};
        annotationRequest.setGeneProductId(geneProductID);
        assertThat(annotationRequest.getGeneProductId(), arrayContaining(geneProductID));
    }

    @Test
    public void setAndGetEvidence() {
        String EVIDENCE_IEA = "IEA";
        annotationRequest.setGoIdEvidence(EVIDENCE_IEA);
        assertThat(annotationRequest.getGoIdEvidence(), arrayContaining(EVIDENCE_IEA));
    }

    @Test
    public void setAndGetEvidenceMulti() {
        String[] EVIDENCE_MULTI = {"IEA", "IBD"};
        annotationRequest.setGoIdEvidence(EVIDENCE_MULTI);
        assertThat(annotationRequest.getGoIdEvidence(), arrayContaining(EVIDENCE_MULTI));
    }

    @Test
    public void setAndGetEvidenceMultiInLowerCase() {
        String[] EVIDENCE_MULTI = {"iea", "ibd"};
        annotationRequest.setGoIdEvidence(EVIDENCE_MULTI);
        assertThat(annotationRequest.getGoIdEvidence(), is(EVIDENCE_MULTI));
    }

    @Test
    public void setAndGetTaxon() {
        String taxonId = "1";

        annotationRequest.setTaxonId(taxonId);

        assertThat(annotationRequest.getTaxonId(), arrayContaining(taxonId));
    }

    @Test
    public void setAndGetUsage() {
        String usage = "exact";

        annotationRequest.setUsage(usage);

        assertThat(annotationRequest.getUsage(), is(usage));
    }

    @Test
    public void setAndGetGoIds() {
        String[] usageIds = {"GO:0000001", "GO:0000002"};

        annotationRequest.setGoId(usageIds);

        assertThat(annotationRequest.getGoId(), is(usageIds));
    }

    @Test
    public void setAndGetUsageRelationships() {
        String[] usageRelationships = {"iS_", "paRt_of"};

        annotationRequest.setUsageRelationships(usageRelationships);

        String[] expectedLowerCaseRels = Stream.of(usageRelationships)
                .map(String::toLowerCase)
                .toArray(String[]::new);

        assertThat(annotationRequest.getUsageRelationships(), arrayContaining(expectedLowerCaseRels));
    }

    @Test
    public void createsFilterWithCaseInsensitiveUsageAndGoIds() {
        String usage = "descEndants";
        String goId = "GO:0000001";

        annotationRequest.setUsage(usage);
        annotationRequest.setGoId(goId);

        FilterRequest request = FilterRequest.newBuilder()
                .addProperty(usage.toLowerCase())
                .addProperty(USAGE_GO_ID, goId.toUpperCase())
                .addProperty(USAGE_RELATIONSHIPS)
                .build();
        assertThat(annotationRequest.createFilterRequests(),
                contains(request));
    }

    @Test
    public void createsFilterWithCaseInsensitiveUsageAndGoIdsAndUsageRelationships() {
        String usage = "deSCendants";
        String goId = "GO:0000001";
        String relationships = "is_A";

        annotationRequest.setUsage(usage);
        annotationRequest.setGoId(goId);
        annotationRequest.setUsageRelationships(relationships);

        assertThat(annotationRequest.createFilterRequests(),
                contains(FilterRequest.newBuilder()
                        .addProperty(usage.toLowerCase())
                        .addProperty(USAGE_GO_ID, goId.toUpperCase())
                        .addProperty(USAGE_RELATIONSHIPS, relationships.toLowerCase())
                        .build()));
    }

    @Test(expected = ParameterException.class)
    public void cannotCreateFilterWithUsageAndNoUsageIds() {
        annotationRequest.setUsage("descendants");

        annotationRequest.createFilterRequests();
    }

    @Test
    public void setAndGetQualifier() {
        String qualifier = "NOT";
        annotationRequest.setQualifier(qualifier);
        assertThat(annotationRequest.getQualifier(), arrayContaining(qualifier));
    }

    @Test
    public void setAndGetReference() {
        String ONE_GOREF = "GO_REF:123456";
        annotationRequest.setReference(ONE_GOREF);
        assertThat(annotationRequest.getReference(), arrayContaining(ONE_GOREF));
    }

    @Test
    public void setAndGetECOId() {
        String ecoId = "ECO:0000256";
        annotationRequest.setEvidenceCode(ecoId);
        assertThat(annotationRequest.getEvidenceCode(), arrayContaining(ecoId));
    }
}