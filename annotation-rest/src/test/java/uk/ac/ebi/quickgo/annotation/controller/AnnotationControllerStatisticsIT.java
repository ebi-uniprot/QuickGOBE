package uk.ac.ebi.quickgo.annotation.controller;

import uk.ac.ebi.quickgo.annotation.AnnotationREST;
import uk.ac.ebi.quickgo.annotation.common.AnnotationDocument;
import uk.ac.ebi.quickgo.annotation.common.AnnotationRepository;
import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocMocker;
import uk.ac.ebi.quickgo.common.QuickGODocument;
import uk.ac.ebi.quickgo.common.solr.TemporarySolrDataStore;

import java.util.*;
import java.util.function.Function;
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

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.ac.ebi.quickgo.annotation.AnnotationParameters.GO_ID_PARAM;
import static uk.ac.ebi.quickgo.annotation.AnnotationParameters.TAXON_ID_PARAM;
import static uk.ac.ebi.quickgo.annotation.controller.ResponseVerifier.contentTypeToBeJson;
import static uk.ac.ebi.quickgo.annotation.controller.ResponseVerifier.totalNumOfResults;
import static uk.ac.ebi.quickgo.annotation.controller.StatsResponseVerifier.keysInTypeWithinGroup;
import static uk.ac.ebi.quickgo.annotation.controller.StatsResponseVerifier.totalHitsInGroup;

