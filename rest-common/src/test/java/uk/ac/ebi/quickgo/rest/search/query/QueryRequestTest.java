package uk.ac.ebi.quickgo.rest.search.query;

import org.junit.Test;

import java.util.Collection;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static uk.ac.ebi.quickgo.rest.search.query.CursorPage.*;

/**
 * Tests the {@link QueryRequest} implementation
 */
public class QueryRequestTest {
    @Test(expected = IllegalArgumentException.class)
    public void nullQueryThrowsException() throws Exception {
        new QueryRequest.Builder(null);
    }

    @Test
    public void buildsQueryRequestOnlyWithQuery() throws Exception {
        QuickGOQuery query = QuickGOQuery.createQuery("field1", "value1");

        QueryRequest request = new QueryRequest.Builder(query).build();

        assertThat(request.getQuery(), is(equalTo(query)));
        assertThat(request.getFacets(), hasSize(0));
        assertThat(request.getPage(), is(nullValue()));
        assertThat(request.getFilters(), hasSize(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidPageParametersThrowsException() throws Exception {
        QuickGOQuery query = QuickGOQuery.createQuery("field1", "value1");

        new QueryRequest.Builder(query).setPage(new RegularPage(-1, 2)).build();
    }

    @Test
    public void buildsQueryRequestWithQueryAndRegularPageParameterComponents() throws Exception {
        QuickGOQuery query = QuickGOQuery.createQuery("field1", "value1");

        int pageNumber = 1;
        int pageSize = 2;
        QueryRequest request = new QueryRequest.Builder(query)
                .setPage(new RegularPage(pageNumber, pageSize))
                .build();

        assertThat(request.getQuery(), is(equalTo(query)));

        Page requestPage = request.getPage();
        assertThat(requestPage, is(instanceOf(RegularPage.class)));
        RegularPage expectedPage = (RegularPage) requestPage;
        assertThat(expectedPage.getPageNumber(), is(pageNumber));
        assertThat(expectedPage.getPageSize(), is(pageSize));

        assertThat(request.getFacets(), hasSize(0));
        assertThat(request.getFilters(), hasSize(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidFacetFieldThrowsException() throws Exception {
        QuickGOQuery query = QuickGOQuery.createQuery("field1", "value1");

        new QueryRequest.Builder(query)
                .addFacetField(null)
                .build();
    }

    @Test
    public void buildsQueryRequestWithQueryAndTwoFacetFields() throws Exception {
        QuickGOQuery query = QuickGOQuery.createQuery("field1", "value1");

        String facetField1 = "facet1";
        String facetField2 = "facet2";

        QueryRequest request = new QueryRequest.Builder(query)
                .addFacetField(facetField1)
                .addFacetField(facetField2)
                .build();

        assertThat(request.getQuery(), is(equalTo(query)));
        assertThat(request.getPage(), is(nullValue()));

        Collection<Facet> expectedFacets = request.getFacets();

        assertThat(expectedFacets, hasSize(2));
        assertThat(extractFacetFields(expectedFacets), containsInAnyOrder(facetField1, facetField2));
        assertThat(request.getFilters(), hasSize(0));
    }

    @Test
    public void buildsQueryRequestWithQueryAndFilterQuery() throws Exception {
        QuickGOQuery query = QuickGOQuery.createQuery("field1", "value1");

        QuickGOQuery filterQuery1 = QuickGOQuery.createQuery("field2", "value2");
        QuickGOQuery filterQuery2 = QuickGOQuery.createQuery("field3", "value3");

        QueryRequest request = new QueryRequest.Builder(query)
                .addQueryFilter(filterQuery1)
                .addQueryFilter(filterQuery2)
                .build();

        assertThat(request.getQuery(), is(equalTo(query)));
        assertThat(request.getPage(), is(nullValue()));
        assertThat(request.getFacets(), hasSize(0));

        assertThat(request.getFilters(), containsInAnyOrder(filterQuery1, filterQuery2));
    }

    @Test
    public void addFilterQueryToQueryRequest() throws Exception {
        QuickGOQuery query = QuickGOQuery.createQuery("field1", "value1");

        QueryRequest request = new QueryRequest.Builder(query)
                .build();

        QuickGOQuery filterQuery = QuickGOQuery.createQuery("filter", "value");
        request.addFilter(filterQuery);

        assertThat(request.getFilters(), hasSize(1));
        assertThat(request.getFilters().get(0), is(filterQuery));
    }

    @Test
    public void buildsQueryWithHighlightingOn() {
        QuickGOQuery query = QuickGOQuery.createQuery("field1", "value1");
        String highlightField = "highlightField";
        QueryRequest request = new QueryRequest.Builder(query)
                .addHighlightedField(highlightField)
                .build();

        assertThat(request.getHighlightedFields(), contains(new FieldHighlight(highlightField)));
    }

    @Test
    public void buildsQueryWithHighlightingOff() {
        QuickGOQuery query = QuickGOQuery.createQuery("field1", "value1");
        QueryRequest request = new QueryRequest.Builder(query)
                .build();

        assertThat(request.getHighlightedFields(), is(empty()));
    }

    @Test
    public void buildsQueryWithProjectedField() {
        QuickGOQuery query = QuickGOQuery.createQuery("field1", "value1");
        String projectedField = "projectedField";
        QueryRequest request = new QueryRequest.Builder(query)
                .addProjectedField(projectedField)
                .build();

        assertThat(request.getProjectedFields(), contains(new FieldProjection(projectedField)));
    }

    @Test
    public void buildsQueryWithNoProjectedField() {
        QuickGOQuery query = QuickGOQuery.createQuery("field1", "value1");
        QueryRequest request = new QueryRequest.Builder(query)
                .build();

        assertThat(request.getHighlightedFields(), is(empty()));
    }

    @Test
    public void buildsQueryWithFirstCursor() {
        QuickGOQuery query = QuickGOQuery.createQuery("field1", "value1");
        int pageSize = 25;
        QueryRequest request = new QueryRequest.Builder(query)
                .setPage(createFirstCursorPage(pageSize))
                .build();

        Page requestPage = request.getPage();
        assertThat(requestPage, is(instanceOf(CursorPage.class)));
        assertThat(((CursorPage) requestPage).getCursor(), is(FIRST_CURSOR));
        assertThat(requestPage.getPageSize(), is(pageSize));
    }

    @Test
    public void buildsQueryWithNextCursor() {
        QuickGOQuery query = QuickGOQuery.createQuery("field1", "value1");
        int pageSize = 25;
        String cursor = "fakeCursor";
        QueryRequest request = new QueryRequest.Builder(query)
                .setPage(createCursorPage(cursor, pageSize))
                .build();

        Page requestPage = request.getPage();
        assertThat(requestPage, is(instanceOf(CursorPage.class)));
        assertThat(((CursorPage) requestPage).getCursor(), is(cursor));
        assertThat(requestPage.getPageSize(), is(pageSize));
    }

    private Collection<String> extractFacetFields(Collection<Facet> facets) {
        return facets.stream()
                .map(Facet::getField)
                .collect(Collectors.toList());
    }
}