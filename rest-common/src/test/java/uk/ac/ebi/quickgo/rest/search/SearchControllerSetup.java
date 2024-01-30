package uk.ac.ebi.quickgo.rest.search;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.ac.ebi.quickgo.common.store.TemporarySolrDataStore;

import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelperImpl.DEFAULT_ENTRIES_PER_PAGE;

/**
 * A template for performing several functional tests to verify that a search controller
 * is capable of providing the client with a workable response.
 *
 */
@ExtendWith({SpringExtension.class, TemporarySolrDataStore.class})
@WebAppConfiguration
public abstract class SearchControllerSetup {
    private static final String QUERY_PARAM = "query";
    private static final String FACET_PARAM = "facet";
    private static final String PAGE_PARAM = "page";
    private static final String LIMIT_PARAM = "limit";
    private static final String HIGHLIGHTING_PARAM = "highlighting";

    protected String resourceUrl;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
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
     * @
     */
    protected void checkInvalidPageInfoInResponse(String query,
            int pageNum,
            int limit,
            int errorStatus) throws Exception {
        MockHttpServletRequestBuilder clientRequest = createRequest(query);

        clientRequest.param(PAGE_PARAM, String.valueOf(pageNum));
        clientRequest.param(LIMIT_PARAM, String.valueOf(limit));

        ResultActions result = mockMvc
                .perform(clientRequest)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(errorStatus));

        checkErrorMessage(result);
    }

    /**
     * Checks whether the page object in the JSON response contains the expected values for: number of entries per
     * page, the number of the current page being displayed, and the total amount of pages.
     *
     * @param query a query in order to get a valid response
     * @param pageNum the page to return
     * @param limit the maximum number of entries that response holds
     * @
     */
    protected void checkValidPageInfoInResponse(String query,
            int pageNum,
            int limit) throws Exception {
        MockHttpServletRequestBuilder clientRequest = createRequest(query);

        clientRequest.param(PAGE_PARAM, String.valueOf(pageNum));
        clientRequest.param(LIMIT_PARAM, String.valueOf(limit));

        mockMvc.perform(clientRequest)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.pageInfo.resultsPerPage").value(limit))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pageInfo.current").value(pageNum))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pageInfo.total").value(pageNum));
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
     * @
     */
    protected void checkValidEmptyResultsResponse(String query) throws Exception {
        MockHttpServletRequestBuilder clientRequest = createRequest(query);

        mockMvc.perform(clientRequest)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.pageInfo.resultsPerPage")
                        .value(DEFAULT_ENTRIES_PER_PAGE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pageInfo.current").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pageInfo.total").value(0));
    }

    protected ResultActions checkResultsBodyResponse(String query) throws Exception {
        MockHttpServletRequestBuilder clientRequest = createRequest(query);

        return mockMvc.perform(clientRequest)
                .andDo(print());
    }

    // facets ---------------------------------------------------------
    protected void checkInvalidFacetResponse(String query, String facet) throws Exception {
        MockHttpServletRequestBuilder clientRequest = createRequest(query);

        addFacetsToRequest(clientRequest, facet);

        ResultActions result = mockMvc.perform(clientRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        checkErrorMessage(result);
    }

    protected void checkValidFacetResponse(String query, String... facets) throws Exception {
        MockHttpServletRequestBuilder clientRequest = createRequest(query);

        addFacetsToRequest(clientRequest, facets);

        mockMvc.perform(clientRequest)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.facet.facetFields.*.field", containsInAnyOrder(facets)));
    }

    private void addFacetsToRequest(MockHttpServletRequestBuilder clientRequest, String... facets) {
        addParamsToRequest(clientRequest, FACET_PARAM, facets);
    }

    // filter queries ---------------------------------------------------------
    protected ResultActions checkInvalidFilterQueryResponse(String query, Param... filterQuery) throws Exception {
        MockHttpServletRequestBuilder clientRequest = createRequest(query);

        addFiltersToRequest(clientRequest, filterQuery);

        ResultActions result = mockMvc.perform(clientRequest)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        checkErrorMessage(result);

        return result;
    }

    protected ResultActions checkValidFilterQueryResponse(String query, int expectedResponseSize,
            Param... filterQueries)
            throws Exception {
        MockHttpServletRequestBuilder clientRequest = createRequest(query);

        addFiltersToRequest(clientRequest, filterQueries);

        return mockMvc.perform(clientRequest)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.results.*", hasSize(expectedResponseSize)));
    }

    // highlighting ---------------------------------------------------------
    protected ResultActions checkValidHighlightOnQueryResponse(String query, String... idHits)
            throws Exception {
        MockHttpServletRequestBuilder clientRequest = createRequest(query);
        addParamsToRequest(clientRequest, HIGHLIGHTING_PARAM, "true");

        int expectedResponseSize = idHits.length;

        return mockMvc.perform(clientRequest)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.results.*.id", containsInAnyOrder(idHits)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.results.*", hasSize(expectedResponseSize)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.highlighting.*.id", containsInAnyOrder(idHits)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.highlighting.*", hasSize(expectedResponseSize)));
    }

    protected ResultActions checkValidHighlightOffQueryResponse(String query, int expectedResponseSize)
            throws Exception {
        MockHttpServletRequestBuilder clientRequest = createRequest(query);
        addParamsToRequest(clientRequest, HIGHLIGHTING_PARAM, "false");

        return mockMvc.perform(clientRequest)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.results.*", hasSize(expectedResponseSize)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.highlighting.*").doesNotExist());
    }

    private void addFiltersToRequest(MockHttpServletRequestBuilder clientRequest, Param... filters) {
        if (filters != null) {
            Stream.of(filters)
                    .forEach(fq -> clientRequest.param(fq.key, fq.value));

        }
    }

    private MockHttpServletRequestBuilder createRequest(String query) {
        return MockMvcRequestBuilders.get(resourceUrl).param(QUERY_PARAM, query);
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
        result.andExpect(MockMvcResultMatchers.jsonPath("$.url", is(getRequestUrl(result))));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.messages").exists());
    }

    public static class Param {
        final String key;
        final String value;

        public Param(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}