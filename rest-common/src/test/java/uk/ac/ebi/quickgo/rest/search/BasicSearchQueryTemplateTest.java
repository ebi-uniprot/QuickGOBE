package uk.ac.ebi.quickgo.rest.search;

import uk.ac.ebi.quickgo.rest.search.filter.FilterConverter;
import uk.ac.ebi.quickgo.rest.search.filter.FilterConverterFactory;
import uk.ac.ebi.quickgo.rest.search.filter.RequestFilterOld;
import uk.ac.ebi.quickgo.rest.search.query.FieldProjection;
import uk.ac.ebi.quickgo.rest.search.query.Page;
import uk.ac.ebi.quickgo.rest.search.query.QueryRequest;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;

import java.util.Arrays;
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
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.mockito.Mockito.when;
import static uk.ac.ebi.quickgo.rest.TestUtil.asSet;

/**
 * Tests the behvaiour of the {@link BasicSearchQueryTemplate} class.
 */
@RunWith(MockitoJUnitRunner.class)
public class BasicSearchQueryTemplateTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private FilterConverterFactory filterConverterFactory;

    private BasicSearchQueryTemplate.Builder builder;

    @Before
    public void setUp() throws Exception {
        List<String> returnedFields = Collections.emptyList();

        BasicSearchQueryTemplate queryTemplate = new BasicSearchQueryTemplate(returnedFields, filterConverterFactory);
        builder = queryTemplate.newBuilder();
        builder.setQuery(QuickGOQuery.createAllQuery());
    }

    @Test
    public void nullReturnedFieldsListInConstructorThrowsException() {
        List<String> returnedFields = null;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Returned fields list cannot be null.");

        new BasicSearchQueryTemplate(returnedFields, filterConverterFactory);
    }

    @Test
    public void nullFilterConverterFactoryInConstructorThrowsException() {
        List<String> returnedFields = Collections.emptyList();
        filterConverterFactory = null;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("FilterConverterFactory can not be null.");

        new BasicSearchQueryTemplate(returnedFields, filterConverterFactory);
    }

    @Test
    public void builderSetWithNullQueryThrowsException() {
        builder.setQuery(null);

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Query cannot be null");

        builder.build();
    }

    @Test
    public void builderSetWithEmptyReturnedValuesCreatesQueryRequestWithEmptyProjectionFields() {
        builder.setReturnedFields(Collections.emptyList());

        QueryRequest queryRequest = builder.build();

        List<FieldProjection> fields = queryRequest.getProjectedFields();

        assertThat(fields, hasSize(0));
    }

    @Test
    public void builderSetWithReturnedValuesToXAndYCreatesQueryRequestWithProjectionFieldsXAndY() {
        String fieldX = "X";
        String fieldY = "Y";

        builder.setReturnedFields(Arrays.asList(fieldX, fieldY));

        QueryRequest queryRequest = builder.build();

        List<FieldProjection> fields = queryRequest.getProjectedFields();

        assertThat(fields, hasSize(2));
        assertThat(fields, containsInAnyOrder(new FieldProjection(fieldX), new FieldProjection(fieldY)));
    }

    @Test
    public void builderSetWithEmptyFiltersCreatesQueryRequestWithEmptyFilterQueries() {
        QueryRequest queryRequest = builder.build();

        List<QuickGOQuery> filters = queryRequest.getFilters();

        assertThat(filters, hasSize(0));
    }

    @Test
    public void builderSetWithFilterOnFieldXCreatesQueryRequestWithFilterQueryOnFieldX() {
        String fieldX = "fieldX";
        String valueX = "valueX";

        RequestFilterOld requestFilter = new RequestFilterOld(fieldX, valueX);

        builder.setFilters(asSet(requestFilter));

        FilterConverter converter = new FakeFilterConverter(requestFilter);
        when(filterConverterFactory.createConverter(requestFilter)).thenReturn(converter);

        QueryRequest queryRequest = builder.build();

        List<QuickGOQuery> filters = queryRequest.getFilters();

        assertThat(filters, hasSize(1));
        assertThat(filters, contains(converter.transform()));

    }

    @Test
    public void builderSetWithNoPageParametersCreatesQueryRequestWithDefaultPageNumberAndLimit() {
        QueryRequest queryRequest = builder.build();

        Page page = queryRequest.getPage();

        assertThat(page.getPageNumber(), is(BasicSearchQueryTemplate.DEFAULT_PAGE_NUMBER));
        assertThat(page.getPageSize(), is(BasicSearchQueryTemplate.DEFAULT_PAGE_SIZE));
    }

    @Test
    public void builderSetWithPageNumberTo23CreatesQueryRequestWithPageNumberOf23() {
        int number = 23;

        builder.setPage(number);
        QueryRequest queryRequest = builder.build();

        Page page = queryRequest.getPage();

        assertThat(page.getPageNumber(), is(number));
    }

    @Test
    public void builderSetWithPageSizeTo23CreatesQueryRequestWithPageSizeOf23() {
        int size = 23;

        builder.setPageSize(size);
        QueryRequest queryRequest = builder.build();

        Page page = queryRequest.getPage();

        assertThat(page.getPageSize(), is(size));
    }

    /**
     * Fake class that does simple conversion simple conversion between a {@link RequestFilterOld} and a
     * {@link QuickGOQuery}.
     *
     * This class assumes that the {@link RequestFilterOld} has a field and just a single value.
     */
    private class FakeFilterConverter implements FilterConverter {
        private final RequestFilterOld filter;

        FakeFilterConverter(RequestFilterOld filter) {
            this.filter = filter;
        }

        @Override public QuickGOQuery transform() {
            return QuickGOQuery.createQuery(filter.getField(), filter.getValues().findFirst().get());
        }
    }
}