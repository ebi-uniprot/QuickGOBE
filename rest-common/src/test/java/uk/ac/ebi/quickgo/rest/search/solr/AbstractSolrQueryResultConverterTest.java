package uk.ac.ebi.quickgo.rest.search.solr;

import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.ac.ebi.quickgo.rest.search.query.Page;
import uk.ac.ebi.quickgo.rest.search.query.QueryRequest;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.query.RegularPage;
import uk.ac.ebi.quickgo.rest.search.results.*;

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;
import static uk.ac.ebi.quickgo.rest.search.query.CursorPage.createCursorPage;
import static uk.ac.ebi.quickgo.rest.search.query.CursorPage.createFirstCursorPage;

/**
 * Tests the {@link AbstractSolrQueryResultConverter} implementation
 * <p>
 * Created 08/02/16
 *
 * @author Edd
 */
@RunWith(MockitoJUnitRunner.class)
public class AbstractSolrQueryResultConverterTest {
    private static final QuickGOQuery DEFAULT_QUERY = QuickGOQuery.createQuery("field1", "value1");
    private static final String COLLECTION = "collection";

    private uk.ac.ebi.quickgo.rest.search.solr.AbstractSolrQueryResultConverter<String> converter;

    @Mock
    private QueryResponse responseMock;

    @Mock
    private Map<String, String> highlightedFieldNameMap;

    @Before
    public void setUp() throws Exception {
        converter = new AbstractSolrQueryResultConverter<String>() {

            @Override
            protected List<String> convertResults(SolrDocumentList results) {
                return results.stream()
                        .map(SolrDocument::toString)
                        .collect(Collectors.toList());
            }
        };

        converter.setQueryResultHighlightingConverter(new SolrQueryResultHighlightingConverter(
                highlightedFieldNameMap));
    }

    @Test
    public void nullQueryResponseThrowsException() throws Exception {
        responseMock = null;
        QueryRequest request = createDefaultRequest(DEFAULT_QUERY);

        try {
            converter.convert(responseMock, request);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Query response cannot be null"));
        }
    }

    @Test
    public void nullQueryRequestThrowsException() throws Exception {
        QueryRequest request = null;

        try {
            converter.convert(responseMock, request);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Query request cannot be null"));
        }
    }

    @Test
    public void responseWithNoResults() throws Exception {
        QueryRequest request = createDefaultRequest(DEFAULT_QUERY);

        QueryResult result = converter.convert(responseMock, request);

        assertThat(result.getNumberOfHits(), is(0L));
        assertThat(result.getResults().isEmpty(), is(true));
        assertThat(result.getPageInfo(), is(nullValue()));
        assertThat(result.getFacet(), is(nullValue()));
    }

    @Test
    public void requestWithRegularPagingRendersResponseWithNoResults() throws Exception {
        QueryRequest request = createRequestWithPaging(DEFAULT_QUERY, new RegularPage(1, 1));

        QueryResult result = converter.convert(responseMock, request);

        assertThat(result.getNumberOfHits(), is(0L));
        assertThat(result.getResults().isEmpty(), is(true));

        int expectedTotalPages = 0;
        int expectedCurrentPage = 0;
        int expectedResultsPerPage = 1;

        checkPageInfo(result.getPageInfo(), expectedTotalPages, expectedCurrentPage, expectedResultsPerPage);

        assertThat(result.getFacet(), is(nullValue()));
    }

    @Test
    public void requestWithFirstCursorPageRendersResponseWithNoResults() throws Exception {
        QueryRequest request = createRequestWithPaging(DEFAULT_QUERY, createFirstCursorPage(1));
        String expectedNextCursor = "fakeCursor";

        when(responseMock.getNextCursorMark()).thenReturn(expectedNextCursor);
        QueryResult result = converter.convert(responseMock, request);

        assertThat(result.getNumberOfHits(), is(0L));
        assertThat(result.getResults().isEmpty(), is(true));

        int expectedTotalPages = 0;
        int expectedResultsPerPage = 1;

        checkPageInfo(result.getPageInfo(), expectedTotalPages, PageInfo.CURSOR_PAGE_NUMBER, expectedNextCursor, expectedResultsPerPage);
        assertThat(result.getFacet(), is(nullValue()));
    }

