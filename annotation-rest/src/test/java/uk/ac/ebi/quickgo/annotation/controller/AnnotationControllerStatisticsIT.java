package uk.ac.ebi.quickgo.annotation.controller;

import uk.ac.ebi.quickgo.annotation.AnnotationREST;
import uk.ac.ebi.quickgo.annotation.common.AnnotationDocument;
import uk.ac.ebi.quickgo.annotation.common.AnnotationRepository;
import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocMocker;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.ConvertedOntologyFilter;
import uk.ac.ebi.quickgo.common.QuickGODocument;
import uk.ac.ebi.quickgo.common.solr.TemporarySolrDataStore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
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
    private RestOperations restOperations;
    private MockRestServiceServer mockRestServiceServer;
    private ObjectMapper dtoMapper;

    private static final String GO_DESCENDANTS_RESOURCE_FORMAT = "/ontology/go/terms/%s/descendants?relations=%s";
    private String resourceFormat = GO_DESCENDANTS_RESOURCE_FORMAT;
    private static final String BASE_URL = "http://localhost";
    private static final String COMMA = ",";

    @Before
    public void setup() {
        repository.deleteAll();

        mockMvc = MockMvcBuilders.
                webAppContextSetup(webApplicationContext)
                .build();

        savedDocs = createGenericDocs(NUMBER_OF_GENERIC_DOCS);
        repository.save(savedDocs);

        mockRestServiceServer = MockRestServiceServer.createServer((RestTemplate) restOperations);
        dtoMapper = new ObjectMapper();
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

        expectRestCallHasDescendants(
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

        expectRestCallHasDescendants(
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

        expectRestCallHasDescendants(
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

        expectRestCallHasDescendants(
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

        expectRestCallHasDescendants(
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

    private String buildResource(String format, String... arguments) {
        int requiredArgsCount = format.length() - format.replace("%", "").length();
        List<String> args = new ArrayList<>();
        for (int i = 0; i < requiredArgsCount; i++) {
            if (i < arguments.length) {
                args.add(arguments[i]);
            } else {
                args.add("");
            }
        }
        return String.format(format, args.toArray());
    }

    private void expectRestCallHasDescendants(
            List<String> termIds,
            List<String> usageRelations,
            List<List<String>> descendants) {
        String termIdsCSV = termIds.stream().collect(Collectors.joining(COMMA));
        String relationsCSV = usageRelations.stream().collect(Collectors.joining(COMMA));

        expectRestCallSuccess(
                GET,
                buildResource(
                        resourceFormat,
                        termIdsCSV,
                        relationsCSV
                ),
                constructResponseObject(termIds, descendants)
        );
    }

    private String constructResponseObject(List<String> termIds, List<List<String>> descendants) {
        checkArgument(termIds != null, "termIds cannot be null");
        checkArgument(descendants != null, "descendants cannot be null");
        checkArgument(termIds.size() == descendants.size(), "Term ID list and the (list of lists) of their " +
                "descendants should be the same size");

        ConvertedOntologyFilter response = new ConvertedOntologyFilter();
        List<ConvertedOntologyFilter.Result> results = new ArrayList<>();

        Iterator<List<String>> descendantListsIterator = descendants.iterator();
        termIds.forEach(t -> {
            ConvertedOntologyFilter.Result result = new ConvertedOntologyFilter.Result();
            result.setId(t);
            result.setDescendants(descendantListsIterator.next());
            results.add(result);
        });

        response.setResults(results);
        try {
            return dtoMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Problem constructing mocked REST response:", e);
        }
    }

    private void expectRestCallSuccess(HttpMethod method, String url, String response) {
        mockRestServiceServer.expect(
                requestTo(BASE_URL + url))
                             .andExpect(method(method))
                             .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));
    }
}
