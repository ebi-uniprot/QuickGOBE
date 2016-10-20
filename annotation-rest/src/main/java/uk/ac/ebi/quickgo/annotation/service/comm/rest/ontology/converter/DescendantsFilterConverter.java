package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.converter;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationFields;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.ConvertedOntologyFilter;
import uk.ac.ebi.quickgo.rest.search.RetrievalException;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;
import uk.ac.ebi.quickgo.rest.search.request.converter.FilterConverter;

import java.util.*;

import static uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery.not;
import static uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery.or;

/**
 * This class is responsible for converting an ontology model containing GO id descendant information, to a
 * {@link ConvertedFilter}. This result encapsulates a {@link QuickGOQuery} applicable to filtering the annotation
 * core data, by these descendants.
 *
 * Created 09/08/16
 * @author Edd
 */
public class DescendantsFilterConverter implements FilterConverter<ConvertedOntologyFilter, QuickGOQuery> {

    private static final ConvertedFilter<QuickGOQuery> FILTER_EVERYTHING =
            new ConvertedFilter<>(not(QuickGOQuery.createAllQuery()));
    public static final String ERROR_MESSAGE_ON_NO_DESCENDANTS = "no descendants found for IDs, ";

    @Override public ConvertedFilter<QuickGOQuery> transform(ConvertedOntologyFilter response) {
        ConvertedFilter<QuickGOQuery> convertedFilter = FILTER_EVERYTHING;

        StringJoiner idsWithNoDescendants = new StringJoiner(",");
        if (response.getResults() != null) {
            Set<QuickGOQuery> queries = new HashSet<>();

            for (ConvertedOntologyFilter.Result result : response.getResults()) {
                if (result.getDescendants() != null) {
                    if (!result.getDescendants().isEmpty()) {
                        for (String desc : result.getDescendants()) {
                            if (notNullOrEmpty(desc)) {
                                queries.add(QuickGOQuery.createQuery(AnnotationFields.GO_ID, desc));
                            }
                        }
                        convertedFilter = new ConvertedFilter<>(or(queries.toArray(new QuickGOQuery[queries.size()])));
                    }
                } else {
                    updateJoinerIfValid(idsWithNoDescendants, result.getId());
                }
            }
        }

        if (idsWithNoDescendants.length() > 0) {
            throw new RetrievalException(ERROR_MESSAGE_ON_NO_DESCENDANTS + idsWithNoDescendants.toString());
        }

        return convertedFilter;
    }

    private static void updateJoinerIfValid(StringJoiner joiner, String value) {
        if (notNullOrEmpty(value)) {
            joiner.add(value);
        }
    }

    private static boolean notNullOrEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
