package uk.ac.ebi.quickgo.annotation.controller;

import uk.ac.ebi.quickgo.annotation.AnnotationREST;
import uk.ac.ebi.quickgo.annotation.common.AnnotationDocument;
import uk.ac.ebi.quickgo.annotation.common.AnnotationRepository;
import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocMocker;
import uk.ac.ebi.quickgo.common.solr.TemporarySolrDataStore;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.lang.StringUtils;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.ac.ebi.quickgo.annotation.AnnotationParameters.*;
import static uk.ac.ebi.quickgo.annotation.IdGeneratorUtil.createGPId;
import static uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocMocker.*;
import static uk.ac.ebi.quickgo.annotation.controller.ResponseVerifier.*;
import static uk.ac.ebi.quickgo.annotation.controller.ResponseVerifier.ResponseItem.responseItem;
import static uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelperImpl.DEFAULT_ENTRIES_PER_PAGE;
import static uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelperImpl.MAX_PAGE_NUMBER;
import static uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelperImpl.MAX_PAGE_RESULTS;

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
    public static final String DATE_STRING_FORMAT = "%04d%02d%02d";
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
    }

    // CONSISTENT ORDER
    @Test
    public void annotationsAlwaysReturnedInOrderWrittenToRepository() throws Exception {
        repository.deleteAll();
        String geneProductId1 = "AAAAA";
        String geneProductId2 = "BBBBB";
        String geneProductId3 = "ZZZZZ";

        //Create sequence number as B,Z,A
        AnnotationDocMocker.rowNumberGenerator = new AtomicLong(100);
        final AnnotationDocument annotationDoc1 = AnnotationDocMocker.createAnnotationDoc(geneProductId1);
        AnnotationDocMocker.rowNumberGenerator = new AtomicLong(10);
        final AnnotationDocument annotationDoc2 = AnnotationDocMocker.createAnnotationDoc(geneProductId2);
        AnnotationDocMocker.rowNumberGenerator = new AtomicLong(50);
        final AnnotationDocument annotationDoc3 = AnnotationDocMocker.createAnnotationDoc(geneProductId3);

        //save in order A, B, Z
        repository.save(annotationDoc1);
        repository.save(annotationDoc2);
        repository.save(annotationDoc3);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search"));

        //Results should arrive in sequence number
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(3))
                .andExpect(fieldInRowHasValue(GENEPRODUCT_ID_FIELD, 0, geneProductId2))
                .andExpect(fieldInRowHasValue(GENEPRODUCT_ID_FIELD, 1, geneProductId3))
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
                get(RESOURCE_URL + "/search").param(TAXON_ID_PARAM.getName(), Integer.toString(taxonId)));

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
                        .param(TAXON_ID_PARAM.getName(), Integer.toString(taxonId2)));

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
                get(RESOURCE_URL + "/search").param(GENE_PRODUCT_ID_PARAM.getName(), geneProductId));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldDoesNotExist(TAXON_ID_FIELD))
                .andExpect(valuesOccurInField(GENEPRODUCT_ID_FIELD, geneProductId));
    }

    @Test
    public void invalidTaxIdThrowsError() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(TAXON_ID_PARAM.getName(), "-2"));

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
    public void filterByUniProtKBAndIntactAndRNACentralGeneProductIDSuccessfully() throws Exception {
        repository.deleteAll();
        String uniprotGp = "A1E959";
        String intactGp = "EBI-10043081";
        String rnaGp = "URS00000064B1_559292";
        repository.save(AnnotationDocMocker.createAnnotationDoc(uniprotGp));
        repository.save(AnnotationDocMocker.createAnnotationDoc(intactGp));
        repository.save(AnnotationDocMocker.createAnnotationDoc(rnaGp));
        StringJoiner sj = new StringJoiner(",");
        sj.add(uniprotGp).add(intactGp).add(rnaGp);
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(GENE_PRODUCT_ID_PARAM.getName(), sj.toString()));

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(3))
                .andExpect(fieldsInAllResultsExist(3))
                .andExpect(itemExistsExpectedTimes(GENEPRODUCT_ID_FIELD, uniprotGp, 1))
                .andExpect(itemExistsExpectedTimes(GENEPRODUCT_ID_FIELD, intactGp, 1))
                .andExpect(itemExistsExpectedTimes(GENEPRODUCT_ID_FIELD, rnaGp, 1));
    }

    @Test
    public void filterByUniProtKBAndIntactAndRNACentralGeneProductIDCaseInsensitiveSuccessfully() throws Exception {
        repository.deleteAll();
        String uniprotGp = "A1E959".toLowerCase();
        String intactGp = "EBI-10043081".toLowerCase();
        String rnaGp = "URS00000064B1_559292".toLowerCase();
        repository.save(AnnotationDocMocker.createAnnotationDoc(uniprotGp));
        repository.save(AnnotationDocMocker.createAnnotationDoc(intactGp));
        repository.save(AnnotationDocMocker.createAnnotationDoc(rnaGp));
        StringJoiner sj = new StringJoiner(",");
        sj.add(uniprotGp).add(intactGp).add(rnaGp);
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(GENE_PRODUCT_ID_PARAM.getName(), sj.toString()));

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(3))
                .andExpect(fieldsInAllResultsExist(3))
                .andExpect(itemExistsExpectedTimes(GENEPRODUCT_ID_FIELD, uniprotGp, 1))
                .andExpect(itemExistsExpectedTimes(GENEPRODUCT_ID_FIELD, intactGp, 1))
                .andExpect(itemExistsExpectedTimes(GENEPRODUCT_ID_FIELD, rnaGp, 1));
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
                get(RESOURCE_URL + "/search").param(GO_ID_PARAM.getName(), AnnotationDocMocker.GO_ID));

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
                get(RESOURCE_URL + "/search").param(GO_ID_PARAM.getName(), AnnotationDocMocker.GO_ID.toLowerCase()));

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
                get(RESOURCE_URL + "/search").param(GO_ID_PARAM.getName(), MISSING_GO_ID));

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

    //---------- EVIDENCE CODE

    @Test
    public void filterAnnotationsUsingSingleEvidenceCodeReturnsResults() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(EVIDENCE_CODE_PARAM.getName(), AnnotationDocMocker.ECO_ID));

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
                get(RESOURCE_URL + "/search").param(EVIDENCE_CODE_PARAM.getName(), AnnotationDocMocker.ECO_ID + ","
                        + doc.evidenceCode + "," + MISSING_ECO_ID));

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(NUMBER_OF_GENERIC_DOCS + 1))
                .andExpect(fieldsInAllResultsExist(4))
                .andExpect(itemExistsExpectedTimes(EVIDENCE_CODE_PARAM.getName(), AnnotationDocMocker.ECO_ID,
                        NUMBER_OF_GENERIC_DOCS))
                .andExpect(itemExistsExpectedTimes(EVIDENCE_CODE_PARAM.getName(), doc.evidenceCode, 1))
                .andExpect(itemExistsExpectedTimes(EVIDENCE_CODE_PARAM.getName(), MISSING_ECO_ID, 0));
    }

    @Test
    public void filterAnnotationsUsingMultipleEvidenceCodesAsIndependentParametersProducesMixedResults()
            throws Exception {

        AnnotationDocument doc = AnnotationDocMocker.createAnnotationDoc("B0A000");
        doc.evidenceCode = ECO_ID2;
        repository.save(doc);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(EVIDENCE_CODE_PARAM.getName(), AnnotationDocMocker.ECO_ID)
                        .param(EVIDENCE_CODE_PARAM.getName(), doc.evidenceCode)
                        .param(EVIDENCE_CODE_PARAM.getName(), MISSING_ECO_ID));

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
                get(RESOURCE_URL + "/search").param(EVIDENCE_CODE_PARAM.getName(), MISSING_ECO_ID));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(0));
    }

    //---------- Page related tests.

    @Test
    public void retrievesSecondPageOfAllEntriesRequest() throws Exception {
        int totalEntries = 60;

        repository.deleteAll();
        repository.save(createGenericDocs(totalEntries));

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
        repository.save(createGenericDocs(resultsPerPage * existingPages));

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
        repository.save(createGenericDocs(docsNecessaryToForcePagination));

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
        repository.save(createGenericDocs(totalEntries));

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

    //---------- Limit related tests.

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

    @Test
    public void limitForPageThrowsErrorWhenNegative() throws Exception {
        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search")
                .param(LIMIT_PARAM.getName(), "-20"));

        response.andDo(print())
                .andExpect(status().isBadRequest());
    }

    //----- Tests for reference ---------------------//

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

    //----- Tests for GeneProductType ---------------------//

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

    //----- Target Sets

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

    //---------- GO Aspect related tests.

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

    @Test
    public void filterByExtensionRelationship() throws Exception {
        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search")
                .param(EXTENSION_PARAM.getName(), EXTENSION_RELATIONSHIP1));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(NUMBER_OF_GENERIC_DOCS))
                .andExpect(fieldsInAllResultsExist(NUMBER_OF_GENERIC_DOCS));
    }

    @Test
    public void filterByExtensionDatabase() throws Exception {
        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search")
                .param(EXTENSION_PARAM.getName(), EXTENSION_DB1));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(NUMBER_OF_GENERIC_DOCS))
                .andExpect(fieldsInAllResultsExist(NUMBER_OF_GENERIC_DOCS));
    }

    //----- Tests for Annotation Extension ---------------------//

    @Test
    public void filterById() throws Exception {
        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search")
                .param(EXTENSION_PARAM.getName(), EXTENSION_ID1));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(NUMBER_OF_GENERIC_DOCS))
                .andExpect(fieldsInAllResultsExist(NUMBER_OF_GENERIC_DOCS));
    }

    @Test
    public void filterByExtensionTarget1() throws Exception {
        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search")
                .param(EXTENSION_PARAM.getName(), EXTENSION_1));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(NUMBER_OF_GENERIC_DOCS))
                .andExpect(fieldsInAllResultsExist(NUMBER_OF_GENERIC_DOCS));
    }

    @Test
    public void filterByExtensionTarget2() throws Exception {
        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search")
                .param(EXTENSION_PARAM.getName(), EXTENSION_2));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(NUMBER_OF_GENERIC_DOCS))
                .andExpect(fieldsInAllResultsExist(NUMBER_OF_GENERIC_DOCS));
    }

    @Test
    public void filterByExtensionTarget3() throws Exception {
        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search")
                .param(EXTENSION_PARAM.getName(), EXTENSION_3));

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
        doc.extensions = Collections.singletonList(extension);
        repository.save(doc);

        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search")
                .param(EXTENSION_PARAM.getName(), extension));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldsInAllResultsExist(1));
    }

    @Test
    public void filterByMultipleUniqueExtensionTargets() throws Exception {

        String extension1 = "results_in_development_of(UBERON:888888)";
        AnnotationDocument doc = AnnotationDocMocker.createAnnotationDoc("A0A123");
        doc.extensions = Collections.singletonList(extension1);
        repository.save(doc);

        String extension2 = "results_in_development_of(UBERON:999999)";
        doc = AnnotationDocMocker.createAnnotationDoc("A0A123");
        doc.extensions = Collections.singletonList(extension2);
        repository.save(doc);

        String fullSearch = extension1 + "," + extension2;

        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search")
                .param(EXTENSION_PARAM.getName(), fullSearch));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(2))
                .andExpect(fieldsInAllResultsExist(2));
    }

    @Test
    public void multipleMatchingExtensionFilterValuesForAnAnnotationReturnsItOnlyOnce() throws Exception {
        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search")
                .param(EXTENSION_PARAM.getName(), EXTENSIONS.get(0)));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(NUMBER_OF_GENERIC_DOCS))
                .andExpect(fieldsInAllResultsExist(NUMBER_OF_GENERIC_DOCS));
    }

    @Test
    public void exactMatchRequestedThatMatchesExceptForDBReturnsNoAnnotations() throws Exception {
        String filter = asExtension(EXTENSION_RELATIONSHIP1, "SOME_OTHER_DB", EXTENSION_ID1);
        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search")
                .param(EXTENSION_PARAM.getName(), filter));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(0));
    }

    @Test
    public void exactMatchRequestedThatMatchesExceptForIDReturnsNoAnnotations() throws Exception {
        String filter = asExtension(EXTENSION_RELATIONSHIP1, EXTENSION_DB1, "9999999");
        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search")
                .param(EXTENSION_PARAM.getName(), filter));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(0));
    }

    @Test
    public void exactMatchRequestedThatMatchesExceptForRelationshipReturnsNoAnnotations() throws Exception {
        String filter = asExtension("something_syntactically_valid_here", EXTENSION_DB1, EXTENSION_ID1);
        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search")
                .param(EXTENSION_PARAM.getName(), filter));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(0));
    }

    @Test
    public void exactMatchRequestedThatMatchesOnlyRelationshipReturnsNoAnnotations() throws Exception {
        String filter = asExtension(EXTENSION_RELATIONSHIP1, "SOME_OTHER_DB", "9999999");
        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search")
                .param(EXTENSION_PARAM.getName(), filter));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(0));
    }

    @Test
    public void exactMatchRequestedThatMatchesOnlyDBReturnsNoAnnotations() throws Exception {
        String filter = asExtension("unused_valid_relationship", EXTENSION_DB1, "9999999");
        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search")
                .param(EXTENSION_PARAM.getName(), filter));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(0));
    }

    @Test
    public void exactMatchRequestedThatMatchesOnlyIDReturnsNoAnnotations() throws Exception {
        String filter = asExtension("unused_valid_relationship", "SOME_OTHER_DB", EXTENSION_ID1);
        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search")
                .param(EXTENSION_PARAM.getName(), filter));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(0));
    }

    @Test
    public void multipleTargetsRequestedThatExistButInDifferentOrderReturnsAnnotations() throws Exception {
        String filter = String.format("%s,%s", EXTENSION_2, EXTENSION_1);
        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search")
                .param(EXTENSION_PARAM.getName(), filter));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(3));
    }

    @Test
    public void multipleTargetsRequestedButOnlyOneOfWhichExistsReturnsAnnotations() throws Exception {
        String unknownExt = asExtension("unused_valid_relationship", "SOME_OTHER_DB", "9999999");
        String filter = String.format("%s,%s", unknownExt, EXTENSION_1);
        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search")
                .param(EXTENSION_PARAM.getName(), filter));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(3));
    }

    @Test
    public void resultsReturnedInOrderWrittenToSolrRelevancyNotUsed() throws Exception {

        //Create an Annotation with a mixture of new and existing extensions.
        String newExtension = "results_in_development_of(UBERON:8888888)";
        final String newGeneProduct = "A0A123";
        AnnotationDocument doc = AnnotationDocMocker.createAnnotationDoc(newGeneProduct);
        doc.extensions = asList(newExtension, EXTENSION_3);
        repository.save(doc);

        //Although this filter will match the newly added Annotation for two extension strings (the existing test
        // Annotations only have one matching extension) the results will still come back in the standard (written to
        // Solr) order.
        String filter = String.format("%s,%s", newExtension, EXTENSION_3);
        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search")
                .param(EXTENSION_PARAM.getName(), filter));

        String expected = "A0A000";

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(4))
                .andExpect(fieldInRowHasValue("geneProductId", 0, expected));
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

    // ------------------------------- Helpers -------------------------------
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

    private String getRequiredDateString(int year, int month, int date) {
        return String.format(DATE_STRING_FORMAT, year, month, date);
    }

    //----- Setup data ---------------------//

    private List<AnnotationDocument> createGenericDocs(int n) {
        return IntStream.range(0, n)
                .mapToObj(i -> AnnotationDocMocker.createAnnotationDoc(createGPId(i))).collect
                        (Collectors.toList());
    }

    private int totalPages(int totalEntries, int resultsPerPage) {
        return (int) Math.ceil(totalEntries / resultsPerPage) + 1;
    }
}