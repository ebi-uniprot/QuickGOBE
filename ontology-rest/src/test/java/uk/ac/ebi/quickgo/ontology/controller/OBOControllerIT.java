package uk.ac.ebi.quickgo.ontology.controller;

import uk.ac.ebi.quickgo.common.store.TemporarySolrDataStore;
import uk.ac.ebi.quickgo.graphics.model.GraphImageLayout;
import uk.ac.ebi.quickgo.graphics.ontology.GraphImage;
import uk.ac.ebi.quickgo.graphics.ontology.GraphImageResult;
import uk.ac.ebi.quickgo.graphics.ontology.GraphPresentation;
import uk.ac.ebi.quickgo.graphics.ontology.RenderingGraphException;
import uk.ac.ebi.quickgo.graphics.service.GraphImageService;
import uk.ac.ebi.quickgo.ontology.OntologyREST;
import uk.ac.ebi.quickgo.ontology.common.OntologyDocument;
import uk.ac.ebi.quickgo.ontology.common.OntologyRepository;
import uk.ac.ebi.quickgo.ontology.model.OntologyRelationType;
import uk.ac.ebi.quickgo.ontology.model.OntologyRelationship;
import uk.ac.ebi.quickgo.ontology.traversal.OntologyGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.http.HttpHeaders;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.ac.ebi.quickgo.common.converter.HelpfulConverter.toCSV;
import static uk.ac.ebi.quickgo.ontology.OntologyRestConfig.CACHE_CONTROL_HEADER;
import static uk.ac.ebi.quickgo.ontology.controller.OBOController.*;

