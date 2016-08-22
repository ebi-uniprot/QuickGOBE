package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.converter;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationFields;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.ConvertedOntologyFilter;
import uk.ac.ebi.quickgo.rest.comm.FilterContext;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;
import uk.ac.ebi.quickgo.rest.search.request.converter.FilterConverter;

import java.util.HashSet;
import java.util.Set;

import static uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery.not;
import static uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery.or;

/**
 * This class is responsible for converting an ontology model containing GO id descendant information, to a
 * {@link ConvertedFilter}. This result encapsulates a {@link QuickGOQuery} applicable to filtering the annotation
 * core data, by these descendants. Additionally, this process records slimming information consisting of original
 * GO ids and their mappings to slimmed up GO ids; this is stored within an instance of {@link SlimmingConversionInfo}
 * available from the {@link ConvertedFilter}'s {@link FilterContext}.
 *
 * Created 09/08/16
 * @author Edd
 */
public class DescendantsFilterConverter implements FilterConverter<ConvertedOntologyFilter, QuickGOQuery> {

    @Override public ConvertedFilter<QuickGOQuery> transform(ConvertedOntologyFilter response) {
        ConvertedFilter<QuickGOQuery> convertedFilter;

        SlimmingConversionInfo conversionInfo = new SlimmingConversionInfo();

        if (response.getResults() != null && atLeastOneDescendantExists(response)) {
            Set<QuickGOQuery> queries = new HashSet<>();
            FilterContext context = new FilterContext();

            for (ConvertedOntologyFilter.Result result : response.getResults()) {
                for (String desc : result.getDescendants()) {
                    queries.add(QuickGOQuery.createQuery(AnnotationFields.GO_ID, desc));
                    conversionInfo.addOriginal2SlimmedGOIdMapping(desc, result.getId());
                }
            }

            context.save(SlimmingConversionInfo.class, conversionInfo);

            convertedFilter = new ConvertedFilter<>(
                    or(queries.toArray(new QuickGOQuery[queries.size()])),
                    context);
        } else {
            convertedFilter = new ConvertedFilter<>(not(QuickGOQuery.createAllQuery()));
        }

        return convertedFilter;
    }

    private boolean atLeastOneDescendantExists(ConvertedOntologyFilter response) {
        return response.getResults().stream()
                .filter(r -> !r.getDescendants().isEmpty())
                .findFirst()
                .isPresent();
    }
}
