package uk.ac.ebi.quickgo.rest.search.query;

import java.util.Collection;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.ebi.quickgo.common.SolrCollectionName;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.ac.ebi.quickgo.rest.search.query.CursorPage.FIRST_CURSOR;
import static uk.ac.ebi.quickgo.rest.search.query.CursorPage.createCursorPage;
import static uk.ac.ebi.quickgo.rest.search.query.CursorPage.createFirstCursorPage;

/**
 * Tests the {@link QueryRequest} implementation
 */
class QueryRequestTest {
    private static final String COLLECTION = SolrCollectionName.COLLECTION;
    private QuickGOQuery query;

    @BeforeEach
    void setUp() {
        this.query = QuickGOQuery.createQuery("field1", "value1");
    }

    @Test
    void nullQueryThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new QueryRequest.Builder(null, COLLECTION));
    }

    @Test
    void nullCollectionThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new QueryRequest.Builder(query, null));
    }

    @Test
    void emptyCollectionThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new QueryRequest.Builder(query, ""));
    }

    @Test
    void buildsQueryRequestOnlyWithQuery() {
        QueryRequest request = new QueryRequest.Builder(query, COLLECTION).build();

        assertThat(request.getQuery(), is(equalTo(query)));
        assertThat(request.getCollection(), is(equalTo(COLLECTION)));
        assertThat(request.getFacets(), hasSize(0));
        assertThat(request.getPage(), is(nullValue()));
        assertThat(request.getFilters(), hasSize(0));
    }

    @Test
    void invalidPageParametersThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new QueryRequest.Builder(query, COLLECTION).setPage(new RegularPage(-1, 2)).build());
    }

    @Test
    void buildsQueryRequestWithQueryAndRegularPageParameterComponents() {
        int pageNumber = 1;
        int pageSize = 2;
        QueryRequest request = new QueryRequest.Builder(query, COLLECTION)
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

    @Test
    void invalidFacetFieldThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new QueryRequest.Builder(query, COLLECTION)
                .addFacetField(null)
                .build());
    }

    @Test
    void buildsQueryRequestWithQueryAndTwoFacetFields() {
        String facetField1 = "facet1";
        String facetField2 = "facet2";

        QueryRequest request = new QueryRequest.Builder(query, COLLECTION)
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
    void buildsQueryRequestWithQueryAndFilterQuery() {
        QuickGOQuery filterQuery1 = QuickGOQuery.createQuery("field2", "value2");
        QuickGOQuery filterQuery2 = QuickGOQuery.createQuery("field3", "value3");

        QueryRequest request = new QueryRequest.Builder(query, COLLECTION)
                .addQueryFilter(filterQuery1)
                .addQueryFilter(filterQuery2)
                .build();

        assertThat(request.getQuery(), is(equalTo(query)));
        assertThat(request.getPage(), is(nullValue()));
        assertThat(request.getFacets(), hasSize(0));

        assertThat(request.getFilters(), containsInAnyOrder(filterQuery1, filterQuery2));
    }

    @Test
    void addFilterQueryToQueryRequest() {
        QueryRequest request = new QueryRequest.Builder(query, COLLECTION)
                .build();

        QuickGOQuery filterQuery = QuickGOQuery.createQuery("filter", "value");
        request.addFilter(filterQuery);

        assertThat(request.getFilters(), hasSize(1));
        assertThat(request.getFilters().get(0), is(filterQuery));
    }

    @Test
    void buildsQueryWithHighlightingOn() {
        String highlightField = "highlightField";
        QueryRequest request = new QueryRequest.Builder(query, COLLECTION)
                .addHighlightedField(highlightField)
                .build();

        assertThat(request.getHighlightedFields(), contains(new FieldHighlight(highlightField)));
    }

    @Test
    void buildsQueryWithHighlightingOff() {
        QueryRequest request = new QueryRequest.Builder(query, COLLECTION)
                .build();

        assertThat(request.getHighlightedFields(), is(empty()));
    }

    @Test
    void buildsQueryWithProjectedField() {
        String projectedField = "projectedField";
        QueryRequest request = new QueryRequest.Builder(query, COLLECTION)
                .addProjectedField(projectedField)
                .build();

        assertThat(request.getProjectedFields(), contains(new FieldProjection(projectedField)));
    }

    @Test
    void buildsQueryWithNoProjectedField() {
        QueryRequest request = new QueryRequest.Builder(query, COLLECTION)
                .build();

        assertThat(request.getHighlightedFields(), is(empty()));
    }

    @Test
    void buildsQueryWithFirstCursor() {
        int pageSize = 25;
        QueryRequest request = new QueryRequest.Builder(query, COLLECTION)
                .setPage(createFirstCursorPage(pageSize))
                .build();

        Page requestPage = request.getPage();
        assertThat(requestPage, is(instanceOf(CursorPage.class)));
        assertThat(((CursorPage) requestPage).getCursor(), is(FIRST_CURSOR));
        assertThat(requestPage.getPageSize(), is(pageSize));
    }

    @Test
    void buildsQueryWithNextCursor() {
        int pageSize = 25;
        String cursor = "fakeCursor";
        QueryRequest request = new QueryRequest.Builder(query, COLLECTION)
                .setPage(createCursorPage(cursor, pageSize))
                .build();

        Page requestPage = request.getPage();
        assertThat(requestPage, is(instanceOf(CursorPage.class)));
        assertThat(((CursorPage) requestPage).getCursor(), is(cursor));
        assertThat(requestPage.getPageSize(), is(pageSize));
    }

    @Test
    void buildsQueryWithSortCriteria() {
        String sortField = "sortField";
        SortCriterion.SortOrder sortOrder = SortCriterion.SortOrder.ASC;
        QueryRequest request = new QueryRequest.Builder(query, COLLECTION)
                .addSortCriterion(sortField, sortOrder)
                .build();

        assertThat(request.getSortCriteria(), hasSize(1));
        assertThat(request.getSortCriteria().get(0).getSortField().getField(), is(sortField));
        assertThat(request.getSortCriteria().get(0).getSortOrder(), is(sortOrder));
    }

    @Test
    void buildsQueryWithSortCriteriaInInsertionOrder() {
        String sortField0 = "sortField0";
        SortCriterion.SortOrder sortOrder0 = SortCriterion.SortOrder.ASC;
        String sortField1 = "sortField1";
        SortCriterion.SortOrder sortOrder1 = SortCriterion.SortOrder.DESC;
        String sortField2 = "sortField2";
        SortCriterion.SortOrder sortOrder2 = SortCriterion.SortOrder.ASC;

        QueryRequest request = new QueryRequest.Builder(query, COLLECTION)
                .addSortCriterion(sortField0, sortOrder0)
                .addSortCriterion(sortField1, sortOrder1)
                .addSortCriterion(sortField2, sortOrder2)
                .build();

        assertThat(request.getSortCriteria(), hasSize(3));
        assertThat(request.getSortCriteria().get(0).getSortField().getField(), is(sortField0));
        assertThat(request.getSortCriteria().get(0).getSortOrder(), is(sortOrder0));
        assertThat(request.getSortCriteria().get(1).getSortField().getField(), is(sortField1));
        assertThat(request.getSortCriteria().get(1).getSortOrder(), is(sortOrder1));
        assertThat(request.getSortCriteria().get(2).getSortField().getField(), is(sortField2));
        assertThat(request.getSortCriteria().get(2).getSortOrder(), is(sortOrder2));
    }

    @Test
    void buildsQueryWithNoSortCriteria() {
        QueryRequest request = new QueryRequest.Builder(query, COLLECTION)
                .build();

        assertThat(request.getSortCriteria(), hasSize(0));
    }

    private Collection<String> extractFacetFields(Collection<Facet> facets) {
        return facets.stream()
                .map(Facet::getField)
                .collect(Collectors.toList());
    }
}