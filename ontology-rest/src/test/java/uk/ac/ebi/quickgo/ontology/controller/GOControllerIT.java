package uk.ac.ebi.quickgo.ontology.controller;

import uk.ac.ebi.quickgo.ontology.common.OntologyDocument;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyDocMocker;
import uk.ac.ebi.quickgo.ontology.model.OntologyRelationType;
import uk.ac.ebi.quickgo.ontology.model.OntologyRelationship;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.ac.ebi.quickgo.common.converter.HelpfulConverter.toCSV;
import static uk.ac.ebi.quickgo.ontology.controller.GOController.MISSING_SLIM_SET_ERROR_MESSAGE;
import static uk.ac.ebi.quickgo.ontology.controller.OBOController.COMPLETE_SUB_RESOURCE;
import static uk.ac.ebi.quickgo.ontology.controller.OBOController.CONSTRAINTS_SUB_RESOURCE;

/**
 * Tests the {@link GOController} class. All tests for GO
 * are covered by the tests in the parent class, {@link OBOControllerIT}.
 *
 * Created 16/11/15
 * @author Edd
 */
public class GOControllerIT extends OBOControllerIT {

    private static final String RESOURCE_URL = "/ontology/go";
    private static final String GO_0000001 = "GO:0000001";
    private static final String GO_0000002 = "GO:0000002";
    private static final String GO_0000003 = "GO:0000003";
    private static final String GO_0000004 = "GO:0000004";

    private static final String GO_SLIM_CHILD1 = "GO:9000001";
    private static final String GO_SLIM_CHILD2 = "GO:9000002";
    private static final String GO_SLIM_CHILD3 = "GO:9000003";
    private static final String GO_SLIM_CHILD4 = "GO:9000004";
    private static final String GO_SLIM_CHILD5 = "GO:9000005";
    private static final String GO_SLIM_CHILD6 = "GO:9000006";
    private static final String GO_SLIM_CHILD8 = "GO:9000008";
    private static final String GO_SLIM_CHILD9 = "GO:9000009";

    private static final String GO_SLIM1 = "GO:1111111";
    private static final String GO_SLIM2 = "GO:2222222";
    private static final String GO_SLIM3 = "GO:3333333";
    private static final String GO_SLIM4 = "GO:4444444";
    private static final String GO_SLIM5 = "GO:5555555";
    private static final String GO_SLIM6 = "GO:6666666";
    private static final String GO_SLIM7 = "GO:7777777";
    private static final String GO_SLIM8 = "GO:8888888";
    private static final String GO_SLIM9 = "GO:9999999";
    private static final String NON_EXISTENT_TERM = "GO:8787878";

    private static final String SLIM_RESOURCE = "/slim";
    private static final String SLIM_TO_IDS_PARAM = "slimsToIds";
    private static final String SLIM_FROM_IDS_PARAM = "slimsFromIds";
    private static final String SLIM_RELATIONS_PARAM = "relations";
    private static final String STOP_NODE = "GO:0008150";
    private static final String NO_VALID_SLIM_TERMS_ERROR_MESSAGE = "Requested slim-set contains no valid terms";

    @Before
    public void setUp() {
        setupGOForSlimTests();
    }

