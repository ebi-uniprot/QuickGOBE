package uk.ac.ebi.quickgo.annotation.controller;

import uk.ac.ebi.quickgo.annotation.AnnotationREST;
import uk.ac.ebi.quickgo.annotation.common.AnnotationDocument;
import uk.ac.ebi.quickgo.annotation.common.AnnotationRepository;
import uk.ac.ebi.quickgo.common.solr.TemporarySolrDataStore;
import uk.ac.ebi.quickgo.ontology.common.OntologyRepoConfig;

import java.util.StringJoiner;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.ac.ebi.quickgo.annotation.IdGeneratorUtil.createGPId;
import static uk.ac.ebi.quickgo.annotation.controller.ResponseVerifier.*;

/**
 * Tests filter parameters in the {@link AnnotationController} that require REST requests to perform
 * joins on annotation data.
 *
 * Created 02/11/16
 * @author Edd
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {AnnotationREST.class, OntologyRepoConfig.class})
@WebAppConfiguration
public abstract class AbstractFilterAnnotationByOntologyRESTIT {
    // temporary data store for solr's data, which is automatically cleaned on exit
    @ClassRule
    public static final TemporarySolrDataStore solrDataStore = new TemporarySolrDataStore();

    static final String FAILED_REST_FETCH_PREFIX = "Failed to fetch REST response due to: ";
    static final String IS_A = "is_a";
    static final String SLIM_USAGE = "slim";
    static final String NO_DESCENDANTS_PREFIX = "No descendants found for IDs, ";
    static final String SEARCH_RESOURCE = "/annotation" + "/search";

    private static final String DELIMITER = ", ";
    private static final String DESCENDANTS_USAGE = "descendants";

    MockMvc mockMvc;
    String resourceFormat;
    String usageParam;
    String idParam;
    String usageRelations;

    @Autowired
    protected AnnotationRepository annotationRepository;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.
                webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    public void filterFor1TermBy0ValidDescendantMeansFilterEverything() throws Exception {
        annotationRepository.save(createAnnotationDocWithId(createGPId(1), ontologyId(1)));
        annotationRepository.save(createAnnotationDocWithId(createGPId(2), ontologyId(2)));

        restTestSupport().expectRestCallHasDescendants(singletonList(ontologyId(1)), emptyList(), singletonList
                (emptyList()));

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(usageParam, DESCENDANTS_USAGE)
                        .param(idParam, ontologyId(1)));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(pageInfoExists())
                .andExpect(totalNumOfResults(0));
    }

    @Test
    public void filterFor1TermBy1ValidDescendant() throws Exception {
        annotationRepository.save(createAnnotationDocWithId(createGPId(1), ontologyId(1)));
        annotationRepository.save(createAnnotationDocWithId(createGPId(2), ontologyId(2)));
        annotationRepository.save(createAnnotationDocWithId(createGPId(3), ontologyId(3)));

        restTestSupport().expectRestCallHasDescendants(singletonList(ontologyId(1)), emptyList(),
                singletonList(singletonList(ontologyId(3))));

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(usageParam, DESCENDANTS_USAGE)
                        .param(idParam, ontologyId(1)));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(pageInfoExists())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldsInAllResultsExist(1))
                .andExpect(fieldDoesNotExist(SLIMMED_ID_FIELD))
                .andExpect(valuesOccurInField(idParam, ontologyId(3)));
    }

    @Test
    public void filterFor1TermBy2ValidDescendants() throws Exception {
        annotationRepository.save(createAnnotationDocWithId(createGPId(1), ontologyId(1)));
        annotationRepository.save(createAnnotationDocWithId(createGPId(2), ontologyId(2)));
        annotationRepository.save(createAnnotationDocWithId(createGPId(3), ontologyId(3)));

        restTestSupport().expectRestCallHasDescendants(
                singletonList(ontologyId(1)),
                singletonList(IS_A),
                singletonList(asList(ontologyId(2), ontologyId(3))));

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(usageParam, DESCENDANTS_USAGE)
                        .param(idParam, ontologyId(1))
                        .param(usageRelations, IS_A));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(pageInfoExists())
                .andExpect(totalNumOfResults(2))
                .andExpect(fieldsInAllResultsExist(2))
                .andExpect(fieldDoesNotExist(SLIMMED_ID_FIELD))
                .andExpect(valuesOccurInField(idParam, ontologyId(2), ontologyId(3)));
    }

    @Test
    public void filterFor2TermsBy0ValidDescendantsMeansFilterEverything() throws Exception {
        annotationRepository.save(createAnnotationDocWithId(createGPId(1), ontologyId(1)));
        annotationRepository.save(createAnnotationDocWithId(createGPId(2), ontologyId(2)));
        annotationRepository.save(createAnnotationDocWithId(createGPId(3), ontologyId(3)));

        restTestSupport().expectRestCallHasDescendants(asList(ontologyId(1), ontologyId(2)), singletonList(IS_A),
                asList(emptyList(), emptyList()));

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(usageParam, DESCENDANTS_USAGE)
                        .param(idParam, ontologyId(1) + "," + ontologyId(2))
                        .param(usageRelations, IS_A));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(pageInfoExists())
                .andExpect(fieldDoesNotExist(SLIMMED_ID_FIELD))
                .andExpect(totalNumOfResults(0));
    }

    @Test
    public void filterFor2TermsBy1ValidDescendant() throws Exception {
        annotationRepository.save(createAnnotationDocWithId(createGPId(1), ontologyId(1)));
        annotationRepository.save(createAnnotationDocWithId(createGPId(2), ontologyId(2)));
        annotationRepository.save(createAnnotationDocWithId(createGPId(3), ontologyId(3)));

        restTestSupport().expectRestCallHasDescendants(
                asList(ontologyId(1), ontologyId(2)),
                emptyList(),
                asList(singletonList(ontologyId(3)), singletonList(ontologyId(3))));

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(usageParam, DESCENDANTS_USAGE)
                        .param(idParam, csv(ontologyId(1), ontologyId(2))));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(pageInfoExists())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldsInAllResultsExist(1))
                .andExpect(fieldDoesNotExist(SLIMMED_ID_FIELD))
                .andExpect(valuesOccurInField(idParam, ontologyId(3)));
    }

    @Test
    public void filterFor2TermsBy2ValidDescendants() throws Exception {
        annotationRepository.save(createAnnotationDocWithId(createGPId(1), ontologyId(1)));
        annotationRepository.save(createAnnotationDocWithId(createGPId(2), ontologyId(2)));
        annotationRepository.save(createAnnotationDocWithId(createGPId(3), ontologyId(3)));
        annotationRepository.save(createAnnotationDocWithId(createGPId(4), ontologyId(4)));

        restTestSupport().expectRestCallHasDescendants(
                asList(ontologyId(1), ontologyId(2)),
                emptyList(),
                asList(
                        singletonList(ontologyId(3)),
                        singletonList(ontologyId(4))));

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(usageParam, DESCENDANTS_USAGE)
                        .param(idParam, csv(ontologyId(1), ontologyId(2))));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(pageInfoExists())
                .andExpect(totalNumOfResults(2))
                .andExpect(fieldsInAllResultsExist(2))
                .andExpect(fieldDoesNotExist(SLIMMED_ID_FIELD))
                .andExpect(valuesOccurInField(idParam, ontologyId(3), ontologyId(4)));
    }

    @Test
    public void termWithNullDescendantsProducesErrorMessage() throws Exception {
        restTestSupport().expectRestCallHasDescendants(singletonList(ontologyId(1)), emptyList(), singletonList(null));

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(usageParam, DESCENDANTS_USAGE)
                        .param(idParam, ontologyId(1)));

        response.andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(contentTypeToBeJson())
                .andExpect(valuesOccurInErrorMessage(
                        failedRESTResponseErrorMessage(NO_DESCENDANTS_PREFIX + ontologyId(1))));
    }

    @Test
    public void termWithOneNullDescendantsListAndOneValidDescendantsProducesError() throws Exception {
        restTestSupport().expectRestCallHasDescendants(
                asList(ontologyId(1), ontologyId(2)),
                emptyList(),
                asList(
                        singletonList(ontologyId(3)),
                        null));

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(usageParam, DESCENDANTS_USAGE)
                        .param(idParam, csv(ontologyId(1), ontologyId(2))));

        response.andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(contentTypeToBeJson())
                .andExpect(valuesOccurInErrorMessage(
                        failedRESTResponseErrorMessage(NO_DESCENDANTS_PREFIX + ontologyId(2))));
    }

    @Test
    public void termWithTwoNullDescendantsListAndOneValidDescendantsProducesErrorShowingBothIds() throws Exception {
        restTestSupport().expectRestCallHasDescendants(
                asList(ontologyId(1), ontologyId(2), ontologyId(3)),
                emptyList(),
                asList(
                        singletonList(ontologyId(4)),
                        null,
                        null));

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(usageParam, DESCENDANTS_USAGE)
                        .param(idParam, csv(ontologyId(1), ontologyId(2), ontologyId(3))));

        response.andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(contentTypeToBeJson())
                .andExpect(valuesOccurInErrorMessage(
                        failedRESTResponseErrorMessage(NO_DESCENDANTS_PREFIX + csv(ontologyId(2),
                                ontologyId(3)))));
    }

    @Test
    public void oneTermWithOneDescendantIdThatIsNullAndOneNonNullSucceeds() throws Exception {
        annotationRepository.save(createAnnotationDocWithId(createGPId(2), ontologyId(2)));

        restTestSupport().expectRestCallHasDescendants(
                singletonList(ontologyId(1)),
                emptyList(),
                singletonList(
                        asList(ontologyId(2), null)));

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(usageParam, DESCENDANTS_USAGE)
                        .param(idParam, ontologyId(1)));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(valuesOccurInField(idParam, ontologyId(2)));
    }

    @Test
    public void wrongResourcePathForDescendantsCausesErrorMessage() throws Exception {
        annotationRepository.save(createAnnotationDocWithId(createGPId(1), ontologyId(1)));
        annotationRepository.save(createAnnotationDocWithId(createGPId(2), ontologyId(2)));

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(usageParam, DESCENDANTS_USAGE)
                        .param(idParam, ontologyId(1)));

        response.andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(contentTypeToBeJson())
                .andExpect(valueStartingWithOccursInErrorMessage(FAILED_REST_FETCH_PREFIX));
    }

    @Test
    public void fetchTimeoutForDescendantFilteringCauses500() throws Exception {
        annotationRepository.save(createAnnotationDocWithId(createGPId(1), ontologyId(1)));
        annotationRepository.save(createAnnotationDocWithId(createGPId(2), ontologyId(2)));

        restTestSupport().expectRestCallResponse(GET, restTestSupport().buildResource(resourceFormat, ontologyId(1)), withTimeout());

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(usageParam, DESCENDANTS_USAGE)
                        .param(idParam, ontologyId(1)));

        response.andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(contentTypeToBeJson())
                .andExpect(valueStartingWithOccursInErrorMessage(FAILED_REST_FETCH_PREFIX));
    }

    @Test
    public void serverErrorForDescendantFilteringCauses500() throws Exception {
        annotationRepository.save(createAnnotationDocWithId(createGPId(1), ontologyId(1)));
        annotationRepository.save(createAnnotationDocWithId(createGPId(2), ontologyId(2)));

        restTestSupport().expectRestCallResponse(GET, restTestSupport().buildResource(resourceFormat, ontologyId(1)), withServerError());

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(usageParam, DESCENDANTS_USAGE)
                        .param(idParam, ontologyId(1)));

        response.andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(contentTypeToBeJson())
                .andExpect(valueStartingWithOccursInErrorMessage(FAILED_REST_FETCH_PREFIX));
    }

    @Test
    public void badRequestForDescendantFilteringCauses500() throws Exception {
        annotationRepository.save(createAnnotationDocWithId(createGPId(1), ontologyId(1)));
        annotationRepository.save(createAnnotationDocWithId(createGPId(2), ontologyId(2)));

        restTestSupport().expectRestCallResponse(GET, restTestSupport().buildResource(resourceFormat, ontologyId(1)), withBadRequest());

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(usageParam, DESCENDANTS_USAGE)
                        .param(idParam, ontologyId(1)));

        response.andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(contentTypeToBeJson())
                .andExpect(valueStartingWithOccursInErrorMessage(FAILED_REST_FETCH_PREFIX));
    }

    @After
    public void deleteCoreContents() {
        annotationRepository.deleteAll();
    }

    protected abstract AnnotationDocument createAnnotationDocWithId(String geneProductId, String id);

    protected abstract String ontologyId(int id);

    static String csv(String... values) {
        StringJoiner joiner = new StringJoiner(DELIMITER);
        for (String value : values) {
            joiner.add(value);
        }
        return joiner.toString();
    }

    String failedRESTResponseErrorMessage(String suffix) {
        return FAILED_REST_FETCH_PREFIX + suffix;
    }

    abstract RestTestSupport restTestSupport();
}
