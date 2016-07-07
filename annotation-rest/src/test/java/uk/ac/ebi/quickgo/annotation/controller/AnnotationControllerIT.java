package uk.ac.ebi.quickgo.annotation.controller;

import uk.ac.ebi.quickgo.annotation.AnnotationREST;
import uk.ac.ebi.quickgo.annotation.common.AnnotationRepository;
import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocMocker;
import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocument;
import uk.ac.ebi.quickgo.annotation.service.search.SearchServiceConfig;
import uk.ac.ebi.quickgo.common.solr.TemporarySolrDataStore;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.ac.ebi.quickgo.annotation.controller.ResponseVerifier.*;

import static uk.ac.ebi.quickgo.annotation.common.document.AnnotationFields.REFERENCE;
import static uk.ac.ebi.quickgo.annotation.common.document.AnnotationFields.WITH_FROM;
import static uk.ac.ebi.quickgo.annotation.model.AnnotationRequest.DEFAULT_ENTRIES_PER_PAGE;

/**
 * RESTful end point for Annotations
 * @author Tony Wardell
 * Date: 26/04/2016
 * Time: 14:21
 * Created with IntelliJ IDEA.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {AnnotationREST.class})
@WebAppConfiguration
public class AnnotationControllerIT {

    // temporary data store for solr's data, which is automatically cleaned on exit
    @ClassRule
    public static final TemporarySolrDataStore solrDataStore = new TemporarySolrDataStore();

    private static final int NUMBER_OF_GENERIC_DOCS = 3;

    private static final String ASSIGNED_BY_PARAM = "assignedBy";
    private static final String GO_EVIDENCE_PARAM = "goEvidence";
    private static final String REF_PARAM = "reference";
    private static final String QUALIFIER_PARAM = "qualifier";
    private static final String ECO_ID = "ecoId";
    private static final String PAGE_PARAM = "page";
    private static final String LIMIT_PARAM = "limit";
    private static final String TAXON_ID_PARAM = "taxon";
    private static final String WITHFROM_PARAM= "withFrom";

    private static final String UNAVAILABLE_ASSIGNED_BY = "ZZZZZ";
    private static final String RESOURCE_URL = "/QuickGO/services/annotation";
    //Test data
    private static final String EXISTING_ECO_ID1 = "ECO:0000256";
    private static final String EXISTING_ECO_ID2 = "ECO:0000323";  //exists
    private static final String NOTEXISTS_ECO_ID3 = "ECO:0000888";  //doesn't exist
    public static final int NUM_RESULTS = 6;
    private MockMvc mockMvc;
    private List<AnnotationDocument> genericDocs;
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
        repository.save(genericDocs);
        genericDocs = createGenericDocs2(NUMBER_OF_GENERIC_DOCS);
        repository.save(genericDocs);
    }

    private List<AnnotationDocument> createGenericDocs(int n) {
        return IntStream.range(0, n)
                .mapToObj(i -> AnnotationDocMocker.createAnnotationDoc(createId(i))).collect
                        (Collectors.toList());
    }

    private List<AnnotationDocument> createGenericDocs2(int n) {
        return IntStream.range(0, n)
                .mapToObj(i -> AnnotationDocMocker.createAnnotationDocUniqueData(createId(i))).collect
                        (Collectors.toList());
    }

    private String createId(int idNum) {
        return String.format("A0A%03d", idNum);
    }

    //ASSIGNED BY
    @Test
    public void lookupAnnotationFilterByAssignedBySuccessfully() throws Exception {
        String geneProductId = "P99999";
        String assignedBy = "ASPGD";

        AnnotationDocument document = createDocWithAssignedBy(geneProductId, assignedBy);
        repository.save(document);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(ASSIGNED_BY_PARAM, assignedBy));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldsInAllResultsExist(1))
                .andExpect(valuesOccurInField(GENEPRODUCT_ID_FIELD, geneProductId));
    }

    private AnnotationDocument createDocWithAssignedBy(String geneProductId, String assignedBy) {
        AnnotationDocument doc = AnnotationDocMocker.createAnnotationDoc(geneProductId);
        doc.assignedBy = assignedBy;

        return doc;
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
                get(RESOURCE_URL + "/search").param(ASSIGNED_BY_PARAM, assignedBy1 + "," + assignedBy2));

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(2))
                .andExpect(fieldsInAllResultsExist(2))
                .andExpect(valuesOccurInField(GENEPRODUCT_ID_FIELD, geneProductId1, geneProductId2));
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
                        .param(ASSIGNED_BY_PARAM, assignedBy1)
                        .param(ASSIGNED_BY_PARAM, assignedBy2));

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(2))
                .andExpect(fieldsInAllResultsExist(2))
                .andExpect(valuesOccurInField(GENEPRODUCT_ID_FIELD, geneProductId1, geneProductId2));
    }

    @Test
    public void lookupAnnotationFilterByInvalidAssignedBy() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(ASSIGNED_BY_PARAM, UNAVAILABLE_ASSIGNED_BY));

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
                get(RESOURCE_URL + "/search").param(ASSIGNED_BY_PARAM, UNAVAILABLE_ASSIGNED_BY + ","
                        + assignedBy));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldsInAllResultsExist(1))
                .andExpect(valuesOccurInField(GENEPRODUCT_ID_FIELD, geneProductId));
    }

    //---------- GoEvidence related tests.

    @Test
    public void invalidAssignedByThrowsAnError() throws Exception {
        String invalidAssignedBy = "_ASPGD";

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(ASSIGNED_BY_PARAM, invalidAssignedBy));

        response.andDo(print())
                .andExpect(status().isBadRequest());
    }

    //TAXON ID
    @Test
    public void lookupAnnotationFilterByTaxonIdSuccessfully() throws Exception {
        String geneProductId = "P99999";
        int taxonId = 2;

        AnnotationDocument document = createDocWithTaxonId(geneProductId, taxonId);
        repository.save(document);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(TAXON_ID_PARAM, "2"));

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldsInAllResultsExist(1))
                .andExpect(valuesOccurInField(GENEPRODUCT_ID_FIELD, geneProductId));
    }

    private AnnotationDocument createDocWithTaxonId(String geneProductId, int taxonId) {
        AnnotationDocument doc = AnnotationDocMocker.createAnnotationDoc(geneProductId);
        doc.taxonId = taxonId;

        return doc;
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
                        .param(TAXON_ID_PARAM, String.valueOf(taxonId1))
                        .param(TAXON_ID_PARAM, String.valueOf(taxonId2)));

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(2))
                .andExpect(fieldsInAllResultsExist(2))
                .andExpect(valuesOccurInField(GENEPRODUCT_ID_FIELD, geneProductId1, geneProductId2));
    }

    @Test
    public void invalidTaxIdThrowsError() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(TAXON_ID_PARAM, "-2"));

        response.andDo(print())
                .andExpect(status().isBadRequest());
    }

    //---------- GO Evidence Code

    @Test
    public void lookupAnnotationFilterByGoEvidenceCodeSuccessfully() throws Exception {
        String goEvidenceCode = "IEA";
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(GO_EVIDENCE_PARAM, goEvidenceCode));

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(genericDocs.size()))
                .andExpect(fieldsInAllResultsExist(genericDocs.size()))
                .andExpect(atLeastOneResultHasItem(GO_EVIDENCE_FIELD, goEvidenceCode));
    }


    @Test
    public void lookupAnnotationFilterByLowercaseGoEvidenceCodeSuccessfully() throws Exception {
        String goEvidenceCode = "iea";
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(GO_EVIDENCE_PARAM, goEvidenceCode));

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(genericDocs.size()))
                .andExpect(fieldsInAllResultsExist(genericDocs.size()))
                .andExpect(atLeastOneResultHasItem(GO_EVIDENCE_FIELD, goEvidenceCode.toUpperCase()));
    }

    @Test public void lookupAnnotationFilterByNonExistentGoEvidenceCodeReturnsNothing() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(GO_EVIDENCE_PARAM, "ZZZ"));

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(0));
    }

    @Test
    public void filterAnnotationsUsingMultipleGoEvidenceCodesSuccessfully() throws Exception {
        String goEvidenceCode = "IEA";

        String goEvidenceCode1 = "BSS";
        AnnotationDocument annoDoc1 = AnnotationDocMocker.createAnnotationDoc(createId(999));
        annoDoc1.goEvidence = goEvidenceCode1;
        repository.save(annoDoc1);

        String goEvidenceCode2 = "AWE";
        AnnotationDocument annoDoc2 = AnnotationDocMocker.createAnnotationDoc(createId(998));
        annoDoc2.goEvidence = goEvidenceCode2;
        repository.save(annoDoc2);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(GO_EVIDENCE_PARAM, "IEA,BSS,AWE,PEG"));

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(genericDocs.size() + 2))
                .andExpect(fieldsInAllResultsExist(genericDocs.size() + 2))
                .andExpect(itemExistsExpectedTimes(GO_EVIDENCE_FIELD, goEvidenceCode1, 1))
                .andExpect(itemExistsExpectedTimes(GO_EVIDENCE_FIELD, goEvidenceCode2, 1))
                .andExpect(itemExistsExpectedTimes(GO_EVIDENCE_FIELD, goEvidenceCode, genericDocs.size()));
    }

    @Test
    public void invalidGoEvidenceThrowsException() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(GO_EVIDENCE_PARAM, "BlahBlah"));

        response.andExpect(status().isBadRequest())
                .andExpect(contentTypeToBeJson());

    }

    //---------- Qualifier
    //todo test valid values for qualifier once a custom validator has been created

    @Test
    public void successfullyLookupAnnotationsByQualifier() throws Exception {
        String qualifier = "enables";
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(QUALIFIER_PARAM, qualifier));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(NUMBER_OF_GENERIC_DOCS))
                .andExpect(fieldsInAllResultsExist(NUMBER_OF_GENERIC_DOCS))
                .andExpect(valueOccurInField(QUALIFIER, qualifier));

    }

    @Test
    public void failToFindAnnotationsWhenQualifierDoesntExist() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(QUALIFIER_PARAM, "peeled"));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(0));

    }


    //---------- ECO ID


    @Test
    public void filterAnnotationsUsingSingleEcoIdReturnsResults() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(ECO_ID, EXISTING_ECO_ID1));

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(genericDocs.size()))
                .andExpect(fieldsInAllResultsExist(genericDocs.size()))
                .andExpect(atLeastOneResultHasItem(ECO_ID, EXISTING_ECO_ID1));
    }

    @Test
    public void filterAnnotationsUsingMultipleEcoIdsInSingleParameterProducesMixedResults() throws Exception {

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(ECO_ID, EXISTING_ECO_ID1 + "," + EXISTING_ECO_ID2));

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(genericDocs.size() * 2))
                .andExpect(fieldsInAllResultsExist(genericDocs.size() * 2))
                .andExpect(itemExistsExpectedTimes(ECO_ID, EXISTING_ECO_ID1, 3))
                .andExpect(itemExistsExpectedTimes(ECO_ID, EXISTING_ECO_ID2, 3))
                .andExpect(itemExistsExpectedTimes(ECO_ID, NOTEXISTS_ECO_ID3, 0));
    }

    @Test
    public void filterAnnotationsUsingMultipleEcoIdsAsIndependentParametersProducesMixedResults() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(ECO_ID, EXISTING_ECO_ID1)
                        .param(ECO_ID, EXISTING_ECO_ID2));

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(genericDocs.size() * 2))
                .andExpect(fieldsInAllResultsExist(genericDocs.size() * 2))
                .andExpect(itemExistsExpectedTimes(ECO_ID, EXISTING_ECO_ID1, 3))
                .andExpect(itemExistsExpectedTimes(ECO_ID, EXISTING_ECO_ID2, 3))
                .andExpect(itemExistsExpectedTimes(ECO_ID, NOTEXISTS_ECO_ID3, 0));
    }

    //---------- Limit related tests.

    @Test
    public void filterByNonExistentEcoIdReturnsZeroResults() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(ECO_ID, NOTEXISTS_ECO_ID3));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(0));
    }

    @Test
    public void retrievesSecondPageOfAllEntriesRequest() throws Exception {
        int totalEntries = 60;

        repository.deleteAll();
        repository.save(createGenericDocs(totalEntries));

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search")
                        .param(PAGE_PARAM, "2"));

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

    private int totalPages(int totalEntries, int resultsPerPage) {
        return (int) Math.ceil(totalEntries / resultsPerPage) + 1;
    }

    @Test
    public void pageRequestEqualToAvailablePagesReturns200() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(PAGE_PARAM, "1"));

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(genericDocs.size() * 2))
                .andExpect(
                        pageInfoMatches(
                                1,
                                totalPages(genericDocs.size() * 2, DEFAULT_ENTRIES_PER_PAGE),
                                DEFAULT_ENTRIES_PER_PAGE)
                );
    }

    @Test
    public void pageRequestOfZeroAndResultsAvailableReturns400() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(PAGE_PARAM, "0"));

        response.andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void pageRequestHigherThanAvailablePagesReturns400() throws Exception {

        repository.deleteAll();

        int existingPages = 4;
        createGenericDocs(SearchServiceConfig.MAX_PAGE_RESULTS * existingPages);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search")
                        .param(PAGE_PARAM, String.valueOf(existingPages + 1)));

        response.andDo(print())
                .andExpect(status().isBadRequest());
    }

    //---------- withFrom related tests.
    @Test
    public void successfulLookupWithFromForSingleId()throws Exception {
        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search").param(WITHFROM_PARAM, "InterPro:IPR015421"));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(NUM_RESULTS))
                .andExpect(valueOccursInCollection(WITH_FROM,"InterPro:IPR015421"));

    }

    @Test
    public void successfulLookupWithFromForMultipleValues()throws Exception {
        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search").param(WITHFROM_PARAM,
                "InterPro:IPR015421,InterPro:IPR015422"));

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(NUM_RESULTS))
                .andExpect(valueOccursInCollection(WITH_FROM,"InterPro:IPR015421"))
                .andExpect(valueOccursInCollection(WITH_FROM,"InterPro:IPR015422"));

    }


    @Test
    public void searchingForUnknownWithFromBringsBackNoResults()throws Exception {
        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search").param(WITHFROM_PARAM, "XXX:54321"));

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(0));
    }


    @Test
    public void successfulLookupWithFromUsingDatabaseNameOnly()throws Exception {
        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search").param(WITHFROM_PARAM, "InterPro"));

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(NUM_RESULTS))
                .andExpect(fieldsInAllResultsExist(NUM_RESULTS))
                .andExpect(valueOccursInCollection(WITH_FROM,"InterPro:IPR015421"))
                .andExpect(valueOccursInCollection(WITH_FROM,"InterPro:IPR015422"));
    }


    @Test
    public void successfulLookupWithFromUsingDatabaseIdOnly()throws Exception {
        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search").param(WITHFROM_PARAM, "IPR015421"));

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(NUM_RESULTS))
                .andExpect(fieldsInAllResultsExist(genericDocs.size()))
                .andExpect(valueOccursInCollection(WITH_FROM,"InterPro:IPR015421"));
    }

    //---------- Limit related tests.

    @Test
    public void limitForPageExceedsMaximumAllowed() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search")
                        .param(LIMIT_PARAM, "101"));

        response.andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void limitForPageWithinMaximumAllowed() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(LIMIT_PARAM, "100"));

        response.andExpect(status().isOk())
                .andExpect(totalNumOfResults(NUM_RESULTS))
                .andExpect(pageInfoMatches(1, 1, 100));
    }

    @Test
    public void limitForPageThrowsErrorWhenNegative() throws Exception {
        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search")
                .param(LIMIT_PARAM, "-20"));

        response.andDo(print())
                .andExpect(status().isBadRequest());
    }


    //----- Tests for reference ---------------------//

    @Test
    public void filterBySingleReferenceReturnsDocumentsThatContainTheReference() throws Exception {

        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search").param(REF_PARAM,
                AnnotationDocMocker.REF2));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(genericDocs.size()))
                .andExpect(fieldsInAllResultsExist(genericDocs.size()))
                .andExpect(itemExistsExpectedTimes(REFERENCE, AnnotationDocMocker.REF2, genericDocs.size()));
    }

    @Test
    public void filterBySingleReferenceReturnsOnlyDocumentsThatContainTheReferenceWhenOthersExists() throws Exception {

        AnnotationDocument doc = AnnotationDocMocker.createAnnotationDoc("A0A123");
        doc.reference = "PMID:0000002";
        repository.save(doc);
        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search").param(REF_PARAM, doc.reference));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldsInAllResultsExist(1))
                .andExpect(itemExistsExpectedTimes(REFERENCE, doc.reference, 1));
    }

    @Test
    public void filterByThreeReferencesReturnsDocumentsThatContainThoseReferences() throws Exception {

        AnnotationDocument docA = AnnotationDocMocker.createAnnotationDoc("A0A123");
        docA.reference = "PMID:0000002";
        repository.save(docA);

        AnnotationDocument docB = AnnotationDocMocker.createAnnotationDoc("A0A124");
        docB.reference = "PMID:0000003";
        repository.save(docB);

        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search").param(REF_PARAM,
                AnnotationDocMocker.REF2 + "," + docA.reference + "," + docB.reference));

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(5))
                .andExpect(fieldsInAllResultsExist(5))
                .andExpect(itemExistsExpectedTimes(REFERENCE, AnnotationDocMocker.REF2, genericDocs.size()))
                .andExpect(itemExistsExpectedTimes(REFERENCE, docA.reference, 1))
                .andExpect(itemExistsExpectedTimes(REFERENCE, docB.reference, 1));
    }

    @Test
    public void filterByThreeIndependentReferencesReturnsDocumentsThatContainThoseReferences() throws Exception {

        AnnotationDocument docA = AnnotationDocMocker.createAnnotationDoc("A0A123");
        docA.reference = "PMID:0000002";
        repository.save(docA);

        AnnotationDocument docB = AnnotationDocMocker.createAnnotationDoc("A0A124");
        docB.reference = "PMID:0000003";
        repository.save(docB);

        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search").param(REF_PARAM,
                AnnotationDocMocker.REF2).param(REF_PARAM, docA.reference).param(REF_PARAM, docB.reference));

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(5))
                .andExpect(fieldsInAllResultsExist(5))
                .andExpect(itemExistsExpectedTimes(REFERENCE, AnnotationDocMocker.REF2, genericDocs.size()))
                .andExpect(itemExistsExpectedTimes(REFERENCE, docA.reference, 1))
                .andExpect(itemExistsExpectedTimes(REFERENCE, docB.reference, 1));
    }

    @Test
    public void filterByReferenceDbOnlyReturnsDocumentsWithReferencesThatStartWithThatDb() throws Exception {

        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search").param(REF_PARAM, "GO_REF"));

        //This one shouldn't be found
        AnnotationDocument docA = AnnotationDocMocker.createAnnotationDoc("A0A123");
        docA.reference = "PMID:0000002";
        repository.save(docA);
        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(NUM_RESULTS));
    }

    @Test
    public void filterByReferenceDbNotAvailableInDocumentsReturnsZeroResults() throws Exception {
        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search").param(REF_PARAM, "GO_LEFT"));
        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(0));
    }

    @Test
    public void filterBySingleReferenceIdReturnsDocumentsThatContainTheReferenceId() throws Exception {
        AnnotationDocument docA = AnnotationDocMocker.createAnnotationDoc("A0A123");
        docA.reference = "PMID:0000002";
        repository.save(docA);

        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search").param(REF_PARAM, "0000002"));
        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(4))
                .andExpect(fieldsInAllResultsExist(4))
                .andExpect(itemExistsExpectedTimes(REFERENCE, AnnotationDocMocker.REF2, genericDocs.size()))
                .andExpect(itemExistsExpectedTimes(REFERENCE, docA.reference, 1));
    }

    @Test
    public void filterByMultipleReferenceIdReturnsDocumentsThatContainTheReferenceId() throws Exception {
        AnnotationDocument docA = AnnotationDocMocker.createAnnotationDoc("A0A123");
        docA.reference = "PMID:0000002";
        repository.save(docA);

        AnnotationDocument docB = AnnotationDocMocker.createAnnotationDoc("A0A124");
        docB.reference = "PMID:0000003";
        repository.save(docB);

        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search").param(REF_PARAM, "0000002")
                .param(REF_PARAM, "0000003"));

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(genericDocs.size() + 2))
                .andExpect(fieldsInAllResultsExist(5))
                .andExpect(itemExistsExpectedTimes(REFERENCE, AnnotationDocMocker.REF2, genericDocs.size()))
                .andExpect(itemExistsExpectedTimes(REFERENCE, docA.reference, 1))
                .andExpect(itemExistsExpectedTimes(REFERENCE, docB.reference, 1));
    }

    @Test
    public void filterByUnknownReferenceIdIsUnsuccessful() throws Exception {

        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search").param(REF_PARAM, "999999"));

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(0));
    }
}
