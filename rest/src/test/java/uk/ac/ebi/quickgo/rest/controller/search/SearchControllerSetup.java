package uk.ac.ebi.quickgo.rest.controller.search;

import uk.ac.ebi.quickgo.repo.solr.TemporarySolrDataStore;
import uk.ac.ebi.quickgo.rest.QuickGOREST;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    protected static final String SEARCH_RESOURCE_URL = "/QuickGO/internal/search";

    private static final String QUERY_PARAM = "query";
    private static final String FACET_PARAM = "facet";

    protected String resourceUrl;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
    }

    protected void checkInvalidFacetResponse(String query, String facet) throws Exception {
        MockHttpServletRequestBuilder clientRequest = get(resourceUrl)
                .param(QUERY_PARAM, query);

        addFacetsToRequest(clientRequest, facet);

        mockMvc.perform(clientRequest)
                .andExpect(status().isBadRequest());
    }

    protected void checkValidFacetResponse(String query, String... facets) throws Exception {
        MockHttpServletRequestBuilder clientRequest = get(resourceUrl)
                .param(QUERY_PARAM, query);

        addFacetsToRequest(clientRequest, facets);

        mockMvc.perform(clientRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.facet.facetFields.*", hasSize(facets.length)));
    }

    private void addFacetsToRequest(MockHttpServletRequestBuilder clientRequest, String... facets) {
        for (String facet : facets) {
            clientRequest = clientRequest.param(FACET_PARAM, facet);
        }
    }
}