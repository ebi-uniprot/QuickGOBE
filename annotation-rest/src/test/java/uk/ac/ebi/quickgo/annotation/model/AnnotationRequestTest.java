package uk.ac.ebi.quickgo.annotation.model;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationFields;
import uk.ac.ebi.quickgo.rest.search.filter.RequestFilter;

import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
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
    private static final String UNI_PROT = "UniProt";
    private static final String ASPGD = "ASPGD";
    private static final String EVIDENCE_IEA = "IEA";
    private static final String EVIDENCE_MULTI = "IEA,IBD";


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
    public void successfullySetPageAndLimitValues() {
        annotationRequest.setPage(4);
        annotationRequest.setLimit(15);

        assertThat(annotationRequest.getPage(), equalTo(4));
        assertThat(annotationRequest.getLimit(), equalTo(15));
    }

    @Test
    public void setAndGetAssignedBy() {
        annotationRequest.setAssignedBy(UNI_PROT);
        assertThat(annotationRequest.getAssignedBy(), is(UNI_PROT));
    }


    @Test
    public void setAndGetEvidence(){
        annotationRequest.setGoEvidence(EVIDENCE_IEA);
        assertThat(annotationRequest.getGoEvidence(), is(EVIDENCE_IEA));
    }

    @Test
    public void setAndGetEvidenceMulti(){
        annotationRequest.setGoEvidence(EVIDENCE_MULTI);
        assertThat(annotationRequest.getGoEvidence(), is(EVIDENCE_MULTI));
    }

}
