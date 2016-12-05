package uk.ac.ebi.quickgo.annotation.validation.model;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.batch.item.ItemWriter;

import static java.util.stream.Collectors.groupingBy;

/**
 * Aggregate ValidationEntity instances.
 *
 * @author Tony Wardell
 * Date: 24/11/2016
 * Time: 11:56
 * Created with IntelliJ IDEA.
 */
public class ValidationEntitiesAggregator implements ItemWriter<ValidationEntity> {

    Map<String, List<ValidationEntity>> mappedEntities = new HashMap<>();

    @Override public void write(List<? extends ValidationEntity> items) {
        Preconditions.checkArgument(items != null, "The list of items written to ValidationEntitiesAggregator " +
                "cannot be null.");

        mappedEntities.putAll(items.stream()
                .filter(Objects::nonNull)
                .filter(e -> Objects.nonNull(e.keyValue()))
                .collect(groupingBy(e -> e.keyValue().toLowerCase())));
    }
}
