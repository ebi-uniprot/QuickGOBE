package uk.ac.ebi.quickgo.client.service.loader.presets.assignedby;

import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;
import uk.ac.ebi.quickgo.rest.search.request.converter.FilterConverter;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Defines the transformation of a {@link AssignedByRelevancyResponseType} instance to
 * a list of {@link String}s, representing preset names in descending order (from high to low).
 *
 * Created 31/08/16
 * @author Edd
 */
public class AssignedByRelevancyResponseConverter
        implements FilterConverter<AssignedByRelevancyResponseType, List<String>> {

    @Override public ConvertedFilter<List<String>> transform(AssignedByRelevancyResponseType response) {
        checkArgument(response != null, "Response cannot be null");

        List<String> keysWithoutCounts = new ArrayList<>();

        if (response.terms != null && response.terms.assignedBy != null) {
            List<String> values = response.terms.assignedBy;
            for (int i = 0; i < values.size() - 1; i += 2) {
                keysWithoutCounts.add(values.get(i));
            }
        }

        return new ConvertedFilter<>(keysWithoutCounts);
    }
}
