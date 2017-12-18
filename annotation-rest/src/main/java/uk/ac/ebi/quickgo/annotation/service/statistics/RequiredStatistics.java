package uk.ac.ebi.quickgo.annotation.service.statistics;

import uk.ac.ebi.quickgo.annotation.common.AnnotationFields;
import uk.ac.ebi.quickgo.rest.search.AggregateFunction;

import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Arrays.asList;
import static uk.ac.ebi.quickgo.annotation.service.statistics.RequiredStatisticType.statsType;

/**
 * Represents the required statistics that must be shown about annotations. Comprises
 * a list of {@link RequiredStatistic} entities, each of which captures particular details
 * of the annotations over which they have been calculated.
 *
 * The {@link RequiredStatistics} include statistics over annotations and gene products. Within each
 * statistic, by default of 10 items will be displayed for each statistic type (e.g., evidences, taxons, etc.),
 * except for GO terms, which by default displayed 200 ids.
 *
 * Created 16/08/17
 * @author Edd
 */
public class RequiredStatistics {
    static final int DEFAULT_GO_TERM_LIMIT = 200;
    static final List<RequiredStatisticType> ANNOTATION_STATS_TYPES;
    static final List<RequiredStatisticType> GENE_PRODUCT_STATS_TYPES;

    static final String ANNOTATION = "annotation";
    static final String GENE_PRODUCT = "geneProduct";

    static {
        ANNOTATION_STATS_TYPES = asList(
                statsType(AnnotationFields.Facetable.GO_ID, DEFAULT_GO_TERM_LIMIT),
                statsType(AnnotationFields.Facetable.TAXON_ID),
                statsType(AnnotationFields.Facetable.REFERENCE),
                statsType(AnnotationFields.Facetable.EVIDENCE_CODE),
                statsType(AnnotationFields.Facetable.ASSIGNED_BY),
                statsType(AnnotationFields.Facetable.GO_ASPECT),
                statsType(AnnotationFields.Facetable.GENE_PRODUCT_ID)
        );

        GENE_PRODUCT_STATS_TYPES = asList(
                statsType(AnnotationFields.Facetable.GO_ID, DEFAULT_GO_TERM_LIMIT),
                statsType(AnnotationFields.Facetable.TAXON_ID),
                statsType(AnnotationFields.Facetable.REFERENCE),
                statsType(AnnotationFields.Facetable.EVIDENCE_CODE),
                statsType(AnnotationFields.Facetable.ASSIGNED_BY),
                statsType(AnnotationFields.Facetable.GO_ASPECT),
                statsType(AnnotationFields.Facetable.GENE_PRODUCT_ID)
        );
    }

    private final List<RequiredStatistic> requiredStats;
    private final List<RequiredStatisticType> configuredTypes;

    RequiredStatistics(StatisticsTypeConfigurer statsConfigurer) {
        checkArgument(statsConfigurer != null, "Stats configurer cannot be null");
        configuredTypes = statsConfigurer.getConfiguredStatsTypes(STATS_TYPES);
        requiredStats = Collections.unmodifiableList(
                asList(annotationStats(), geneProductStats()));
    }

    public List<RequiredStatistic> getStats() {
        return requiredStats;
    }

    private RequiredStatistic annotationStats() {
        return new RequiredStatistic(ANNOTATION, AnnotationFields.Facetable.ID,
                AggregateFunction.COUNT.getName(), configuredTypes);
    }

    private RequiredStatistic geneProductStats() {
        return new RequiredStatistic(GENE_PRODUCT, AnnotationFields.Facetable.GENE_PRODUCT_ID,
                AggregateFunction.UNIQUE.getName(), configuredTypes);
    }
}
