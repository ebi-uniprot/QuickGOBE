package uk.ac.ebi.quickgo.annotation.validation.model;

import java.util.List;

/**
 * Holds a aggregation of  validation objects retrievable by their identifier.
 *
 * @author Tony Wardell
 * Date: 22/11/2016
 * Time: 15:06
 * Created with IntelliJ IDEA.
 */
public class ValidationEntitiesImpl implements ValidationEntities<ValidationEntity> {
    ValidationEntitiesAggregator aggregator;

    public ValidationEntitiesImpl(ValidationEntitiesAggregator aggregator) {
        this.aggregator = aggregator;
    }

    public List<ValidationEntity> get(String id) {
        return aggregator.mappedEntities.get(id);
    }
}
