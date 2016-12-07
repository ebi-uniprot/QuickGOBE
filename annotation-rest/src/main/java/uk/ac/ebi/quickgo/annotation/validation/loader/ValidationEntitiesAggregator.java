package uk.ac.ebi.quickgo.annotation.validation.loader;

import uk.ac.ebi.quickgo.annotation.validation.model.ValidationEntities;
import uk.ac.ebi.quickgo.annotation.validation.model.ValidationEntity;

import com.google.common.base.Preconditions;
import java.util.List;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Aggregate ValidationEntity instances.
 *
 * @author Tony Wardell
 * Date: 24/11/2016
 * Time: 11:56
 * Created with IntelliJ IDEA.
 */
public class ValidationEntitiesAggregator implements ItemWriter<ValidationEntity> {

    private final ValidationEntities validationEntities;

    @Autowired
    public ValidationEntitiesAggregator(ValidationEntities validationEntities) {
        this.validationEntities = validationEntities;
    }

    @Override public void write(List<? extends ValidationEntity> items) {
        Preconditions.checkArgument(items != null, "The list of items written to ValidationEntitiesAggregator " +
                "cannot be null.");

        validationEntities.addEntities(items);
    }

}
