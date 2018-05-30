package uk.ac.ebi.quickgo.annotation.service.search;

import uk.ac.ebi.quickgo.annotation.common.AnnotationFields;
import uk.ac.ebi.quickgo.annotation.common.AnnotationRepoConfig;
import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.geneproduct.transformer.GeneProductNameInjector;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.geneproduct.transformer.GeneProductSynonymsInjector;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.annotation.OntologyNameInjector;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.annotation.SlimResultsTransformer;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.annotation.TaxonomyNameInjector;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.completablevalue.EvidenceNameInjector;
import uk.ac.ebi.quickgo.annotation.service.converter.AnnotationDocConverterImpl;
import uk.ac.ebi.quickgo.common.SearchableField;
import uk.ac.ebi.quickgo.common.loader.DbXRefLoader;
import uk.ac.ebi.quickgo.common.validator.DbXRefEntityValidation;
import uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelper;
import uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelperImpl;
import uk.ac.ebi.quickgo.rest.model.CompletableValue;
import uk.ac.ebi.quickgo.rest.search.RequestRetrieval;
import uk.ac.ebi.quickgo.rest.search.SearchService;
import uk.ac.ebi.quickgo.rest.search.query.QueryRequestConverter;
import uk.ac.ebi.quickgo.rest.search.query.SortCriterion;
import uk.ac.ebi.quickgo.rest.search.request.converter.RESTFilterConverterFactory;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;
import uk.ac.ebi.quickgo.rest.search.results.config.FieldNameTransformer;
import uk.ac.ebi.quickgo.rest.search.results.transformer.*;
import uk.ac.ebi.quickgo.rest.search.solr.SolrQueryConverter;
import uk.ac.ebi.quickgo.rest.search.solr.SolrRequestRetrieval;
import uk.ac.ebi.quickgo.rest.search.solr.SolrRetrievalConfig;
import uk.ac.ebi.quickgo.rest.search.solr.UnsortedSolrQuerySerializer;
import uk.ac.ebi.quickgo.rest.service.ServiceRetrievalConfig;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.sf.ehcache.CacheManager;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.solr.core.SolrTemplate;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

/**
 * Spring Configuration facilitating Annotation search functionality.
 *
 * @author Tony Wardell
 *         Date: 26/04/2016
 *         Time: 15:07
 *         Created with IntelliJ IDEA.
 */