/**
 * Performs common tests on REST controllers that derive from {@link OBOController}.
 * Uses an embedded Solr server that is cleaned up automatically after tests complete.
 *
 * Created by edd on 14/01/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {OntologyREST.class, GraphicsConfig.class})
@WebAppConfiguration
public abstract class OBOControllerIT {
    // temporary data store for solr's data, which is automatically cleaned on exit
    @ClassRule
    public static final TemporarySolrDataStore solrDataStore = new TemporarySolrDataStore();
    private static final int WAIT_PERIOD = 10;
    private static final String QUERY_PARAM = "query";
    private static final String PAGE_PARAM = "page";
    private static final String LIMIT_PARAM = "limit";
    private static final String RELATIONS_PARAM = "relations";
    private static final String BASE64_PARAM = "base64";
    private static final int RELATIONSHIP_CHAIN_LENGTH = 10;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Autowired
    protected OntologyRepository ontologyRepository;

    @Autowired
    protected OntologyGraph ontologyGraph;
    protected MockMvc mockMvc;

    @Autowired
    private GraphImageService graphImageService;
    private String resourceUrl;
    private String validId;
    private String validIdsShortCSV;
    private String validIdsCSV;
    private List<String> validIdShortList;
    private List<String> validIdList;
    private List<OntologyRelationship> relationships;
    private String validRelation;
    private String invalidRelation;

    @Value("${ontology.default_page_size:25}")
    private int defaultPageSize;

    @Value("${ontology.max_page_size:30}")
    private int maxPageSize;

    @Value("${ontology.cache.control.start.time:18}")
    private int cacheControlStartTime;

    @Value("${ontology.cache.control.end.time:18}")
    private int cacheControlEndTime;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();

        resourceUrl = getResourceURL();

        List<OntologyDocument> basicDocs = createBasicDocs();
        assertThat(basicDocs.size(), is(greaterThan(1)));

        validId = basicDocs.get(0).id;
        validIdList = basicDocs.stream().map(doc -> doc.id).collect(Collectors.toList());
        validIdsCSV = toCSV(validIdList);

        ontologyRepository.deleteAll();
        ontologyRepository.save(basicDocs);

        setupSimpleRelationshipChain();

        validIdShortList = validIdList.subList(0, 2);
        validIdsShortCSV = toCSV(validIdShortList);

        when(graphImageService.graphPresentationBuilder()).thenReturn(new GraphPresentation.Builder());
    }

    @After
    public void after() {
        reset(graphImageService);
    }

    @Test
    public void whenNoGraphDataExistsForTermWeCanStillRetrieveOtherTermInfo() throws Exception {
        List<OntologyDocument> docsWithGraphIds = createNDocs(RELATIONSHIP_CHAIN_LENGTH + 1);
        OntologyDocument validDocWithNoGraphData = docsWithGraphIds.get(docsWithGraphIds.size() - 1);
        ontologyRepository.save(validDocWithNoGraphData);

        ResultActions response = mockMvc.perform(get(buildTermsURL(validDocWithNoGraphData.id)));

        expectBasicFieldsInResults(response, singletonList(validDocWithNoGraphData.id))
                .andExpect(jsonPath("$.results.*.id", hasSize(1)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveOntologyViaSecondaryId() throws Exception {
        ontologyRepository.deleteAll();

        String primaryId = createId(1);
        String secondaryId = createId(2);

        OntologyDocument doc = createBasicDoc(primaryId, "name");
        doc.secondaryIds = Collections.singletonList(secondaryId);

        ontologyRepository.save(doc);

        ResultActions response = mockMvc.perform(get(buildTermsURL(secondaryId)));

        expectCoreFieldsInResults(response, singletonList(primaryId))
                .andExpect(jsonPath("$.results.*.id", hasSize(1)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveOntologyViaLowerCasedSecondaryId() throws Exception {
        ontologyRepository.deleteAll();

        String primaryId = createId(1);
        String secondaryId = createId(2);

        OntologyDocument doc = createBasicDoc(primaryId, "name");
        doc.secondaryIds = Collections.singletonList(secondaryId);

        ontologyRepository.save(doc);

        ResultActions response = mockMvc.perform(get(buildTermsURL(secondaryId.toLowerCase())));

        expectCoreFieldsInResults(response, singletonList(primaryId))
                .andExpect(jsonPath("$.results.*.id", hasSize(1)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveCoreAttrByOneId() throws Exception {
        ResultActions response = mockMvc.perform(get(buildTermsURL(validId)));

        expectCoreFieldsInResults(response, singletonList(validId))
                .andExpect(jsonPath("$.results.*.id", hasSize(1)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveCoreAttrByOneIdInLowercase() throws Exception {
        ResultActions response = mockMvc.perform(get(buildTermsURL(validId.toLowerCase())));

        expectCoreFieldsInResults(response, singletonList(validId))
                .andExpect(jsonPath("$.results.*.id", hasSize(1)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveCoreAttrBySubsetOfSavedIds() throws Exception {

        ResultActions response = mockMvc.perform(get(buildTermsURL(validIdsCSV)));

        expectCoreFieldsInResults(response, validIdList)
                .andExpect(jsonPath("$.results.*.id", hasSize(validIdList.size())))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveCoreAttrByAllIds() throws Exception {
        ResultActions response = mockMvc.perform(get(buildTermsURL(validIdsShortCSV)));

        expectCoreFieldsInResults(response, validIdShortList)
                .andDo(print())
                .andExpect(jsonPath("$.results.*.id", hasSize(validIdShortList.size())))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveCompleteByOneId() throws Exception {
        ResultActions response = mockMvc.perform(get(buildTermsURLWithSubResource(validId, COMPLETE_SUB_RESOURCE)));

        expectCompleteFieldsInResults(response, singletonList(validId))
                .andExpect(jsonPath("$.results.*.history", hasSize(1)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveCompleteByTwoIds() throws Exception {
        ResultActions response =
                mockMvc.perform(get(buildTermsURLWithSubResource(validIdsShortCSV, COMPLETE_SUB_RESOURCE)));

        expectCompleteFieldsInResults(response, validIdShortList)
                .andExpect(jsonPath("$.results.*.history", hasSize(2)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveHistoryByOneId() throws Exception {
        ResultActions response = mockMvc.perform(get(buildTermsURLWithSubResource(validId, HISTORY_SUB_RESOURCE)));

        expectBasicFieldsInResults(response, singletonList(validId))
                .andExpect(jsonPath("$.results.*.history", hasSize(1)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveHistoryByTwoIds() throws Exception {
        ResultActions response =
                mockMvc.perform(get(buildTermsURLWithSubResource(validIdsShortCSV, HISTORY_SUB_RESOURCE)));

        expectBasicFieldsInResults(response, validIdShortList)
                .andExpect(jsonPath("$.results.*.history", hasSize(2)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveXRefsByOneId() throws Exception {
        ResultActions response = mockMvc.perform(get(buildTermsURLWithSubResource(validId, XREFS_SUB_RESOURCE)));

        expectBasicFieldsInResults(response, singletonList(validId))
                .andExpect(jsonPath("$.results.*.xRefs", hasSize(1)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveXRefsByTwoIds() throws Exception {
        ResultActions response =
                mockMvc.perform(get(buildTermsURLWithSubResource(validIdsShortCSV, XREFS_SUB_RESOURCE)));

        expectBasicFieldsInResults(response, validIdShortList)
                .andExpect(jsonPath("$.results.*.xRefs", hasSize(2)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveTaxonConstraintsByOneId() throws Exception {
        ResultActions response = mockMvc.perform(get(buildTermsURLWithSubResource(validId, CONSTRAINTS_SUB_RESOURCE)));

        expectBasicFieldsInResults(response, singletonList(validId))
                .andExpect(jsonPath("$.results.*.taxonConstraints", hasSize(1)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveTaxonConstraintsByTwoIds() throws Exception {
        ResultActions response =
                mockMvc.perform(get(buildTermsURLWithSubResource(validIdsShortCSV, CONSTRAINTS_SUB_RESOURCE)));

        expectBasicFieldsInResults(response, validIdShortList)
                .andExpect(jsonPath("$.results.*.taxonConstraints", hasSize(2)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveAnnotationGuideLinesByOneId() throws Exception {
        ResultActions response = mockMvc.perform(get(buildTermsURLWithSubResource(validId, GUIDELINES_SUB_RESOURCE)));

        expectBasicFieldsInResults(response, singletonList(validId))
                .andExpect(jsonPath("$.results.*.annotationGuidelines", hasSize(1)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveAnnotationGuideLinesByTwoIds() throws Exception {
        ResultActions response =
                mockMvc.perform(get(buildTermsURLWithSubResource(validIdsShortCSV, GUIDELINES_SUB_RESOURCE)));

        expectBasicFieldsInResults(response, validIdShortList)
                .andExpect(jsonPath("$.results.*.annotationGuidelines", hasSize(2)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveXORelsByOneId() throws Exception {
        ResultActions response = mockMvc.perform(get(buildTermsURLWithSubResource(validId, XRELATIONS_SUB_RESOURCE)));

        expectBasicFieldsInResults(response, singletonList(validId))
                .andExpect(jsonPath("$.results.*.xRelations", hasSize(1)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveXORelsByTwoIds() throws Exception {
        ResultActions response =
                mockMvc.perform(get(buildTermsURLWithSubResource(validIdsShortCSV, XRELATIONS_SUB_RESOURCE)));

        expectBasicFieldsInResults(response, validIdShortList)
                .andExpect(jsonPath("$.results.*.xRelations", hasSize(2)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void finds400IfUrlIsEmpty() throws Exception {
        mockMvc.perform(get(resourceUrl + "/"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void finds400IfUrlIsJustWrong() throws Exception {
        mockMvc.perform(get(resourceUrl + "/thisIsAnEndPointThatDoesNotExist"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void finds200IfNoResultsBecauseIdsDoNotExist() throws Exception {
        mockMvc.perform(get(buildTermsURL(idMissingInRepository())))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void finds400OnInvalidId() throws Exception {
        ResultActions response = mockMvc.perform(get(buildTermsURL(invalidId())))
                .andDo(print())
                .andExpect(status().isBadRequest());

        expectInvalidIdError(response, invalidId());
    }

    @Test
    public void searchesForTermSuccessfullyAndReceivesValidResults() throws Exception {
        ResultActions response = mockMvc.perform(
                get(buildSearchURL())
                        .param(QUERY_PARAM, validId));

        expectResultsInfoExists(response)
                .andExpect(jsonPath("$.numberOfHits").value(1))
                .andExpect(jsonPath("$.results.*").exists());
    }

    @Test
    public void searchesForInvalidIdAndReceivesZeroValidResults() throws Exception {
        ResultActions response = mockMvc.perform(
                get(buildSearchURL())
                        .param(QUERY_PARAM, invalidId()));

        expectResultsInfoExists(response)
                .andExpect(jsonPath("$.numberOfHits").value(0))
                .andExpect(jsonPath("$.results").isArray());
    }

    @Test
    public void searchesForMissingIdAndReceivesZeroValidResults() throws Exception {
        ResultActions response = mockMvc.perform(
                get(buildSearchURL())
                        .param(QUERY_PARAM, idMissingInRepository()));

        expectResultsInfoExists(response)
                .andExpect(jsonPath("$.numberOfHits").value(0))
                .andExpect(jsonPath("$.results").isArray());
    }

    @Test
    public void searchesForFieldThatDoesNotExistAndReceivesZeroValidResults() throws Exception {
        ResultActions response = mockMvc.perform(
                get(buildSearchURL())
                        .param(QUERY_PARAM, "fieldDoesNotExist:sandwiches"));

        expectResultsInfoExists(response)
                .andExpect(jsonPath("$.numberOfHits").value(0))
                .andExpect(jsonPath("$.results").isArray());
    }

    @Test
    public void negativePageRequestOfAllEntriesRequestReturns400() throws Exception {
        ontologyRepository.deleteAll();

        int existingPages = 4;
        createAndSaveDocs(defaultPageSize * existingPages);

        mockMvc.perform(
                get(buildTermsURL())
                        .param(PAGE_PARAM, "-1"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void pageRequestHigherThanAvailablePagesForAllEntriesRequestReturns400() throws Exception {
        ontologyRepository.deleteAll();

        int existingPages = 4;
        createAndSaveDocs(defaultPageSize * existingPages);

        mockMvc.perform(
                get(buildTermsURL())
                        .param(PAGE_PARAM, String.valueOf(existingPages + 1)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void pageRequestHigherThanPaginationLimitReturns400() throws Exception {
        ontologyRepository.deleteAll();

        int totalEntries = defaultPageSize + 1;
        int pageSize = 1;
        int pageNumWhichIsTooHigh = totalEntries;

        createAndSaveDocs(totalEntries);

        ResultActions response = mockMvc.perform(
                get(buildSearchURL())
                        .param(QUERY_PARAM, validId)
                        .param(LIMIT_PARAM, String.valueOf(pageSize))
                        .param(PAGE_PARAM, String.valueOf(pageNumWhichIsTooHigh)));

        response.andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void retrievesFirstPageOfAllEntriesRequest() throws Exception {
        ontologyRepository.deleteAll();

        int existingPages = 4;
        createAndSaveDocs(defaultPageSize * existingPages);

        ResultActions response = mockMvc.perform(
                get(buildTermsURL())
                        .param(PAGE_PARAM, "1"));

        expectResultsInfoExists(response)
                .andExpect(jsonPath("$.results").isArray())
                .andExpect(jsonPath("$.results", hasSize(defaultPageSize)));
    }

    @Test
    public void retrievesSecondPageOfAllEntriesRequest() throws Exception {
        ontologyRepository.deleteAll();

        int existingPages = 4;
        createAndSaveDocs(defaultPageSize * existingPages);

        ResultActions response = mockMvc.perform(
                get(buildTermsURL())
                        .param(PAGE_PARAM, "2"));

        expectResultsInfoExists(response)
                .andExpect(jsonPath("$.results").isArray())
                .andExpect(jsonPath("$.results", hasSize(defaultPageSize)));
    }

    @Test
    public void retrievesLastPageOfAllEntriesRequest() throws Exception {
        ontologyRepository.deleteAll();

        int existingPages = 4;
        createAndSaveDocs(defaultPageSize * existingPages);

        ResultActions response = mockMvc.perform(
                get(buildTermsURL())
                        .param(PAGE_PARAM, String.valueOf(existingPages - 1)));

        expectResultsInfoExists(response)
                .andExpect(jsonPath("$.results").isArray())
                .andExpect(jsonPath("$.results", hasSize(defaultPageSize)));
    }

    @Test
    public void retrieveDefaultPageSizeWhenNoIdsSpecified() throws Exception {
        ontologyRepository.deleteAll();
        int createPages = 2;
        final int recordsToCreate = defaultPageSize * createPages;
        createAndSaveDocs(recordsToCreate);
        ResultActions response = mockMvc.perform(get(buildTermsURL()));
        expectBasicFieldsInResults(response, validIdShortList)
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results", hasSize(defaultPageSize)));
    }

    @Test
    public void canRetrieveMaxPageSizeWhenIdsAreSpecified() throws Exception {
        ontologyRepository.deleteAll();
        List<OntologyDocument> nDocs = createAndSaveDocs(maxPageSize);
        List<String> ids = nDocs.stream()
                .map(doc -> doc.id)
                .collect(Collectors.toList());
        ResultActions response = mockMvc.perform(get(buildTermsURL(ids)));
        expectBasicFieldsInResults(response, ids)
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results", hasSize(maxPageSize)));
    }

    @Test
    public void numberOfTermsRequestedGreaterThanTermLimitReturns400() throws Exception {
        ontologyRepository.deleteAll();
        List<OntologyDocument> nDocs = createAndSaveDocs(maxPageSize + 1);
        List<String> ids = nDocs.stream()
                .map(doc -> doc.id)
                .collect(Collectors.toList());
        ResultActions response = mockMvc.perform(get(buildTermsURL(ids)));
        response.andExpect(status().isBadRequest());
    }

    @Test
    public void canFetchAllAncestorsFrom1Term() throws Exception {
        String lowestChild = relationships.get(0).child;
        ResultActions response = mockMvc.perform(
                get(buildTermsURLWithSubResource(lowestChild, ANCESTORS_SUB_RESOURCE)));

        response.andDo(print())
                .andExpect(jsonPath("$.numberOfHits").value(1))
                .andExpect(jsonPath("$.results[0].ancestors", hasSize(10)));
    }

    @Test
    public void canFetchAllAncestorsFrom2Terms() throws Exception {
        String bottom = relationships.get(0).child;
        String secondBottom = relationships.get(1).child;

        ResultActions response = mockMvc.perform(
                get(buildTermsURLWithSubResource(toCSV(bottom, secondBottom), ANCESTORS_SUB_RESOURCE)));

        response.andDo(print())
                .andExpect(jsonPath("$.numberOfHits").value(2))
                .andExpect(jsonPath("$.results[0].ancestors", hasSize(10)))
                .andExpect(jsonPath("$.results[1].ancestors", hasSize(9)));
    }

    @Test
    public void canFetchAllAncestorsFromRelation() throws Exception {

        String bottom = relationships.get(0).child;

        ResultActions response = mockMvc.perform(
                get(buildTermsURLWithSubResource(bottom, ANCESTORS_SUB_RESOURCE))
                        .param(RELATIONS_PARAM, validRelation));

        response.andDo(print())
                .andExpect(jsonPath("$.numberOfHits").value(1))
                .andExpect(jsonPath("$.results[0].ancestors", hasSize(10)));
    }

    @Test
    public void invalidAncestorsProduces400AndErrorMessage() throws Exception {
        ResultActions response = mockMvc.perform(
                get(buildTermsURLWithSubResource(invalidId(), ANCESTORS_SUB_RESOURCE)));

        expectInvalidIdError(response, invalidId());
    }

    @Test
    public void invalidAncestorsRelationProduces400AndErrorMessage() throws Exception {
        String bottom = relationships.get(0).child;

        ResultActions response = mockMvc.perform(
                get(buildTermsURLWithSubResource(bottom, ANCESTORS_SUB_RESOURCE))
                        .param(RELATIONS_PARAM, invalidRelation));

        expectInvalidRelationError(response, invalidRelation);
    }

    @Test
    public void canFetchAllDescendantsFrom1Term() throws Exception {
        int relCount = relationships.size();
        String highestParent = relationships.get(relCount - 1).parent;

        ontologyRepository.deleteAll();
        createAndSaveDocs(relCount + 1);

        ResultActions response = mockMvc.perform(
                get(buildTermsURLWithSubResource(highestParent, DESCENDANTS_SUB_RESOURCE)));

        response.andDo(print())
                .andExpect(jsonPath("$.numberOfHits").value(1))
                .andExpect(jsonPath("$.results[0].descendants", hasSize(10)));
    }

    @Test
    public void canFetchAllDescendantsFrom2Terms() throws Exception {
        int relCount = relationships.size();
        String top = relationships.get(relCount - 1).parent;
        String secondTop = relationships.get(relCount - 2).parent;

        ontologyRepository.deleteAll();
        createAndSaveDocs(relCount + 1);

        ResultActions response = mockMvc.perform(
                get(buildTermsURLWithSubResource(toCSV(top, secondTop), DESCENDANTS_SUB_RESOURCE)));

        response.andDo(print())
                .andExpect(jsonPath("$.numberOfHits").value(2))
                .andExpect(jsonPath("$.results[0].descendants", hasSize(9)))
                .andExpect(jsonPath("$.results[1].descendants", hasSize(10)));
    }

    @Test
    public void canFetchAllDescendantsFromRelation() throws Exception {
        int relCount = relationships.size();
        String highestParent = relationships.get(relCount - 1).parent;

        ontologyRepository.deleteAll();
        createAndSaveDocs(relCount + 1);

        ResultActions response = mockMvc.perform(
                get(buildTermsURLWithSubResource(highestParent, DESCENDANTS_SUB_RESOURCE))
                        .param(RELATIONS_PARAM, validRelation));

        response.andDo(print())
                .andExpect(jsonPath("$.numberOfHits").value(1))
                .andExpect(jsonPath("$.results[0].descendants", hasSize(10)));
    }

    @Test
    public void canFetchChildrenFromTerm() throws Exception {
        ResultActions response = mockMvc.perform(get(buildTermsURL()));

        response.andDo(print())
                .andExpect(jsonPath("$.numberOfHits").value(validIdList.size()))
                .andExpect(jsonPath("$.results.*.children").exists());
    }

    @Test
    public void invalidDescendantsProduces400AndErrorMessage() throws Exception {
        ResultActions response = mockMvc.perform(
                get(buildTermsURLWithSubResource(invalidId(), DESCENDANTS_SUB_RESOURCE)));

        expectInvalidIdError(response, invalidId());
    }

    @Test
    public void invalidDescendantsRelationProduces400AndErrorMessage() throws Exception {
        String highestParent = relationships.get(relationships.size() - 1).parent;

        ResultActions response = mockMvc.perform(
                get(buildTermsURLWithSubResource(highestParent, DESCENDANTS_SUB_RESOURCE))
                        .param(RELATIONS_PARAM, invalidRelation));

        expectInvalidRelationError(response, invalidRelation);
    }

    @Test
    public void canFetchAllPathsFrom1Term() throws Exception {
        String bottomChild = relationships.get(0).child;
        String highestParent = relationships.get(relationships.size() - 1).parent;

        ResultActions response = mockMvc.perform(
                get(buildPathsURL(bottomChild, highestParent)));

        response.andDo(print())
                .andExpect(jsonPath("$.numberOfHits").value(1))
                .andExpect(jsonPath("$.results").isArray());
    }

    @Test
    public void canFetchAllPathsFrom2Terms() throws Exception {
        String bottom = relationships.get(0).child;
        String secondBottom = relationships.get(1).child;
        String highest = relationships.get(relationships.size() - 1).parent;

        ResultActions response = mockMvc.perform(
                get(buildPathsURL(toCSV(bottom, secondBottom), highest)));

        response.andDo(print())
                .andExpect(jsonPath("$.numberOfHits").value(2))
                .andExpect(jsonPath("$.results").isArray());
    }

    @Test
    public void canFetchAllPathsTo2Terms() throws Exception {
        String bottom = relationships.get(0).child;

        String top = relationships.get(relationships.size() - 1).parent;
        String secondTop = relationships.get(relationships.size() - 2).parent;

        ResultActions response = mockMvc.perform(
                get(buildPathsURL(bottom, toCSV(top, secondTop))));

        response.andDo(print())
                .andExpect(jsonPath("$.numberOfHits").value(2))
                .andExpect(jsonPath("$.results").isArray());
    }

    @Test
    public void canFetchAllPathsFrom1TermWithRelation() throws Exception {
        String bottomChild = relationships.get(0).child;
        String highestParent = relationships.get(relationships.size() - 1).parent;

        ResultActions response = mockMvc.perform(
                get(buildPathsURL(bottomChild, highestParent))
                        .param(RELATIONS_PARAM, validRelation));

        response.andDo(print())
                .andExpect(jsonPath("$.numberOfHits").value(1))
                .andExpect(jsonPath("$.results").isArray());
    }

    @Test
    public void invalidStartPathsProduces400AndErrorMessage() throws Exception {
        String highest = relationships.get(relationships.size() - 1).parent;
        ResultActions response = mockMvc.perform(
                get(buildPathsURL(invalidId(), highest)));

        expectInvalidIdError(response, invalidId());
    }

    @Test
    public void invalidEndPathsProduces400AndErrorMessage() throws Exception {
        String bottom = relationships.get(0).child;
        ResultActions response = mockMvc.perform(
                get(buildPathsURL(bottom, invalidId())));

        expectInvalidIdError(response, invalidId());
    }

    @Test
    public void invalidPathsRelationProduces400AndErrorMessage() throws Exception {
        String bottomChild = relationships.get(0).child;
        String highestParent = relationships.get(relationships.size() - 1).parent;

        ResultActions response = mockMvc.perform(
                get(buildPathsURL(bottomChild, highestParent))
                        .param(RELATIONS_PARAM, invalidRelation));

        expectInvalidRelationError(response, invalidRelation);
    }

    @Test
    public void canLoadDefaultChartImageIfSourcesWereLoaded() throws Exception {
        requestToChartServiceReturnsValidImage();

        ResultActions response = mockMvc.perform(
                get(buildTermsURLWithSubResource(validId, CHART_SUB_RESOURCE)));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE))
                .andExpect(header().doesNotExist(HttpHeaders.CONTENT_ENCODING));

        MvcResult result = response.andReturn();
        assertThat(result.getResponse().getContentLength(), is(greaterThan(0)));
    }

    @Test
    public void canLoadChartImageIfSourcesWereLoaded() throws Exception {
        requestToChartServiceReturnsValidImage();

        ResultActions response = mockMvc.perform(
                get(buildTermsURLWithSubResource(validId, CHART_SUB_RESOURCE))
                        .param(BASE64_PARAM, "false"));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE))
                .andExpect(header().doesNotExist(HttpHeaders.CONTENT_ENCODING));

        MvcResult result = response.andReturn();
        assertThat(result.getResponse().getContentLength(), is(greaterThan(0)));
    }

    @Test
    public void canLoadBase64EncodedChartIfSourcesWereLoaded() throws Exception {
        requestToChartServiceReturnsValidImage();

        ResultActions response = mockMvc.perform(
                get(buildTermsURLWithSubResource(validId, CHART_SUB_RESOURCE))
                        .param("base64", "true"));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE))
                .andExpect(header().string(HttpHeaders.CONTENT_ENCODING, BASE_64_CONTENT_ENCODING));

        MvcResult result = response.andReturn();
        assertThat(result.getResponse().getContentLength(), is(greaterThan(0)));
    }

    @Test
    public void failedChartRequestProduces500() throws Exception {
        String exceptionDescription = "Error encountered during creation of ontology chart graphics.";
        when(graphImageService.createChart(anyListOf(String.class), anyString(), any(GraphPresentation.class)))
                .thenThrow(
                new RenderingGraphException("Problem rendering graphics")
        );

        ResultActions response = mockMvc.perform(
                get(buildTermsURLWithSubResource(validId, CHART_SUB_RESOURCE)));

        expectResponseCreationError(response.andExpect(status().is5xxServerError()), exceptionDescription);
    }

    @Test
    public void failedChartRequestDueToInvalidIdProduces400() throws Exception {
        requestToChartServiceReturnsValidImage();

        ResultActions response = mockMvc.perform(
                get(buildTermsURLWithSubResource(invalidId(), CHART_SUB_RESOURCE)));

        expectInvalidIdError(response, invalidId());
    }

    @Test
    public void canLoadChartCoordsIfSourcesWereLoaded() throws Exception {
        requestToChartServiceReturnsValidImage();

        ResultActions response = mockMvc.perform(
                get(buildTermsURLWithSubResource(validId, CHART_COORDINATES_SUB_RESOURCE)));

        response.andExpect(status().isOk());

        response.andDo(print())
                .andExpect(jsonPath("$.imageWidth").exists())
                .andExpect(jsonPath("$.imageHeight").exists())
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.nodePositions").exists())
                .andExpect(jsonPath("$.legendPositions").exists());
    }

    @Test
    public void failedChartCoordsRequestProduces500() throws Exception {
        String exceptionDescription = "Error encountered during creation of ontology chart graphics.";
        when(graphImageService.createChart(anyListOf(String.class), anyString(), any(GraphPresentation.class)))
                .thenThrow(
                new RenderingGraphException("Problem rendering graphics")
        );

        ResultActions response = mockMvc.perform(
                get(buildTermsURLWithSubResource(validId, CHART_COORDINATES_SUB_RESOURCE)));

        expectResponseCreationError(response.andExpect(status().is5xxServerError()), exceptionDescription);
    }

    @Test
    public void failedChartCoordsRequestDueToInvalidIdProduces400() throws Exception {
        requestToChartServiceReturnsValidImage();

        ResultActions response = mockMvc.perform(
                get(buildTermsURLWithSubResource(invalidId(), CHART_COORDINATES_SUB_RESOURCE)));

        expectInvalidIdError(response, invalidId());
    }

    @Test
    public void canFetchAncestorGraphFor1Term() throws Exception {
        String startIds = relationships.get(0).child;
        ResultActions response = mockMvc.perform(get(resourceUrl + "/terms/graph").param("startIds", startIds));

        response.andDo(print())
                .andExpect(jsonPath("$.numberOfHits").value(1))
                .andExpect(jsonPath("$.results").isArray());
    }

    @Test
    public void canFetchAncestorGraphForMultipleTerms() throws Exception {
        String startIds = relationships.stream().limit(10).map(r -> r.child).collect(Collectors.joining(","));

        ResultActions response = mockMvc.perform(get(resourceUrl + "/terms/graph").param("startIds", startIds));

        response.andDo(print())
                .andExpect(jsonPath("$.numberOfHits").value(1))
                .andExpect(jsonPath("$.results").isArray());
    }

    @Test
    public void fetchingGraphForUnknownTermResultsInEmptyResult() throws Exception {
        ResultActions response = mockMvc.perform(get(resourceUrl + "/terms/graph").param("startIds",
                                                                                         idMissingInRepository()));

        response.andDo(print())
                .andExpect(jsonPath("$.numberOfHits").value(0))
                .andExpect(jsonPath("$.results").isArray());
    }

    @Test
    public void canUseValidRelationsForSubGraph() throws Exception {
        String startIds = relationships.get(0).child;
        ResultActions response = mockMvc.perform(get(getResourceURL() + "/terms/graph")
                                                         .param("startIds", startIds)
                                                         .param(RELATIONS_PARAM, getValidRelations()));

        response.andDo(print())
                .andExpect(jsonPath("$.numberOfHits").value(1))
                .andExpect(jsonPath("$.results").isArray());
    }

    @Test
    public void cannotUseInvalidRelationsForSubGraph() throws Exception {
        String startIds = relationships.get(0).child;
        ResultActions response = mockMvc.perform(get(getResourceURL() + "/terms/graph")
                                                         .param("startIds", startIds)
                                                         .param(RELATIONS_PARAM, getInvalidRelations()));

        expectUntraverseableRelationError(response, getInvalidRelations());
    }

    @Test
    public void invalidGraphParameterProduces400() throws Exception {

        final String urlTarget = buildTermsURLWithSubResource(validId, CHART_SUB_RESOURCE) + "?showKey=bloom";
        ResultActions response = mockMvc.perform(get(urlTarget));

        expectInvalidPropertyError(response);
    }

    //-----------------------  Check Http Header for Cache-Control content ------------------------------------------

    @Test
    public void cacheControlMaxAgeReducesOnSubsequentRequests() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(buildTermsURL(validId))).andReturn();

        String ccHeader = mvcResult.getResponse().getHeader(HttpHeaders.CACHE_CONTROL);
        assertThat(ccHeader, org.hamcrest.Matchers.startsWith(CACHE_CONTROL_HEADER));
        String[] keyValEarlier = ccHeader.split("=");

        assertThat(keyValEarlier[0], is(CACHE_CONTROL_HEADER));
        long maxAgeInFirstCall = Long.parseLong(keyValEarlier[1]);
        assertThat(maxAgeInFirstCall, is(greaterThanOrEqualTo(0L)));

        //Now wait to see if cache expiry time changes.
        Thread.sleep(WAIT_PERIOD);

        mvcResult = mockMvc.perform(get(buildTermsURL(validId))).andReturn();

        ccHeader = mvcResult.getResponse().getHeader(HttpHeaders.CACHE_CONTROL);
        String[] keyValLater = ccHeader.split("=");

        assertThat(keyValLater[0], is(CACHE_CONTROL_HEADER));
        long maxAgeInSecondCall = Long.parseLong(keyValLater[1]);

        //Compare earlier to later
        assertThat(maxAgeInSecondCall, is(greaterThanOrEqualTo(0L)));
        assertThat(maxAgeInFirstCall, is(greaterThanOrEqualTo(maxAgeInSecondCall)));
    }

    protected abstract String getResourceURL();

    protected abstract OntologyDocument createBasicDoc(String id, String name);

    /**
     * Create a basic document to be stored in the repository.
     * It must be a valid document, with a valid document ID.
     * This document will serve as the basis for numerous common
     * tests, relevant to all OBO controllers.
     *
     * @return a valid document with a valid ID
     */
    protected abstract List<OntologyDocument> createBasicDocs();

    protected abstract List<OntologyDocument> createNDocs(int n);

    protected abstract String idMissingInRepository();

    protected abstract String invalidId();

    protected abstract String createId(int idNum);

    protected abstract String getValidRelations();

    protected abstract String getInvalidRelations();

    protected ResultActions expectCoreFields(ResultActions result, String id) throws Exception {
        return expectCoreFields(result, id, "$.");
    }

    protected ResultActions expectCoreFields(ResultActions result, String id, String path) throws Exception {
        return expectBasicFields(result, id, path)
                .andExpect(jsonPath(path + "synonyms").exists());
    }

    protected ResultActions expectBasicFields(ResultActions result, String id, String path) throws Exception {
        return result
                .andDo(print())
                .andExpect(jsonPath(path + "id").value(id))
                .andExpect(jsonPath(path + "name").exists())
                .andExpect(jsonPath(path + "isObsolete").exists())
                .andExpect(jsonPath(path + "comment").exists())
                .andExpect(jsonPath(path + "definition").exists());
    }

    protected String buildTermsURL() {
        return getResourceURL() + "/" + TERMS_RESOURCE;
    }

    protected String buildTermsURL(String id) {
        return getResourceURL() + "/" + TERMS_RESOURCE + "/" + id;
    }

    protected String buildTermsURL(List<String> ids) {
        String csv = ids.stream().collect(Collectors.joining(","));
        return getResourceURL() + "/" + TERMS_RESOURCE + "/" + csv;
    }

    protected String buildTermsURLWithSubResource(String id, String subResource) {
        return buildTermsURL(id) + "/" + subResource;
    }

    protected String buildSearchURL() {
        return resourceUrl + "/" + SEARCH_RESOUCE;
    }

    protected String buildPathsURL(String terms1, String terms2) {
        return buildTermsURL(terms1) + "/" + PATHS_SUB_RESOURCE + "/" + terms2;
    }

    protected ResultActions expectCoreFieldsInResults(ResultActions result, List<String> ids) throws Exception {
        int index = 0;

        for (String id : ids) {
            expectCoreFields(result, id, "$.results[" + index++ + "].");
        }

        return result;
    }

    protected ResultActions expectCompleteFieldsInResults(ResultActions result, List<String> ids) throws Exception {
        int index = 0;

        for (String id : ids) {
            expectCompleteFields(result, id, "$.results[" + index++ + "].");
        }

        return result;
    }

    protected ResultActions expectCompleteFields(ResultActions result, String id, String path) throws Exception {
        return expectCoreFields(result, id, path)
                .andExpect(jsonPath(path + "secondaryIds").exists())
                .andExpect(jsonPath(path + "history").exists())
                .andExpect(jsonPath(path + "xRefs").exists())
                .andExpect(jsonPath(path + "xRelations").exists())
                .andExpect(jsonPath(path + "annotationGuidelines").exists())
                .andExpect(jsonPath(path + "taxonConstraints").exists())
                .andExpect(jsonPath(path + "subsets").exists())
                .andExpect(jsonPath(path + "replacements").exists())
                .andExpect(jsonPath(path + "replaces").exists())
                .andExpect(jsonPath(path + "credits").exists());
    }

    protected ResultActions expectInvalidIdError(ResultActions result, String id) throws Exception {
        return result
                .andDo(print())
                .andExpect(jsonPath("$.url", is(requestUrl(result))))
                .andExpect(jsonPath("$.messages", hasItem(containsString("Provided ID: '" + id + "'"))));
    }

    protected ResultActions expectInvalidPropertyError(ResultActions result) throws Exception {
        return result
                .andDo(print())
                .andExpect(jsonPath("$.url", is(requestUrl(result))))
                .andExpect(jsonPath("$.messages", hasItem(containsString(
                        "Failed to convert property value of type [java.lang.String] to required type [boolean] for " +
                                "property 'showKey'; nested exception is java.lang.IllegalArgumentException: Invalid boolean value [bloom]"))));
    }

    protected ResultActions expectInvalidRelationError(ResultActions result, String relation) throws Exception {
        return result
                .andDo(print())
                .andExpect(jsonPath("$.url", is(requestUrl(result))))
                .andExpect(jsonPath("$.messages", hasItem(
                        containsString("Unknown relationship requested: '" + relation + "'"))));
    }

    protected ResultActions expectUntraverseableRelationError(ResultActions result, String relation) throws Exception {
        return result
                .andDo(print())
                .andExpect(jsonPath("$.url", is(requestUrl(result))))
                .andExpect(jsonPath("$.messages", hasItem(
                        containsString(
                                "Cannot traverse over relation type: " + relation + ". Can only traverse over:"))));
    }

    protected ResultActions expectResponseCreationError(ResultActions result, String messagePrefix) throws Exception {
        return result
                .andDo(print())
                .andExpect(jsonPath("$.url", is(requestUrl(result))))
                .andExpect(jsonPath("$.messages", hasItem(containsString(messagePrefix))));
    }

    protected ResultActions expectResultsInfoExists(ResultActions result) throws Exception {
        return result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageInfo").exists())
                .andExpect(jsonPath("$.pageInfo.resultsPerPage").exists())
                .andExpect(jsonPath("$.pageInfo.total").exists())
                .andExpect(jsonPath("$.pageInfo.current").exists());
    }

    protected ResultActions expectBasicFieldsInResults(ResultActions result, List<String> ids) throws Exception {
        int index = 0;

        for (String id : ids) {
            expectBasicFields(result, id, "$.results[" + index++ + "].");
        }

        return result;
    }

    private void requestToChartServiceReturnsValidImage() {
        GraphImageResult mockGraphImageResult = mock(GraphImageResult.class);
        when(mockGraphImageResult.getGraphImage()).thenReturn(new GraphImage("Mocked GraphImage"));
        GraphImageLayout layout = new GraphImageLayout();
        layout.title = "layout title";
        when(mockGraphImageResult.getLayout()).thenReturn(layout);
        when(graphImageService.createChart(anyListOf(String.class), anyString(), any(GraphPresentation.class)))
                .thenReturn(mockGraphImageResult);
    }

    private void setupSimpleRelationshipChain() {
        setupSimpleRelationshipChain(RELATIONSHIP_CHAIN_LENGTH);
    }

    private void setupSimpleRelationshipChain(int idCount) {
        List<OntologyRelationship> simpleRelationships = new ArrayList<>();

        List<OntologyDocument> fakeDocuments = createNDocs(idCount);
        OntologyRelationType validRelationType = OntologyRelationType.IS_A;
        for (int i = 0; i < fakeDocuments.size() - 1; i++) {
            simpleRelationships.add(
                    new OntologyRelationship(
                            fakeDocuments.get(i).id,                        // child
                            fakeDocuments.get(i + 1).id,                    // parent
                            validRelationType));                            // relationship
        }

        relationships = simpleRelationships;
        validRelation = OntologyRelationType.IS_A.getLongName();
        invalidRelation = "this-does-not-exist";
        ontologyGraph.addRelationships(simpleRelationships);
    }

    private String requestUrl(ResultActions resultActions) {
        return resultActions.andReturn().getRequest().getRequestURL().toString();
    }

    private List<OntologyDocument> createAndSaveDocs(int n) {
        final List<OntologyDocument> nDocs = createNDocs(n);
        ontologyRepository.save(nDocs);
        setupSimpleRelationshipChain(n);
        return nDocs;
    }
}
