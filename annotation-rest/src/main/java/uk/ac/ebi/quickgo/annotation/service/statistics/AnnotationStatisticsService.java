package uk.ac.ebi.quickgo.annotation.service.statistics;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationFields;
import uk.ac.ebi.quickgo.annotation.model.*;
import uk.ac.ebi.quickgo.rest.search.FacetedSearchQueryTemplate;
import uk.ac.ebi.quickgo.rest.search.SearchService;
import uk.ac.ebi.quickgo.rest.search.request.converter.RequestConverterFactory;
import uk.ac.ebi.quickgo.rest.search.results.AggregationResult;
import uk.ac.ebi.quickgo.rest.search.results.Aggregation;
import uk.ac.ebi.quickgo.rest.search.results.AggregationBucket;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import com.google.common.base.Preconditions;
import java.util.*;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static uk.ac.ebi.quickgo.rest.search.AggregateFunction.*;

/**
 * Service that collects distribution statistics of annotations and gene products throughout a given set of annotation
 * fields.
 *
 * @author Ricardo Antunes
 */
@Service
public class AnnotationStatisticsService implements StatisticsService {
    private static final int FIRST_PAGE = 1;
    private static final int RESULTS_PER_PAGE = 0;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private RequestConverterFactory converterFactory;

    private FacetedSearchQueryTemplate queryTemplate;

    private SearchService searchService;

    @Override public QueryResult<StatisticsGroup> calculate(AnnotationRequest request) {

        //        QueryRequest queryRequest = queryTemplate.newBuilder()
        //                .setFacets(null)
        //                .setQuery(QuickGOQuery.createAllQuery())
        //                .setFilters(request.createRequestFilters().stream()
        //                        .map(converterFactory::convertAggregationPerField)
        //                        .collect(Collectors.toSet()))
        //                .setPage(FIRST_PAGE)
        //                .setPageSize(RESULTS_PER_PAGE)
        //
        //                .build();

        List<Annotation> results = new ArrayList<>();

        QueryResult<Annotation> annotationQueryResult = new QueryResult.Builder<>(6, results)
                .appendAggregations(mockResponse())
                .build();

        List<Aggregation> aggregations = annotationQueryResult.getAggregations();

        //TODO: remove this check, put something that makes more sense.
        Preconditions.checkArgument(aggregations.size() == 1, "Aggregation size is not 1: " + aggregations.size());

        Aggregation globalAggregation = aggregations.get(0);

        GlobalCounts globalCounts = extractGlobalCounts(globalAggregation);

        StatisticsConverter annotationConverter = new StatisticsConverter(AnnotationFields.ID, globalCounts
                .annotations, "annotation");
        StatisticsConverter geneProductConverter = new StatisticsConverter(AnnotationFields.GENE_PRODUCT_ID, globalCounts
                .geneProducts,
                "geneProduct");

        StatisticsGroup annotationGroup = annotationConverter.convert(globalAggregation.getNestedAggregations());
        StatisticsGroup geneProductGroup = geneProductConverter.convert(globalAggregation.getNestedAggregations());

        return new QueryResult.Builder<>(2L, Arrays.asList(annotationGroup, geneProductGroup)).build();
    }

    /**
     * Extracts the counts made on the whole data set for:
     * <ul>
     *     <li>annotations</li>
     *     <li>gene products</li>
     * </ul>
     *
     * @param globalAggregation the aggregation object containing the count values
     * @return an object containing the global counts of things that are of interest
     */
    private GlobalCounts extractGlobalCounts(Aggregation globalAggregation) {
        GlobalCounts globalCounts = new GlobalCounts();

        //TODO: remove DRY when extracting Optional
        globalCounts.annotations = globalAggregation.getAggregationResult(COUNT, AnnotationFields.ID)
                .map(agg -> (long) agg.getResult()).orElseThrow(() ->
                        new IllegalArgumentException("Annotation count is null"));

        globalCounts.geneProducts = globalAggregation.getAggregationResult(COUNT, AnnotationFields.GENE_PRODUCT_ID)
                .map(agg -> (long) agg.getResult()).orElseThrow(() ->
                        new IllegalArgumentException("Gene product count is null"));

        return globalCounts;
    }

    private Aggregation mockResponse() {
        Aggregation goIdAgg = new Aggregation(AnnotationFields.GO_ID);

        AggregationBucket goIdBucket1 = new AggregationBucket("GO:0016020");
        goIdBucket1.addAggregateResult(UNIQUE, AnnotationFields.ID, 2);
        goIdBucket1.addAggregateResult(UNIQUE, AnnotationFields.GENE_PRODUCT_ID, 3);
        goIdAgg.addBucket(goIdBucket1);

        AggregationBucket goIdBucket2 = new AggregationBucket("GO:0016021");
        goIdBucket2.addAggregateResult(UNIQUE, AnnotationFields.ID, 2);
        goIdBucket2.addAggregateResult(UNIQUE, AnnotationFields.GENE_PRODUCT_ID, 3);
        goIdAgg.addBucket(goIdBucket2);

        AggregationBucket goIdBucket3 = new AggregationBucket("GO:0005737");
        goIdBucket3.addAggregateResult(UNIQUE, AnnotationFields.ID, 2);
        goIdBucket3.addAggregateResult(UNIQUE, AnnotationFields.GENE_PRODUCT_ID, 4);
        goIdAgg.addBucket(goIdBucket3);

        Aggregation globalAgg = new Aggregation("global");
        globalAgg.addAggregationResult(COUNT, AnnotationFields.ID, 6);
        globalAgg.addAggregationResult(COUNT, AnnotationFields.GENE_PRODUCT_ID, 10);
        globalAgg.addAggregation(goIdAgg);

        return globalAgg;
    }

    /**
     * Stores the global counts that are necessary to run the statistics
     */
    private class GlobalCounts {
        long annotations;
        long geneProducts;
    }

    /**
     * Converts a collection of {@link Aggregation} data retrieved from a {@link QueryResult}, into a
     * {@link StatisticsGroup}, that can be presented to the client.
     * </p>
     * This class is capable of creating a single {@link StatisticsGroup} per call to the
     * {@link StatisticsConverter#convert(Collection)} method.
     */
    private class StatisticsConverter {
        private final String field;
        private final long totalHits;
        private final String groupName;

        public StatisticsConverter(String field, long totalHits, String groupName) {
            //TODO: add preconditions
            this.field = field;
            this.totalHits = totalHits;
            this.groupName = groupName;
        }

        public StatisticsGroup convert(Collection<Aggregation> aggregations) {
            StatisticsGroup statsGroup = new StatisticsGroup(groupName, totalHits);

            aggregations.stream().map(this::createStatsType)
                    .forEach(statsGroup::addStatsType);

            return statsGroup;
        }

        private StatisticsByType createStatsType(Aggregation aggregation) {
            StatisticsByType type = new StatisticsByType(aggregation.getName());

            Set<AggregationBucket> buckets = aggregation.getBuckets();

            buckets.stream()
                    .map(this::createStatsValue)
                    .forEach(value -> {
                        if (value.isPresent()) {
                            type.addValue(value.get());
                        } else {
                            logger.warn("No stats for field {}, for type {}", field, type.getType());
                        }
                    });

            return type;
        }

        private Optional<StatisticsValue> createStatsValue(AggregationBucket bucket) {
            Optional<AggregationResult> resultOpt = bucket.getAggregationResult(UNIQUE, field);

            return resultOpt.
                    map(aggResult -> new StatisticsValue(bucket.getValue(), (long) aggResult.getResult(), totalHits));
        }
    }
}