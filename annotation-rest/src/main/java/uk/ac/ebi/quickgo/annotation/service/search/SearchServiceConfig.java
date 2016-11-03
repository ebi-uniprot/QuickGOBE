package uk.ac.ebi.quickgo.annotation.service.search;

import uk.ac.ebi.quickgo.annotation.common.AnnotationRepoConfig;
import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.SlimResultsTransformer;
import uk.ac.ebi.quickgo.annotation.service.converter.AnnotationDocConverterImpl;
import uk.ac.ebi.quickgo.common.loader.DbXRefLoader;
import uk.ac.ebi.quickgo.common.validator.DbXRefEntityValidation;
import uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelper;
import uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelperImpl;
import uk.ac.ebi.quickgo.rest.search.RequestRetrieval;
import uk.ac.ebi.quickgo.rest.search.SearchService;
import uk.ac.ebi.quickgo.rest.search.query.QueryRequestConverter;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;
import uk.ac.ebi.quickgo.rest.search.results.config.FieldNameTransformer;
import uk.ac.ebi.quickgo.rest.search.results.transformer.ResultTransformerChain;
import uk.ac.ebi.quickgo.rest.search.solr.SolrQueryConverter;
import uk.ac.ebi.quickgo.rest.search.solr.SolrRequestRetrieval;
import uk.ac.ebi.quickgo.rest.search.solr.SolrRetrievalConfig;
import uk.ac.ebi.quickgo.rest.search.solr.UnsortedSolrQuerySerializer;
import uk.ac.ebi.quickgo.rest.service.ServiceRetrievalConfig;

import java.util.Arrays;
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

/**
 *
 * Spring Configuration facilitating Annotation search functionality.
 *
 * @author Tony Wardell
 * Date: 26/04/2016
 * Time: 15:07
 * Created with IntelliJ IDEA.
 */
@Configuration
@Import({AnnotationRepoConfig.class})
@ComponentScan({"uk.ac.ebi.quickgo.annotation.service.search"})
@PropertySource("classpath:search.properties")
public class SearchServiceConfig {

    public static final int MAX_PAGE_RESULTS = 100;

    private static final boolean DEFAULT_XREF_VALIDATION_IS_CASE_SENSITIVE = true;
    private static final String COMMA = ",";
    private static final String DEFAULT_UNSORTED_QUERY_FIELDS =
            "assignedBy_unsorted,dbSubset_unsorted,evidenceCode_unsorted,goEvidence_unsorted," +
                    "goId_unsorted,geneProductId_unsorted,geneProductType_unsorted," +
                    "qualifier_unsorted,targetSet_unsorted,taxonId_unsorted";
    private static final String DEFAULT_ANNOTATION_SEARCH_RETURN_FIELDS =
            "id,geneProductId,qualifier,goId,goEvidence," +
                    "evidenceCode,reference,withFrom,taxonId,assignedBy,extensions,symbol";
    private static final String SOLR_ANNOTATION_QUERY_REQUEST_HANDLER = "/query";

    @Value("${geneproduct.db.xref.valid.regexes}")
    String xrefValidationRegexFile;
    @Value("${geneproduct.db.xref.valid.casesensitive:" + DEFAULT_XREF_VALIDATION_IS_CASE_SENSITIVE + "}")
    boolean xrefValidationCaseSensitive;

    @Value("${annotation.terms.query.compatible.fields:" +
            DEFAULT_UNSORTED_QUERY_FIELDS + "}")
    private String fieldsThatCanBeUnsorted;

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

            @Override public Map<String, String> repo2DomainFieldMap() {
                return fieldNameTransformer.getTransformations();
            }

            @Override public List<String> getSearchReturnedFields() {
                return Arrays.asList(annotationSearchSolrReturnedFields.split(COMMA));
            }

            //Not called
            @Override public String getHighlightStartDelim() {
                return "";
            }

            //Not called
            @Override public String getHighlightEndDelim() {
                return "";
            }
        };
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public ResultTransformerChain<QueryResult<Annotation>> resultTransformerChain() {
        ResultTransformerChain<QueryResult<Annotation>> transformerChain = new ResultTransformerChain<>();
        transformerChain.addTransformer(new SlimResultsTransformer());
        return transformerChain;
    }

    @Bean
    public DbXRefEntityValidation geneProductValidator() {
        return DbXRefEntityValidation.createWithData(geneProductLoader().load());
    }

    private DbXRefLoader geneProductLoader() {
        return new DbXRefLoader(this.xrefValidationRegexFile, xrefValidationCaseSensitive);
    }

    public interface AnnotationCompositeRetrievalConfig extends SolrRetrievalConfig, ServiceRetrievalConfig {}
}
