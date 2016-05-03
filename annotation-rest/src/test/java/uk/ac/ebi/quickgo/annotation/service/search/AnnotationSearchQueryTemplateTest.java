package uk.ac.ebi.quickgo.annotation.service.search;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationFields;
import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.model.AnnotationFilter;
import uk.ac.ebi.quickgo.rest.search.query.PrototypeFilter;
import uk.ac.ebi.quickgo.rest.search.query.QueryRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;

/**
 * @author Tony Wardell
 * Date: 29/04/2016
 * Time: 15:57
 * Created with IntelliJ IDEA.
 */
@RunWith(MockitoJUnitRunner.class)
public class AnnotationSearchQueryTemplateTest {

    private List<String> returnedFields;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    AnnotationFilter mockFilter;

    @Mock
    PrototypeFilter prototypeFilter;

    List<String> assignedBy;

    @Before
    public void setup(){
        returnedFields = new ArrayList<>();
        returnedFields.add("taxonid");
        returnedFields.add("aspect");
        returnedFields.add("qualifier");

        assignedBy =  Collections.singletonList("UniProt");

        List<PrototypeFilter> filterList =  Collections.singletonList(prototypeFilter);

        when(mockFilter.getPage()).thenReturn("1");
        when(mockFilter.getLimit()).thenReturn("25");
        when(mockFilter.stream()).thenReturn(filterList.stream());

        when(prototypeFilter.getArgs()).thenReturn(assignedBy);
        when(prototypeFilter.getSolrName()).thenReturn(AnnotationFields.ASSIGNED_BY);


    }

    @Test
    public void successfullyCreateFilterFromOneArgument(){
        AnnotationSearchQueryTemplate aTemplate = new AnnotationSearchQueryTemplate(returnedFields);
        AnnotationSearchQueryTemplate.Builder builder = aTemplate.newBuilder();
        builder.addAnnotationFilter(mockFilter);

        //don't need to mock the call to mockFilter.requestConsumptionOfPrototypeFilters(consumer)) as its void
        //just emulate the call back on to the builder
        QueryRequest queryRequest = builder.build();
        //builder.accept(prototypeFilter);
        assertThat(queryRequest.getFilters(), hasSize(1));
    }


    @Test
    public void exceptionThrownIfFiltersNotAdded(){
        AnnotationSearchQueryTemplate aTemplate = new AnnotationSearchQueryTemplate(returnedFields);
        AnnotationSearchQueryTemplate.Builder builder = aTemplate.newBuilder();
        thrown.expect(NullPointerException.class);
        QueryRequest queryRequest = builder.build();
    }
}
