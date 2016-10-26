package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.converter;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationFields;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.ConvertedOntologyFilter;
import uk.ac.ebi.quickgo.rest.comm.FilterContext;
import uk.ac.ebi.quickgo.rest.search.RetrievalException;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;
import uk.ac.ebi.quickgo.rest.search.request.converter.FilterConverter;

import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

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
public class SlimmingFilterConverter implements FilterConverter<ConvertedOntologyFilter, QuickGOQuery> {

    private static final ConvertedFilter<QuickGOQuery> FILTER_EVERYTHING =
            new ConvertedFilter<>(not(QuickGOQuery.createAllQuery()));
    private static final String ERROR_MESSAGE_ON_NO_DESCENDANTS = "no descendants found for IDs, ";
    private static final String DELIMITER = ",";

    @Override public ConvertedFilter<QuickGOQuery> transform(ConvertedOntologyFilter response) {
        ConvertedFilter<QuickGOQuery> convertedFilter = FILTER_EVERYTHING;
        SlimmingConversionInfo conversionInfo = new SlimmingConversionInfo();

        StringJoiner idsWithNoDescendants = new StringJoiner(DELIMITER);
        if (response.getResults() != null) {
            Set<QuickGOQuery> queries = new HashSet<>();
            FilterContext context = new FilterContext();

            for (ConvertedOntologyFilter.Result result : response.getResults()) {
                if (result.getDescendants() != null) {
                    insertQueryForEachDescendant(result, queries, conversionInfo);
                } else {
                    updateJoinerIfValid(idsWithNoDescendants, result.getId());
                }
            }

            convertedFilter = createFilterForAllDescendants(queries, context, conversionInfo);
        }

        if (idsWithNoDescendants.length() > 0) {
            throw new RetrievalException(ERROR_MESSAGE_ON_NO_DESCENDANTS + idsWithNoDescendants.toString());
        }

        return convertedFilter;
    }

    private ConvertedFilter<QuickGOQuery> createFilterForAllDescendants(
            Set<QuickGOQuery> queries,
            FilterContext context,
            SlimmingConversionInfo conversionInfo) {
        if (!queries.isEmpty()) {
            context.save(SlimmingConversionInfo.class, conversionInfo);
            return new ConvertedFilter<>(or(queries.toArray(new QuickGOQuery[queries.size()])), context);
        } else {
            return FILTER_EVERYTHING;
        }
    }

    private void insertQueryForEachDescendant(ConvertedOntologyFilter.Result result, Set<QuickGOQuery> queries,
            SlimmingConversionInfo conversionInfo) {
        result.getDescendants().stream()
                .filter(this::notNullOrEmpty)
                .forEach(desc -> {
                    queries.add(QuickGOQuery.createQuery(AnnotationFields.GO_ID, desc));
                    conversionInfo.addOriginal2SlimmedGOIdMapping(desc, result.getId());
                });
    }

    private void updateJoinerIfValid(StringJoiner joiner, String value) {
        if (notNullOrEmpty(value)) {
            joiner.add(value);
        }
    }

    private boolean notNullOrEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
