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
    public void nullSerializerInConstructorThrowsException() throws Exception {
        converter = new SolrQueryConverter("validValue", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullQueryRequestThrowsException() throws Exception {
        converter.convert(null);
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
}