    @Test
    public void requestWithNextCursorPageRendersResponseWithNoResults() throws Exception {
        QueryRequest request = createRequestWithPaging(DEFAULT_QUERY, createCursorPage("cursor", 1));
        String expectedNextCursor = "fakeCursor";

        when(responseMock.getNextCursorMark()).thenReturn(expectedNextCursor);
        QueryResult result = converter.convert(responseMock, request);

        assertThat(result.getNumberOfHits(), is(0L));
        assertThat(result.getResults().isEmpty(), is(true));

        int expectedTotalPages = 0;
        int expectedResultsPerPage = 1;

        checkPageInfo(result.getPageInfo(), expectedTotalPages, PageInfo.CURSOR_PAGE_NUMBER, expectedNextCursor, expectedResultsPerPage);
        assertThat(result.getFacet(), is(nullValue()));
    }

    @Test
    public void requestWithFacetingRendersResponseWithNoResults() throws Exception {
        String facet1 = "facet1";
        String facet2 = "facet2";

        QueryRequest request = createRequestWithFaceting(DEFAULT_QUERY, Arrays.asList(facet1, facet2));

        QueryResponse response = new QueryResponse();

        QueryResult result = converter.convert(response, request);

        assertThat(result.getNumberOfHits(), is(0L));
        assertThat(result.getResults().isEmpty(), is(true));
        assertThat(result.getPageInfo(), is(nullValue()));
        assertThat(result.getFacet(), is(nullValue()));
    }

    @Test
    public void responseWith2Results() {
        QueryRequest request = createDefaultRequest(DEFAULT_QUERY);

        SolrDocumentList docList = new SolrDocumentList();
        docList.add(new SolrDocument());
        docList.add(new SolrDocument());
        docList.setNumFound(2);

        when(responseMock.getResults()).thenReturn(docList);

        QueryResult result = converter.convert(responseMock, request);

        assertThat(result.getNumberOfHits(), is(2L));
        assertThat(result.getResults().size(), is(2));
    }

    @Test
    public void responseWith2ResultsAndRegularPaging() {
        QueryRequest request = createRequestWithPaging(DEFAULT_QUERY, new RegularPage(1, 1));

        SolrDocumentList docList = new SolrDocumentList();
        docList.add(new SolrDocument());
        docList.add(new SolrDocument());
        docList.setNumFound(2);

        when(responseMock.getResults()).thenReturn(docList);

        QueryResult result = converter.convert(responseMock, request);

        checkPageInfo(result.getPageInfo(), 2, 1, 1);
    }

    @Test
    public void responseWith2ResultsAndCursorPaging() {
        QueryRequest request = createRequestWithPaging(DEFAULT_QUERY, createCursorPage("anyCursor", 1));
        String expectedNextCursor = "fakeCursor";

        SolrDocumentList docList = new SolrDocumentList();
        docList.add(new SolrDocument());
        docList.add(new SolrDocument());
        docList.setNumFound(2);

        when(responseMock.getResults()).thenReturn(docList);
        when(responseMock.getNextCursorMark()).thenReturn(expectedNextCursor);

        QueryResult result = converter.convert(responseMock, request);

        checkPageInfo(result.getPageInfo(), 2, PageInfo.CURSOR_PAGE_NUMBER, expectedNextCursor, 1);
    }

    @Test
    public void responseWith2ResultsAndSingleFacet() {
        String facetFieldField = "field1";
        QueryRequest request = createRequestWithFaceting(DEFAULT_QUERY, Collections.singletonList(facetFieldField));

        List<FacetField> solrFacetResponse = Collections.singletonList(setupSolrFacetResponse(facetFieldField));
        when(responseMock.getFacetFields()).thenReturn(solrFacetResponse);

        QueryResult result = converter.convert(responseMock, request);

        Facet facet = result.getFacet();
        assertThat(facet, is(not(nullValue())));

        Set<FieldFacet> facetFields = facet.getFacetFields();
        assertThat(facetFields, hasSize(1));

        FieldFacet fieldFacet = facetFields.iterator().next();
        assertThat(fieldFacet.getField(), is(facetFieldField));
        assertThat(fieldFacet.getCategories().size(), is(2));

        Category category1 = new Category("value1", 0L);
        Category category2 = new Category("value2", 2L);
        assertThat(fieldFacet.getCategories(), containsInAnyOrder(category1, category2));
    }

