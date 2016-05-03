package uk.ac.ebi.quickgo.rest.search;

import uk.ac.ebi.quickgo.rest.search.query.FilterProvider;
import uk.ac.ebi.quickgo.rest.search.query.PrototypeFilter;
import uk.ac.ebi.quickgo.rest.search.query.QueryRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;

/**
 * @author Tony Wardell
 * Date: 29/04/2016
 * Time: 15:57
 * Created with IntelliJ IDEA.
 */
@RunWith(MockitoJUnitRunner.class)
public class FilterOnlySearchQueryTemplateTest {

    public static final String ASSIGNED_BY = "AssignedBy";
    private List<String> returnedFields;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    FilterProvider filterProvider;

    @Mock
    PrototypeFilter prototypeFilter;

    private List<String> assignedBy;

    @Before
    public void setup(){
        returnedFields = new ArrayList<>();
        returnedFields.add("taxonid");
        returnedFields.add("aspect");
        returnedFields.add("qualifier");

        assignedBy =  Collections.singletonList("UniProt");

        List<PrototypeFilter> filterList =  Collections.singletonList(prototypeFilter);

        when(filterProvider.getPage()).thenReturn(1);
        when(filterProvider.getLimit()).thenReturn(25);
        when(filterProvider.stream()).thenReturn(filterList.stream());

        when(prototypeFilter.getArgs()).thenReturn(assignedBy);
        when(prototypeFilter.getSolrName()).thenReturn(ASSIGNED_BY);


    }

    @Test
    public void successfullyCreateFilterFromOneArgument(){
        FilterOnlySearchQueryTemplate aTemplate = new FilterOnlySearchQueryTemplate(returnedFields);
        FilterOnlySearchQueryTemplate.Builder builder = aTemplate.newBuilder();
        builder.setFilterProvider(filterProvider);
        QueryRequest queryRequest = builder.build();
        assertThat(queryRequest.getFilters(), hasSize(1));
    }


    @Test
    public void exceptionThrownIfFiltersNotAdded(){
        FilterOnlySearchQueryTemplate aTemplate = new FilterOnlySearchQueryTemplate(returnedFields);
        FilterOnlySearchQueryTemplate.Builder builder = aTemplate.newBuilder();
        thrown.expect(NullPointerException.class);
        builder.build();
    }
}