    // GO specific data  ------------------
    @Test
    public void canRetrieveBlacklistByIds() throws Exception {
        ResultActions response = mockMvc.perform(get(
                buildTermsURLWithSubResource(toCSV(GO_0000001, GO_0000002), CONSTRAINTS_SUB_RESOURCE)));

        expectBasicFieldsInResults(response, asList(GO_0000001, GO_0000002))
                .andExpect(jsonPath("$.results.*.blacklist", hasSize(2)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveGoDiscussions() throws Exception {
        ResultActions response = mockMvc.perform(get(buildTermsURLWithSubResource(GO_0000001, COMPLETE_SUB_RESOURCE)));

        expectBasicFieldsInResults(response, singletonList(GO_0000001))
                .andExpect(jsonPath("$.results.*.goDiscussions", hasSize(1)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // meta-data ------------------
    @Test
    public void about() throws Exception {
        ResultActions response = mockMvc.perform(get(getResourceURL() + "/about"));
        final String expectedVersion = "http://purl.obolibrary.org/obo/go/releases/2017-01-12/go.owl";
        final String expectedTimestamp = "2017-01-13 02:19";
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.go.version").value(expectedVersion))
                .andExpect(jsonPath("$.go.timestamp").value(expectedTimestamp));
    }

    // slimming ------------------
    @Test
    public void slimmingFromAndToTheSameTermReturnsIdentitySlim() throws Exception {
        ResultActions response = mockMvc.perform(get(getSlimURL())
                .param(SLIM_TO_IDS_PARAM, GO_SLIM1)
                .param(SLIM_FROM_IDS_PARAM, GO_SLIM1));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.numberOfHits").value(1));

        expectIdentitySlims(response, singletonList(GO_SLIM1));
    }

    @Test
    public void oneIdHasOneSlim() throws Exception {
        ResultActions response = mockMvc.perform(get(getSlimURL())
                .param(SLIM_TO_IDS_PARAM, GO_SLIM1));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.numberOfHits").value(2));

        expectSlimInfo(response, GO_SLIM_CHILD1, singletonList(GO_SLIM1));
        expectIdentitySlims(response, singletonList(GO_SLIM1));
    }

    @Test
    public void oneIdAndOneNonExistingIdHasOneSlim() throws Exception {
        ResultActions response = mockMvc.perform(get(getSlimURL())
                .param(SLIM_TO_IDS_PARAM, toCSV(GO_SLIM1, NON_EXISTENT_TERM)));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.numberOfHits").value(2));

        expectSlimInfo(response, GO_SLIM_CHILD1, singletonList(GO_SLIM1));
        expectIdentitySlims(response, singletonList(GO_SLIM1));
    }

    @Test
    public void oneIdHasTwoSlims() throws Exception {
        ResultActions response = mockMvc.perform(get(getSlimURL())
                .param(SLIM_TO_IDS_PARAM, GO_SLIM2));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.numberOfHits").value(2));
        expectSlimInfo(response, GO_SLIM_CHILD2, singletonList(GO_SLIM2));
        expectIdentitySlims(response, singletonList(GO_SLIM2));

        response = mockMvc.perform(get(getSlimURL())
                .param(SLIM_TO_IDS_PARAM, GO_SLIM3));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.numberOfHits").value(2));
        expectSlimInfo(response, GO_SLIM_CHILD2, singletonList(GO_SLIM3));
        expectIdentitySlims(response, singletonList(GO_SLIM3));
    }

    @Test
    public void twoIdsHave1Slim() throws Exception {
        ResultActions response = mockMvc.perform(get(getSlimURL())
                .param(SLIM_TO_IDS_PARAM, GO_SLIM4));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.numberOfHits").value(3));

        expectSlimInfo(response, GO_SLIM_CHILD3, singletonList(GO_SLIM4));
        expectSlimInfo(response, GO_SLIM_CHILD4, singletonList(GO_SLIM4));
        expectIdentitySlims(response, singletonList(GO_SLIM4));
    }

    @Test
    public void twoIdsHave2Slims() throws Exception {
        ResultActions response = mockMvc.perform(get(getSlimURL())
                .param(SLIM_TO_IDS_PARAM, toCSV(GO_SLIM5, GO_SLIM6)));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.numberOfHits").value(4));

        expectSlimInfo(response, GO_SLIM_CHILD5, asList(GO_SLIM5, GO_SLIM6));
        expectSlimInfo(response, GO_SLIM_CHILD6, asList(GO_SLIM5, GO_SLIM6));
        expectIdentitySlims(response, asList(GO_SLIM5, GO_SLIM6));
    }

    @Test
    public void twoIdsHave2SlimsButOnlyOneIsRequested() throws Exception {
        ResultActions response = mockMvc.perform(get(getSlimURL())
                .param(SLIM_TO_IDS_PARAM, toCSV(GO_SLIM5, GO_SLIM6))
                .param(SLIM_FROM_IDS_PARAM, GO_SLIM_CHILD5));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.numberOfHits").value(1));

        expectSlimInfo(response, GO_SLIM_CHILD5, asList(GO_SLIM5, GO_SLIM6));
    }

    @Test
    public void twoIdsHave2SlimsButNoneAreRequested() throws Exception {
        ResultActions response = mockMvc.perform(get(getSlimURL())
                .param(SLIM_TO_IDS_PARAM, toCSV(GO_SLIM5, GO_SLIM6))
                .param(SLIM_FROM_IDS_PARAM, NON_EXISTENT_TERM));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.numberOfHits").value(0));
    }

    @Test
    public void slimmingWithEmptyIDsCausesCauses400() throws Exception {
        ResultActions response = mockMvc.perform(get(getSlimURL())
                .param(SLIM_TO_IDS_PARAM, ""));

        expectResponseCreationError(response, MISSING_SLIM_SET_ERROR_MESSAGE)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void slimmingWithNoValidIDsCausesCauses400() throws Exception {
        ResultActions response = mockMvc.perform(get(getSlimURL())
                .param(SLIM_TO_IDS_PARAM, NON_EXISTENT_TERM));

        expectResponseCreationError(response, NO_VALID_SLIM_TERMS_ERROR_MESSAGE)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void slimmingWithInvalidFromIdButValidToIdProduces400() throws Exception {
        ResultActions response = mockMvc.perform(get(getSlimURL())
                .param(SLIM_FROM_IDS_PARAM, invalidId())
                .param(SLIM_TO_IDS_PARAM, GO_SLIM1));

        expectInvalidIdError(response, invalidId());
    }

    @Test
    public void slimmingWithFromIdButNoToIdsProduces400() throws Exception {
        ResultActions response = mockMvc.perform(get(getSlimURL())
                .param(SLIM_FROM_IDS_PARAM, GO_SLIM_CHILD1));

        expectResponseCreationError(response, MISSING_SLIM_SET_ERROR_MESSAGE)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void slimmingWithFromIdButToIdIsInvalidProduces400() throws Exception {
        ResultActions response = mockMvc.perform(get(getSlimURL())
                .param(SLIM_FROM_IDS_PARAM, GO_SLIM_CHILD1)
                .param(SLIM_TO_IDS_PARAM, invalidId()));

        expectInvalidIdError(response, invalidId());
    }

    @Test
    public void slimmingWithInvalidFromAndInvalidToIdProduces400() throws Exception {
        ResultActions response = mockMvc.perform(get(getSlimURL())
                .param(SLIM_FROM_IDS_PARAM, invalidId())
                .param(SLIM_TO_IDS_PARAM, invalidId() + "XXX"));

        expectInvalidIdError(response, invalidId());
    }

    @Test
    public void slimmingOverInvalidRelationshipCauses400() throws Exception {
        String invalidRelation = "XXXX";
        ResultActions response = mockMvc.perform(get(getSlimURL())
                .param(SLIM_TO_IDS_PARAM, GO_SLIM1)
                .param(SLIM_RELATIONS_PARAM, invalidRelation));

        expectInvalidRelationError(response, invalidRelation)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void slimmingOverRelationshipNotInGraphReturnsIdentitySlim() throws Exception {
        String nonExistentGraphRelationship = "occurs_in";
        ResultActions response = mockMvc.perform(get(getSlimURL())
                .param(SLIM_TO_IDS_PARAM, GO_SLIM1)
                .param(SLIM_RELATIONS_PARAM, nonExistentGraphRelationship));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.numberOfHits").value(1));

        expectIdentitySlims(response, asList(GO_SLIM1, GO_SLIM1));
    }

    @Test
    public void canSlimWhereRelationshipIsRegulates() throws Exception {
        ResultActions response = mockMvc.perform(get(getSlimURL())
                .param(SLIM_TO_IDS_PARAM, GO_SLIM8));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.numberOfHits").value(2));
        expectSlimInfo(response, GO_SLIM_CHILD8, asList(GO_SLIM8));
    }

    @Test
    public void canSlimWhereRelationshipIsASubclassOfRegulates() throws Exception {
        ResultActions response = mockMvc.perform(get(getSlimURL())
                .param(SLIM_TO_IDS_PARAM, GO_SLIM9));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.numberOfHits").value(2));
        expectSlimInfo(response, GO_SLIM_CHILD9, asList(GO_SLIM9));
    }

    @Override
    protected String getResourceURL() {
        return RESOURCE_URL;
    }

    @Override protected OntologyDocument createBasicDoc(String id, String name) {
        return OntologyDocMocker.createGODoc(id, name);
    }

    @Override protected String getValidRelations() {
        return "part_of";
    }

    @Override protected String getInvalidRelations() {
        return "used_in";
    }

    @Override
    protected List<OntologyDocument> createBasicDocs() {
        return asList(
                OntologyDocMocker.createGODoc(GO_0000001, "doc name 1"),
                OntologyDocMocker.createGODoc(GO_0000002, "doc name 2"),
                OntologyDocMocker.createGODoc(GO_0000003, "doc name 3"),
                OntologyDocMocker.createGODoc(GO_0000004, "doc name 4"));
    }

    @Override protected List<OntologyDocument> createNDocs(int n) {
        return IntStream.range(1, n + 1)
                .mapToObj(i -> OntologyDocMocker.createGODoc(createId(i), "go doc name " + i)).collect
                        (Collectors.toList());
    }

    @Override
    protected String idMissingInRepository() {
        return "GO:0000399";
    }

    @Override
    protected String invalidId() {
        return "GO|0000001";
    }

    @Override
    protected String createId(int idNum) {
        return String.format("GO:%07d", idNum);
    }

    /*
     * GO produces two more attributes in its response (aspect and usage), when compared
     * to the standard OBO response.
     */
    @Override
    protected ResultActions expectCoreFields(ResultActions result, String id) throws Exception {
        return super
                .expectCoreFields(result, id)
                .andExpect(jsonPath("$.aspect").value("Biological Process"))
                .andExpect(jsonPath("$.usage").value("Unrestricted"));
    }

    /**
     * Adds to the gene ontology graph several GO term relationships that are used in testing the
     * behaviour of the slimming resource.
     */
    private void setupGOForSlimTests() {
        List<OntologyRelationship> relationships = new ArrayList<>();

        relationships.add(createSlimRelationship(GO_SLIM_CHILD1, GO_SLIM1));
        relationships.add(createSlimRelationship(GO_SLIM_CHILD2, GO_SLIM2));
        relationships.add(createSlimRelationship(GO_SLIM_CHILD2, GO_SLIM3));
        relationships.add(createSlimRelationship(GO_SLIM_CHILD3, GO_SLIM4));
        relationships.add(createSlimRelationship(GO_SLIM_CHILD4, GO_SLIM4));
        relationships.add(createSlimRelationship(GO_SLIM_CHILD5, GO_SLIM5));
        relationships.add(createSlimRelationship(GO_SLIM_CHILD5, GO_SLIM6));
        relationships.add(createSlimRelationship(GO_SLIM_CHILD6, GO_SLIM5));
        relationships.add(createSlimRelationship(GO_SLIM_CHILD6, GO_SLIM6));

        relationships.add(createSlimRelationship(GO_SLIM1, STOP_NODE));
        relationships.add(createSlimRelationship(GO_SLIM2, STOP_NODE));
        relationships.add(createSlimRelationship(GO_SLIM3, STOP_NODE));
        relationships.add(createSlimRelationship(GO_SLIM4, STOP_NODE));
        relationships.add(createSlimRelationship(GO_SLIM5, STOP_NODE));
        relationships.add(createSlimRelationship(GO_SLIM6, STOP_NODE));
        relationships.add(createSlimRelationship(GO_SLIM7, STOP_NODE));

        relationships.add(new OntologyRelationship(GO_SLIM_CHILD8, GO_SLIM8, OntologyRelationType.REGULATES));
        relationships.add(new OntologyRelationship(GO_SLIM_CHILD9, GO_SLIM9, OntologyRelationType.NEGATIVE_REGULATES));
        ontologyGraph.addRelationships(relationships);
    }

    private void expectIdentitySlims(ResultActions response, List<String> slimSet) throws Exception {
        for (String slim : slimSet) {
            expectSlimInfo(response, slim, singletonList(slim));
        }
    }

    private void expectSlimInfo(ResultActions response, String from, List<String> to) throws Exception {
        response.andExpect(
                jsonPath("$..results[?(@.slimsFromId==\"" + from + "\")].slimsToIds.*",
                        equalTo(to)));
    }

    private OntologyRelationship createSlimRelationship(String term, String slimmedUpTerm) {
        return new OntologyRelationship(term, slimmedUpTerm, OntologyRelationType.IS_A);
    }

    private String getSlimURL() {
        return getResourceURL() + "/" + SLIM_RESOURCE;
    }
}
