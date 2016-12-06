package uk.ac.ebi.quickgo.annotation.validation.model;

import com.google.common.base.Preconditions;
import java.util.List;
import java.util.Objects;

/**
 * Holds a aggregation of  validation objects retrievable by their identifier.
 *
 * @author Tony Wardell
 * Date: 22/11/2016
 * Time: 15:06
 * Created with IntelliJ IDEA.
 */
public class ValidationEntitiesImpl implements ValidationEntities {
    private final ValidationEntitiesAggregator aggregator;

    public ValidationEntitiesImpl(ValidationEntitiesAggregator aggregator) {
        Preconditions.checkArgument(Objects.nonNull(aggregator), "The ValidationEntitiesAggregator instance cannot " +
                "be null.");
        this.aggregator = aggregator;
    }

    public List<ValidationEntity> get(String id) {
        return aggregator.mappedEntities.get(id);
    }
}
