package uk.ac.ebi.quickgo.rest.search.query;

import org.apache.solr.client.solrj.SolrQuery;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static uk.ac.ebi.quickgo.rest.search.query.SolrQueryConverter.CROSS_CORE_JOIN_SYNTAX;
import static uk.ac.ebi.quickgo.rest.search.query.TestUtil.asSet;

/**
 * Tests the implementations of the {@link SolrQueryConverter} implementation.
 */
public class SolrQueryConverterTest {
    private static final String REQUEST_HANDLER_NAME = "/select";

    private SolrQueryConverter converter;

    @Before
    public void setUp() throws Exception {
        converter = new SolrQueryConverter(REQUEST_HANDLER_NAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullRequestHandlerArgumentInConstructorThrowsException() throws Exception {
        converter = new SolrQueryConverter(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyRequestHandlerArgumentInConstructorThrowsException() throws Exception {
        converter = new SolrQueryConverter("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullQueryRequestThrowsException() throws Exception {
        converter.convert(null);
    }

    @Test
    public void visitTransformsFieldQueryToString() throws Exception {
        String field = "field1";
        String value = "value1";
        FieldQuery fieldQuery = new FieldQuery(field, value);

        String queryString = converter.visit(fieldQuery);

        assertThat(queryString, is(buildFieldQuery(field, value)));
    }

    @Test
    public void visitTransformsNoFieldQueryToString() throws Exception {
        String value = "value1";
        NoFieldQuery noFieldQuery = new NoFieldQuery(value);

        String queryString = converter.visit(noFieldQuery);

        assertThat(queryString, is(buildValueOnlyQuery(value)));
    }

    @Test
    public void visitTransformsFieldQueryWithSolrReservedCharacterToString() throws Exception {
        String field = "field1";
        String value = "prefix:value1";
        String escapedValue = "prefix\\:value1";

        FieldQuery fieldQuery = new FieldQuery(field, value);

        String queryString = converter.visit(fieldQuery);

        assertThat(queryString, is(buildFieldQuery(field, escapedValue)));
    }

    @Test
    public void visitTransformsCompositeQueryToString() throws Exception {
        CompositeQuery complexQuery = createComplexQuery();

        String queryString = converter.visit(complexQuery);

        String expectedQuery = "(((field1:value1) AND (field2:value2)) OR (field3:value3))";
        assertThat(queryString, is(expectedQuery));
    }

    private CompositeQuery createComplexQuery() {
        FieldQuery query1 = new FieldQuery("field1", "value1");
        FieldQuery query2 = new FieldQuery("field2", "value2");

        CompositeQuery andQuery = new CompositeQuery(asSet(query1, query2), CompositeQuery.QueryOp.AND);

        FieldQuery query3 = new FieldQuery("field3", "value3");

        return new CompositeQuery(asSet(andQuery, query3), CompositeQuery.QueryOp.OR);
    }

    @Test
    public void visitTransformsAllQueryToString() {
        AllQuery allQuery = new AllQuery();

        String queryString = converter.visit(allQuery);

        String expectedQuery = "*:*";
        assertThat(queryString, is(expectedQuery));
    }

    @Test
    public void visitTransformsJoinQueryWithNoFromFilterToString() {
        String joinFromTable = "annotation";
        String joinFromAttribute = "id";
        String joinToTable = "ontology";
        String joinToAttribute = "id";

        String fromFilterString = "";

        JoinQuery query = new JoinQuery(joinFromTable, joinFromAttribute, joinToTable, joinToAttribute);

        String solrJoinString = converter.visit(query);

        assertThat(solrJoinString, is(String.format(CROSS_CORE_JOIN_SYNTAX, joinFromAttribute, joinToAttribute,
                joinFromTable, fromFilterString)));
    }

    @Test
    public void visitTransformsJoinQueryWithAFromFilterToString() {
        String joinFromTable = "annotation";
        String joinFromAttribute = "id";
        String joinToTable = "ontology";
        String joinToAttribute = "id";

        String fromFilterField = "aspect";
        String fromFilterValue = "molecular_function";
        QuickGOQuery fromFilter = QuickGOQuery.createQuery(fromFilterField, fromFilterValue);

        String fromFilterString = buildFieldQuery(fromFilterField, fromFilterValue);

        JoinQuery query = new JoinQuery(joinFromTable, joinFromAttribute, joinToTable, joinToAttribute,
                fromFilter);

        String solrJoinString = converter.visit(query);

        assertThat(solrJoinString, is(String.format(CROSS_CORE_JOIN_SYNTAX, joinFromAttribute, joinToAttribute,
                joinFromTable, fromFilterString)));
    }

    @Test
    public void solrQueryReferencesCorrectRequestHandlerName() throws Exception {
        String field = "field1";
        String value = "value1";
        QuickGOQuery fieldQuery = QuickGOQuery.createQuery(field, value);

        QueryRequest request = new QueryRequest.Builder(fieldQuery).build();

        SolrQuery query = converter.convert(request);

        assertThat(query.getRequestHandler(), is(REQUEST_HANDLER_NAME));
    }

    @Test
    public void convertQueryRequestWithQueryAndPageParameters() throws Exception {
        String field = "field1";
        String value = "value1";
        QuickGOQuery fieldQuery = QuickGOQuery.createQuery(field, value);

        int currentPage = 2;
        int pageSize = 25;

        QueryRequest request = new QueryRequest.Builder(fieldQuery)
                .setPageParameters(currentPage, pageSize)
                .build();

        SolrQuery query = converter.convert(request);

        assertThat(query.getStart(), is(equalTo(25)));
        assertThat(query.getRows(), is(equalTo(pageSize)));
    }

    @Test
    public void convertQueryRequestWithQueryAndFacets() throws Exception {
        String field = "field1";
        String value = "value1";
        QuickGOQuery fieldQuery = QuickGOQuery.createQuery(field, value);

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
        String field = "field1";
        String value = "value1";
        QuickGOQuery fieldQuery = QuickGOQuery.createQuery(field, value);

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
        String field = "field1";
        String value = "value1";
        QuickGOQuery fieldQuery = QuickGOQuery.createQuery(field, value);

        QueryRequest request = new QueryRequest.Builder(fieldQuery).build();

        SolrQuery query = converter.convert(request);

        assertThat(query.getHighlight(), is(false));
    }

    @Test
    public void convertQueryRequestWithHighlightingOffWillNotUseHighlighting() {
        String field = "field1";
        String value = "value1";
        QuickGOQuery fieldQuery = QuickGOQuery.createQuery(field, value);

        QueryRequest request = new QueryRequest
                .Builder(fieldQuery)
                .build();

        SolrQuery query = converter.convert(request);

        assertThat(query.getHighlight(), is(false));
    }

    @Test
    public void convertQueryRequestWithHighlightingWillUseHighlighting() {
        String field = "field1";
        String value = "value1";
        QuickGOQuery fieldQuery = QuickGOQuery.createQuery(field, value);

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
        String field = "field1";
        String value = "value1";
        QuickGOQuery fieldQuery = QuickGOQuery.createQuery(field, value);

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
        String field = "field1";
        String value = "value1";
        QuickGOQuery fieldQuery = QuickGOQuery.createQuery(field, value);

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
        String field = "field1";
        String value = "value1";
        QuickGOQuery fieldQuery = QuickGOQuery.createQuery(field, value);

        QueryRequest request = new QueryRequest
                .Builder(fieldQuery)
                .build();

        SolrQuery query = converter.convert(request);

        assertThat(query.getFields(), is(nullValue()));
    }

    private String buildFieldQuery(String field, String value) {
        return "(" + field + SolrQueryConverter.SOLR_FIELD_SEPARATOR + value + ")";
    }

    private String buildValueOnlyQuery(String value) {
        return "(" + value + ")";
    }
}