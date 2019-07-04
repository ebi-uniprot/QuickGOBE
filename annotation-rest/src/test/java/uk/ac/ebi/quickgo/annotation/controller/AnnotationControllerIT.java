package uk.ac.ebi.quickgo.annotation.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import uk.ac.ebi.quickgo.annotation.AnnotationREST;
import uk.ac.ebi.quickgo.annotation.common.AnnotationDocument;
import uk.ac.ebi.quickgo.annotation.common.AnnotationRepository;
import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocMocker;
import uk.ac.ebi.quickgo.annotation.model.AnnotationRequestBody;
import uk.ac.ebi.quickgo.common.store.TemporarySolrDataStore;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.ac.ebi.quickgo.annotation.AnnotationParameters.*;
import static uk.ac.ebi.quickgo.annotation.IdGeneratorUtil.createGPId;
import static uk.ac.ebi.quickgo.annotation.IdGeneratorUtil.createGoId;
import static uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocMocker.EXTENSIONS;
import static uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocMocker.createGenericDocsChangingGoId;
import static uk.ac.ebi.quickgo.annotation.controller.ResponseVerifier.*;
import static uk.ac.ebi.quickgo.annotation.controller.ResponseVerifier.ResponseItem.responseItem;
import static uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelperImpl.*;
import static uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery.SELECT_ALL_WHERE_FIELD_IS_NOT_EMPTY;

