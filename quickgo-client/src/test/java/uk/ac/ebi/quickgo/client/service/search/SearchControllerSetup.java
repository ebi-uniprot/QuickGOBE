package uk.ac.ebi.quickgo.client.service.search;

import uk.ac.ebi.quickgo.client.QuickGOREST;
import uk.ac.ebi.quickgo.common.solr.TemporarySolrDataStore;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.mockito.internal.matchers.GreaterOrEqual;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Does several functional tests to verify that the search controller is capable of providing the client with a
 * workable response.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {QuickGOREST.class})
@WebAppConfiguration
public abstract class SearchControllerSetup {
    @ClassRule
    public static final TemporarySolrDataStore solrDataStore = new TemporarySolrDataStore();

    private static final int DEFAULT_ENTRIES_PER_PAGE = 25;

    protected static final String SEARCH_RESOURCE_URL = "/QuickGO/internal/search";

    private static final String QUERY_PARAM = "query";
    private static final String FACET_PARAM = "facet";
    private static final String PAGE_PARAM = "page";
    private static final String LIMIT_PARAM = "limit";
    private static final String FILTER_QUERY_PARAM = "filterQuery";
    private static final String HIGHLIGHTING_PARAM = "highlighting";

    protected String resourceUrl;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
    }

    /**
     * Verifies that a request with an invalid page related parameter will received a 400 response.
     *
     * @param query a query in order to get a valid response
     * @param pageNum the page to return
     * @param limit the maximum number of entries that response holds
     * @param errorStatus the expectedErrorStatus code returned from the server
     * @throws Exception
     */
    protected void checkInvalidPageInfoInResponse(String query,
            int pageNum,
            int limit,
            int errorStatus) throws Exception {
        MockHttpServletRequestBuilder clientRequest = createRequest(query);

        clientRequest.param(PAGE_PARAM, String.valueOf(pageNum));
        clientRequest.param(LIMIT_PARAM, String.valueOf(limit));

        ResultActions result = mockMvc.perform(clientRequest)
                .andExpect(status().is(errorStatus));


    }

    /**
     * Checks whether the page object in the JSON response contains the expected values for: number of entries per
     * page, the number of the current page being displayed, and the total amount of pages.
     *
     * @param query a query in order to get a valid response
     * @param pageNum the page to return
     * @param limit the maximum number of entries that response holds
     * @throws Exception
     */
    protected void checkValidPageInfoInResponse(String query,
            int pageNum,
            int limit ) throws Exception {
        MockHttpServletRequestBuilder clientRequest = createRequest(query);

        clientRequest.param(PAGE_PARAM, String.valueOf(pageNum));
        clientRequest.param(LIMIT_PARAM, String.valueOf(limit));

        mockMvc.perform(clientRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageInfo.resultsPerPage").value(limit))
                .andExpect(jsonPath("$.pageInfo.current").value(pageNum))
                .andExpect(jsonPath("$.pageInfo.total").value(new GreaterOrEqual<>(pageNum)));
    }

    /**
     * Checks that a query returns a valid response, representing zero results. That is:
     *
     * <ul>
     *     <li>0 as the current page</li>
     *     <li>0 total results</li>
     *     <li>the default number of entries per page</li>
     * </ul>
     *
     * @param query the query that should return a response that contains no results.
     * @throws Exception
     */
    protected void checkValidEmptyResultsResponse(String query) throws Exception {
        MockHttpServletRequestBuilder clientRequest = createRequest(query);

        mockMvc.perform(clientRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageInfo.resultsPerPage")
                        .value(DEFAULT_ENTRIES_PER_PAGE))
                .andExpect(jsonPath("$.pageInfo.current").value(0))
                .andExpect(jsonPath("$.pageInfo.total").value(0));
    }

    // facets ---------------------------------------------------------
    protected void checkInvalidFacetResponse(String query, String facet) throws Exception {
        MockHttpServletRequestBuilder clientRequest = createRequest(query);

        addFacetsToRequest(clientRequest, facet);

        ResultActions result = mockMvc.perform(clientRequest)
                .andExpect(status().isBadRequest());

        checkErrorMessage(result);
    }

    protected void checkValidFacetResponse(String query, String... facets) throws Exception {
        MockHttpServletRequestBuilder clientRequest = createRequest(query);

        addFacetsToRequest(clientRequest, facets);

        mockMvc.perform(clientRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.facet.facetFields.*", hasSize(facets.length)));
    }

    private void addFacetsToRequest(MockHttpServletRequestBuilder clientRequest, String... facets) {
        addParamsToRequest(clientRequest, FACET_PARAM, facets);
    }

    // filter queries ---------------------------------------------------------
    protected void checkInvalidFilterQueryResponse(String query, String... filterQuery) throws Exception {
        MockHttpServletRequestBuilder clientRequest = createRequest(query);

        addFiltersToRequest(clientRequest, filterQuery);

        ResultActions result = mockMvc.perform(clientRequest)
                .andDo(print())
                .andExpect(status().isBadRequest());

        checkErrorMessage(result);
    }

    protected ResultActions checkValidFilterQueryResponse(String query, int expectedResponseSize, String... filterQuery)
            throws Exception {
        MockHttpServletRequestBuilder clientRequest = createRequest(query);

        addFiltersToRequest(clientRequest, filterQuery);

        return mockMvc.perform(clientRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results.*", hasSize(expectedResponseSize)));
    }

    // highlighting ---------------------------------------------------------
    protected ResultActions checkValidHighlightOnQueryResponse(String query, String... idHits)
            throws Exception {
        MockHttpServletRequestBuilder clientRequest = createRequest(query);
        addParamsToRequest(clientRequest, HIGHLIGHTING_PARAM, "true");

        int expectedResponseSize = idHits.length;

        return mockMvc.perform(clientRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results.*.id", containsInAnyOrder(idHits)))
                .andExpect(jsonPath("$.results.*", hasSize(expectedResponseSize)))
                .andExpect(jsonPath("$.highlighting.*.id", containsInAnyOrder(idHits)))
                .andExpect(jsonPath("$.highlighting.*", hasSize(expectedResponseSize)));
    }

    protected ResultActions checkValidHighlightOffQueryResponse(String query, int expectedResponseSize)
            throws Exception {
        MockHttpServletRequestBuilder clientRequest = createRequest(query);
        addParamsToRequest(clientRequest, "highlighting", "false");

        return mockMvc.perform(clientRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results.*", hasSize(expectedResponseSize)))
                .andExpect(jsonPath("$.highlighting.*").doesNotExist());
    }

    private void addFiltersToRequest(MockHttpServletRequestBuilder clientRequest, String... filters) {
        addParamsToRequest(clientRequest, FILTER_QUERY_PARAM, filters);
    }

    private MockHttpServletRequestBuilder createRequest(String query) {
        return get(resourceUrl).param(QUERY_PARAM, query);
    }

    private void addParamsToRequest(MockHttpServletRequestBuilder clientRequest, String paramName, String... params) {
        for (String param : params) {
            clientRequest = clientRequest.param(paramName, param);
        }
    }

    private String getRequestUrl(ResultActions result) {
        return result.andReturn().getRequest().getRequestURL().toString();
    }

    private void checkErrorMessage(ResultActions result) throws Exception {
        result.andExpect(jsonPath("$.url", is(getRequestUrl(result))));
        result.andExpect(jsonPath("$.message").exists());
    }
}