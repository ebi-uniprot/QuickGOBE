package uk.ac.ebi.quickgo.client.service.loader.presets.taxon;

import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;
import uk.ac.ebi.quickgo.rest.search.request.converter.FilterConverter;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Defines the transformation of a {@link TaxonRelevancyResponseType} instance to
 * a list of {@link String}s, representing preset names in descending order (from high to low).
 *
 * Created 31/08/16
 * @author Edd
 */
public class TaxonRelevancyResponseConverter
        implements FilterConverter<TaxonRelevancyResponseType, List<String>> {

    @Override public ConvertedFilter<List<String>> transform(TaxonRelevancyResponseType response) {
        checkArgument(response != null, "Response cannot be null");

        List<String> keysWithoutCounts = new ArrayList<>();

        if (response.terms != null && response.terms.taxonIds != null) {
            List<String> values = response.terms.taxonIds;
            for (int i = 0; i < values.size() - 1; i += 2) {
                keysWithoutCounts.add(values.get(i));
            }
        }

        return new ConvertedFilter<>(keysWithoutCounts);
    }
}
