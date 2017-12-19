package uk.ac.ebi.quickgo.annotation.service.statistics;

import uk.ac.ebi.quickgo.annotation.common.AnnotationFields;
import uk.ac.ebi.quickgo.rest.search.AggregateFunction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Arrays.asList;
import static uk.ac.ebi.quickgo.annotation.service.statistics.RequiredStatisticType.statsType;

/**
 * As per {@link RequiredStatistics}, but gene product is an additional stats type, used only for the annotation
 * group of stats.
 *
 * Created 19/12/17
 * @author Tony Wardell
 */
public class RequiredStatisticsWithGeneProduct extends RequiredStatistics {
    static final List<RequiredStatisticType> STATS_TYPES;

    static {
        STATS_TYPES = new ArrayList<>(RequiredStatistics.STATS_TYPES);
        STATS_TYPES.add(statsType(AnnotationFields.Facetable.GENE_PRODUCT_ID));
    }

    RequiredStatisticsWithGeneProduct(StatisticsTypeConfigurer statsConfigurer) {
        super();
        checkArgument(statsConfigurer != null, "Stats configurer cannot be null");
        configuredTypes = statsConfigurer.getConfiguredStatsTypes(STATS_TYPES);
        requiredStats = Collections.unmodifiableList(asList(annotationStats(), geneProductStats()));
    }

    public List<RequiredStatistic> getRequiredStatistics() {
        return requiredStats;
    }

    protected RequiredStatistic annotationStats() {
        return new RequiredStatistic(ANNOTATION, AnnotationFields.Facetable.ID,
                AggregateFunction.COUNT.getName(), configuredTypes);
    }
}
