package uk.ac.ebi.quickgo.annotation.model;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
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

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @Test
    public void testSuccessfullyAddSingleAssignedBy(){

        AnnotationFilter annotationFilter = new AnnotationFilter();
        annotationFilter.setAssignedby("UniProt");
        assertThat(annotationFilter.getAssignedby(), hasSize(1));
        assertThat(annotationFilter.getAssignedby(), hasItem("UniProt"));
    }

    @Test
    public void testSuccessfullyAddMultipleAssignedBy(){

        AnnotationFilter annotationFilter = new AnnotationFilter();
        annotationFilter.setAssignedby("UniProt,ASPGD");
        assertThat(annotationFilter.getAssignedby(), hasSize(2));
        assertThat(annotationFilter.getAssignedby(), hasItems("UniProt","ASPGD"));
    }

    /**
     *  Validate shouldn't do anything.
     */
    @Test
    public void testSuccessfullyAddMultipleAssignedByAndValidate(){

        AnnotationFilter annotationFilter = new AnnotationFilter();
        annotationFilter.validation();

        annotationFilter.setAssignedby("UniProt,ASPGD");
        annotationFilter.validation();

        assertThat(annotationFilter.getAssignedby(), hasSize(2));
        assertThat(annotationFilter.getAssignedby(), hasItems("UniProt","ASPGD"));
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
