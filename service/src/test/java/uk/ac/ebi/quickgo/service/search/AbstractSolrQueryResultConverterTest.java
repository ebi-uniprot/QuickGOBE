package uk.ac.ebi.quickgo.service.search;

import uk.ac.ebi.quickgo.repo.solr.query.model.QueryRequest;
import uk.ac.ebi.quickgo.repo.solr.query.model.QuickGOQuery;
import uk.ac.ebi.quickgo.repo.solr.query.results.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

/**
 * Tests the {@link AbstractSolrQueryResultConverter} implementation
 */
@RunWith(MockitoJUnitRunner.class)
public class AbstractSolrQueryResultConverterTest {
    private static final QuickGOQuery DEFAULT_QUERY = QuickGOQuery.createQuery("field1", "field2");

    private AbstractSolrQueryResultConverter<String> converter;

    @Mock
    private QueryResponse responseMock;

    @Before
    public void setUp() throws Exception {
        converter = new AbstractSolrQueryResultConverter<String>() {
            @Override protected List<String> convertResults(SolrDocumentList results) {
                return results.stream()
                        .map(SolrDocument::toString)
                        .collect(Collectors.toList());
            }
        };
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
    public void responseWithNogetResults() throws Exception {
        QueryRequest request = createDefaultRequest(DEFAULT_QUERY);

        QueryResult result = converter.convert(responseMock, request);

        assertThat(result.getNumberOfHits(), is(0L));
        assertThat(result.getResults().isEmpty(), is(true));
        assertThat(result.getPageInfo(), is(nullValue()));
        assertThat(result.getFacet(), is(nullValue()));
    }

    @Test
    public void requestWithPagingRendersResponseWithNoResults() throws Exception {
        QueryRequest request = createRequestWithPaging(DEFAULT_QUERY, 1, 1);

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
    public void requestWithFacetingRendersResponseWithNogetResults() throws Exception {
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
    public void responseWith2getResults() {
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
    public void responseWith2ResultsAndPaging() {
        QueryRequest request = createRequestWithPaging(DEFAULT_QUERY, 1, 1);

        SolrDocumentList docList = new SolrDocumentList();
        docList.add(new SolrDocument());
        docList.add(new SolrDocument());
        docList.setNumFound(2);

        when(responseMock.getResults()).thenReturn(docList);

        QueryResult result = converter.convert(responseMock, request);

        checkPageInfo(result.getPageInfo(), 2, 1, 1);
    }

    @Test
    public void responseWith2ResultsAndSinglegetFacet() {
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

    private QueryRequest createDefaultRequest(QuickGOQuery query) {
        return new QueryRequest.Builder(query).build();
    }

    private QueryRequest createRequestWithPaging(QuickGOQuery query, int currentPage, int resultsPerPage) {
        return new QueryRequest.Builder(query).setPageParameters(currentPage, resultsPerPage).build();
    }

    private QueryRequest createRequestWithFaceting(QuickGOQuery query, List<String> facets) {
        QueryRequest.Builder builder = new QueryRequest.Builder(query);

        for (String facet : facets) {
            builder = builder.addFacetField(facet);
        }

        return builder.build();
    }
}