/**
 * RESTful end point for Annotations
 * @author Tony Wardell
 * Date: 26/04/2016
 * Time: 14:21
 * Created with IntelliJ IDEA.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {AnnotationREST.class})
@WebAppConfiguration
public class AnnotationControllerIT {

    // temporary data store for solr's data, which is automatically cleaned on exit
    @ClassRule
    public static final TemporarySolrDataStore solrDataStore = new TemporarySolrDataStore();
    public static final String DATE_STRING_FORMAT = "%04d%02d%02d";
    public static final String EXACT = "exact";
    public static final String DESCENDANTS = "descendants";
    //Test Data
    private static final String MISSING_ASSIGNED_BY = "ZZZZZ";
    private static final String RESOURCE_URL = "/annotation";
    private static final String MISSING_GO_ID = "GO:0009871";
    private static final String INVALID_GO_ID = "GO:1";
    private static final String ECO_ID2 = "ECO:0000323";
    private static final String MISSING_ECO_ID = "ECO:0000888";
    private static final String WITH_FROM_PATH = "withFrom.*.connectedXrefs";
    //Configuration
    private static final int NUMBER_OF_GENERIC_DOCS = 3;
    public static final String EXACT_USAGE = "exact";
    private MockMvc mockMvc;
    private List<AnnotationDocument> genericDocs;
    private MockRestServiceServer mockRestServiceServer;
    @Autowired
    private RestOperations restOperations;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AnnotationRepository repository;

    @Before
    public void setup() {
        repository.deleteAll();

        mockMvc = MockMvcBuilders.
                webAppContextSetup(webApplicationContext)
                .build();

        genericDocs = createGenericDocs(NUMBER_OF_GENERIC_DOCS);
        repository.saveAll(genericDocs);
    }

    @Test
    public void annotationsReturnedInDefaultSortingOrder() throws Exception {
        repository.deleteAll();
        String geneProductId1 = "A0A000";
        String geneProductId2 = "CPX-102";
        String geneProductId3 = "URS00000064B1_559292";

        AnnotationDocument annotationDoc1 = AnnotationDocMocker.createAnnotationDoc(geneProductId1);
        AnnotationDocument annotationDoc2 = AnnotationDocMocker.createAnnotationDoc(geneProductId2);
        AnnotationDocument annotationDoc3 = AnnotationDocMocker.createAnnotationDoc(geneProductId3);

        repository.save(annotationDoc2);
        repository.save(annotationDoc3);
        repository.save(annotationDoc1);

        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search"));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(3))
                .andExpect(fieldInRowHasValue(GENEPRODUCT_ID_FIELD, 0, geneProductId1))
                .andExpect(fieldInRowHasValue(GENEPRODUCT_ID_FIELD, 1, geneProductId2))
                .andExpect(fieldInRowHasValue(GENEPRODUCT_ID_FIELD, 2, geneProductId3));

    }

    @Test
    public void whenDefaultSortFieldIsSame_annotationsReturnedInDefaultOrderWrittenToRepository() throws Exception {
        repository.deleteAll();
        String geneProductId1 = "A0A000";
        String geneProductId2 = "CPX-102";
        String geneProductId3 = "URS00000064B1_559292";

        final AnnotationDocument annotationDoc1 = AnnotationDocMocker.createAnnotationDoc(geneProductId1);
        annotationDoc1.defaultSort = "";
        final AnnotationDocument annotationDoc2 = AnnotationDocMocker.createAnnotationDoc(geneProductId2);
        annotationDoc2.defaultSort = "";
        final AnnotationDocument annotationDoc3 = AnnotationDocMocker.createAnnotationDoc(geneProductId3);
        annotationDoc3.defaultSort = "";

        repository.save(annotationDoc3);
        repository.save(annotationDoc2);
        repository.save(annotationDoc1);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search"));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(3))
                .andExpect(fieldInRowHasValue(GENEPRODUCT_ID_FIELD, 0, geneProductId3))
                .andExpect(fieldInRowHasValue(GENEPRODUCT_ID_FIELD, 1, geneProductId2))
                .andExpect(fieldInRowHasValue(GENEPRODUCT_ID_FIELD, 2, geneProductId1));

    }

    // ASSIGNED BY
    @Test
    public void lookupAnnotationFilterByAssignedBySuccessfully() throws Exception {
        String geneProductId = "P99999";
        String assignedBy = "ASPGD";

        AnnotationDocument document = createDocWithAssignedBy(geneProductId, assignedBy);
        repository.save(document);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(ASSIGNED_BY_PARAM.getName(), assignedBy));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldsInAllResultsExist(1))
                .andExpect(valuesOccurInField(GENEPRODUCT_ID_FIELD, geneProductId));
    }

    @Test
    public void lookupAnnotationFilterByMultipleAssignedBySuccessfully() throws Exception {
        String geneProductId1 = "P99999";
        String assignedBy1 = "ASPGD";

        AnnotationDocument document1 = createDocWithAssignedBy(geneProductId1, assignedBy1);
        repository.save(document1);

        String geneProductId2 = "P99998";
        String assignedBy2 = "BHF-UCL";

        AnnotationDocument document2 = createDocWithAssignedBy(geneProductId2, assignedBy2);
        repository.save(document2);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(ASSIGNED_BY_PARAM.getName(), assignedBy1 + "," + assignedBy2));

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(2))
                .andExpect(fieldsInAllResultsExist(2))
                .andExpect(valuesOccurInField(GENEPRODUCT_ID_FIELD, geneProductId2, geneProductId1));
    }

    @Test
    public void lookupAnnotationFilterByRepetitionOfParmsSuccessfully() throws Exception {
        String geneProductId1 = "P99999";
        String assignedBy1 = "ASPGD";

        AnnotationDocument document1 = createDocWithAssignedBy(geneProductId1, assignedBy1);
        repository.save(document1);

        String geneProductId2 = "P99998";
        String assignedBy2 = "BHF-UCL";

        AnnotationDocument document2 = createDocWithAssignedBy(geneProductId2, assignedBy2);
        repository.save(document2);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search")
                        .param(ASSIGNED_BY_PARAM.getName(), assignedBy1)
                        .param(ASSIGNED_BY_PARAM.getName(), assignedBy2));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(2))
                .andExpect(fieldsInAllResultsExist(2))
                .andExpect(valuesOccurInField(GENEPRODUCT_ID_FIELD, geneProductId2, geneProductId1));
    }

    @Test
    public void lookupAnnotationFilterByInvalidAssignedBy() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(ASSIGNED_BY_PARAM.getName(), MISSING_ASSIGNED_BY));

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(0));
    }

    @Test
    public void lookupAnnotationFilterByMultipleAssignedByOneCorrectAndOneUnavailable() throws Exception {
        String geneProductId = "P99999";
        String assignedBy = "ASPGD";

        AnnotationDocument document = createDocWithAssignedBy(geneProductId, assignedBy);
        repository.save(document);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(ASSIGNED_BY_PARAM.getName(), MISSING_ASSIGNED_BY + ","
                        + assignedBy));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldsInAllResultsExist(1))
                .andExpect(valuesOccurInField(GENEPRODUCT_ID_FIELD, geneProductId));
    }

    @Test
    public void invalidAssignedByThrowsAnError() throws Exception {
        String invalidAssignedBy = "_ASPGD";

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(ASSIGNED_BY_PARAM.getName(), invalidAssignedBy));

        response.andDo(print())
                .andExpect(status().isBadRequest());
    }

    // TAXON ID
    @Test
    public void lookupAnnotationFilterByTaxonIdSuccessfully() throws Exception {
        String geneProductId = "P99999";
        int taxonId = 2;

        AnnotationDocument document = createDocWithTaxonId(geneProductId, taxonId);
        repository.save(document);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search")
                        .param(TAXON_ID_PARAM.getName(), Integer.toString(taxonId))
                        .param(TAXON_USAGE_PARAM.getName(), EXACT));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldsInAllResultsExist(1))
                .andExpect(valuesOccursInField(TAXON_ID_FIELD, taxonId))
                .andExpect(valuesOccurInField(GENEPRODUCT_ID_FIELD, geneProductId));
    }

    @Test
    public void lookupAnnotationFilterByMultipleTaxonIdsSuccessfully() throws Exception {
        String geneProductId1 = "P99999";
        int taxonId1 = 2;

        AnnotationDocument document1 = createDocWithTaxonId(geneProductId1, taxonId1);
        repository.save(document1);

        String geneProductId2 = "P99998";
        int taxonId2 = 3;

        AnnotationDocument document2 = createDocWithTaxonId(geneProductId2, taxonId2);
        repository.save(document2);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search")
                        .param(TAXON_ID_PARAM.getName(), Integer.toString(taxonId1))
                        .param(TAXON_ID_PARAM.getName(), Integer.toString(taxonId2))
                        .param(TAXON_USAGE_PARAM.getName(), EXACT));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(2))
                .andExpect(fieldsInAllResultsExist(2))
                .andExpect(valuesOccursInField(TAXON_ID_FIELD, taxonId2, taxonId1))
                .andExpect(valuesOccurInField(GENEPRODUCT_ID_FIELD, geneProductId2, geneProductId1));
    }

    @Test
    public void lookupWhereTaxonIdIsZeroWillNotShowTaxonIdBecauseThisMeansInvalid() throws Exception {
        String geneProductId = "P99999";
        int taxonId = 0;

        AnnotationDocument document = createDocWithTaxonId(geneProductId, taxonId);
        repository.save(document);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search")
                        .param(GENE_PRODUCT_ID_PARAM.getName(), geneProductId));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldDoesNotExist(TAXON_ID_FIELD))
                .andExpect(valuesOccurInField(GENEPRODUCT_ID_FIELD, geneProductId));
    }

    @Test
    public void filterByMissingTaxonIdFindsNothingSuccessfully() throws Exception {
        int desiredTaxonId = 999999;

        String geneProductId = "P99999";
        int taxonId = 2;
        AnnotationDocument document = createDocWithTaxonId(geneProductId, taxonId);
        repository.save(document);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search")
                        .param(TAXON_ID_PARAM.getName(), Integer.toString(desiredTaxonId))
                        .param(TAXON_USAGE_PARAM.getName(), EXACT));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(0));
    }

    @Test
    public void invalidTaxIdThrowsError() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search")
                        .param(TAXON_ID_PARAM.getName(), "-2")
                        .param(TAXON_USAGE_PARAM.getName(), EXACT));

        response.andDo(print())
                .andExpect(status().isBadRequest());
    }

    // -- taxon descendant filtering
    @Test
    public void filterByTaxonAncestorSuccessfully() throws Exception {
        int taxonId = 3;
        int parentTaxonId = 4;
        int grandParentTaxonId = 5;

        List<AnnotationDocument> documents =
                createDocsWithTaxonAncestors(asList(taxonId, parentTaxonId, grandParentTaxonId));
        repository.saveAll(documents);

        String[] expectedGPIds = asArray(transformDocs(documents, d -> d.geneProductId));

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search")
                        .param(TAXON_ID_PARAM.getName(), Integer.toString(grandParentTaxonId))
                        .param(TAXON_USAGE_PARAM.getName(), DESCENDANTS));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(3))
                .andExpect(fieldsInAllResultsExist(3))
                .andExpect(valuesOccursInField(TAXON_ID_FIELD, taxonId, parentTaxonId, grandParentTaxonId))
                .andExpect(valuesOccurInField(GENEPRODUCT_ID_FIELD, expectedGPIds));
    }

    @Test
    public void filterByTaxonAncestorsSuccessfully() throws Exception {
        int taxonId1 = 3;
        int parentTaxonId1 = 4;
        int grandParentTaxonId1 = 5;

        int taxonId2 = 8;
        int parentTaxonId2 = 9;
        int grandParentTaxonId2 = 10;

        List<AnnotationDocument> documents =
                createDocsWithTaxonAncestors(asList(taxonId1, parentTaxonId1, grandParentTaxonId1));
        documents.addAll(createDocsWithTaxonAncestors(asList(taxonId2, parentTaxonId2, grandParentTaxonId2)));
        repository.saveAll(documents);

        Integer[] expectedTaxonIds = {taxonId1,
                parentTaxonId1,
                grandParentTaxonId1,
                taxonId2,
                parentTaxonId2};

        String[] expectedGPIds = asArray(
                transformDocs(
                        filterDocuments(
                                documents,
                                expectedTaxonIds,
                                doc -> doc.taxonId),
                        doc -> doc.geneProductId));

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search")
                        .param(TAXON_ID_PARAM.getName(),
                                Integer.toString(grandParentTaxonId1),
                                Integer.toString(parentTaxonId2))
                        .param(TAXON_USAGE_PARAM.getName(), DESCENDANTS));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(5))
                .andExpect(fieldsInAllResultsExist(5))
                .andExpect(valuesOccursInField(TAXON_ID_FIELD, expectedTaxonIds))
                .andExpect(valuesOccurInField(GENEPRODUCT_ID_FIELD, expectedGPIds));
    }

    @Test
    public void filterByMissingTaxonAncestorFindsNothingSuccessfully() throws Exception {
        int desiredTaxonId = 99999999;
        int taxonId = 3;
        int parentTaxonId = 4;
        int grandParentTaxonId = 5;

        List<AnnotationDocument> documents =
                createDocsWithTaxonAncestors(asList(taxonId, parentTaxonId, grandParentTaxonId));
        repository.saveAll(documents);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search")
                        .param(TAXON_ID_PARAM.getName(), Integer.toString(desiredTaxonId))
                        .param(TAXON_USAGE_PARAM.getName(), DESCENDANTS));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(0));
    }

    @Test
    public void invalidTaxIdWithDescendantsThrowsError() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search")
                        .param(TAXON_ID_PARAM.getName(), "-2")
                        .param(TAXON_USAGE_PARAM.getName(), DESCENDANTS));

        response.andDo(print())
                .andExpect(status().isBadRequest());
    }

    //---------- GoEvidence related tests.
    @Test
    public void filterAnnotationsByGoEvidenceCodeSuccessfully() throws Exception {
        String goEvidenceCode = "IEA";
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(GO_EVIDENCE_PARAM.getName(), goEvidenceCode));

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(NUMBER_OF_GENERIC_DOCS))
                .andExpect(fieldsInAllResultsExist(NUMBER_OF_GENERIC_DOCS))
                .andExpect(atLeastOneResultHasItem(GO_EVIDENCE_FIELD, goEvidenceCode));
    }

    @Test
    public void filterAnnotationsByLowercaseGoEvidenceCodeSuccessfully() throws Exception {
        String goEvidenceCode = "iea";
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(GO_EVIDENCE_PARAM.getName(), goEvidenceCode));

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(NUMBER_OF_GENERIC_DOCS))
                .andExpect(fieldsInAllResultsExist(NUMBER_OF_GENERIC_DOCS))
                .andExpect(atLeastOneResultHasItem(GO_EVIDENCE_FIELD, goEvidenceCode.toUpperCase()));
    }

    @Test
    public void filterAnnotationsByNonExistentGoEvidenceCodeReturnsNothing() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(GO_EVIDENCE_PARAM.getName(), "ZZZ"));

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(0));
    }

    @Test
    public void filterAnnotationsUsingMultipleGoEvidenceCodesSuccessfully() throws Exception {
        String goEvidenceCode = "IEA";

        String goEvidenceCode1 = "BSS";
        AnnotationDocument annoDoc1 = AnnotationDocMocker.createAnnotationDoc(createGPId(999));
        annoDoc1.goEvidence = goEvidenceCode1;
        repository.save(annoDoc1);

        String goEvidenceCode2 = "AWE";
        AnnotationDocument annoDoc2 = AnnotationDocMocker.createAnnotationDoc(createGPId(998));
        annoDoc2.goEvidence = goEvidenceCode2;
        repository.save(annoDoc2);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(GO_EVIDENCE_PARAM.getName(), "IEA,BSS,AWE,PEG"));

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(NUMBER_OF_GENERIC_DOCS + 2))
                .andExpect(fieldsInAllResultsExist(NUMBER_OF_GENERIC_DOCS + 2))
                .andExpect(itemExistsExpectedTimes(GO_EVIDENCE_FIELD, goEvidenceCode1, 1))
                .andExpect(itemExistsExpectedTimes(GO_EVIDENCE_FIELD, goEvidenceCode2, 1))
                .andExpect(itemExistsExpectedTimes(GO_EVIDENCE_FIELD, goEvidenceCode, NUMBER_OF_GENERIC_DOCS));
    }

    @Test
    public void invalidGoEvidenceThrowsException() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(GO_EVIDENCE_PARAM.getName(), "BlahBlah"));

        response.andExpect(status().isBadRequest())
                .andExpect(contentTypeToBeJson());

    }

    //---------- Qualifier related tests.
    @Test
    public void successfullyLookupAnnotationsByQualifier() throws Exception {
        String qualifier = "enables";
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(QUALIFIER_PARAM.getName(), qualifier));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(NUMBER_OF_GENERIC_DOCS))
                .andExpect(fieldsInAllResultsExist(NUMBER_OF_GENERIC_DOCS))
                .andExpect(valueOccursInField(QUALIFIER_FIELD, qualifier));

    }

    @Test
    public void successfullyLookupAnnotationsByNegatedQualifier() throws Exception {
        String qualifier = "not|enables";
        AnnotationDocument doc = AnnotationDocMocker.createAnnotationDoc("A0A123");
        doc.qualifier = qualifier;
        repository.save(doc);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(QUALIFIER_PARAM.getName(), qualifier));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldsInAllResultsExist(1))
                .andExpect(valueOccursInField(QUALIFIER_FIELD, qualifier));

    }

    @Test
    public void failToFindAnnotationsWhenQualifierDoesntExist() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(QUALIFIER_PARAM.getName(), "peeled"));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(0));

    }

    //---------- Search by Gene Product ID tests.
    @Test
    public void filterByGeneProductIDSuccessfully() throws Exception {
        String geneProductId = "A1E959";
        String fullGeneProductId = "UniProtKB:" + geneProductId;
        AnnotationDocument doc = AnnotationDocMocker.createAnnotationDoc(fullGeneProductId);
        repository.save(doc);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(GENE_PRODUCT_ID_PARAM.getName(), geneProductId));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldsInAllResultsExist(1))
                .andExpect(itemExistsExpectedTimes(GENEPRODUCT_ID_FIELD, fullGeneProductId, 1));
    }

    @Test
    public void filterByFullyQualifiedUniProtGeneProductIDSuccessfully() throws Exception {
        String geneProductId = "UniProtKB:A1E959";
        AnnotationDocument doc = AnnotationDocMocker.createAnnotationDoc(geneProductId);
        repository.save(doc);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(GENE_PRODUCT_ID_PARAM.getName(), geneProductId));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldsInAllResultsExist(1))
                .andExpect(itemExistsExpectedTimes(GENEPRODUCT_ID_FIELD, geneProductId, 1));
    }

    @Test
    public void filterByPartiallyQualifiedGeneProductIDSuccessfully() throws Exception {
        String db = "UniProtKB";
        String id = "A1E959";
        String geneProductId = db + ":" + id;
        AnnotationDocument doc = AnnotationDocMocker.createAnnotationDoc(geneProductId);
        repository.save(doc);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(GENE_PRODUCT_ID_PARAM.getName(), id));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldsInAllResultsExist(1))
                .andExpect(itemExistsExpectedTimes(GENEPRODUCT_ID_FIELD, geneProductId, 1));
    }

    // -- filter gene product ids of the form UniProtKB:P12345:VAR_12345 or UniProtKB:P12345:PRO_12345, etc
    @Test
    public void findGeneProductVarById() throws Exception {
        String db = "UniProtKB";
        String id = "P05067";
        String var = "VAR_000023";
        String geneProductId = db + ":" + id + ":" + var;
        AnnotationDocument doc = AnnotationDocMocker.createAnnotationDoc(geneProductId);
        repository.save(doc);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(GENE_PRODUCT_ID_PARAM.getName(), id));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldsInAllResultsExist(1))
                .andExpect(itemExistsExpectedTimes(GENEPRODUCT_ID_FIELD, geneProductId, 1));
    }

    @Test
    public void findTwoGeneProductsIncludingOneWithVarById() throws Exception {
        String db = "UniProtKB";
        String id = "P05067";
        String var = "VAR_000023";
        String geneProductId = db + ":" + id + ":" + var;
        AnnotationDocument doc1 = AnnotationDocMocker.createAnnotationDoc(db + ":" + id);
        AnnotationDocument doc2 = AnnotationDocMocker.createAnnotationDoc(geneProductId);
        repository.save(doc1);
        repository.save(doc2);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(GENE_PRODUCT_ID_PARAM.getName(), id));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(2))
                .andExpect(fieldsInAllResultsExist(2))
                .andExpect(itemExistsExpectedTimes(GENEPRODUCT_ID_FIELD, doc1.geneProductId, 1))
                .andExpect(itemExistsExpectedTimes(GENEPRODUCT_ID_FIELD, doc2.geneProductId, 1));
    }

    @Test
    public void findGeneProductVarByDbAndId() throws Exception {
        String db = "UniProtKB";
        String id = "P05067";
        String var = "VAR_000023";
        String geneProductId = db + ":" + id + ":" + var;
        AnnotationDocument doc = AnnotationDocMocker.createAnnotationDoc(geneProductId);
        repository.save(doc);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(GENE_PRODUCT_ID_PARAM.getName(), db + ":" + id));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldsInAllResultsExist(1))
                .andExpect(itemExistsExpectedTimes(GENEPRODUCT_ID_FIELD, geneProductId, 1));
    }

    @Test
    public void findGeneProductVarByDbIdAndVar() throws Exception {
        String db = "UniProtKB";
        String id = "P05068";
        String var = "VAR_000023";
        String geneProductId = db + ":" + id + ":" + var;
        AnnotationDocument doc = AnnotationDocMocker.createAnnotationDoc(geneProductId);
        repository.save(doc);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(GENE_PRODUCT_ID_PARAM.getName(), geneProductId));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldsInAllResultsExist(1))
                .andExpect(itemExistsExpectedTimes(GENEPRODUCT_ID_FIELD, geneProductId, 1));
    }

    @Test
    public void findGeneProductVarByIdAndVar() throws Exception {
        String db = "UniProtKB";
        String id = "P05067";
        String var = "VAR_000023";
        String idWithVar = id + ":" + var;
        String geneProductId = db + ":" + idWithVar;
        AnnotationDocument doc1 = AnnotationDocMocker.createAnnotationDoc(db + ":" + id);
        AnnotationDocument doc2 = AnnotationDocMocker.createAnnotationDoc(geneProductId);
        repository.save(doc1);
        repository.save(doc2);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(GENE_PRODUCT_ID_PARAM.getName(), idWithVar));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldsInAllResultsExist(1))
                .andExpect(itemExistsExpectedTimes(GENEPRODUCT_ID_FIELD, geneProductId, 1));
    }

    // -- filter gene product ids of the form UniProtKB:P12345-2
    @Test
    public void findGeneProductIsoformById() throws Exception {
        String db = "UniProtKB";
        String id = "P05067";
        String iso = "-2";
        String geneProductId = db + ":" + id + iso;
        AnnotationDocument doc = AnnotationDocMocker.createAnnotationDoc(geneProductId);
        repository.save(doc);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(GENE_PRODUCT_ID_PARAM.getName(), id));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldsInAllResultsExist(1))
                .andExpect(itemExistsExpectedTimes(GENEPRODUCT_ID_FIELD, geneProductId, 1));
    }

    @Test
    public void findGeneProductIsoformByDbAndId() throws Exception {
        String db = "UniProtKB";
        String id = "P05067";
        String iso = "-2";
        String dbWithId = db + ":" + id;
        String geneProductId = db + ":" + id + iso;
        AnnotationDocument doc = AnnotationDocMocker.createAnnotationDoc(geneProductId);
        repository.save(doc);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(GENE_PRODUCT_ID_PARAM.getName(), dbWithId));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldsInAllResultsExist(1))
                .andExpect(itemExistsExpectedTimes(GENEPRODUCT_ID_FIELD, geneProductId, 1));
    }

    @Test
    public void findGeneProductIsoformByIdAndIsoform() throws Exception {
        String db = "UniProtKB";
        String id = "P05067";
        String iso = "-2";
        String idWithIso = id + iso;
        String geneProductIdWithIsoform = db + ":" + idWithIso;
        AnnotationDocument docWithIsoform = AnnotationDocMocker.createAnnotationDoc(geneProductIdWithIsoform);
        AnnotationDocument docWithoutIsoform = AnnotationDocMocker.createAnnotationDoc(db + ":" + id);
        repository.save(docWithIsoform);
        repository.save(docWithoutIsoform);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(GENE_PRODUCT_ID_PARAM.getName(), idWithIso));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldsInAllResultsExist(1))
                .andExpect(itemExistsExpectedTimes(GENEPRODUCT_ID_FIELD, geneProductIdWithIsoform, 1));
    }

    @Test
    public void findGeneProductIsoformByDbIdAndIsoform() throws Exception {
        String db = "UniProtKB";
        String id = "P05068";
        String iso = "-2";
        String geneProductId = db + ":" + id + iso;
        AnnotationDocument doc = AnnotationDocMocker.createAnnotationDoc(geneProductId);
        repository.save(doc);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(GENE_PRODUCT_ID_PARAM.getName(), geneProductId));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldsInAllResultsExist(1))
                .andExpect(itemExistsExpectedTimes(GENEPRODUCT_ID_FIELD, geneProductId, 1));
    }

    // -- filter gene product ids of the form UniProtKB:P12345-2:VAR_12345 -- not a valid UniProt identifier
    @Test
    public void filteringUsingInvalidGeneProductIdCausesBadRequest() throws Exception {
        String geneProductId = "UniProtKB:P05068-2:VAR_000023";
        AnnotationDocument doc = AnnotationDocMocker.createAnnotationDoc(geneProductId);
        repository.save(doc);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(GENE_PRODUCT_ID_PARAM.getName(), geneProductId));

        response.andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void findIsoformEntryAndNonIsoformEntryById() throws Exception {
        String id = "P05067";
        String geneProductIdWithIso = "UniProtKB:P05067-2";
        String geneProductIdWithVar = "UniProtKB:P05067:PRO_0000005211";
        AnnotationDocument doc1 = AnnotationDocMocker.createAnnotationDoc(geneProductIdWithIso);
        AnnotationDocument doc2 = AnnotationDocMocker.createAnnotationDoc(geneProductIdWithVar);
        repository.save(doc1);
        repository.save(doc2);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(GENE_PRODUCT_ID_PARAM.getName(), id));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(2))
                .andExpect(fieldsInAllResultsExist(2))
                .andExpect(itemExistsExpectedTimes(GENEPRODUCT_ID_FIELD, geneProductIdWithIso, 1))
                .andExpect(itemExistsExpectedTimes(GENEPRODUCT_ID_FIELD, geneProductIdWithVar, 1));
    }

    @Test
    public void filterByUniProtKBAndIntactAndRNACentralAndComplexPortalWithCaseInsensitivity() throws
                                                                                               Exception {
        repository.deleteAll();
        String uniprotGp = "A0A000";
        String rnaGp = "URS00000064B1_559292";
        String complexPortalGp = "CPX-101";
        repository.save(AnnotationDocMocker.createAnnotationDoc(uniprotGp));
        repository.save(AnnotationDocMocker.createAnnotationDoc(rnaGp));
        repository.save(AnnotationDocMocker.createAnnotationDoc(complexPortalGp));
        StringJoiner sj = new StringJoiner(",");
        sj.add(uniprotGp).add(complexPortalGp).add(rnaGp);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(GENE_PRODUCT_ID_PARAM.getName(), sj.toString()));

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson()).andExpect(totalNumOfResults(3)).andExpect(fieldsInAllResultsExist(3))
                .andExpect(itemExistsExpectedTimes(GENEPRODUCT_ID_FIELD, uniprotGp, 1))
                .andExpect(itemExistsExpectedTimes(GENEPRODUCT_ID_FIELD, rnaGp, 1))
                .andExpect(itemExistsExpectedTimes(GENEPRODUCT_ID_FIELD, complexPortalGp, 1));
    }

    @Test
    public void filterByGeneProductUsingInvalidIDFailsValidation() throws Exception {
        String invalidGeneProductID = "99999";
        AnnotationDocument doc = AnnotationDocMocker.createAnnotationDoc(invalidGeneProductID);
        repository.save(doc);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(GENE_PRODUCT_ID_PARAM.getName(), invalidGeneProductID));

        response.andExpect(status().isBadRequest());
    }

    @Test
    public void filterByValidIdThatDoesNotExistExpectZeroResultsButNoError() throws Exception {
        String geneProductId = "Z0Z000";
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(GENE_PRODUCT_ID_PARAM.getName(), geneProductId));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(0));
    }

    @Test
    public void filterByThreeGeneProductIdsTwoOfWhichExistToEnsureTheyAreReturned() throws Exception {
        String geneProductId = "Z0Z000";
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(GENE_PRODUCT_ID_PARAM.getName(), geneProductId)
                        .param(GENE_PRODUCT_ID_PARAM.getName(), genericDocs.get(0).geneProductId)
                        .param(GENE_PRODUCT_ID_PARAM.getName(), genericDocs.get(1).geneProductId));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(2))
                .andExpect(fieldsInAllResultsExist(2))
                .andExpect(itemExistsExpectedTimes(GENEPRODUCT_ID_FIELD, genericDocs.get(0).geneProductId, 1))
                .andExpect(itemExistsExpectedTimes(GENEPRODUCT_ID_FIELD, genericDocs.get(1).geneProductId, 1));
    }

    @Test
    public void filterByGeneProductIDAndAssignedBySuccessfully() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search")
                        .param(GENE_PRODUCT_ID_PARAM.getName(), genericDocs.get(0).geneProductId)
                        .param(ASSIGNED_BY_PARAM.getName(), genericDocs.get(0).assignedBy));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldsInAllResultsExist(1))
                .andExpect(itemExistsExpectedTimes(GENEPRODUCT_ID_FIELD, genericDocs.get(0).geneProductId, 1))
                .andExpect(itemExistsExpectedTimes(ASSIGNED_BY_FIELD, genericDocs.get(1).assignedBy, 1));
    }

    @Test
    public void idValidationTestWorksCorrectlyForGeneProductIDWithFeature() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search")
                        .param(GENE_PRODUCT_ID_PARAM.getName(), "P19712:PRO_0000038050"));
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(0));
    }

    //---------- Gene Ontology Id
    @Test
    public void successfullyLookupAnnotationsByGoId() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search")
                        .param(GO_ID_PARAM.getName(), AnnotationDocMocker.GO_ID)
                        .param(GO_USAGE_PARAM.getName(), EXACT_USAGE));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(NUMBER_OF_GENERIC_DOCS))
                .andExpect(fieldsInAllResultsExist(NUMBER_OF_GENERIC_DOCS))
                .andExpect(itemExistsExpectedTimes(GO_ID_FIELD, AnnotationDocMocker.GO_ID, NUMBER_OF_GENERIC_DOCS));
    }

    @Test
    public void successfullyLookupAnnotationsByGoIdCaseInsensitive() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search")
                        .param(GO_ID_PARAM.getName(), AnnotationDocMocker.GO_ID.toLowerCase())
                        .param(GO_USAGE_PARAM.getName(), EXACT_USAGE));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(NUMBER_OF_GENERIC_DOCS))
                .andExpect(fieldsInAllResultsExist(NUMBER_OF_GENERIC_DOCS))
                .andExpect(itemExistsExpectedTimes(GO_ID_FIELD, AnnotationDocMocker.GO_ID, NUMBER_OF_GENERIC_DOCS));
    }

    @Test
    public void failToFindAnnotationsWhenGoIdDoesntExist() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search")
                        .param(GO_ID_PARAM.getName(), MISSING_GO_ID)
                        .param(GO_USAGE_PARAM.getName(), EXACT_USAGE));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(0));
    }

    @Test
    public void incorrectFormattedGoIdCausesError() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(GO_ID_PARAM.getName(), INVALID_GO_ID));

        response.andExpect(status().isBadRequest())
                .andExpect(contentTypeToBeJson());
    }

    @Test
    public void filterAnnotationsUsingSingleEvidenceCodeReturnsResults() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search")
                        .param(EVIDENCE_CODE_PARAM.getName(), AnnotationDocMocker.ECO_ID)
                        .param(EVIDENCE_CODE_USAGE_PARAM.getName(), EXACT_USAGE));

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(NUMBER_OF_GENERIC_DOCS))
                .andExpect(fieldsInAllResultsExist(NUMBER_OF_GENERIC_DOCS))
                .andExpect(atLeastOneResultHasItem(EVIDENCE_CODE_PARAM.getName(), AnnotationDocMocker.ECO_ID));
    }

    @Test
    public void filterAnnotationsUsingMultipleEvidenceCodesInSingleParameterProducesMixedResults() throws Exception {

        AnnotationDocument doc = AnnotationDocMocker.createAnnotationDoc("B0A000");
        doc.evidenceCode = ECO_ID2;
        repository.save(doc);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search")
                        .param(EVIDENCE_CODE_PARAM.getName(),
                                AnnotationDocMocker.ECO_ID + "," + doc.evidenceCode + "," + MISSING_ECO_ID)
                        .param(EVIDENCE_CODE_USAGE_PARAM.getName(), EXACT_USAGE));

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(NUMBER_OF_GENERIC_DOCS + 1))
                .andExpect(fieldsInAllResultsExist(4))
                .andExpect(itemExistsExpectedTimes(EVIDENCE_CODE_PARAM.getName(), AnnotationDocMocker.ECO_ID,
                        NUMBER_OF_GENERIC_DOCS))
                .andExpect(itemExistsExpectedTimes(EVIDENCE_CODE_PARAM.getName(), doc.evidenceCode, 1))
                .andExpect(itemExistsExpectedTimes(EVIDENCE_CODE_PARAM.getName(), MISSING_ECO_ID, 0));
    }

    //---------- EVIDENCE CODE

    @Test
    public void filterAnnotationsUsingMultipleEvidenceCodesAsIndependentParametersProducesMixedResults()
            throws Exception {

        AnnotationDocument doc = AnnotationDocMocker.createAnnotationDoc("B0A000");
        doc.evidenceCode = ECO_ID2;
        repository.save(doc);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(EVIDENCE_CODE_PARAM.getName(), AnnotationDocMocker.ECO_ID)
                        .param(EVIDENCE_CODE_PARAM.getName(), doc.evidenceCode)
                        .param(EVIDENCE_CODE_PARAM.getName(), MISSING_ECO_ID)
                        .param(EVIDENCE_CODE_USAGE_PARAM.getName(), EXACT_USAGE));

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(NUMBER_OF_GENERIC_DOCS + 1))
                .andExpect(fieldsInAllResultsExist(NUMBER_OF_GENERIC_DOCS + 1))
                .andExpect(itemExistsExpectedTimes(EVIDENCE_CODE_PARAM.getName(), AnnotationDocMocker.ECO_ID,
                        NUMBER_OF_GENERIC_DOCS))
                .andExpect(itemExistsExpectedTimes(EVIDENCE_CODE_PARAM.getName(), doc.evidenceCode, 1))
                .andExpect(itemExistsExpectedTimes(EVIDENCE_CODE_PARAM.getName(), MISSING_ECO_ID, 0));
    }

    @Test
    public void filterByNonExistentEvidenceCodeReturnsZeroResults() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search")
                        .param(EVIDENCE_CODE_PARAM.getName(), MISSING_ECO_ID)
                        .param(EVIDENCE_CODE_USAGE_PARAM.getName(), EXACT_USAGE));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(0));
    }

    @Test
    public void retrievesSecondPageOfAllEntriesRequest() throws Exception {
        int totalEntries = 60;

        repository.deleteAll();
        repository.saveAll(createGenericDocs(totalEntries));

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search")
                        .param(PAGE_PARAM.getName(), "2"));

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(totalEntries))
                .andExpect(resultsInPage(DEFAULT_ENTRIES_PER_PAGE))
                .andExpect(
                        pageInfoMatches(
                                2,
                                totalPages(totalEntries, DEFAULT_ENTRIES_PER_PAGE),
                                DEFAULT_ENTRIES_PER_PAGE)
                );
    }

    @Test
    public void pageRequestEqualToAvailablePagesReturns200() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(PAGE_PARAM.getName(), "1"));

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(NUMBER_OF_GENERIC_DOCS))
                .andExpect(
                        pageInfoMatches(
                                1,
                                totalPages(NUMBER_OF_GENERIC_DOCS, DEFAULT_ENTRIES_PER_PAGE),
                                DEFAULT_ENTRIES_PER_PAGE)
                );
    }

    //---------- Page related tests.

    @Test
    public void pageRequestOfZeroAndResultsAvailableReturns400() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(PAGE_PARAM.getName(), "0"));

        response.andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void pageRequestHigherThanAvailablePagesReturns400() throws Exception {
        repository.deleteAll();

        int existingPages = 4;
        int resultsPerPage = 10;
        repository.saveAll(createGenericDocs(resultsPerPage * existingPages));

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search")
                        .param(LIMIT_PARAM.getName(), String.valueOf(resultsPerPage))
                        .param(PAGE_PARAM.getName(), String.valueOf(existingPages + 1)));

        response.andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void requestingMoreResultsPerPageThanPermittedReturns400() throws Exception {
        repository.deleteAll();

        int docsNecessaryToForcePagination = MAX_PAGE_RESULTS + 1;
        repository.saveAll(createGenericDocs(docsNecessaryToForcePagination));

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search")
                        .param(LIMIT_PARAM.getName(), String.valueOf(MAX_PAGE_RESULTS + 1))
                        .param(PAGE_PARAM.getName(), String.valueOf(1)));

        response.andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void pageRequestHigherThanPaginationLimitReturns400() throws Exception {
        int totalEntries = MAX_PAGE_NUMBER + 1;
        int pageSize = 1;
        int pageNumWhichIsTooHigh = totalEntries;

        repository.deleteAll();
        repository.saveAll(createGenericDocs(totalEntries));

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search")
                        .param(LIMIT_PARAM.getName(), String.valueOf(pageSize))
                        .param(PAGE_PARAM.getName(), String.valueOf(pageNumWhichIsTooHigh)));

        response.andDo(print())
                .andExpect(status().isBadRequest());
    }

    //---------- withFrom related tests.
    @Test
    public void successfulLookupWithFromForSingleId() throws Exception {
        ResultActions response =
                mockMvc.perform(get(RESOURCE_URL + "/search").param(WITHFROM_PARAM.getName(), "InterPro:IPR015421"));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(NUMBER_OF_GENERIC_DOCS))
                .andExpect(valueOccursInFieldList(WITH_FROM_PATH,
                        responseItem()
                                .withAttribute("db", "InterPro")
                                .withAttribute("id", "IPR015421").build()));
    }

    @Test
    public void successfulLookupWithFromForMultipleValues() throws Exception {
        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search").param(WITHFROM_PARAM.getName(),
                "InterPro:IPR015421,InterPro:IPR015422"));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(NUMBER_OF_GENERIC_DOCS))
                .andExpect(valueOccursInFieldList(WITH_FROM_PATH, responseItem()
                        .withAttribute("db", "InterPro")
                        .withAttribute("id", "IPR015421").build()))
                .andExpect(valueOccursInFieldList(WITH_FROM_PATH, responseItem()
                        .withAttribute("db", "InterPro")
                        .withAttribute("id", "IPR015422").build()));
    }

    @Test
    public void searchingForUnknownWithFromCreatesError() throws Exception {
        ResultActions response =
                mockMvc.perform(get(RESOURCE_URL + "/search").param(WITHFROM_PARAM.getName(), "XXX:54321"));

        response.andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void successfulLookupWithFromUsingDatabaseNameOnly() throws Exception {
        ResultActions response =
                mockMvc.perform(get(RESOURCE_URL + "/search").param(WITHFROM_PARAM.getName(), "InterPro"));

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(NUMBER_OF_GENERIC_DOCS))
                .andExpect(fieldsInAllResultsExist(NUMBER_OF_GENERIC_DOCS))
                .andExpect(valueOccursInFieldList(WITH_FROM_PATH, responseItem()
                        .withAttribute("db", "InterPro")
                        .withAttribute("id", "IPR015421").build()))
                .andExpect(valueOccursInFieldList(WITH_FROM_PATH, responseItem()
                        .withAttribute("db", "InterPro")
                        .withAttribute("id", "IPR015422").build()));
    }

    @Test
    public void successfulLookupWithFromUsingDatabaseIdOnly() throws Exception {
        ResultActions response =
                mockMvc.perform(get(RESOURCE_URL + "/search").param(WITHFROM_PARAM.getName(), "IPR015421"));

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(NUMBER_OF_GENERIC_DOCS))
                .andExpect(fieldsInAllResultsExist(NUMBER_OF_GENERIC_DOCS))
                .andExpect(valueOccursInFieldList(WITH_FROM_PATH, responseItem()
                        .withAttribute("db", "InterPro")
                        .withAttribute("id", "IPR015421").build()));
    }

    @Test
    public void limitForPageExceedsMaximumAllowed() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search")
                        .param(LIMIT_PARAM.getName(), "101"));

        response.andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void limitForPageWithinMaximumAllowed() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(LIMIT_PARAM.getName(), "100"));

        response.andExpect(status().isOk())
                .andExpect(totalNumOfResults(NUMBER_OF_GENERIC_DOCS))
                .andExpect(pageInfoMatches(1, 1, 100));
    }

    //---------- Limit related tests.

    @Test
    public void limitForPageThrowsErrorWhenNegative() throws Exception {
        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search")
                .param(LIMIT_PARAM.getName(), "-20"));

        response.andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void filterBySingleReferenceReturnsDocumentsThatContainTheReference() throws Exception {

        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search").param(REF_PARAM.getName(),
                AnnotationDocMocker.REFERENCE));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(NUMBER_OF_GENERIC_DOCS))
                .andExpect(fieldsInAllResultsExist(NUMBER_OF_GENERIC_DOCS))
                .andExpect(itemExistsExpectedTimes(REFERENCE_FIELD, AnnotationDocMocker.REFERENCE,
                        NUMBER_OF_GENERIC_DOCS));
    }

    @Test
    public void filterBySingleReferenceReturnsOnlyDocumentsThatContainTheReferenceWhenOthersExists() throws Exception {

        AnnotationDocument doc = AnnotationDocMocker.createAnnotationDoc("A0A123");
        doc.reference = "PMID:0000002";
        repository.save(doc);
        ResultActions response =
                mockMvc.perform(get(RESOURCE_URL + "/search").param(REF_PARAM.getName(), doc.reference));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldsInAllResultsExist(1))
                .andExpect(itemExistsExpectedTimes(REFERENCE_FIELD, doc.reference, 1));
    }

    //----- Tests for reference ---------------------//

    @Test
    public void filterByThreeReferencesReturnsDocumentsThatContainThoseReferences() throws Exception {

        AnnotationDocument docA = AnnotationDocMocker.createAnnotationDoc("A0A123");
        docA.reference = "PMID:0000002";
        repository.save(docA);

        AnnotationDocument docB = AnnotationDocMocker.createAnnotationDoc("A0A124");
        docB.reference = "PMID:0000003";
        repository.save(docB);

        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search").param(REF_PARAM.getName(),
                AnnotationDocMocker.REFERENCE + "," + docA.reference + "," + docB.reference));

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(NUMBER_OF_GENERIC_DOCS + 2))
                .andExpect(fieldsInAllResultsExist(NUMBER_OF_GENERIC_DOCS + 2))
                .andExpect(
                        itemExistsExpectedTimes(REFERENCE_FIELD, AnnotationDocMocker.REFERENCE, NUMBER_OF_GENERIC_DOCS))
                .andExpect(itemExistsExpectedTimes(REFERENCE_FIELD, docA.reference, 1))
                .andExpect(itemExistsExpectedTimes(REFERENCE_FIELD, docB.reference, 1));
    }

    @Test
    public void filterByThreeIndependentReferencesReturnsDocumentsThatContainThoseReferences() throws Exception {

        AnnotationDocument docA = AnnotationDocMocker.createAnnotationDoc("A0A123");
        docA.reference = "PMID:0000002";
        repository.save(docA);

        AnnotationDocument docB = AnnotationDocMocker.createAnnotationDoc("A0A124");
        docB.reference = "PMID:0000003";
        repository.save(docB);

        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search").param(REF_PARAM.getName(),
                AnnotationDocMocker.REFERENCE).param(REF_PARAM.getName(), docA.reference)
                .param(REF_PARAM.getName(), docB.reference));

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(NUMBER_OF_GENERIC_DOCS + 2))
                .andExpect(fieldsInAllResultsExist(NUMBER_OF_GENERIC_DOCS + 2))
                .andExpect(
                        itemExistsExpectedTimes(REFERENCE_FIELD, AnnotationDocMocker.REFERENCE, NUMBER_OF_GENERIC_DOCS))
                .andExpect(itemExistsExpectedTimes(REFERENCE_FIELD, docA.reference, 1))
                .andExpect(itemExistsExpectedTimes(REFERENCE_FIELD, docB.reference, 1));
    }

    @Test
    public void filterByReferenceDbOnlyReturnsDocumentsWithReferencesThatStartWithThatDb() throws Exception {

        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search").param(REF_PARAM.getName(), "GO_REF"));

        //This one shouldn't be found
        AnnotationDocument docA = AnnotationDocMocker.createAnnotationDoc("A0A123");
        docA.reference = "PMID:0000002";
        repository.save(docA);
        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(NUMBER_OF_GENERIC_DOCS));
    }

    @Test
    public void filterByReferenceDbNotAvailableInDocumentsReturnsZeroResults() throws Exception {
        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search").param(REF_PARAM.getName(), "GO_LEFT"));
        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(0));
    }

    @Test
    public void filterBySingleReferenceIdReturnsDocumentsThatContainTheReferenceId() throws Exception {
        AnnotationDocument docA = AnnotationDocMocker.createAnnotationDoc("A0A123");
        docA.reference = "PMID:0000002";
        repository.save(docA);

        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search").param(REF_PARAM.getName(), "0000002"));
        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(NUMBER_OF_GENERIC_DOCS + 1))
                .andExpect(fieldsInAllResultsExist(NUMBER_OF_GENERIC_DOCS + 1))
                .andExpect(
                        itemExistsExpectedTimes(REFERENCE_FIELD, AnnotationDocMocker.REFERENCE, NUMBER_OF_GENERIC_DOCS))
                .andExpect(itemExistsExpectedTimes(REFERENCE_FIELD, docA.reference, 1));
    }

    @Test
    public void filterByMultipleReferenceIdReturnsDocumentsThatContainTheReferenceId() throws Exception {
        AnnotationDocument docA = AnnotationDocMocker.createAnnotationDoc("A0A123");
        docA.reference = "PMID:0000002";
        repository.save(docA);

        AnnotationDocument docB = AnnotationDocMocker.createAnnotationDoc("A0A124");
        docB.reference = "PMID:0000003";
        repository.save(docB);

        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search").param(REF_PARAM.getName(), "0000002")
                .param(REF_PARAM.getName(), "0000003"));

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(NUMBER_OF_GENERIC_DOCS + 2))
                .andExpect(fieldsInAllResultsExist(NUMBER_OF_GENERIC_DOCS + 2))
                .andExpect(
                        itemExistsExpectedTimes(REFERENCE_FIELD, AnnotationDocMocker.REFERENCE, NUMBER_OF_GENERIC_DOCS))
                .andExpect(itemExistsExpectedTimes(REFERENCE_FIELD, docA.reference, 1))
                .andExpect(itemExistsExpectedTimes(REFERENCE_FIELD, docB.reference, 1));
    }

    @Test
    public void filterByUnknownReferenceIdIsUnsuccessful() throws Exception {

        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search").param(REF_PARAM.getName(), "999999"));

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(0));
    }

    @Test
    public void filterByGeneProductTypeReturnsMatchingDocuments() throws Exception {
        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search").param(GENE_PRODUCT_TYPE_PARAM.getName(),
                "protein"));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(NUMBER_OF_GENERIC_DOCS))
                .andExpect(fieldsInAllResultsExist(NUMBER_OF_GENERIC_DOCS));
    }

    @Test
    public void filterBySingleGeneProductTypeOfRnaReturnsMatchingDocument() throws Exception {
        AnnotationDocument doc = AnnotationDocMocker.createAnnotationDoc("A0A123");
        doc.geneProductType = "miRNA";
        repository.save(doc);

        ResultActions response =
                mockMvc.perform(get(RESOURCE_URL + "/search").param(GENE_PRODUCT_TYPE_PARAM.getName(), "miRNA"));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldsInAllResultsExist(1));
    }

    //----- Tests for GeneProductType ---------------------//

    @Test
    public void filterAnnotationsByTwoGeneProductTypesAsOneParameterReturnsMatchingDocuments() throws Exception {
        AnnotationDocument doc = AnnotationDocMocker.createAnnotationDoc("A0A123");
        doc.geneProductType = "complex";
        repository.save(doc);

        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search").param(GENE_PRODUCT_TYPE_PARAM.getName(),
                "protein,complex"));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(4))
                .andExpect(fieldsInAllResultsExist(4));
    }

    @Test
    public void filterAnnotationsByTwoGeneProductTypesAsTwoParametersReturnsMatchingDocuments() throws Exception {
        AnnotationDocument doc = AnnotationDocMocker.createAnnotationDoc("A0A123");
        doc.geneProductType = "complex";
        repository.save(doc);

        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search").param(GENE_PRODUCT_TYPE_PARAM.getName(),
                "protein").param(GENE_PRODUCT_TYPE_PARAM.getName(), "complex"));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(4))
                .andExpect(fieldsInAllResultsExist(4));
    }

    @Test
    public void filterByNonExistentGeneProductTypeReturnsNothing() throws Exception {
        AnnotationDocument doc = AnnotationDocMocker.createAnnotationDoc("A0A123");
        doc.geneProductType = "complex";
        repository.save(doc);

        ResultActions response =
                mockMvc.perform(get(RESOURCE_URL + "/search").param(GENE_PRODUCT_TYPE_PARAM.getName(), "miRNA"));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(0));
    }

    @Test
    public void filterByTargetSetReturnsMatchingDocuments() throws Exception {
        ResultActions response =
                mockMvc.perform(get(RESOURCE_URL + "/search").param(TARGET_SET_PARAM.getName(), "KRUK"));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(NUMBER_OF_GENERIC_DOCS))
                .andExpect(fieldsInAllResultsExist(NUMBER_OF_GENERIC_DOCS));
    }

    @Test
    public void filterByTwoTargetSetValuesReturnsMatchingDocuments() throws Exception {
        ResultActions response =
                mockMvc.perform(get(RESOURCE_URL + "/search").param(TARGET_SET_PARAM.getName(), "KRUK,BHF-UCL"));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(NUMBER_OF_GENERIC_DOCS))
                .andExpect(fieldsInAllResultsExist(NUMBER_OF_GENERIC_DOCS));
    }

    //----- Target Sets

    @Test
    public void filterByNewTargetSetValueReturnsMatchingDocuments() throws Exception {
        AnnotationDocument doc = AnnotationDocMocker.createAnnotationDoc("A0A123");
        doc.targetSets = Collections.singletonList("Parkinsons");
        repository.save(doc);

        ResultActions response =
                mockMvc.perform(get(RESOURCE_URL + "/search").param(TARGET_SET_PARAM.getName(), "Parkinsons"));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldsInAllResultsExist(1));
    }

    @Test
    public void filterByTargetSetCaseInsensitiveReturnsMatchingDocuments() throws Exception {
        AnnotationDocument doc = AnnotationDocMocker.createAnnotationDoc("A0A123");
        doc.targetSets = Collections.singletonList("parkinsons");
        repository.save(doc);

        ResultActions response =
                mockMvc.perform(get(RESOURCE_URL + "/search").param(TARGET_SET_PARAM.getName(), "PARKINSONS"));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldsInAllResultsExist(1));
    }

    @Test
    public void filterByNonExistentTargetSetReturnsNoDocuments() throws Exception {
        ResultActions response =
                mockMvc.perform(get(RESOURCE_URL + "/search").param(TARGET_SET_PARAM.getName(), "CLAP"));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(0));
    }

    @Test
    public void filterAnnotationsByGoAspectSuccessfully() throws Exception {
        String goAspect = AnnotationDocMocker.GO_ASPECT;
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(GO_ASPECT_PARAM.getName(), goAspect));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(NUMBER_OF_GENERIC_DOCS))
                .andExpect(fieldsInAllResultsExist(NUMBER_OF_GENERIC_DOCS));
    }

    @Test
    public void filterAnnotationsByInvertedCaseGoAspectSuccessfully() throws Exception {
        String goAspect = StringUtils.swapCase(AnnotationDocMocker.GO_ASPECT);
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(GO_ASPECT_PARAM.getName(), goAspect));

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(NUMBER_OF_GENERIC_DOCS))
                .andExpect(fieldsInAllResultsExist(NUMBER_OF_GENERIC_DOCS));
    }

    //---------- GO Aspect related tests.

    @Test
    public void filterAnnotationsByInvalidGoAspectReturnsError() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(GO_ASPECT_PARAM.getName(), "ZZZ"));

        response.andExpect(status().isBadRequest());
    }

    @Test
    public void filterWithAspectMolecularFunctionReturnsAnnotationsWithMolecularFunction() throws Exception {
        String goAspect = "molecular_function";

        AnnotationDocument annoDoc1 = AnnotationDocMocker.createAnnotationDoc(createGPId(999));
        annoDoc1.goAspect = goAspect;
        repository.save(annoDoc1);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(GO_ASPECT_PARAM.getName(), goAspect));

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldsInAllResultsExist(1));
    }

    //----- Tests for Annotation Extension ---------------------//

    @Test
    public void filterByExtensionFull() throws Exception {
        ResultActions response =
                mockMvc.perform(get(RESOURCE_URL + "/search").param(EXTENSION_PARAM.getName(), EXTENSIONS));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(NUMBER_OF_GENERIC_DOCS))
                .andExpect(fieldsInAllResultsExist(NUMBER_OF_GENERIC_DOCS));
    }

    @Test
    public void filterByUniqueExtensionTarget() throws Exception {
        String extension = "results_in_development_of(UBERON:1234567)";
        AnnotationDocument doc = AnnotationDocMocker.createAnnotationDoc("A0A123");
        doc.extensions = extension;
        repository.save(doc);

        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search")
                .param(EXTENSION_PARAM.getName(), extension));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldsInAllResultsExist(1));
    }

    //GOA-3246
    @Test
    public void filterBySingleAndExtension() throws Exception {
        String extension = "results_in_development_of(UBERON:1234567)";
        String extensionQuery= extension + " and happy_about(P01234:QWE_90hy)" ;
        AnnotationDocument doc = AnnotationDocMocker.createAnnotationDoc("A0A123");
        doc.extensions = extension;
        repository.save(doc);

        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search")
                .param(EXTENSION_PARAM.getName(), extensionQuery));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(0));
    }

    @Test
    public void filterBySingleOrExtension() throws Exception {
        String extension = "results_in_development_of(UBERON:1234567)";
        String extensionQuery= extension + " or happy_about(PO1234:QWE_90hy)" ;
        AnnotationDocument doc = AnnotationDocMocker.createAnnotationDoc("A0A123");
        doc.extensions = extension;
        repository.save(doc);

        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search")
                .param(EXTENSION_PARAM.getName(), extensionQuery));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(NUMBER_OF_GENERIC_DOCS + 1))
                .andExpect(fieldsInAllResultsExist(NUMBER_OF_GENERIC_DOCS + 1));
    }

    @Test
    public void filterByMultipleOrExtension() throws Exception {
        String extensionQuery= Stream.of(EXTENSIONS.split(",|\\|")).collect(Collectors.joining(" or "));

        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search")
                .param(EXTENSION_PARAM.getName(), extensionQuery));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(NUMBER_OF_GENERIC_DOCS))
                .andExpect(fieldsInAllResultsExist(NUMBER_OF_GENERIC_DOCS));
    }

    @Test
    public void filterByMultipleAndExtension() throws Exception {
        String extensionQuery= Stream.of(EXTENSIONS.split(",|\\|")).collect(Collectors.joining(" and "));

        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search")
                .param(EXTENSION_PARAM.getName(), extensionQuery));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(NUMBER_OF_GENERIC_DOCS))
                .andExpect(fieldsInAllResultsExist(NUMBER_OF_GENERIC_DOCS));
    }

    @Test
    public void filterByAndOrExtension() throws Exception {
        String[] extensions = EXTENSIONS.split(",|\\|");
        String extensionQuery=  extensions[0] + " And " + extensions[1] + " oR " + extensions[2] + " AND "+ extensions[3];

        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search")
                .param(EXTENSION_PARAM.getName(), extensionQuery));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(NUMBER_OF_GENERIC_DOCS))
                .andExpect(fieldsInAllResultsExist(NUMBER_OF_GENERIC_DOCS));
    }

    // ------------------------------- Wildcard searches  -------------------------------

    @Test
    public void retrieveWhereAnnotationExtensionIsNotEmpty() throws Exception {
        int numberOfDocsWithExtensions = 3;
        int numberOfDocsWithoutExtensions = 2;
        repository.deleteAll();
        List<AnnotationDocument> docsWithExtensions = createGenericDocs(numberOfDocsWithExtensions);
        List<AnnotationDocument> docsWithoutExtensions = createGenericDocs(numberOfDocsWithoutExtensions);
        for (AnnotationDocument doc : docsWithoutExtensions) {
            doc.extensions = null;
        }
        repository.saveAll(docsWithExtensions);
        repository.saveAll(docsWithoutExtensions);

        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search").param(EXTENSION_PARAM.getName(),
                                                                                     SELECT_ALL_WHERE_FIELD_IS_NOT_EMPTY));
        List<String> geneProductsThatAppearInDocumentsThatHaveExtensions = docsWithExtensions.stream()
                          .map(d -> d.geneProductId).collect(toList());

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(numberOfDocsWithExtensions))
                .andExpect(fieldsInAllResultsExist(2))
                .andExpect(valuesOccurInField(GENEPRODUCT_ID_FIELD, geneProductsThatAppearInDocumentsThatHaveExtensions));
    }

    @Test
    public void usingInvalidWildCardFieldResultsInError() throws Exception {
        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search").param(GENE_PRODUCT_ID_PARAM.getName(),
                                                                                     SELECT_ALL_WHERE_FIELD_IS_NOT_EMPTY));

        response.andDo(print())
                .andExpect(status().isBadRequest());
    }

    // ------------------------------- Filter by geneProductSubset -------------------------------
    // Holds values TrEMBL, Swiss-Prot and maybe more
    @Test
    public void filterByGeneProductSubsetWillNotWorkWhenGeneProductTypeIsNotProteinPartOfRequest() throws Exception {
        AnnotationDocument doc = AnnotationDocMocker.createAnnotationDoc("A0A842");
        String geneProductSubset = "Swiss-Prot";
        doc.geneProductSubset = geneProductSubset;
        repository.save(doc);

        ResultActions response =
                mockMvc.perform(get(RESOURCE_URL + "/search").param(GP_SUBSET_PARAM.getName(), geneProductSubset));

        response.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(contentTypeToBeJson());
    }

    @Test
    public void filterByGeneProductSubset() throws Exception {
        AnnotationDocument doc = AnnotationDocMocker.createAnnotationDoc("A0A842");
        String geneProductSubset = "Swiss-Prot";
        doc.geneProductSubset = geneProductSubset;
        repository.save(doc);

        ResultActions response =
                mockMvc.perform(get(RESOURCE_URL + "/search").param(GP_SUBSET_PARAM.getName(), geneProductSubset)
                        .param(GENE_PRODUCT_TYPE_PARAM.getName(), "protein"));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldsInAllResultsExist(1));
    }

    @Test
    public void filterByGeneProductSubsetMixedCaseSearchValue() throws Exception {
        AnnotationDocument doc = AnnotationDocMocker.createAnnotationDoc("A0A849");
        doc.geneProductSubset = "Swiss-Prot";
        repository.save(doc);

        ResultActions response =
                mockMvc.perform(get(RESOURCE_URL + "/search").param(GP_SUBSET_PARAM.getName(), "swisS-proT").param
                        (GENE_PRODUCT_TYPE_PARAM.getName(), "protein"));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldsInAllResultsExist(1));
    }


    // ------------------------------- Filter by proteome -------------------------------

    @Test
    public void filterByProteomeWillNotWorkWhenGeneProductTypeIsNotProteinPartOfRequest() throws Exception {
        AnnotationDocument doc = AnnotationDocMocker.createAnnotationDoc("A0A123");
        doc.proteome = "gcrpCan";
        repository.save(doc);

        ResultActions response =
                mockMvc.perform(get(RESOURCE_URL + "/search").param(PROTEOME_PARAM.getName(), "gcrpCan"));

        response.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(contentTypeToBeJson());
    }

    @Test
    public void filterByProteome() throws Exception {
        AnnotationDocument doc = AnnotationDocMocker.createAnnotationDoc("A0A123");
        doc.proteome = "gcrpCan";
        repository.save(doc);

        ResultActions response =
                mockMvc.perform(get(RESOURCE_URL + "/search").param(PROTEOME_PARAM.getName(), "gcrpCan").param
                        (GENE_PRODUCT_TYPE_PARAM.getName(), "protein"));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldsInAllResultsExist(1));
    }

    @Test
    public void filterByProteomeMixedCaseSearchValue() throws Exception {
        AnnotationDocument doc = AnnotationDocMocker.createAnnotationDoc("A0A772");
        doc.proteome = "gcrpCan";
        repository.save(doc);

        ResultActions response =
                mockMvc.perform(get(RESOURCE_URL + "/search").param(PROTEOME_PARAM.getName(), "GcrPcan").param
                        (GENE_PRODUCT_TYPE_PARAM.getName(), "protein"));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldsInAllResultsExist(1));
    }

    @Test
    public void filterByMultipleProteomeParameters() throws Exception {
        AnnotationDocument doc = AnnotationDocMocker.createAnnotationDoc("A0A123");
        doc.proteome = "gcrpCan";
        repository.save(doc);

        ResultActions response =
                mockMvc.perform(get(RESOURCE_URL + "/search").param(PROTEOME_PARAM.getName(), "none,gcrpCan").param
                        (GENE_PRODUCT_TYPE_PARAM.getName(), "protein"));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(4))
                .andExpect(fieldsInAllResultsExist(4));
    }


    @Test
    public void filterByInvalidProteomeValueProducesNoResults() throws Exception {
        ResultActions response =
                mockMvc.perform(get(RESOURCE_URL + "/search").param(PROTEOME_PARAM.getName(), "pancake").param
                        (GENE_PRODUCT_TYPE_PARAM.getName(), "protein"));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(0));
    }

    // GOA-3266
    @Test
    public void filterByMultipleGeneProductAndProteinSpecificProperties() throws Exception {
        AnnotationDocument doc = AnnotationDocMocker.createAnnotationDoc("A0A842");
        AnnotationDocument doc2 = AnnotationDocMocker.createAnnotationDoc("A0A843");

        doc.proteome = "gcrpCan";
        doc.geneProductSubset = "swiss-prot";
        doc2.proteome= "";
        doc2.geneProductSubset="";
        doc2.geneProductType="miRNA";
        repository.save(doc);
        repository.save(doc2);

        ResultActions response =
                mockMvc.perform(get(RESOURCE_URL + "/search").param(GP_SUBSET_PARAM.getName(), "swiss-prot")
                        .param(GENE_PRODUCT_TYPE_PARAM.getName(), "protein,miRNA")
                        .param(PROTEOME_PARAM.getName(), "gcrpCan")
                );

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(2))
                .andExpect(fieldsInAllResultsExist(2));
    }

    // ------------------------------- Check date format -------------------------------
    @Test
    public void checkDateFormatIsCorrect() throws Exception {
        String geneProductId = "P99999";
        AnnotationDocument doc = AnnotationDocMocker.createAnnotationDoc(geneProductId);
        int year = 1900;
        int month = 1;
        int date = 31;
        doc.date = Date.from(
                LocalDate.of(year, month, date).atStartOfDay(ZoneId.systemDefault()).toInstant());

        repository.save(doc);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(GENE_PRODUCT_ID_PARAM.getName(), geneProductId));

        String expectedResponseDate = getRequiredDateString(year, month, date);
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldsInAllResultsExist(1))
                .andExpect(valuesOccurInField(GENEPRODUCT_ID_FIELD, geneProductId))
                .andExpect(valuesOccurInField(DATE_FIELD, expectedResponseDate));
    }

    // ------------------------------- Check about information -------------------------------

    @Test
    public void about() throws Exception {
        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/about"));

        response.andDo(print())
                .andExpect(jsonPath("$.annotation.timestamp").value("2017-03-01 18:00"));
    }

    @Test
    public void advanceFilterNotGoId_descendents_defaultRelations() throws Exception {
        mockRestToOntologyForDescendants();
        advanceFilterTest(20, false, false);
    }

    @Test
    public void advanceFilterNotGoId_exact() throws Exception {
        advanceFilterTest(20, false, true);
    }

    @Test
    public void advanceFilterAndGoId_descendents_defaultRelations() throws Exception {
        mockRestToOntologyForDescendants();
        advanceFilterTest(5, true, false);
    }

    private void mockRestToOntologyForDescendants(){
        mockRestServiceServer = MockRestServiceServer.createServer((RestTemplate) restOperations);

        String term = createGoId(1);
        String url = String.format("https://localhost/QuickGO/services/ontology/go/terms/%s/descendants?relations=is_a,part_of,occurs_in",term);

        mockRestServiceServer.expect(requestTo(url)).andExpect(method(HttpMethod.GET))
          .andRespond(withSuccess("{\"results\": [{\"descendants\": [\"GO:0000001\"]}]}", MediaType.APPLICATION_JSON));
    }

    @Test
    public void advanceFilterAndGoId_exact() throws Exception {
        advanceFilterTest(5, true, true);
    }

    private void advanceFilterTest(int expected, boolean and, boolean exact) throws Exception {
        List<AnnotationDocument> docs = createGenericDocsChangingGoId(5);
        repository.deleteAll();
        repository.saveAll(docs);
        AnnotationRequestBody.GoDescription description = AnnotationRequestBody.GoDescription.builder()
          .goTerms(new String[]{createGoId(1)})
          .goUsage(exact ? "exact" : DESCENDANTS)
          .build();
        AnnotationRequestBody body = and ? AnnotationRequestBody.builder().and(description).build()
          : AnnotationRequestBody.builder().not(description).build();

        ResultActions response = mockMvc.perform(
          post(RESOURCE_URL + "/search").contentType(MediaType.APPLICATION_JSON).content(json(body))
        );

        response.andDo(print())
          .andExpect(status().isOk())
          .andExpect(contentTypeToBeJson())
          .andExpect(totalNumOfResults(expected));
    }

    private String json(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }

    // ------------------------------- Helpers -------------------------------
    private <T> List<T> transformDocs(List<AnnotationDocument> docs, Function<AnnotationDocument, T> transformation) {
        return docs.stream().map(transformation).collect(toList());
    }

    private <T> List<AnnotationDocument> filterDocuments(
            List<AnnotationDocument> documents,
            T[] expectedValues,
            Function<AnnotationDocument, T> docTransformer) {
        return documents.stream()
                .filter(doc -> Stream
                        .of(expectedValues)
                        .anyMatch(e -> e == docTransformer.apply(doc)))
                .collect(toList());
    }

    private String[] asArray(List<String> list) {
        return list.toArray(new String[list.size()]);
    }

    private AnnotationDocument createDocWithAssignedBy(String geneProductId, String assignedBy) {
        AnnotationDocument doc = AnnotationDocMocker.createAnnotationDoc(geneProductId);
        doc.assignedBy = assignedBy;

        return doc;
    }

    private AnnotationDocument createDocWithTaxonId(String geneProductId, int taxonId) {
        AnnotationDocument doc = AnnotationDocMocker.createAnnotationDoc(geneProductId);
        doc.taxonId = taxonId;

        return doc;
    }

    private List<AnnotationDocument> createDocsWithTaxonAncestors(List<Integer> lineage) {
        List<AnnotationDocument> documents = new ArrayList<>();

        for (int i = 0; i < lineage.size(); i++) {
            documents.add(createDocWithTaxonAncestors(createGPId(i), lineage.subList(i, lineage.size())));
        }

        return documents;
    }

    private AnnotationDocument createDocWithTaxonAncestors(
            String geneProductId,
            List<Integer> lineage) {

        AnnotationDocument doc = AnnotationDocMocker.createAnnotationDoc(geneProductId);
        doc.taxonId = lineage.get(0);
        doc.taxonAncestors = lineage;

        return doc;
    }

    private String getRequiredDateString(int year, int month, int date) {
        return String.format(DATE_STRING_FORMAT, year, month, date);
    }

    //----- Setup data ---------------------//
    private List<AnnotationDocument> createGenericDocs(int n) {
        return IntStream.range(0, n)
                .mapToObj(i -> AnnotationDocMocker.createAnnotationDoc("UniProtKB:"+ createGPId(i))).collect
                        (toList());
    }

    private int totalPages(int totalEntries, int resultsPerPage) {
        return (int) Math.ceil(totalEntries / resultsPerPage) + 1;
    }
}
