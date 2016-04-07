package uk.ac.ebi.quickgo.geneproduct.controller;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.ac.ebi.quickgo.common.solr.TemporarySolrDataStore;
import uk.ac.ebi.quickgo.geneproduct.GeneProductREST;
import uk.ac.ebi.quickgo.geneproduct.common.GeneProductRepository;
import uk.ac.ebi.quickgo.geneproduct.common.common.GeneProductDocMocker;
import uk.ac.ebi.quickgo.geneproduct.common.document.GeneProductDocument;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Performs tests on GeneProduct REST controller.
 * Uses an embedded Solr server that is cleaned up automatically after tests complete.
 *
 * @Author Tony Wardell
 * Date: 04/04/2016
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {GeneProductREST.class})
@WebAppConfiguration
public class GeneProductControllerIT {

	// temporary data store for solr's data, which is automatically cleaned on exit
	@ClassRule
	public static final TemporarySolrDataStore solrDataStore = new TemporarySolrDataStore();
	private static final String RESOURCE_URL = "/QuickGO/services/geneproduct";

	protected static final String COMMA = ",";


	@Autowired
	protected WebApplicationContext webApplicationContext;

	@Autowired
	protected GeneProductRepository geneProductRepository;

	protected MockMvc mockMvc;

	private String validId;
	private String validIdsCSV;
	private List<String> validIdList;

	@Before
	public void setup() {
		geneProductRepository.deleteAll();

		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
				.build();


		List<GeneProductDocument> basicDocs = createBasicDocs();
		assertThat(basicDocs.size(), is(greaterThan(1)));

		validId = basicDocs.get(0).id;
		validIdsCSV = basicDocs.stream().map(doc -> doc.id).collect(Collectors.joining(","));
		validIdList = Arrays.asList(validIdsCSV.split(COMMA));

		geneProductRepository.save(basicDocs);
	}

	@Test
	public void canRetrieveOneGeneProductById() throws Exception {
		ResultActions response = mockMvc.perform(get(buildGeneProductURL(validId)));

		response.andDo(print())
				.andExpect(jsonPath("$.results.*.identifier", hasSize(1)))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk());
	}


	@Test
	public void canRetrieveMultiGeneProductById() throws Exception {
		ResultActions response = mockMvc.perform(get(buildGeneProductURL(validIdsCSV)));

		response.andDo(print())
				.andExpect(jsonPath("$.results.*.identifier", hasSize(3)))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk());
	}


	protected String buildGeneProductURL(String id) {
		return RESOURCE_URL + "/" + id;
	}

	protected List<GeneProductDocument> createBasicDocs() {
		return Arrays.asList(
				GeneProductDocMocker.createDocWithId("A0A000"),
				GeneProductDocMocker.createDocWithId("A0A001"),
				GeneProductDocMocker.createDocWithId("A0A002"));


	}
}