    @Test
    public void responseWithOneResultAndZeroHitsInHighlighting() {
        QueryRequest request = createDefaultRequest(DEFAULT_QUERY);

        SolrDocumentList docList = new SolrDocumentList();
        docList.add(new SolrDocument());
        docList.setNumFound(1);

        when(responseMock.getResults()).thenReturn(docList);

        Map<String, Map<String, List<String>>> solrHighlightingResponse = new HashMap<>();

        when(responseMock.getHighlighting()).thenReturn(solrHighlightingResponse);

        QueryResult result = converter.convert(responseMock, request);

        assertThat(result.getHighlighting(), is(Collections.emptyList()));
    }

    @Test
    public void responseWithOneResultAnd1HitInHighlighting() {
        QueryRequest request = createDefaultRequest(DEFAULT_QUERY);

        SolrDocumentList docList = new SolrDocumentList();
        SolrDocument solrDocument = new SolrDocument();
        solrDocument.setField("id", "doc1");
        docList.add(solrDocument);
        docList.setNumFound(1);

        when(responseMock.getResults()).thenReturn(docList);

        Map<String, Map<String, List<String>>> solrHighlightingResponse = new HashMap<>();
        Map<String, List<String>> doc1FieldHighlights = new HashMap<>();
        doc1FieldHighlights.put("field1", Arrays.asList("hit1", "hit2"));
        solrHighlightingResponse.put("doc1", doc1FieldHighlights);

        when(responseMock.getHighlighting()).thenReturn(solrHighlightingResponse);

        QueryResult result = converter.convert(responseMock, request);

        assertThat(result.getHighlighting().size(), is(1));
    }

    /**
     * Setup a default facet response so that its conversion can be tested
     *
     * @param facetFieldField the name of the fact field
     * @return a populated Solr FacetField object
     */
    private FacetField setupSolrFacetResponse(String facetFieldField) {
        //setup Solr facet response
        FacetField facetField = new FacetField(facetFieldField);

        facetField.add("value1", 0);
        facetField.add("value2", 2);

        return facetField;
    }

    private void checkPageInfo(PageInfo pageInfo, int expectedTotalPages, int expectedCurrentPage, int
            expectedResultsPerPage) {
        assertThat(pageInfo.getResultsPerPage(), is(expectedResultsPerPage));
        assertThat(pageInfo.getCurrent(), is(expectedCurrentPage));
        assertThat(pageInfo.getTotal(), is(expectedTotalPages));
    }

    private void checkPageInfo(PageInfo pageInfo, int expectedTotalPages, int expectedCurrentPage, String nextCursor,
                               int expectedResultsPerPage) {
        assertThat(pageInfo.getResultsPerPage(), is(expectedResultsPerPage));
        assertThat(pageInfo.getCurrent(), is(expectedCurrentPage));
        assertThat(pageInfo.getNextCursor(), is(nextCursor));
        assertThat(pageInfo.getTotal(), is(expectedTotalPages));
    }

    private QueryRequest createDefaultRequest(QuickGOQuery query) {
        return new QueryRequest.Builder(query, COLLECTION).build();
    }

    private QueryRequest createRequestWithPaging(QuickGOQuery query, Page page) {
        return new QueryRequest.Builder(query, COLLECTION).setPage(page).build();
    }

    private QueryRequest createRequestWithFaceting(QuickGOQuery query, List<String> facets) {
        QueryRequest.Builder builder = new QueryRequest.Builder(query, COLLECTION);

        for (String facet : facets) {
            builder = builder.addFacetField(facet);
        }

        return builder.build();
    }
}