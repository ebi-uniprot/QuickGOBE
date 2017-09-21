package uk.ac.ebi.quickgo.rest.search.solr;

import uk.ac.ebi.quickgo.rest.search.query.*;

import java.util.HashSet;
import java.util.Set;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.params.CursorMarkParams;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static uk.ac.ebi.quickgo.rest.search.query.CursorPage.createCursorPage;
import static uk.ac.ebi.quickgo.rest.search.query.CursorPage.createFirstCursorPage;

/**
 * Tests the implementations of the {@link SolrQueryConverter} implementation.
 */
public class SolrQueryConverterTest {
    private static final String REQUEST_HANDLER_NAME = "/select";
    private static final Set<String> WILDCARD_COMPATIBLE_FIELDS = new HashSet<>();

    private SolrQueryConverter converter;

    @Before
    public void setUp() throws Exception {
        converter = SolrQueryConverter.create(REQUEST_HANDLER_NAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullRequestHandlerArgumentInConstructorThrowsException() throws Exception {
        converter = SolrQueryConverter.createWithWildCardSupport(null, WILDCARD_COMPATIBLE_FIELDS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyRequestHandlerArgumentInConstructorThrowsException() throws Exception {
        converter = SolrQueryConverter.createWithWildCardSupport("", WILDCARD_COMPATIBLE_FIELDS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullWildCardListPassedToInstantiatingMethodThrowsException() throws Exception {
        converter = SolrQueryConverter.createWithWildCardSupport("validValue", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullSerializerInConstructorThrowsException() throws Exception {
        converter = new SolrQueryConverter("validValue", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullQueryRequestThrowsException() throws Exception {
        converter.convert(null);
    }

    @Test
    public void solrQueryReferencesCorrectRequestHandlerName() throws Exception {
        QuickGOQuery fieldQuery = createBasicQuery();

        QueryRequest request = new QueryRequest.Builder(fieldQuery).build();

        SolrQuery query = converter.convert(request);

        assertThat(query.getRequestHandler(), is(REQUEST_HANDLER_NAME));
    }

    @Test
    public void convertQueryRequestWithQueryAndPageParameters() throws Exception {
        QuickGOQuery fieldQuery = createBasicQuery();

        int currentPage = 2;
        int pageSize = 25;

        QueryRequest request = new QueryRequest.Builder(fieldQuery)
                .setPage(new RegularPage(currentPage, pageSize))
                .build();

        SolrQuery query = converter.convert(request);

        assertThat(query.getStart(), is(equalTo(25)));
        assertThat(query.getRows(), is(equalTo(pageSize)));
    }

    @Test
    public void convertQueryRequestWithQueryAndFacets() throws Exception {
        QuickGOQuery fieldQuery = createBasicQuery();

        String facetField1 = "facet1";
        String facetField2 = "facet2";

        QueryRequest request = new QueryRequest.Builder(fieldQuery)
                .addFacetField(facetField1)
                .addFacetField(facetField2)
                .build();

        SolrQuery query = converter.convert(request);

        assertThat(query.getFacetFields(), arrayContainingInAnyOrder(facetField1, facetField2));
    }

    @Test
    public void convertQueryRequestWithQueryAndFilterQuery() throws Exception {
        QuickGOQuery fieldQuery = createBasicQuery();

        String filterField = "filterField1";
        String filterValue = "filterValue1";
        QuickGOQuery filterFieldQuery = QuickGOQuery.createQuery(filterField, filterValue);

        QueryRequest request = new QueryRequest.Builder(fieldQuery)
                .addQueryFilter(filterFieldQuery)
                .build();

        SolrQuery query = converter.convert(request);

        assertThat(query.getFilterQueries(), arrayContaining(buildFieldQuery(filterField, filterValue)));
    }

    @Test
    public void defaultConvertQueryRequestDoesNotUseHighlighting() {
        QuickGOQuery fieldQuery = createBasicQuery();

        QueryRequest request = new QueryRequest.Builder(fieldQuery).build();

        SolrQuery query = converter.convert(request);

        assertThat(query.getHighlight(), is(false));
    }

    @Test
    public void convertQueryRequestWithHighlightingOffWillNotUseHighlighting() {
        QuickGOQuery fieldQuery = createBasicQuery();

        QueryRequest request = new QueryRequest
                .Builder(fieldQuery)
                .build();

        SolrQuery query = converter.convert(request);

        assertThat(query.getHighlight(), is(false));
    }

    @Test
    public void convertQueryRequestWithHighlightingWillUseHighlighting() {
        QuickGOQuery fieldQuery = createBasicQuery();

        String highlightedField = "highlightedField";

        QueryRequest request = new QueryRequest
                .Builder(fieldQuery)
                .addHighlightedField(highlightedField)
                .build();

        SolrQuery query = converter.convert(request);

        assertThat(query.getHighlight(), is(true));
        assertThat(query.getHighlightFields(), arrayContainingInAnyOrder(highlightedField));
    }

    @Test
    public void convertQueryRequestWithProjectedFieldWillProjectThatField() {
        QuickGOQuery fieldQuery = createBasicQuery();

        String projectedField = "projectedField";

        QueryRequest request = new QueryRequest
                .Builder(fieldQuery)
                .addProjectedField(projectedField)
                .build();

        SolrQuery query = converter.convert(request);

        assertThat(query.getFields(), is(projectedField));
    }

    @Test
    public void convertQueryRequestWithTwoProjectedFieldsWillProjectTwoFields() {
        QuickGOQuery fieldQuery = createBasicQuery();

        String projectedField1 = "projectedField1";
        String projectedField2 = "projectedField2";

        QueryRequest request = new QueryRequest
                .Builder(fieldQuery)
                .addProjectedField(projectedField1)
                .addProjectedField(projectedField2)
                .build();

        SolrQuery query = converter.convert(request);

        assertThat(query.getFields(), is(projectedField1 + "," + projectedField2));
    }

    @Test
    public void convertQueryRequestWithNoProjectedFieldWillProjectNoFields() {
        QuickGOQuery fieldQuery = createBasicQuery();

        QueryRequest request = new QueryRequest
                .Builder(fieldQuery)
                .build();

        SolrQuery query = converter.convert(request);

        assertThat(query.getFields(), is(nullValue()));
    }

    @Test
    public void convertFirstQueryRequestWithCursorUsage() {
        QuickGOQuery fieldQuery = createBasicQuery();

        int pageSize = 10;
        QueryRequest request = new QueryRequest
                .Builder(fieldQuery)
                .setPage(createFirstCursorPage(pageSize))
                .build();

        SolrQuery query = converter.convert(request);

        assertThat(query.get(CursorMarkParams.CURSOR_MARK_PARAM), is(CursorPage.FIRST_CURSOR));
        assertThat(query.getRows(), is(pageSize));
        assertThat(query.getStart(), is(nullValue()));
    }

    @Test
    public void convertQueryRequestWithCursorPosition() {
        QuickGOQuery fieldQuery = createBasicQuery();

        int pageSize = 10;
        String cursor = "fakeCursor";
        QueryRequest request = new QueryRequest
                .Builder(fieldQuery)
                .setPage(createCursorPage(cursor, pageSize))
                .build();

        SolrQuery query = converter.convert(request);

        assertThat(query.get(CursorMarkParams.CURSOR_MARK_PARAM), is(cursor));
        assertThat(query.getRows(), is(pageSize));
        assertThat(query.getStart(), is(nullValue()));
    }

    @Test
    public void convertQueryRequestWithZeroSortCriteria() {
        QuickGOQuery fieldQuery = createBasicQuery();

        QueryRequest request = new QueryRequest
                .Builder(fieldQuery)
                .build();

        SolrQuery query = converter.convert(request);

        assertThat(query.getSorts(), hasSize(0));
    }

    @Test
    public void convertQueryRequestWithSortCriterion() {
        QuickGOQuery fieldQuery = createBasicQuery();

        String sortField = "field";
        SortCriterion.SortOrder sortOrder = SortCriterion.SortOrder.ASC;
        QueryRequest request = new QueryRequest
                .Builder(fieldQuery)
                .addSortCriterion(sortField, sortOrder)
                .build();

        SolrQuery query = converter.convert(request);

        assertThat(query.getSorts(), hasSize(1));
        checkSortCriterion(query.getSorts().get(0), sortField, sortOrder);
    }

    @Test
    public void convertQueryRequestWithSortCriteria() {
        QuickGOQuery fieldQuery = createBasicQuery();

        String sortField0 = "field0";
        SortCriterion.SortOrder sortOrder0 = SortCriterion.SortOrder.ASC;
        String sortField1 = "fiel1";
        SortCriterion.SortOrder sortOrder1 = SortCriterion.SortOrder.DESC;
        String sortField2 = "field2";
        SortCriterion.SortOrder sortOrder2 = SortCriterion.SortOrder.ASC;

        QueryRequest request = new QueryRequest
                .Builder(fieldQuery)
                .addSortCriterion(sortField0, sortOrder0)
                .addSortCriterion(sortField1, sortOrder1)
                .addSortCriterion(sortField2, sortOrder2)
                .build();

        SolrQuery query = converter.convert(request);

        assertThat(query.getSorts(), hasSize(3));
        checkSortCriterion(query.getSorts().get(0), sortField0, sortOrder0);
        checkSortCriterion(query.getSorts().get(1), sortField1, sortOrder1);
        checkSortCriterion(query.getSorts().get(2), sortField2, sortOrder2);
    }

    private void checkSortCriterion(
            SolrQuery.SortClause sortClause,
            String sortField,
            SortCriterion.SortOrder sortOrder) {
        assertThat(sortClause.getItem(), is(sortField));
        switch (sortClause.getOrder()) {
            case desc:
                assertThat(sortOrder, is(SortCriterion.SortOrder.DESC));
                break;
            case asc:
                assertThat(sortOrder, is(SortCriterion.SortOrder.ASC));
                break;
            default:
                throw new IllegalStateException("Could not verify sort criterion");
        }
    }

    private QuickGOQuery createBasicQuery() {
        String field = "field1";
        String value = "value1";
        return QuickGOQuery.createQuery(field, value);
    }

    private String buildFieldQuery(String field, String value) {
        return "(" + field + SolrQueryConverter.SOLR_FIELD_SEPARATOR + value + ")";
    }
}
