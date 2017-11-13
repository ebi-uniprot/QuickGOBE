package uk.ac.ebi.quickgo.geneproduct.service.search;

import uk.ac.ebi.quickgo.common.FacetableField;
import uk.ac.ebi.quickgo.common.SearchableField;
import uk.ac.ebi.quickgo.geneproduct.common.GeneProductFields;
import uk.ac.ebi.quickgo.geneproduct.common.GeneProductRepoConfig;
import uk.ac.ebi.quickgo.geneproduct.model.GeneProduct;
import uk.ac.ebi.quickgo.geneproduct.service.converter.GeneProductDocConverterImpl;
import uk.ac.ebi.quickgo.rest.search.RequestRetrieval;
import uk.ac.ebi.quickgo.rest.search.SearchService;
import uk.ac.ebi.quickgo.rest.search.query.QueryRequestConverter;
import uk.ac.ebi.quickgo.rest.search.solr.SolrQueryConverter;
import uk.ac.ebi.quickgo.rest.search.solr.SolrRequestRetrieval;
import uk.ac.ebi.quickgo.rest.search.solr.SolrRetrievalConfig;
import uk.ac.ebi.quickgo.rest.service.ServiceRetrievalConfig;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.solr.core.SolrTemplate;

import static uk.ac.ebi.quickgo.rest.search.solr.SolrRetrievalConfigHelper.DEFAULT_HIGHLIGHT_DELIMS;
import static uk.ac.ebi.quickgo.rest.search.solr.SolrRetrievalConfigHelper.HIGHLIGHT_END_DELIM_INDEX;
import static uk.ac.ebi.quickgo.rest.search.solr.SolrRetrievalConfigHelper.HIGHLIGHT_START_DELIM_INDEX;
import static uk.ac.ebi.quickgo.rest.search.solr.SolrRetrievalConfigHelper.convertHighlightDelims;
import static uk.ac.ebi.quickgo.rest.service.ServiceRetrievalConfigHelper.extractFieldMappings;

/**
 * Spring configuration for the gene product search service.
 *
 * Created 04/04/16
 * @author Edd
 */
@Configuration
@Import({GeneProductRepoConfig.class})
@ComponentScan({"uk.ac.ebi.quickgo.geneproduct.service.search"})
@PropertySource("classpath:search.properties")
public class SearchServiceConfig {

    private static final String SOLR_GENE_PRODUCT_QUERY_REQUEST_HANDLER = "/search";

    private static final String COMMA = ",";
    private static final String DEFAULT_GENE_PRODUCT_SEARCH_RETURN_FIELDS = "id,name,synonym,symbol";

    @Bean
    public SearchService<GeneProduct> geneProductSearchService(RequestRetrieval<GeneProduct>
            geneProductSolrRequestRetrieval) {
        return new SearchServiceImpl(geneProductSolrRequestRetrieval);
    }

    @Bean
    public SearchableField searchableField() {
        return new SearchableField() {
            @Override public boolean isSearchable(String field) {
                return GeneProductFields.Searchable.isSearchable(field);
            }

            @Override public Stream<String> searchableFields() {
                return GeneProductFields.Searchable.searchableFields().stream();
            }
        };
    }

    @Bean
    public FacetableField facetableField() {
        return new FacetableField() {
            @Override public boolean isFacetable(String field) {
                return GeneProductFields.Facetable.isFacetable(field);
            }

            @Override public Stream<String> facetableFields() {
                return GeneProductFields.Facetable.facetableFields().stream();
            }
        };
    }

    @Bean
    public RequestRetrieval<GeneProduct> geneProductSolrRequestRetrieval(
            SolrTemplate geneProductTemplate,
            QueryRequestConverter<SolrQuery> solrSelectQueryRequestConverter,
            GeneProductCompositeRetrievalConfig geneProductRetrievalConfig) {

        GeneProductSolrQueryResultConverter resultConverter = new GeneProductSolrQueryResultConverter(
                new DocumentObjectBinder(),
                new GeneProductDocConverterImpl(),
                geneProductRetrievalConfig.repo2DomainFieldMap()
        );

        return new SolrRequestRetrieval<>(
                geneProductTemplate.getSolrClient(),
                solrSelectQueryRequestConverter,
                resultConverter,
                geneProductRetrievalConfig);
    }

    @Bean
    public QueryRequestConverter<SolrQuery> geneProductSolrQueryRequestConverter() {
        return SolrQueryConverter.create(SOLR_GENE_PRODUCT_QUERY_REQUEST_HANDLER);
    }

    @Bean
    public GeneProductCompositeRetrievalConfig geneProductRetrievalConfig(
            @Value("${search.return.fields:" + DEFAULT_GENE_PRODUCT_SEARCH_RETURN_FIELDS + "}")
                    String geneProductSearchSolrReturnedFields,
            @Value("${search.field.repo2domain.map:}") String geneProductSearchRepo2DomainFieldMap,
            @Value("${search.highlight.delims:" + DEFAULT_HIGHLIGHT_DELIMS + "}") String highlightDelims) {

        String[] highlightDelimsArr = convertHighlightDelims(highlightDelims, COMMA);

        return new GeneProductCompositeRetrievalConfig() {

            @Override public Map<String, String> repo2DomainFieldMap() {
                return extractFieldMappings(geneProductSearchRepo2DomainFieldMap, COMMA);
            }

            @Override public List<String> getSearchReturnedFields() {
                return Arrays.asList(geneProductSearchSolrReturnedFields.split(COMMA));
            }

            @Override public String getHighlightStartDelim() {
                return highlightDelimsArr[HIGHLIGHT_START_DELIM_INDEX];
            }

            @Override public String getHighlightEndDelim() {
                return highlightDelimsArr[HIGHLIGHT_END_DELIM_INDEX];
            }
        };
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    public interface GeneProductCompositeRetrievalConfig extends SolrRetrievalConfig, ServiceRetrievalConfig {}
}