@Configuration
@Import({AnnotationRepoConfig.class})
@ComponentScan({"uk.ac.ebi.quickgo.annotation.service.search"})
@PropertySource("classpath:search.properties")
@EnableCaching
public class SearchServiceConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchServiceConfig.class);
    private static final int MAX_PAGE_RESULTS = 100;

    private static final boolean DEFAULT_XREF_VALIDATION_IS_CASE_SENSITIVE = true;
    private static final String COMMA = ",";
    private static final String DEFAULT_UNSORTED_QUERY_FIELDS =
            "assignedBy_unsorted,dbSubset_unsorted,evidenceCode_unsorted,goEvidence_unsorted," +
                    "goId_unsorted,geneProductId_unsorted,geneProductType_unsorted," +
                    "qualifier_unsorted,targetSet_unsorted,taxonId_unsorted,extension_unsorted";
    private static final String DEFAULT_ANNOTATION_SEARCH_RETURN_FIELDS =
            "id,geneProductId,qualifier,goId,goEvidence," +
                    "evidenceCode,reference,withFrom,taxonId,assignedBy,extension,symbol,geneProductId," +
                    "interactingTaxonId,date";
    private static final String SOLR_ANNOTATION_QUERY_REQUEST_HANDLER = "/query";
    private static final String DEFAULT_DOWNLOAD_SORT_FIELDS = "rowNumber,id";
    private static final int DEFAULT_DOWNLOAD_PAGE_SIZE = 500;
    private static final String CACHE_CONFIG_FILE = "ehcache.xml";

    @Value("${geneproduct.db.xref.valid.regexes}")
    String xrefValidationRegexFile;
    @Value("${geneproduct.db.xref.valid.casesensitive:" + DEFAULT_XREF_VALIDATION_IS_CASE_SENSITIVE + "}")
    boolean xrefValidationCaseSensitive;

    @Value("${annotation.terms.query.compatible.fields:" + DEFAULT_UNSORTED_QUERY_FIELDS + "}")
    private String fieldsThatCanBeUnsorted;

    @Value("${annotation.download.sort.fields:" + DEFAULT_DOWNLOAD_SORT_FIELDS + "}")
    private String defaultDownloadSortFields;

    @Value("${annotation.download.pageSize:" + DEFAULT_DOWNLOAD_PAGE_SIZE + "}")
    private int downloadPageSize;

    @Value("${search.wildcard.fields:}")
    private String fieldsThatCanBeSearchedByWildCard;

    @Value("${cache.config.path:" + CACHE_CONFIG_FILE + "}")
    private String cacheConfigPath;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public SearchService<Annotation> annotationSearchService(
            RequestRetrieval<Annotation> annotationSolrRequestRetrieval) {
        return new SearchServiceImpl(annotationSolrRequestRetrieval);
    }

    @Bean
    public RequestRetrieval<Annotation> annotationSolrRequestRetrieval(
            SolrTemplate annotationTemplate,
            QueryRequestConverter<SolrQuery> queryRequestConverter,
            AnnotationCompositeRetrievalConfig annotationRetrievalConfig) {

        SolrQueryResultConverter resultConverter = new SolrQueryResultConverter(
                new DocumentObjectBinder(),
                new AnnotationDocConverterImpl(),
                annotationRetrievalConfig);

        return new SolrRequestRetrieval<>(
                annotationTemplate.getSolrClient(),
                queryRequestConverter,
                resultConverter,
                annotationRetrievalConfig);
    }

    @Bean
    public ControllerValidationHelper validationHelper() {
        return new ControllerValidationHelperImpl(MAX_PAGE_RESULTS);
    }

    @Bean
    public QueryRequestConverter<SolrQuery> annotationSolrQueryRequestConverter() {
        Set<String> unsortedFields =
                Stream.of(fieldsThatCanBeUnsorted.split(COMMA)).collect(Collectors.toSet());
        Set<String> wildCardFields =
                Stream.of(fieldsThatCanBeSearchedByWildCard.split(COMMA)).collect(Collectors.toSet());
        return new SolrQueryConverter(
                SOLR_ANNOTATION_QUERY_REQUEST_HANDLER,
                new UnsortedSolrQuerySerializer(unsortedFields, wildCardFields));
    }

    /**
     * Annotation retrieval config. Annotations searches don't use highlighting.
     *
     * @param annotationSearchSolrReturnedFields A list of fields that can be used for filtering.
     * @return An instance of AnnotationCompositeRetrievalConfig which meets the requirements of the Configuration
     * interfaces it extends.
     */
    @Bean
    public AnnotationCompositeRetrievalConfig annotationRetrievalConfig(
            @Value("${search.return.fields:" + DEFAULT_ANNOTATION_SEARCH_RETURN_FIELDS + "}") String
                    annotationSearchSolrReturnedFields,
            FieldNameTransformer fieldNameTransformer) {

        return new AnnotationCompositeRetrievalConfig() {

            @Override
            public List<SortCriterion> getDownloadSortCriteria() {
                return Stream.of(defaultDownloadSortFields.split(COMMA))
                        .map(downloadSortField -> new SortCriterion(downloadSortField, SortCriterion.SortOrder.ASC))
                        .collect(Collectors.toList());
            }

            @Override public int getDownloadPageSize() {
                return downloadPageSize;
            }

            @Override
            public Map<String, String> repo2DomainFieldMap() {
                return fieldNameTransformer.getTransformations();
            }

            @Override
            public List<String> getSearchReturnedFields() {
                return asList(annotationSearchSolrReturnedFields.split(COMMA));
            }

            //Not called
            @Override
            public String getHighlightStartDelim() {
                return "";
            }

            //Not called
            @Override
            public String getHighlightEndDelim() {
                return "";
            }
        };
    }

    @Bean
    public ResultTransformerChain<QueryResult<Annotation>> resultTransformerChain(
            ExternalServiceResultsTransformer<QueryResult<Annotation>, Annotation> ontologyResultsTransformer,
            ExternalServiceResultsTransformer<QueryResult<Annotation>, Annotation> geneProductResultsTransformer) {
        ResultTransformerChain<QueryResult<Annotation>> transformerChain = new ResultTransformerChain<>();
        transformerChain.addTransformer(new SlimResultsTransformer());
        transformerChain.addTransformer(ontologyResultsTransformer);
        transformerChain.addTransformer(geneProductResultsTransformer);
        return transformerChain;
    }

    @Bean
    public ExternalServiceResultsTransformer<QueryResult<Annotation>, Annotation> ontologyResultsTransformer
            (RESTFilterConverterFactory converterFactory) {
        List<ResponseValueInjector<Annotation>> responseValueInjectors = asList(
                new OntologyNameInjector(),
                new TaxonomyNameInjector());
        return new ExternalServiceResultsTransformer<>(responseValueInjectors, queryResultMutator(converterFactory));
    }

    @Bean
    public ExternalServiceResultsTransformer<QueryResult<Annotation>, Annotation> geneProductResultsTransformer
            (RESTFilterConverterFactory converterFactory) {
        List<ResponseValueInjector<Annotation>> responseValueInjectors = asList(
                new GeneProductNameInjector(),
                new GeneProductSynonymsInjector());
        return new ExternalServiceResultsTransformer<>(responseValueInjectors, queryResultMutator(converterFactory));
    }

    @Bean
    public DbXRefEntityValidation geneProductValidator() {
        return DbXRefEntityValidation.createWithData(geneProductLoader().load());
    }

    @Bean
    public SearchableField annotationSearchableField() {
        return new SearchableField() {
            @Override
            public boolean isSearchable(String field) {
                return AnnotationFields.Searchable.isSearchable(field);
            }

            @Override
            public Stream<String> searchableFields() {
                return AnnotationFields.Searchable.searchableFields().stream();
            }
        };
    }

    @Bean
    public ResultTransformerChain<CompletableValue> completableValueResultTransformerChain(
            ExternalServiceResultsTransformer<CompletableValue, CompletableValue> ontologyNameTransformer,
            ExternalServiceResultsTransformer<CompletableValue, CompletableValue> taxonNameTransformer,
            ExternalServiceResultsTransformer<CompletableValue, CompletableValue> evidenceNameTransformer) {
        ResultTransformerChain<CompletableValue> transformerChain = new ResultTransformerChain<>();
        transformerChain.addTransformer(ontologyNameTransformer);
        transformerChain.addTransformer(taxonNameTransformer);
        transformerChain.addTransformer(evidenceNameTransformer);
        return transformerChain;
    }

    @Bean
    public EhCacheCacheManager cacheManager(CacheManager cm) {
        return new EhCacheCacheManager(cm);
    }

    @Bean
    public EhCacheManagerFactoryBean ehCache() {
        EhCacheManagerFactoryBean factoryBean = new EhCacheManagerFactoryBean();
        try {
            if (Objects.nonNull(cacheConfigPath)) {
                FileSystemResource fileSystemResource = new FileSystemResource(cacheConfigPath);
                if (fileSystemResource.exists() && fileSystemResource.isReadable()) {
                    factoryBean.setConfigLocation(fileSystemResource);
                    return factoryBean;
                }
            }
        } catch (Exception e) {
            LOGGER.error(String.format("Failed to load cache configuration file from %s", cacheConfigPath));
        }
        //Failed to load config file, so use the version bundled with this jar
        factoryBean.setConfigLocation(new ClassPathResource(CACHE_CONFIG_FILE));
        return factoryBean;
    }

    @Bean
    public NameService nameService(ResultTransformerChain<CompletableValue> completableValueResultTransformerChain) {
        return new NameService(completableValueResultTransformerChain);
    }

    @Bean
    public ExternalServiceResultsTransformer<CompletableValue, CompletableValue> ontologyNameTransformer
            (RESTFilterConverterFactory converterFactory) {
        List<ResponseValueInjector<CompletableValue>> responseValueInjectors =
                singletonList(new uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer
                        .completablevalue.OntologyNameInjector());
        return new ExternalServiceResultsTransformer<>(responseValueInjectors,
                completableValueResultMutator(converterFactory));
    }

    @Bean
    public ValueInjectionToSingleResult<CompletableValue> completableValueResultMutator(
            RESTFilterConverterFactory converterFactory) {
        return new ValueInjectionToSingleResult<>(converterFactory);
    }

    @Bean
    public ExternalServiceResultsTransformer<CompletableValue, CompletableValue> taxonNameTransformer
            (RESTFilterConverterFactory converterFactory) {
        List<ResponseValueInjector<CompletableValue>> responseValueInjectors = singletonList(
                new uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.completablevalue
                        .TaxonomyNameInjector());
        return new ExternalServiceResultsTransformer<>(responseValueInjectors,
                completableValueResultMutator(converterFactory));
    }

    @Bean
    public ExternalServiceResultsTransformer<CompletableValue, CompletableValue> evidenceNameTransformer
            (RESTFilterConverterFactory converterFactory) {
        List<ResponseValueInjector<CompletableValue>> responseValueInjectors = singletonList(
                new EvidenceNameInjector());
        return new ExternalServiceResultsTransformer<>(responseValueInjectors,
                completableValueResultMutator(converterFactory));
    }

    private ValueInjectionToQueryResults<Annotation> queryResultMutator(
            RESTFilterConverterFactory converterFactory) {
        return new ValueInjectionToQueryResults<>(converterFactory);
    }

    private DbXRefLoader geneProductLoader() {
        return new DbXRefLoader(this.xrefValidationRegexFile, xrefValidationCaseSensitive);
    }

    public interface AnnotationCompositeRetrievalConfig extends SolrRetrievalConfig, ServiceRetrievalConfig {
        List<SortCriterion> getDownloadSortCriteria();

        int getDownloadPageSize();
    }
}
