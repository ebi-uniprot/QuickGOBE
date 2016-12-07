package uk.ac.ebi.quickgo.annotation.validation.model;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.groupingBy;

/**
 * Holds a aggregation of  validation objects retrievable by their identifier.
 *
 * @author Tony Wardell
 * Date: 22/11/2016
 * Time: 15:06
 * Created with IntelliJ IDEA.
 */
public class ValidationEntitiesImpl implements ValidationEntities {

    private Map<String, List<ValidationEntity>> mappedEntities = new HashMap<>();

    public List<ValidationEntity> get(String id) {
        return mappedEntities.get(id);
    }

    public void addEntities(List<? extends ValidationEntity> items){
        Preconditions.checkArgument(Objects.nonNull(items), "The list of items added to ValidationEntitiesImpl " +
                "should not be null");

        mappedEntities.putAll(items.stream()
                                   .filter(Objects::nonNull)
                                   .filter(e -> Objects.nonNull(e.keyValue()))
                                   .collect(groupingBy(e -> e.keyValue().toLowerCase())));
    }

}
