package uk.ac.ebi.quickgo.annotation.controller;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.cache.CacheManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
import uk.ac.ebi.quickgo.common.QuickGODocument;
import uk.ac.ebi.quickgo.common.store.TemporarySolrDataStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.ac.ebi.quickgo.annotation.AnnotationParameters.*;
import static uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocMocker.*;
import static uk.ac.ebi.quickgo.annotation.controller.ResponseVerifier.contentTypeToBeJson;
import static uk.ac.ebi.quickgo.annotation.controller.ResponseVerifier.totalNumOfResults;
import static uk.ac.ebi.quickgo.annotation.controller.StatsResponseVerifier.*;

/**
 * Tests the behaviour of the statistics endpoint of the {@link AnnotationController}.
 */
// temporary data store for solr's data, which is automatically cleaned on exit
@ExtendWith(TemporarySolrDataStore.class)
@SpringBootTest(classes = {AnnotationREST.class})
@WebAppConfiguration
class AnnotationControllerStatisticsIT {
    private static final String RESOURCE_URL = "/annotation";
    private static final String STATS_ENDPOINT = RESOURCE_URL + "/stats";
    private static final String GO_TERM_NAME = "catalytic activity";
    private static final String ECO_TERM_NAME = "match to sequence model evidence used in automatic assertion";
    private static final String TAXON_TERM_NAME = "taxon name: " + 12345;

    private static final int NUMBER_OF_GENERIC_DOCS = 6;
    private static final String ANNOTATION_GROUP = "annotation";
    private static final String GENE_PRODUCT_GROUP = "geneProduct";

    //statistics model fields
    private static final String ASSIGNED_BY_STATS_FIELD = "assignedBy";
    private static final String EVIDENCE_CODE_STATS_FIELD = "evidenceCode";
    private static final String GO_ID_STATS_FIELD = "goId";
    private static final String REFERENCE_STATS_FIELD = "reference";
    private static final String TAXON_ID_STATS_FIELD = "taxonId";
    private static final String GO_ASPECT_STATS_FIELD = "aspect";
    private static final String EXACT_USAGE = "exact";
    private static final String APPROXIMATE_COUNT = "approximateCount";
    private static final String TAXON_NAME = "taxon name: " + TAXON_ID;
    private static final String GENE_PRODUCT_ID_STATS_FIELD = "geneProductId";
    public static final int EXPECTED_NUMBER_OF_TYPES = 6;
    public static final int EXPECTED_NUMBER_OF_TYPES_IF_FILTERING_BY_GENE_PRODUCT = 7;

    private MockMvc mockMvc;
    @Autowired
    CacheManager cacheManager;
    @Autowired
    private RestOperations restOperations;
    private MockRestServiceServer mockRestServiceServer;


    private List<AnnotationDocument> savedDocs;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AnnotationRepository repository;

    @BeforeEach
    void setup() {
        repository.deleteAll();

        mockMvc = MockMvcBuilders.
                webAppContextSetup(webApplicationContext)
                .build();
        mockRestServiceServer = MockRestServiceServer.createServer((RestTemplate) restOperations);
        savedDocs = createGenericDocs();
        repository.saveAll(savedDocs);
    }

