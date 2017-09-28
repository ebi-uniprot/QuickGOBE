package uk.ac.ebi.quickgo.annotation.service.search;

import uk.ac.ebi.quickgo.annotation.common.AnnotationFields;
import uk.ac.ebi.quickgo.annotation.common.AnnotationRepoConfig;
import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.geneproduct.transformer.GeneProductNameInjector;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.geneproduct.transformer.GeneProductSynonymsInjector;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.OntologyNameInjector;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.SlimResultsTransformer;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.TaxonomyNameInjector;
import uk.ac.ebi.quickgo.annotation.service.converter.AnnotationDocConverterImpl;
import uk.ac.ebi.quickgo.common.SearchableField;
import uk.ac.ebi.quickgo.common.loader.DbXRefLoader;
import uk.ac.ebi.quickgo.common.validator.DbXRefEntityValidation;
import uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelper;
import uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelperImpl;
import uk.ac.ebi.quickgo.rest.search.RequestRetrieval;
import uk.ac.ebi.quickgo.rest.search.SearchService;
import uk.ac.ebi.quickgo.rest.search.query.QueryRequestConverter;
import uk.ac.ebi.quickgo.rest.search.query.SortCriterion;
import uk.ac.ebi.quickgo.rest.search.request.converter.RESTFilterConverterFactory;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;
import uk.ac.ebi.quickgo.rest.search.results.config.FieldNameTransformer;
import uk.ac.ebi.quickgo.rest.search.results.transformer.ExternalServiceResultsTransformer;
import uk.ac.ebi.quickgo.rest.search.results.transformer.ResponseValueInjector;
import uk.ac.ebi.quickgo.rest.search.results.transformer.ResultTransformerChain;
import uk.ac.ebi.quickgo.rest.search.solr.SolrQueryConverter;
import uk.ac.ebi.quickgo.rest.search.solr.SolrRequestRetrieval;
import uk.ac.ebi.quickgo.rest.search.solr.SolrRetrievalConfig;
import uk.ac.ebi.quickgo.rest.search.solr.UnsortedSolrQuerySerializer;
import uk.ac.ebi.quickgo.rest.service.ServiceRetrievalConfig;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.solr.core.SolrTemplate;

import static java.util.Arrays.asList;

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
public class SearchServiceConfig {

    private static final int MAX_PAGE_RESULTS = 100;

    private static final boolean DEFAULT_XREF_VALIDATION_IS_CASE_SENSITIVE = true;
    private static final String COMMA = ",";
    private static final String DEFAULT_UNSORTED_QUERY_FIELDS =
            "assignedBy_unsorted,dbSubset_unsorted,evidenceCode_unsorted,goEvidence_unsorted," +
                    "goId_unsorted,geneProductId_unsorted,geneProductType_unsorted," +
                    "qualifier_unsorted,targetSet_unsorted,taxonId_unsorted";
    private static final String DEFAULT_ANNOTATION_SEARCH_RETURN_FIELDS =
            "id,geneProductId,qualifier,goId,goEvidence," +
                    "evidenceCode,reference,withFrom,taxonId,assignedBy,extensions,symbol,geneProductId";
    private static final String SOLR_ANNOTATION_QUERY_REQUEST_HANDLER = "/query";
    private static final String DEFAULT_DOWNLOAD_SORT_FIELDS = "rowNumber,id";
    private static final int DEFAULT_DOWNLOAD_PAGE_SIZE = 500;
    private static final int DEFAULT_STATISTICS_LIMIT = 50000;

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

    @Value("${annotation.statistics.limit:" + DEFAULT_STATISTICS_LIMIT + "}")
    private int statisticsLimit;

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

        return new SolrQueryConverter(
                SOLR_ANNOTATION_QUERY_REQUEST_HANDLER,
                new UnsortedSolrQuerySerializer(unsortedFields));
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
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public ResultTransformerChain<QueryResult<Annotation>> resultTransformerChain(
            ExternalServiceResultsTransformer<Annotation> ontologyResultsTransformer,
            ExternalServiceResultsTransformer<Annotation> geneProductResultsTransformer) {
        ResultTransformerChain<QueryResult<Annotation>> transformerChain = new ResultTransformerChain<>();
        transformerChain.addTransformer(new SlimResultsTransformer());
        transformerChain.addTransformer(ontologyResultsTransformer);
        transformerChain.addTransformer(geneProductResultsTransformer);
        return transformerChain;
    }

    @Bean
    public ExternalServiceResultsTransformer<Annotation> ontologyResultsTransformer(RESTFilterConverterFactory
    restFilterConverterFactory) {
        List<ResponseValueInjector<Annotation>> responseValueInjectors = asList(
                new OntologyNameInjector(),
                new TaxonomyNameInjector());
        return new ExternalServiceResultsTransformer<>(restFilterConverterFactory, responseValueInjectors);
    }

    @Bean
    public ExternalServiceResultsTransformer<Annotation> geneProductResultsTransformer(RESTFilterConverterFactory
            restFilterConverterFactory) {
        List<ResponseValueInjector<Annotation>> responseValueInjectors = asList(
                new GeneProductNameInjector(),
                new GeneProductSynonymsInjector());
        return new ExternalServiceResultsTransformer<>(restFilterConverterFactory, responseValueInjectors);
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
    public StatisticsSearchConfig statisticsDownloadConfig() {
        return () -> statisticsLimit;
    }

    private DbXRefLoader geneProductLoader() {
        return new DbXRefLoader(this.xrefValidationRegexFile, xrefValidationCaseSensitive);
    }

    public interface AnnotationCompositeRetrievalConfig extends SolrRetrievalConfig, ServiceRetrievalConfig {
        List<SortCriterion> getDownloadSortCriteria();
        int getDownloadPageSize();
    }

    public interface StatisticsSearchConfig {
        long defaultDownloadLimit();
    }
}
