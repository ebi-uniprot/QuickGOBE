package uk.ac.ebi.quickgo.annotation.validation.loader;

import uk.ac.ebi.quickgo.annotation.validation.model.ValidationEntity;
import uk.ac.ebi.quickgo.annotation.validation.service.ValidationEntityChecker;

import com.google.common.base.Preconditions;
import org.springframework.batch.item.Chunk;

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
class ValidationEntitiesAggregator implements ItemWriter<ValidationEntity> {

    private final ValidationEntityChecker validationEntityChecker;

    @Autowired
    public ValidationEntitiesAggregator(ValidationEntityChecker validationEntityChecker) {
        this.validationEntityChecker = validationEntityChecker;
    }

    @Override
    public void write(Chunk<? extends ValidationEntity> items) throws Exception {
        Preconditions.checkArgument(items != null, "The list of items written to ValidationEntitiesAggregator " +
                "cannot be null.");
        this.validationEntityChecker.addEntities(items.getItems());
    }

}