    @Test
    void statsRequestFailsIfNoFilteringParametersAreIncluded() throws Exception {
        ResultActions response = mockMvc.perform(get(STATS_ENDPOINT));

        response.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages",
                        hasItem(containsString("Statistics requests require at least one filtering parameter."))));
    }

    @Test
    void queryWithNoHitsProducesEmptyStats() throws Exception {
        repository.deleteAll();

        ResultActions response = mockMvc.perform(get(STATS_ENDPOINT)
                .param(TAXON_ID_STATS_FIELD, AnnotationDocMocker.TAXON_ID));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(0));
    }

    //----------- Ontology ID -----------//
    @Test
    void statsForAllDocsContaining1OntologyIdReturns1OntologyIdStat() throws Exception {
        executesAndAssertsCalculatedStatsForAttribute(GO_ID_STATS_FIELD, savedDocs, doc -> doc.goId, 1);
    }

    @Test
    void statsForAllDocsContaining2OntologyIdsReturns2OntologyIdStats() throws Exception {
        AnnotationDocument extraDoc = createAnnotationDoc("P99999");
        extraDoc.goId = "GO:0016020";
        repository.save(extraDoc);

        List<AnnotationDocument> savedDocsPlusOne = new ArrayList<>(savedDocs);
        savedDocsPlusOne.add(extraDoc);

        executesAndAssertsCalculatedStatsForAttribute(GO_ID_STATS_FIELD, savedDocsPlusOne, doc -> doc.goId, 2);
    }

    @Test
    void statsForFilteredDocsContaining2OntologyIdsReturns2OntologyIdStats() throws Exception {
        AnnotationDocument extraDoc1 = createAnnotationDoc("P99999");
        extraDoc1.goId = "GO:1111111";
        extraDoc1.taxonId = 42;
        repository.save(extraDoc1);

        AnnotationDocument extraDoc2 = createAnnotationDoc("P99998");
        extraDoc2.goId = "GO:2222222";
        extraDoc2.taxonId = 42;
        repository.save(extraDoc2);

        List<String> relevantGOIds = asList(extraDoc1.goId, extraDoc2.goId);

        ResultActions response = mockMvc.perform(
                get(STATS_ENDPOINT)
                        .param(TAXON_ID_PARAM.getName(), "42")
                        .param(TAXON_USAGE_PARAM.getName(), EXACT_USAGE)
        );

        assertStatsResponse(response, GO_ID_STATS_FIELD, 2, relevantGOIds, 2, 6);
    }

    //----------- Names for GO ids, taxon ids and eco codes -----------//
    @Test
    void namesForGoIdsAndTaxonIdsAndEvidenceCodes() throws Exception {
        cacheManager.getCache("names").clear();
        final int expectedDistinctValueCount = 1;
        StatsSetupHelper statsSetupHelper = new StatsSetupHelper(mockRestServiceServer);
        statsSetupHelper.expectGoTermHasNameViaRest(GO_ID, GO_TERM_NAME);
        statsSetupHelper.expectTaxonIdHasNameViaRest(TAXON_ID, TAXON_NAME);
        statsSetupHelper.expectEcoCodeHasNameViaRest(ECO_ID, ECO_TERM_NAME, expectedDistinctValueCount);

        assertStatsResponseIncludingNames(expectedDistinctValueCount);
    }

    //----------- Taxon ID -----------//
    @Test
    void statsForAllDocsContaining1TaxonIdReturns1TaxonIdStat() throws Exception {
        executesAndAssertsCalculatedStatsForAttribute(TAXON_ID_STATS_FIELD, savedDocs,
                doc -> String.valueOf(doc.taxonId), 1);
    }

    @Test
    void statsForAllDocsContaining2TaxonIdsReturns2TaxonIdStats() throws Exception {
        AnnotationDocument extraDoc = createAnnotationDoc("P99999");
        extraDoc.taxonId = 7890;
        repository.save(extraDoc);

        List<AnnotationDocument> savedDocsPlusOne = new ArrayList<>(savedDocs);
        savedDocsPlusOne.add(extraDoc);

        executesAndAssertsCalculatedStatsForAttribute(TAXON_ID_STATS_FIELD, savedDocsPlusOne,
                doc -> String.valueOf(doc.taxonId), 2);
    }

    @Test
    void statsForFilteredDocsContaining2TaxonIdsReturns2TaxonIdStats() throws Exception {
        String filteringGoId = "GO:9999999";

        AnnotationDocument extraDoc1 = createAnnotationDoc("P99999");
        extraDoc1.goId = filteringGoId;
        extraDoc1.taxonId = 9999;
        repository.save(extraDoc1);

        AnnotationDocument extraDoc2 = createAnnotationDoc("P99998");
        extraDoc2.goId = filteringGoId;
        extraDoc2.taxonId = 9998;
        repository.save(extraDoc2);

        List<String> relevantTaxonIds = asList(String.valueOf(extraDoc1.taxonId), String.valueOf(extraDoc2.taxonId));

        ResultActions response = mockMvc.perform(
                get(STATS_ENDPOINT)
                        .param(GO_ID_PARAM.getName(), filteringGoId)
                        .param(GO_USAGE_PARAM.getName(), EXACT_USAGE)
        );

        assertStatsResponse(response, TAXON_ID_STATS_FIELD, 2, relevantTaxonIds, 2, EXPECTED_NUMBER_OF_TYPES);
    }

    //----------- Reference -----------//
    @Test
    void statsForAllDocsContaining1ReferenceIdReturns1ReferenceIdStat() throws Exception {
        executesAndAssertsCalculatedStatsForAttribute(REFERENCE_STATS_FIELD, savedDocs, doc -> doc.reference, 1);
    }

    @Test
    void statsForAllDocsContaining2ReferencesReturns2ReferenceStats() throws Exception {
        AnnotationDocument extraDoc = createAnnotationDoc("P99999");
        extraDoc.reference = "PMID:19864465";
        repository.save(extraDoc);

        List<AnnotationDocument> savedDocsPlusOne = new ArrayList<>(savedDocs);
        savedDocsPlusOne.add(extraDoc);

        executesAndAssertsCalculatedStatsForAttribute(REFERENCE_STATS_FIELD, savedDocsPlusOne, doc -> doc.reference, 2);
    }

    @Test
    void statsForFilteredDocsContaining2RefernecesReturns2ReferenceStats() throws Exception {
        String filteringGoId = "GO:9999999";

        AnnotationDocument extraDoc1 = createAnnotationDoc("P99999");
        extraDoc1.goId = filteringGoId;
        extraDoc1.reference = "PMID:19864465";
        repository.save(extraDoc1);

        AnnotationDocument extraDoc2 = createAnnotationDoc("P99998");
        extraDoc2.goId = filteringGoId;
        extraDoc2.reference = "GO_REF:0000020";
        repository.save(extraDoc2);

        List<String> relevantReferenceIds = asList(extraDoc1.reference, extraDoc2.reference);

        ResultActions response = mockMvc.perform(
                get(STATS_ENDPOINT)
                        .param(GO_ID_PARAM.getName(), filteringGoId)
                        .param(GO_USAGE_PARAM.getName(), EXACT_USAGE)
        );

        assertStatsResponse(response, REFERENCE_STATS_FIELD, 2, relevantReferenceIds, 2, 6);
    }

    //----------- Evidence code -----------//
    @Test
    void statsForAllDocsContaining1EvidenceCodeReturns1EvidenceCodeStat() throws Exception {
        executesAndAssertsCalculatedStatsForAttribute(EVIDENCE_CODE_STATS_FIELD, savedDocs, doc -> doc.evidenceCode,
                1);
    }

    @Test
    void statsForAllDocsContaining2evidenceCodesReturns2EvidenceCodeStats() throws Exception {
        AnnotationDocument extraDoc = createAnnotationDoc("P99999");
        extraDoc.evidenceCode = "ECO:0000888";
        repository.save(extraDoc);

        List<AnnotationDocument> savedDocsPlusOne = new ArrayList<>(savedDocs);
        savedDocsPlusOne.add(extraDoc);

        executesAndAssertsCalculatedStatsForAttribute(EVIDENCE_CODE_STATS_FIELD, savedDocsPlusOne,
                doc -> doc.evidenceCode, 2);
    }

    @Test
    void statsForFilteredDocsContaining2EvidenceCodesReturns2EvidenceCodesStats() throws Exception {
        String filteringGoId = "GO:9999999";

        AnnotationDocument extraDoc1 = createAnnotationDoc("P99999");
        extraDoc1.goId = filteringGoId;
        extraDoc1.evidenceCode = "ECO:0000888";
        repository.save(extraDoc1);

        AnnotationDocument extraDoc2 = createAnnotationDoc("P99998");
        extraDoc2.goId = filteringGoId;
        extraDoc2.evidenceCode = "ECO:0000777";
        repository.save(extraDoc2);

        List<String> relevantEvidenceCodes = asList(extraDoc1.evidenceCode, extraDoc2.evidenceCode);

        ResultActions response = mockMvc.perform(
                get(STATS_ENDPOINT)
                        .param(GO_ID_PARAM.getName(), filteringGoId)
                        .param(GO_USAGE_PARAM.getName(), EXACT_USAGE)
        );

        assertStatsResponse(response, EVIDENCE_CODE_STATS_FIELD, 2, relevantEvidenceCodes, 2, 6);
    }

    //----------- Assigned by -----------//
    @Test
    void statsForAllDocsContaining1AssignedByReturns1AssignedByStat() throws Exception {
        executesAndAssertsCalculatedStatsForAttribute(ASSIGNED_BY_STATS_FIELD, savedDocs, doc -> doc.assignedBy, 1);
    }

    @Test
    void statsForAllDocsContaining2AssignedByReturns2AssignedByStats() throws Exception {
        AnnotationDocument extraDoc = createAnnotationDoc("P99999");
        extraDoc.assignedBy = "Agbase";
        repository.save(extraDoc);

        List<AnnotationDocument> savedDocsPlusOne = new ArrayList<>(savedDocs);
        savedDocsPlusOne.add(extraDoc);

        executesAndAssertsCalculatedStatsForAttribute(ASSIGNED_BY_STATS_FIELD, savedDocsPlusOne, doc -> doc
                .assignedBy, 2);
    }

    @Test
    void statsForFilteredDocsContaining2AssignedByReturns2AssignedByStats() throws Exception {
        String filteringGoId = "GO:9999999";

        AnnotationDocument extraDoc1 = createAnnotationDoc("P99999");
        extraDoc1.goId = filteringGoId;
        extraDoc1.assignedBy = "Agbase";
        repository.save(extraDoc1);

        AnnotationDocument extraDoc2 = createAnnotationDoc("P99998");
        extraDoc2.goId = filteringGoId;
        extraDoc2.assignedBy = "Roslin_Institute";
        repository.save(extraDoc2);

        List<String> relevantAssignedBy = asList(extraDoc1.assignedBy, extraDoc2.assignedBy);

        ResultActions response = mockMvc.perform(
                get(STATS_ENDPOINT)
                        .param(GO_ID_PARAM.getName(), filteringGoId)
                        .param(GO_USAGE_PARAM.getName(), EXACT_USAGE)
        );

        assertStatsResponse(response, ASSIGNED_BY_STATS_FIELD, 2, relevantAssignedBy, 2, 6);
    }

    //----------- GO aspect -----------//
    @Test
    void statsForAllDocsContaining1AspectReturns1AspectByStat() throws Exception {
        executesAndAssertsCalculatedStatsForAttribute(GO_ASPECT_STATS_FIELD, savedDocs, doc -> doc.goAspect, 1);
    }

    @Test
    void statsForAllDocsContaining2AspectsReturns2AspectsStats() throws Exception {
        AnnotationDocument extraDoc = createAnnotationDoc("P99999");
        extraDoc.goAspect = "molecular_function";
        repository.save(extraDoc);

        List<AnnotationDocument> savedDocsPlusOne = new ArrayList<>(savedDocs);
        savedDocsPlusOne.add(extraDoc);

        executesAndAssertsCalculatedStatsForAttribute(GO_ASPECT_STATS_FIELD, savedDocsPlusOne, doc -> doc.goAspect, 2);
    }

    @Test
    void statsForFilteredDocsContaining2AspectsReturns2AspectStats() throws Exception {
        String filteringGoId = "GO:9999999";

        AnnotationDocument extraDoc1 = createAnnotationDoc("P99999");
        extraDoc1.goId = filteringGoId;
        extraDoc1.goAspect = "molecular_function";
        repository.save(extraDoc1);

        AnnotationDocument extraDoc2 = createAnnotationDoc("P99998");
        extraDoc2.goId = filteringGoId;
        extraDoc2.goAspect = "cellular_component";
        repository.save(extraDoc2);

        List<String> relevantAspect = asList(extraDoc1.goAspect, extraDoc2.goAspect);

        ResultActions response = mockMvc.perform(
                get(STATS_ENDPOINT)
                        .param(GO_ID_PARAM.getName(), filteringGoId)
                        .param(GO_USAGE_PARAM.getName(), EXACT_USAGE)
        );

        assertStatsResponse(response, GO_ASPECT_STATS_FIELD, 2, relevantAspect, 2, 6);
    }

    //----------- Gene Product Id -----------//

    @Test
    void statsWhenFilteredByGeneProductContainsGeneProductIdBucket() throws Exception {
        final String gene = "P99999";
        repository.save(createAnnotationDoc(gene));

        ResultActions response = mockMvc.perform(
                get(STATS_ENDPOINT)
                        .param(GENE_PRODUCT_ID_PARAM.getName(), gene)
        );

        assertStatsResponse(response, GENE_PRODUCT_ID_STATS_FIELD, 1, singletonList(gene), 1,
                EXPECTED_NUMBER_OF_TYPES_IF_FILTERING_BY_GENE_PRODUCT);
    }

    private static <T, D extends QuickGODocument> Set<T> selectValuesFromDocs(
            Collection<D> documents,
            Function<D, T> docTransformation) {
        return documents.stream().map(docTransformation).collect(Collectors.toSet());
    }

    /**
     * Extracts all values of a given attribute, within all saved document, and then checks to see if the statistics run
     * on that attribute are correct.
     *
     * @param attribute the attribute to run statistics on
     * @param docs a collection of docs that the stats will be run on
     * @param extractAttributeValuesFromDoc a function to extract the values of {@code statsType} from the saved
     * documents
     * @throws Exception if an error occurs whilst saving or retrieving documents
     */
    private void executesAndAssertsCalculatedStatsForAttribute(String attribute, Collection<AnnotationDocument> docs,
            Function<AnnotationDocument, String> extractAttributeValuesFromDoc, int expectedDistinctValueCount)
            throws Exception {
        Set<String> extractedAttributeValues = selectValuesFromDocs(docs, extractAttributeValuesFromDoc);

        ResultActions response = mockMvc.perform(get(STATS_ENDPOINT)
                .param(TAXON_ID_STATS_FIELD, AnnotationDocMocker.TAXON_ID));

        assertStatsResponse(response, attribute, docs.size(), extractedAttributeValues, expectedDistinctValueCount,
                6);
    }

    private void assertStatsResponse(ResultActions response, String statsType, int totalHits,
            Collection<String> statsValues, int expectedDistinctValueCount, int expectedNumberOfTypes)
            throws Exception {
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(2))
                .andExpect(totalHitsInGroup(ANNOTATION_GROUP, totalHits))
                .andExpect(totalHitsInGroup(GENE_PRODUCT_GROUP, totalHits))
                .andExpect(numberOfTypes(ANNOTATION_GROUP, expectedNumberOfTypes))
                .andExpect(numberOfTypes(GENE_PRODUCT_GROUP, expectedNumberOfTypes))
                .andExpect(numericValueForGroup(ANNOTATION_GROUP, statsType, APPROXIMATE_COUNT,
                        expectedDistinctValueCount))
                .andExpect(keysInTypeWithinGroup(ANNOTATION_GROUP, statsType, asArray(statsValues)))
                .andExpect(keysInTypeWithinGroup(GENE_PRODUCT_GROUP, statsType, asArray(statsValues)));
    }

    private void assertStatsResponseIncludingNames(int expectedDistinctValueCount) throws Exception {
        ResultActions response = mockMvc.perform(get(STATS_ENDPOINT)
                .param(TAXON_ID_STATS_FIELD, AnnotationDocMocker.TAXON_ID));

        final String[] goNames = expectedNames(expectedDistinctValueCount, GO_TERM_NAME);
        final String[] taxonNames = expectedNames(expectedDistinctValueCount, TAXON_TERM_NAME);
        final String[] ecoNames = expectedNames(expectedDistinctValueCount, ECO_TERM_NAME);

        response.andDo(print())
                .andExpect(namesInTypeWithinGroup(ANNOTATION_GROUP, GO_ID_STATS_FIELD, goNames))
                .andExpect(namesInTypeWithinGroup(GENE_PRODUCT_GROUP, GO_ID_STATS_FIELD, goNames))
                .andExpect(namesInTypeWithinGroup(ANNOTATION_GROUP, TAXON_ID_STATS_FIELD, taxonNames))
                .andExpect(namesInTypeWithinGroup(GENE_PRODUCT_GROUP, TAXON_ID_STATS_FIELD, taxonNames))
                .andExpect(namesInTypeWithinGroup(ANNOTATION_GROUP, EVIDENCE_CODE_STATS_FIELD, ecoNames))
                .andExpect(namesInTypeWithinGroup(GENE_PRODUCT_GROUP, EVIDENCE_CODE_STATS_FIELD, ecoNames));
    }

    private String[] asArray(Collection<String> elements) {
        return elements.toArray(new String[0]);
    }

    private List<AnnotationDocument> createGenericDocs() {
        return IntStream.range(0, AnnotationControllerStatisticsIT.NUMBER_OF_GENERIC_DOCS)
                .mapToObj(i -> createAnnotationDoc(createId(i)))
                .collect(Collectors.toList());
    }

    private String createId(int idNum) {
        return "A0A%03d".formatted(idNum);
    }

    private String[] expectedNames(int expectedSize, String source) {
        String[] names = new String[expectedSize];
        IntStream.range(0, names.length)
                .forEach(i -> names[i] = source);
        return names;
    }
}
