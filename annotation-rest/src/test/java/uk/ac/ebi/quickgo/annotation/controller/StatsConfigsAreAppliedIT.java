package uk.ac.ebi.quickgo.annotation.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.ac.ebi.quickgo.annotation.AnnotationREST;
import uk.ac.ebi.quickgo.annotation.common.AnnotationDocument;
import uk.ac.ebi.quickgo.annotation.common.AnnotationFields;
import uk.ac.ebi.quickgo.annotation.common.AnnotationRepository;
import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocMocker;
import uk.ac.ebi.quickgo.annotation.service.statistics.RequiredStatisticsProvider;
import uk.ac.ebi.quickgo.annotation.service.statistics.StatisticsTypeConfigurer;
import uk.ac.ebi.quickgo.common.store.TemporarySolrDataStore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocMocker.createGenericDocs;
import static uk.ac.ebi.quickgo.annotation.controller.ResponseVerifier.contentTypeToBeJson;
import static uk.ac.ebi.quickgo.annotation.controller.ResponseVerifier.totalNumOfResults;

/**
 * This class demonstrates the effects of configuring the limits of statistics types. E.g., specifying a limit
 * for the number of GO ids should have the effect of limiting the number of GO ids returned when requests to
 * the statistics end-point are made.
 *
 * Created 16/08/17
 * @author Edd
 */

// temporary data store for solr's data, which is automatically cleaned on exit
@ExtendWith(TemporarySolrDataStore.class)
@SpringBootTest(classes = {AnnotationREST.class, StatsConfigsAreAppliedIT.TestStatsTypeConfig.class})
@WebAppConfiguration
class StatsConfigsAreAppliedIT {
    private static final String GO_ID = "goId";
    private static final String TAXON_ID = "taxonId";

    // the configured stats limits used in this test
    private static final int GO_ID_LIMIT_PROPERTY = 6;
    private static final int GO_ID_LIMIT_PROPERTY_FOR_DOWNLOAD = 10;
    private static final int TAXON_ID_LIMIT_PROPERTY = 7;

    private static final int SAVED_DOC_COUNT = 50;
    private static final int DEFAULT_STATS_TYPE_COUNT = 10;
    private static final String STATS_VALUES_JSON_PATH_FORMAT =
            "$.results[?(@.groupName == '%s')].types[?(@.type == '%s')].values[*].key";
    private static final String STATS_ENDPOINT = "/annotation/stats";
    private static final String ANNOTATION = "annotation";
    private static final String GENE_PRODUCT = "geneProduct";
    private static final String REFERENCE = "reference";
    private MockMvc mockMvc;
    private static final String TAXON_ID_PARAMETER_NAME = "taxonId";

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
    }

    @Test
    void goIdConfigReadAndApplied() throws Exception {
        List<AnnotationDocument> docs =
                createDocsAndApply((i, doc) -> doc.goId = createGOId(i));
        repository.saveAll(docs);

        ResultActions response = mockMvc.perform(get(STATS_ENDPOINT)
                .param(TAXON_ID_PARAMETER_NAME, AnnotationDocMocker.TAXON_ID));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(2))
                .andExpect(jsonPath(statsValuesJSONPath(ANNOTATION, GO_ID), hasSize(GO_ID_LIMIT_PROPERTY)))
                .andExpect(jsonPath(statsValuesJSONPath(GENE_PRODUCT, GO_ID), hasSize(GO_ID_LIMIT_PROPERTY)));
    }

    @Test
    void taxonIdConfigReadAndApplied() throws Exception {
        List<AnnotationDocument> docs =
                createDocsAndApply((i, doc) -> doc.taxonId = i);
        repository.saveAll(docs);

        ResultActions response = mockMvc.perform(get(STATS_ENDPOINT)
                .param(TAXON_ID_PARAMETER_NAME, AnnotationDocMocker.TAXON_ID));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(2))
                .andExpect(jsonPath(statsValuesJSONPath(ANNOTATION, TAXON_ID), hasSize(TAXON_ID_LIMIT_PROPERTY)))
                .andExpect(jsonPath(statsValuesJSONPath(GENE_PRODUCT, TAXON_ID), hasSize(TAXON_ID_LIMIT_PROPERTY)));
    }

    @Test
    void noConfigForReferenceMeansThereAreDefaultNumber() throws Exception {
        List<AnnotationDocument> docs =
                createDocsAndApply((i, doc) -> doc.reference = createRef(i));
        repository.saveAll(docs);

        ResultActions response = mockMvc.perform(get(STATS_ENDPOINT)
                .param(TAXON_ID_PARAMETER_NAME, AnnotationDocMocker.TAXON_ID));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(2))
                .andExpect(jsonPath(statsValuesJSONPath(ANNOTATION, REFERENCE), hasSize(DEFAULT_STATS_TYPE_COUNT)))
                .andExpect(jsonPath(statsValuesJSONPath(GENE_PRODUCT, REFERENCE), hasSize(DEFAULT_STATS_TYPE_COUNT)));
    }

    private String statsValuesJSONPath(String statType, String subType) {
        return STATS_VALUES_JSON_PATH_FORMAT.formatted(statType, subType);
    }

    /**
     * Creates a list of {@link AnnotationDocument}s and applies a transformation to each document
     * according to their index in this document list.
     *
     * @param docTransformer the document transformation function
     * @return a list of {@link AnnotationDocument}s
     */
    private List<AnnotationDocument> createDocsAndApply(BiConsumer<Integer, AnnotationDocument> docTransformer) {
        List<AnnotationDocument> docs = createGenericDocs(SAVED_DOC_COUNT);
        for (int i = 0; i < docs.size(); i++) {
            docTransformer.accept(i, docs.get(i));
        }
        return docs;
    }

    private String createRef(int i) {
        return REFERENCE + i;
    }

    private String createGOId(int idNum) {
        return "GO:000%03d".formatted(idNum);
    }

    /**
     * <p>This class configures the statistics type limits for the purposes of this test, which would otherwise
     * be configured from a properties file.
     *
     * <p>The "goID" and "taxonId" statistics types are limited here explicitly. All other statistics types will
     * not be explicitly limited, and therefore will return the default number of values, i.e., 10.
     */
    @Configuration
    static class TestStatsTypeConfig {

        static final Map<String, Integer> typeLimitProperties = typeLimitTestValues();
        static final Map<String, Integer> typeLimitTestValuesForDownload = typeLimitTestValuesForDownload();

        @Primary
        @Bean
        public RequiredStatisticsProvider requiredStatisticsProvider() {
            return new RequiredStatisticsProvider(new StatisticsTypeConfigurer(typeLimitProperties),
                    new StatisticsTypeConfigurer(typeLimitTestValuesForDownload));
        }

        private static Map<String, Integer> typeLimitTestValues() {
            Map<String, Integer> properties = new HashMap<>();
            properties.put(AnnotationFields.Facetable.GO_ID, GO_ID_LIMIT_PROPERTY);
            properties.put(AnnotationFields.Facetable.TAXON_ID, TAXON_ID_LIMIT_PROPERTY);
            return properties;
        }

        private static Map<String, Integer> typeLimitTestValuesForDownload() {
            Map<String, Integer> properties = new HashMap<>();
            properties.put(AnnotationFields.Facetable.GO_ID, GO_ID_LIMIT_PROPERTY_FOR_DOWNLOAD);
            properties.put(AnnotationFields.Facetable.TAXON_ID, TAXON_ID_LIMIT_PROPERTY);
            return properties;
        }
    }
}
