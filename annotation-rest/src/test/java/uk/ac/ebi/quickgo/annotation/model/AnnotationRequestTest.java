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

    private String multiAssignedBy = UNI_PROT + "," + ASPGD;

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

//    @Test
//    public void addSingleFilter() {
//        annotationRequest.setAssignedBy(UNI_PROT);
//        final List<RequestFilter> pfList = annotationRequest.stream().collect(toList());
//        assertThat(pfList.get(0).getField(), is(equalTo(AnnotationFields.ASSIGNED_BY)));
//        assertThat(pfList, hasSize(1));
//
//        assertThat(pfList.get(0)
//                .provideArgStream()
//                .findFirst().isPresent(), is(true));
//        assertThat(pfList.get(0)
//                .provideArgStream()
//                .findFirst().get(), is(equalTo(UNI_PROT)));
//        assertThat(pfList.get(0)
//                .provideArgStream()
//                .count(), is(1L));
//    }

//    @Test
//    public void successfullyAddMultiFilterForAssignedBy() {
//
//        annotationRequest.setAssignedBy(multiAssignedBy);
//        final List<RequestFilter> pfList = annotationRequest.stream().collect(toList());
//        assertThat(pfList, hasSize(1));
//        assertThat(pfList.get(0).getField(), is(equalTo(AnnotationFields.ASSIGNED_BY)));
//
//        assertThat(pfList.get(0)
//                .provideArgStream()
//                .findFirst().get(), is(equalTo(UNI_PROT)));
//
//        assertThat(pfList.get(0)
//                .provideArgStream()
//                .filter(a -> a.equals(ASPGD))
//                .findFirst().get(), is(equalTo(ASPGD)));
//
//        long countASPGD = pfList.get(0)
//                .provideArgStream()
//                .filter(a -> a.equals(ASPGD))
//                .count();
//        assertThat(countASPGD, is(1L));
//    }
}