package uk.ac.ebi.quickgo.annotation.service.statistics;

import uk.ac.ebi.quickgo.rest.search.query.AggregateRequest;

import java.util.Collection;

/**
 * Contract to convert a collection of {@link uk.ac.ebi.quickgo.annotation.model.AnnotationRequest.StatsRequest} into
 * a domain specific {@link AggregateRequest}.
 *
 * @author Ricardo Antunes
 */
// TODO: 16/08/17 rename to statsconverter 
public interface StatsRequestConverter {
    AggregateRequest convert(Collection<RequiredStatistic> requiredStatistics);
}
