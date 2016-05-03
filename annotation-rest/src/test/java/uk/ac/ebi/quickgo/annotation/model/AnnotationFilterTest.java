package uk.ac.ebi.quickgo.annotation.model;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationFields;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

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

    public static final String UNI_PROT = "UniProt";
    public static final String ASPGD = "ASPGD";
    String multiAssignedBy;

    @Before
    public void setUp(){
        multiAssignedBy = UNI_PROT+","+ASPGD;
    }

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @Test
    public void successfullyAddOnlyOneSingleFilter(){

        AnnotationFilter annotationFilter = new AnnotationFilter();
        annotationFilter.setAssignedby(UNI_PROT);
        List<AnnotationFilter.PrototypeFilter> filterList = annotationFilter.stream().collect(toList());
        assertThat(filterList, hasSize(1));
        assertThat(filterList.get(0).getSolrName(), is(equalTo(AnnotationFields.ASSIGNED_BY)));
        assertThat(filterList.get(0).getArgs().get(0), is(equalTo(UNI_PROT)));
    }


    @Test
    public void successfullyAddMultiFilter(){

        AnnotationFilter annotationFilter = new AnnotationFilter();
        annotationFilter.setAssignedby(multiAssignedBy);
        List<AnnotationFilter.PrototypeFilter> filterList = annotationFilter.stream().collect(toList());
        assertThat(filterList, hasSize(1));
        assertThat(filterList.get(0).getSolrName(), is(equalTo(AnnotationFields.ASSIGNED_BY)));
        assertThat(filterList.get(0).getArgs().get(0), is(equalTo(UNI_PROT)));
        assertThat(filterList.get(0).getArgs().get(1), is(equalTo(ASPGD)));
    }



    @Test
    public void defaultPageAndLimitValuesAreCorrect(){
        AnnotationFilter annotationFilter = new AnnotationFilter();
        assertThat(annotationFilter.getPage(), equalTo("1"));
        assertThat(annotationFilter.getLimit(), equalTo("25"));
    }

    @Test
    public void successfullySetPageAndLimitValues(){
        AnnotationFilter annotationFilter = new AnnotationFilter();
        annotationFilter.setPage("4");
        annotationFilter.setLimit("15");
        assertThat(annotationFilter.getPage(), equalTo("4"));
        assertThat(annotationFilter.getLimit(), equalTo("15"));
    }


    @Test
    public void exceptionThrownWhenAsForMoreResultsThanLimit(){
        thrown.expect(IllegalArgumentException.class);
        AnnotationFilter annotationFilter = new AnnotationFilter();
        annotationFilter.setLimit("200");
        annotationFilter.validation();
    }

}