/**
 * Tests the behaviour of the statistics endpoint of the {@link AnnotationController}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {AnnotationREST.class})
@WebAppConfiguration
public class AnnotationControllerStatisticsIT {
    // temporary data store for solr's data, which is automatically cleaned on exit
    @ClassRule
    public static final TemporarySolrDataStore solrDataStore = new TemporarySolrDataStore();

    private static final String RESOURCE_URL = "/annotation";
    private static final String STATS_ENDPOINT = RESOURCE_URL + "/stats";

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

    private MockMvc mockMvc;

    private List<AnnotationDocument> savedDocs;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AnnotationRepository repository;

    @Autowired
    private RestTestSupport goRestTestSupport;

    @Before
    public void setup() {
        repository.deleteAll();

        mockMvc = MockMvcBuilders.
                webAppContextSetup(webApplicationContext)
                .build();

        savedDocs = createGenericDocs(NUMBER_OF_GENERIC_DOCS);
        repository.save(savedDocs);
    }

    @Test
    public void queryWithNoHitsProducesEmptyStats() throws Exception {
        repository.deleteAll();

        ResultActions response = mockMvc.perform(get(STATS_ENDPOINT));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(0));
    }

    //----------- Ontology ID -----------//
    @Test
    public void statsForAllDocsContaining1OntologyIdReturns1OntologyIdStat() throws Exception {
        executesAndAssertsCalculatedStatsForAttribute(GO_ID_STATS_FIELD, savedDocs, doc -> doc.goId);
    }

    @Test
    public void statsForAllDocsContaining2OntologyIdsReturns2OntologyIdStats() throws Exception {
        AnnotationDocument extraDoc = AnnotationDocMocker.createAnnotationDoc("P99999");
        extraDoc.goId = "GO:0016020";
        repository.save(extraDoc);

        List<AnnotationDocument> savedDocsPlusOne = new ArrayList<>(savedDocs);
        savedDocsPlusOne.add(extraDoc);

        executesAndAssertsCalculatedStatsForAttribute(GO_ID_STATS_FIELD, savedDocsPlusOne, doc -> doc.goId);
    }

    @Test
    public void statsForFilteredDocsContaining2OntologyIdsReturns2OntologyIdStats() throws Exception {
        AnnotationDocument extraDoc1 = AnnotationDocMocker.createAnnotationDoc("P99999");
        extraDoc1.goId = "GO:1111111";
        extraDoc1.taxonId = 42;
        repository.save(extraDoc1);

        AnnotationDocument extraDoc2 = AnnotationDocMocker.createAnnotationDoc("P99998");
        extraDoc2.goId = "GO:2222222";
        extraDoc2.taxonId = 42;
        repository.save(extraDoc2);

        List<String> relevantGOIds = asList(extraDoc1.goId, extraDoc2.goId);

        ResultActions response = mockMvc.perform(
                get(STATS_ENDPOINT)
                        .param(TAXON_ID_PARAM.getName(), "42")
        );

        assertStatsResponse(response, GO_ID_STATS_FIELD, 2, relevantGOIds);
    }

    //----------- Taxon ID -----------//
    @Test
    public void statsForAllDocsContaining1TaxonIdReturns1TaxonIdStat() throws Exception {
        executesAndAssertsCalculatedStatsForAttribute(TAXON_ID_STATS_FIELD, savedDocs,
                doc -> String.valueOf(doc.taxonId));
    }

    @Test
    public void statsForAllDocsContaining2TaxonIdsReturns2TaxonIdStats() throws Exception {
        AnnotationDocument extraDoc = AnnotationDocMocker.createAnnotationDoc("P99999");
        extraDoc.taxonId = 7890;
        repository.save(extraDoc);

        List<AnnotationDocument> savedDocsPlusOne = new ArrayList<>(savedDocs);
        savedDocsPlusOne.add(extraDoc);

        executesAndAssertsCalculatedStatsForAttribute(TAXON_ID_STATS_FIELD, savedDocsPlusOne,
                doc -> String.valueOf(doc.taxonId));
    }

    @Test
    public void statsForFilteredDocsContaining2TaxonIdsReturns2TaxonIdStats() throws Exception {
        String filteringGoId = "GO:9999999";

        AnnotationDocument extraDoc1 = AnnotationDocMocker.createAnnotationDoc("P99999");
        extraDoc1.goId = filteringGoId;
        extraDoc1.taxonId = 9999;
        repository.save(extraDoc1);

        AnnotationDocument extraDoc2 = AnnotationDocMocker.createAnnotationDoc("P99998");
        extraDoc2.goId = filteringGoId;
        extraDoc2.taxonId = 9998;
        repository.save(extraDoc2);

        List<String> relevantTaxonIds = asList(String.valueOf(extraDoc1.taxonId), String.valueOf(extraDoc2.taxonId));

        goRestTestSupport.expectRestCallHasDescendants(
                singletonList(filteringGoId),
                emptyList(),
                singletonList(singletonList(filteringGoId)));

        ResultActions response = mockMvc.perform(
                get(STATS_ENDPOINT)
                        .param(GO_ID_PARAM.getName(), filteringGoId)
        );

        assertStatsResponse(response, TAXON_ID_STATS_FIELD, 2, relevantTaxonIds);
    }

    //----------- Reference -----------//
    @Test
    public void statsForAllDocsContaining1ReferenceIdReturns1ReferenceIdStat() throws Exception {
        executesAndAssertsCalculatedStatsForAttribute(REFERENCE_STATS_FIELD, savedDocs, doc -> doc.reference);
    }

    @Test
    public void statsForAllDocsContaining2ReferencesReturns2ReferenceStats() throws Exception {
        AnnotationDocument extraDoc = AnnotationDocMocker.createAnnotationDoc("P99999");
        extraDoc.reference = "PMID:19864465";
        repository.save(extraDoc);

        List<AnnotationDocument> savedDocsPlusOne = new ArrayList<>(savedDocs);
        savedDocsPlusOne.add(extraDoc);

        executesAndAssertsCalculatedStatsForAttribute(REFERENCE_STATS_FIELD, savedDocsPlusOne, doc -> doc.reference);
    }

    @Test
    public void statsForFilteredDocsContaining2RefernecesReturns2ReferenceStats() throws Exception {
        String filteringGoId = "GO:9999999";

        AnnotationDocument extraDoc1 = AnnotationDocMocker.createAnnotationDoc("P99999");
        extraDoc1.goId = filteringGoId;
        extraDoc1.reference = "PMID:19864465";
        repository.save(extraDoc1);

        AnnotationDocument extraDoc2 = AnnotationDocMocker.createAnnotationDoc("P99998");
        extraDoc2.goId = filteringGoId;
        extraDoc2.reference = "GO_REF:0000020";
        repository.save(extraDoc2);

        List<String> relevantReferenceIds = asList(extraDoc1.reference, extraDoc2.reference);

        goRestTestSupport.expectRestCallHasDescendants(
                singletonList(filteringGoId),
                emptyList(),
                singletonList(singletonList(filteringGoId)));

        ResultActions response = mockMvc.perform(
                get(STATS_ENDPOINT)
                        .param(GO_ID_PARAM.getName(), filteringGoId)
        );

        assertStatsResponse(response, REFERENCE_STATS_FIELD, 2, relevantReferenceIds);
    }

    //----------- Evidence code -----------//
    @Test
    public void statsForAllDocsContaining1EvidenceCodeReturns1EvidenceCodeStat() throws Exception {
        executesAndAssertsCalculatedStatsForAttribute(EVIDENCE_CODE_STATS_FIELD, savedDocs, doc -> doc.evidenceCode);
    }

    @Test
    public void statsForAllDocsContaining2evidenceCodesReturns2EvidenceCodeStats() throws Exception {
        AnnotationDocument extraDoc = AnnotationDocMocker.createAnnotationDoc("P99999");
        extraDoc.evidenceCode = "ECO:0000888";
        repository.save(extraDoc);

        List<AnnotationDocument> savedDocsPlusOne = new ArrayList<>(savedDocs);
        savedDocsPlusOne.add(extraDoc);

        executesAndAssertsCalculatedStatsForAttribute(EVIDENCE_CODE_STATS_FIELD, savedDocsPlusOne,
                doc -> doc.evidenceCode);
    }

    @Test
    public void statsForFilteredDocsContaining2EvidenceCodesReturns2EvidenceCodesStats() throws Exception {
        String filteringGoId = "GO:9999999";

        AnnotationDocument extraDoc1 = AnnotationDocMocker.createAnnotationDoc("P99999");
        extraDoc1.goId = filteringGoId;
        extraDoc1.evidenceCode = "ECO:0000888";
        repository.save(extraDoc1);

        AnnotationDocument extraDoc2 = AnnotationDocMocker.createAnnotationDoc("P99998");
        extraDoc2.goId = filteringGoId;
        extraDoc2.evidenceCode = "ECO:0000777";
        repository.save(extraDoc2);

        List<String> relevantEvidenceCodes = asList(extraDoc1.evidenceCode, extraDoc2.evidenceCode);

        goRestTestSupport.expectRestCallHasDescendants(
                singletonList(filteringGoId),
                emptyList(),
                singletonList(singletonList(filteringGoId)));

        ResultActions response = mockMvc.perform(
                get(STATS_ENDPOINT)
                        .param(GO_ID_PARAM.getName(), filteringGoId)
        );

        assertStatsResponse(response, EVIDENCE_CODE_STATS_FIELD, 2, relevantEvidenceCodes);
    }

    //----------- Assigned by -----------//
    @Test
    public void statsForAllDocsContaining1AssignedByReturns1AssignedByStat() throws Exception {
        executesAndAssertsCalculatedStatsForAttribute(ASSIGNED_BY_STATS_FIELD, savedDocs, doc -> doc.assignedBy);
    }

    @Test
    public void statsForAllDocsContaining2AssignedByReturns2AssignedByStats() throws Exception {
        AnnotationDocument extraDoc = AnnotationDocMocker.createAnnotationDoc("P99999");
        extraDoc.assignedBy = "Agbase";
        repository.save(extraDoc);

        List<AnnotationDocument> savedDocsPlusOne = new ArrayList<>(savedDocs);
        savedDocsPlusOne.add(extraDoc);

        executesAndAssertsCalculatedStatsForAttribute(ASSIGNED_BY_STATS_FIELD, savedDocsPlusOne, doc -> doc.assignedBy);
    }

    @Test
    public void statsForFilteredDocsContaining2AssignedByReturns2AssignedByStats() throws Exception {
        String filteringGoId = "GO:9999999";

        AnnotationDocument extraDoc1 = AnnotationDocMocker.createAnnotationDoc("P99999");
        extraDoc1.goId = filteringGoId;
        extraDoc1.assignedBy = "Agbase";
        repository.save(extraDoc1);

        AnnotationDocument extraDoc2 = AnnotationDocMocker.createAnnotationDoc("P99998");
        extraDoc2.goId = filteringGoId;
        extraDoc2.assignedBy = "Roslin_Institute";
        repository.save(extraDoc2);

        List<String> relevantAssignedBy = asList(extraDoc1.assignedBy, extraDoc2.assignedBy);

        goRestTestSupport.expectRestCallHasDescendants(
                singletonList(filteringGoId),
                emptyList(),
                singletonList(singletonList(filteringGoId)));

        ResultActions response = mockMvc.perform(
                get(STATS_ENDPOINT)
                        .param(GO_ID_PARAM.getName(), filteringGoId)
        );

        assertStatsResponse(response, ASSIGNED_BY_STATS_FIELD, 2, relevantAssignedBy);
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
            Function<AnnotationDocument, String> extractAttributeValuesFromDoc) throws Exception {
        Set<String> extractedAttributeValues = selectValuesFromDocs(docs, extractAttributeValuesFromDoc);

        ResultActions response = mockMvc.perform(get(STATS_ENDPOINT));

        assertStatsResponse(response, attribute, docs.size(), extractedAttributeValues);
    }

    private void assertStatsResponse(ResultActions response, String statsType, int totalHits,
            Collection<String> statsValues) throws Exception {
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(2))
                .andExpect(totalHitsInGroup(ANNOTATION_GROUP, totalHits))
                .andExpect(totalHitsInGroup(GENE_PRODUCT_GROUP, totalHits))
                .andExpect(keysInTypeWithinGroup(ANNOTATION_GROUP, statsType, asArray(statsValues)))
                .andExpect(keysInTypeWithinGroup(GENE_PRODUCT_GROUP, statsType, asArray(statsValues)));
    }

    //----------- GO aspect -----------//
    @Test
    public void statsForAllDocsContaining1AspectReturns1AspectByStat() throws Exception {
        executesAndAssertsCalculatedStatsForAttribute(GO_ASPECT_STATS_FIELD, savedDocs, doc -> doc.goAspect);
    }

    @Test
    public void statsForAllDocsContaining2AspectsReturns2AspectsStats() throws Exception {
        AnnotationDocument extraDoc = AnnotationDocMocker.createAnnotationDoc("P99999");
        extraDoc.goAspect = "molecular_function";
        repository.save(extraDoc);

        List<AnnotationDocument> savedDocsPlusOne = new ArrayList<>(savedDocs);
        savedDocsPlusOne.add(extraDoc);

        executesAndAssertsCalculatedStatsForAttribute(GO_ASPECT_STATS_FIELD, savedDocsPlusOne, doc -> doc.goAspect);
    }

    @Test
    public void statsForFilteredDocsContaining2AspectsReturns2AspectStats() throws Exception {
        String filteringGoId = "GO:9999999";

        AnnotationDocument extraDoc1 = AnnotationDocMocker.createAnnotationDoc("P99999");
        extraDoc1.goId = filteringGoId;
        extraDoc1.goAspect = "molecular_function";
        repository.save(extraDoc1);

        AnnotationDocument extraDoc2 = AnnotationDocMocker.createAnnotationDoc("P99998");
        extraDoc2.goId = filteringGoId;
        extraDoc2.goAspect = "cellular_component";
        repository.save(extraDoc2);

        List<String> relevantAspect = asList(extraDoc1.goAspect, extraDoc2.goAspect);

        goRestTestSupport.expectRestCallHasDescendants(
                singletonList(filteringGoId),
                emptyList(),
                singletonList(singletonList(filteringGoId)));

        ResultActions response = mockMvc.perform(
                get(STATS_ENDPOINT)
                        .param(GO_ID_PARAM.getName(), filteringGoId)
        );

        assertStatsResponse(response, GO_ASPECT_STATS_FIELD, 2, relevantAspect);
    }

    private String[] asArray(Collection<String> elements) {
        return elements.stream().toArray(String[]::new);
    }

    private static <T, D extends QuickGODocument> Set<T> selectValuesFromDocs(
            Collection<D> documents,
            Function<D, T> docTransformation) {
        return documents.stream().map(docTransformation).collect(Collectors.toSet());
    }

    private List<AnnotationDocument> createGenericDocs(int n) {
        return IntStream.range(0, n)
                .mapToObj(i -> AnnotationDocMocker.createAnnotationDoc(createId(i)))
                .collect(Collectors.toList());
    }

    private String createId(int idNum) {
        return String.format("A0A%03d", idNum);
    }
}
