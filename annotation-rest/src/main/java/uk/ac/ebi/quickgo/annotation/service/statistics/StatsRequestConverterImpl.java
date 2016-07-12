package uk.ac.ebi.quickgo.annotation.service.statistics;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationFields;
import uk.ac.ebi.quickgo.annotation.model.AnnotationRequest;
import uk.ac.ebi.quickgo.rest.search.AggregateFunction;
import uk.ac.ebi.quickgo.rest.search.query.Aggregate;

import com.google.common.base.Preconditions;
import java.util.Collection;
import org.springframework.stereotype.Component;

/**
 * A simple implementation of the {@link StatsRequestConverter} interface.
 *
 * @author Ricardo Antunes
 */
@Component
public class StatsRequestConverterImpl implements StatsRequestConverter {
    @Override public Aggregate convert(Collection<AnnotationRequest.StatsRequest> statsRequests) {
        Preconditions.checkArgument(statsRequests != null, "Stats request collection cannot be null");

        return createDefaultStatsAggregate();
    }

    private Aggregate createDefaultStatsAggregate() {
        Aggregate globalAggregate = new Aggregate("global");
        globalAggregate.addField(AnnotationFields.ID, AggregateFunction.UNIQUE);
        globalAggregate.addField(AnnotationFields.GENE_PRODUCT_ID, AggregateFunction.UNIQUE);

        Aggregate goIdType = new Aggregate(AnnotationFields.GO_ID);
        goIdType.addField(AnnotationFields.ID, AggregateFunction.UNIQUE);
        goIdType.addField(AnnotationFields.GENE_PRODUCT_ID, AggregateFunction.UNIQUE);

        globalAggregate.addNestedAggregate(goIdType);

        return globalAggregate;
    }
}
