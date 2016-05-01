package uk.ac.ebi.quickgo.annotation.model;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationFields;

import java.util.function.Consumer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
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

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @Test
    public void successfullyAddOnlyOneSingleFilter(){

        AnnotationFilter annotationFilter = new AnnotationFilter();
        annotationFilter.setAssignedby("UniProt");
        final MyNumeric counter = new MyNumeric();
        Consumer<AnnotationFilter.PrototypeFilter> consumer = (e) -> counter.increment();
        annotationFilter.requestConsumptionOfPrototypeFilters(consumer);
        assertThat(counter.count,is(1));
    }

    @Test
    public void theFilterAddedIsForAssignedBy(){

        AnnotationFilter annotationFilter = new AnnotationFilter();
        annotationFilter.setAssignedby("UniProt");
        final MyNumeric counter = new MyNumeric();
        Consumer<AnnotationFilter.PrototypeFilter> consumer = (e) -> {
            if (e.getSolrName().equals(AnnotationFields.ASSIGNED_BY) && e.getArgs().contains("UniProt") && e.getArgs().size()
                    ==1){
                counter.increment();
            }
        };
        annotationFilter.requestConsumptionOfPrototypeFilters(consumer);
        assertThat(counter.count,is(1));
    }


    @Test
    public void successfullyAddMultipleAssignedBy(){

        AnnotationFilter annotationFilter = new AnnotationFilter();
        annotationFilter.setAssignedby("UniProt,ASPGD");
//        assertThat(annotationFilter.getAssignedby(), hasSize(2));
//        assertThat(annotationFilter.getAssignedby(), hasItems("UniProt","ASPGD"));
        final MyNumeric counter = new MyNumeric();
        Consumer<AnnotationFilter.PrototypeFilter> consumer = (e) -> {
            if (e.getSolrName().equals(AnnotationFields.ASSIGNED_BY)
                    && e.getArgs().contains("UniProt")
                    && e.getArgs().contains("ASPGD")
                    && e.getArgs().size()==2){
                counter.increment();
            }
        };
        annotationFilter.requestConsumptionOfPrototypeFilters(consumer);
        assertThat(counter.count,is(1));
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

    public static class MyNumeric{
        public int count;

        public MyNumeric() {
            count=0;
        }

        public void increment(){
            count++;
        }
    }
}
