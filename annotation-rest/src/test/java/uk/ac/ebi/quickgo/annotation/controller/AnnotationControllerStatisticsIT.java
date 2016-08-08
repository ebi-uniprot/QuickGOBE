package uk.ac.ebi.quickgo.annotation.controller;

import uk.ac.ebi.quickgo.annotation.AnnotationREST;
import uk.ac.ebi.quickgo.annotation.common.AnnotationRepository;
import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocMocker;
import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocument;
import uk.ac.ebi.quickgo.annotation.common.document.AnnotationFields;
import uk.ac.ebi.quickgo.common.QuickGODocument;
import uk.ac.ebi.quickgo.common.solr.TemporarySolrDataStore;

import java.util.Collection;
import java.util.List;
import java.util.Set;
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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
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

    private static final String RESOURCE_URL = "/QuickGO/services/annotation";
    private static final String STATS_ENDPOINT = RESOURCE_URL + "/stats";

    private static final int NUMBER_OF_GENERIC_DOCS = 6;
    private static final String ANNOTATION_GROUP = "annotation";
    private static final String GENE_PRODUCT_GROUP = "geneProduct";
    private static final String TAXON_PARAM = "taxon";
    private static final String GO_ID_PARAM = "goId";

    private MockMvc mockMvc;

    private List<AnnotationDocument> savedDocs;

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

        savedDocs = createGenericDocs(NUMBER_OF_GENERIC_DOCS);
        repository.save(savedDocs);
    }

    //----------- Ontology ID -----------//
    @Test
    public void statsForAllDocsContaining1OntologyIdReturns1OntologyIdStat() throws Exception {
        Set<String> savedGOIds = selectValuesFromDocs(savedDocs, doc -> doc.goId);

        String type = AnnotationFields.GO_ID_INDEXED_ORIGINAL;

        ResultActions response = mockMvc.perform(get(STATS_ENDPOINT));

        assertStatsResponse(response, type, NUMBER_OF_GENERIC_DOCS, savedGOIds);
    }

    @Test
    public void statsForAllDocsContaining2OntologyIdsReturns2OntologyIdStats()
            throws Exception {
        AnnotationDocument extraDoc = AnnotationDocMocker.createAnnotationDoc("P99999");
        extraDoc.goId = "GO:0016020";
        repository.save(extraDoc);

        Set<String> savedGOIds = selectValuesFromDocs(savedDocs, doc -> doc.goId);
        savedGOIds.add(extraDoc.goId);

        String type = AnnotationFields.GO_ID_INDEXED_ORIGINAL;

        ResultActions response = mockMvc.perform(get(STATS_ENDPOINT));

        assertStatsResponse(response, type, NUMBER_OF_GENERIC_DOCS + 1, savedGOIds);
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

        String type = AnnotationFields.GO_ID_INDEXED_ORIGINAL;

        ResultActions response = mockMvc.perform(
                get(STATS_ENDPOINT)
                        .param(TAXON_PARAM, "42")
        );

        assertStatsResponse(response, type, 2, relevantGOIds);
    }

    //----------- Taxon ID -----------//
    @Test
    public void statsForAllDocsContaining1TaxonIdReturns1TaxonIdStat() throws Exception {
        Set<String> savedTaxonIds = selectValuesFromDocs(savedDocs, doc -> String.valueOf(doc.taxonId));

        String type = AnnotationFields.TAXON_ID;

        ResultActions response = mockMvc.perform(get(STATS_ENDPOINT));

        assertStatsResponse(response, type, NUMBER_OF_GENERIC_DOCS, savedTaxonIds);
    }

    @Test
    public void statsForAllDocsContaining2TaxonIdsReturns2TaxonIdStats() throws Exception {
        AnnotationDocument extraDoc = AnnotationDocMocker.createAnnotationDoc("P99999");
        extraDoc.taxonId = 7890;
        repository.save(extraDoc);

        Set<String> savedTaxonIds = selectValuesFromDocs(savedDocs, doc -> String.valueOf(doc.taxonId));
        savedTaxonIds.add(String.valueOf(extraDoc.taxonId));

        String type = AnnotationFields.TAXON_ID;

        ResultActions response = mockMvc.perform(get(STATS_ENDPOINT));

        assertStatsResponse(response, type, NUMBER_OF_GENERIC_DOCS + 1, savedTaxonIds);
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

        String type = AnnotationFields.TAXON_ID;

        ResultActions response = mockMvc.perform(
                get(STATS_ENDPOINT)
                        .param(GO_ID_PARAM, filteringGoId)
        );

        assertStatsResponse(response, type, 2, relevantTaxonIds);
    }

    //----------- Reference -----------//
    @Test
    public void statsForAllDocsContaining1ReferenceIdReturns1ReferenceIdStat() throws Exception {
        Set<String> savedReferences = selectValuesFromDocs(savedDocs, doc -> doc.reference);

        String type = AnnotationFields.REFERENCE;

        ResultActions response = mockMvc.perform(get(STATS_ENDPOINT));

        assertStatsResponse(response, type, NUMBER_OF_GENERIC_DOCS, savedReferences);
    }

    @Test
    public void statsForAllDocsContaining2ReferencesReturns2ReferenceStats() throws Exception {
        AnnotationDocument extraDoc = AnnotationDocMocker.createAnnotationDoc("P99999");
        extraDoc.reference = "PMID:19864465";
        repository.save(extraDoc);

        Set<String> savedReferences = selectValuesFromDocs(savedDocs, doc -> doc.reference);
        savedReferences.add(extraDoc.reference);

        String type = AnnotationFields.REFERENCE;

        ResultActions response = mockMvc.perform(get(STATS_ENDPOINT));

        assertStatsResponse(response, type, NUMBER_OF_GENERIC_DOCS + 1, savedReferences);
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

        String type = AnnotationFields.REFERENCE;

        ResultActions response = mockMvc.perform(
                get(STATS_ENDPOINT)
                        .param(GO_ID_PARAM, filteringGoId)
        );

        assertStatsResponse(response, type, 2, relevantReferenceIds);
    }

    //----------- Evidence code -----------//
    @Test
    public void statsForAllDocsContaining1EvidenceCodeReturns1EvidenceCodeStat() throws Exception {
        Set<String> savedEcoEvidenceCodes = selectValuesFromDocs(savedDocs, doc -> doc.evidenceCode);

        String type = AnnotationFields.EVIDENCE_CODE;

        ResultActions response = mockMvc.perform(get(STATS_ENDPOINT));

        assertStatsResponse(response, type, NUMBER_OF_GENERIC_DOCS, savedEcoEvidenceCodes);
    }

    @Test
    public void statsForAllDocsContaining2evidenceCodesReturns2EvidenceCodeStats() throws Exception {
        AnnotationDocument extraDoc = AnnotationDocMocker.createAnnotationDoc("P99999");
        extraDoc.evidenceCode = "ECO:0000888";
        repository.save(extraDoc);

        Set<String> savedEvidenceCodes = selectValuesFromDocs(savedDocs, doc -> doc.evidenceCode);
        savedEvidenceCodes.add(extraDoc.evidenceCode);

        String type = AnnotationFields.EVIDENCE_CODE;

        ResultActions response = mockMvc.perform(get(STATS_ENDPOINT));

        assertStatsResponse(response, type, NUMBER_OF_GENERIC_DOCS + 1, savedEvidenceCodes);
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

        String type = AnnotationFields.EVIDENCE_CODE;

        ResultActions response = mockMvc.perform(
                get(STATS_ENDPOINT)
                        .param(GO_ID_PARAM, filteringGoId)
        );

        assertStatsResponse(response, type, 2, relevantEvidenceCodes);
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
