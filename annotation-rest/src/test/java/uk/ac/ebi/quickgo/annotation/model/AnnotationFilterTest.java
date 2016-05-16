package uk.ac.ebi.quickgo.annotation.model;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationFields;
import uk.ac.ebi.quickgo.rest.search.query.PrototypeFilter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
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
 * Test methods and structure of AnnotationFilter
 *
 * @author Tony Wardell
 * Date: 29/04/2016
 * Time: 11:25
 * Created with IntelliJ IDEA.
 */
public class AnnotationFilterTest {

    private static final String UNI_PROT = "UniProt";
    private static final String ASPGD = "ASPGD";

    private AnnotationFilter annotationFilter;

    private String multiAssignedBy = UNI_PROT + "," + ASPGD;

    @Before
    public void setUp() {
        annotationFilter = new AnnotationFilter();
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void successfullyAddOnlyOneSingleFilter() {
        annotationFilter.setAssignedBy(UNI_PROT);
        final List<PrototypeFilter> pfList = annotationFilter.stream().collect(toList());
        assertThat(pfList.get(0).getFilterField(), is(equalTo(AnnotationFields.ASSIGNED_BY)));
        assertThat(pfList, hasSize(1));

        Optional<String> result = pfList.get(0)
                .provideArgStream()
                .findFirst();

        assertThat(result.isPresent(), is(true));
        assertThat(result.get(), is(equalTo(UNI_PROT)));
    }


    @Test
    public void successfullyAddMultiFilterForAssignedBy(){

        annotationFilter.setAssignedBy(multiAssignedBy);
        final List<PrototypeFilter> pfList = annotationFilter.stream().collect(toList());
        assertThat(pfList, hasSize(1));
        assertThat(pfList.get(0).getFilterField(), is(equalTo(AnnotationFields.ASSIGNED_BY)));

        assertThat(pfList.get(0)
                .provideArgStream()
                .findFirst().get(), is(equalTo(UNI_PROT)));

        assertThat(pfList.get(0)
                .provideArgStream()
                .filter(a -> a.equals(ASPGD))
                .findFirst().get(), is(equalTo(ASPGD)));

       long countASPGD = pfList.get(0)
                .provideArgStream()
                .filter(a -> a.equals(ASPGD))
                .count();
        assertThat(countASPGD, is(1L));
    }



    @Test
    public void defaultPageAndLimitValuesAreCorrect() {
        assertThat(annotationFilter.getPage(), equalTo(1));
        assertThat(annotationFilter.getLimit(), equalTo(25));
    }

    @Test
    public void successfullySetPageAndLimitValues() {
        annotationFilter.setPage(4);
        annotationFilter.setLimit(15);
        assertThat(annotationFilter.getPage(), equalTo(4));
        assertThat(annotationFilter.getLimit(), equalTo(15));
    }
}
