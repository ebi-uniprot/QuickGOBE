package uk.ac.ebi.quickgo.annotation.service.statistics;

import uk.ac.ebi.quickgo.annotation.model.AnnotationRequest;
import uk.ac.ebi.quickgo.rest.search.query.Aggregate;

import java.util.Collection;

/**
 * Contract to convert a collection of {@link uk.ac.ebi.quickgo.annotation.model.AnnotationRequest.StatsRequest} into
 * a domain specific {@link Aggregate}.
 *
 * @author Ricardo Antunes
 */
public interface StatsRequestConverter {
    Aggregate convert(Collection<AnnotationRequest.StatsRequest> statsRequests);
}
