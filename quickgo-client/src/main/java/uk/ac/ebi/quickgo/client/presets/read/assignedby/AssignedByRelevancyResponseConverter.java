package uk.ac.ebi.quickgo.client.presets.read.assignedby;

import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;
import uk.ac.ebi.quickgo.rest.search.request.converter.FilterConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines the transformation of a {@link AssignedByRelevancyResponseType} instance to
 * an ordered list of {@link String}s, representing
 *
 * Created 31/08/16
 * @author Edd
 */
public class AssignedByRelevancyResponseConverter
        implements FilterConverter<AssignedByRelevancyResponseType, List<String>> {

    @Override public ConvertedFilter<List<String>> transform(AssignedByRelevancyResponseType response) {
        List<String> values = response.terms.assignedBy;
        List<String> keysWithOutCounts = new ArrayList<>();
        for (int i = 0; i < values.size() - 1; i += 2) {
            keysWithOutCounts.add(values.get(i));
        }
        return new ConvertedFilter<>(keysWithOutCounts);
    }
}
