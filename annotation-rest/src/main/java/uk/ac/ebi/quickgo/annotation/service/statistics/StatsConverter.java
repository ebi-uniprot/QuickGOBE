package uk.ac.ebi.quickgo.annotation.service.statistics;

import uk.ac.ebi.quickgo.rest.search.query.AggregateRequest;

import java.util.Collection;

/**
 * Contract to convert a collection of {@link RequiredStatistic}s into
 * a domain specific {@link AggregateRequest}.
 *
 * @author Ricardo Antunes
 */
public interface StatsConverter {
    AggregateRequest convert(Collection<RequiredStatistic> requiredStatistics);
}
