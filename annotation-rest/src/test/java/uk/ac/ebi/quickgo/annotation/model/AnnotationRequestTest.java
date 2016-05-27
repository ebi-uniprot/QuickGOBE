package uk.ac.ebi.quickgo.annotation.model;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

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
@RunWith(MockitoJUnitRunner.class)
public class AnnotationRequestTest {

    //AssignedBy values
    private static final String UNI_PROT = "UniProt";

    //Reference
    private static final String EXAMPLE_REFERENCES="DOI,PMID,Reactome,GO_REF:0000037";
    private static final String ONE_GOREF = "GO_REF:123456";

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
    public void setAndGetReference(){
        annotationRequest.setReference(ONE_GOREF);
        assertThat(annotationRequest.getReference(), is(ONE_GOREF));
    }
}
