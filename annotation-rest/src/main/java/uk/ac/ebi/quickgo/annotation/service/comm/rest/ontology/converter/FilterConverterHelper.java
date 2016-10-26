package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.converter;

import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.ConvertedOntologyFilter;
import uk.ac.ebi.quickgo.rest.search.RetrievalException;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;

import java.util.StringJoiner;
import java.util.function.Consumer;

import static uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery.not;

/**
 * Common logic to classes responsible for converting filters, such as {@link DescendantsFilterConverter} and
 * {@link SlimmingFilterConverter}.
 *
 * Created 26/10/16
 * @author Edd
 */
class FilterConverterHelper {
    static final ConvertedFilter<QuickGOQuery> FILTER_EVERYTHING =
            new ConvertedFilter<>(not(QuickGOQuery.createAllQuery()));
    private static final String ERROR_MESSAGE_ON_NO_DESCENDANTS = "no descendants found for IDs, %s";
    private static final String DELIMITER = ", ";

    /**
     * Check whether an object is null (a simple check, but using a more fluent api style)
     * @param object the object to check
     * @return whether or not the object is null
     */
    static boolean isNotNull(Object object) {
        return object != null;
    }

    /**
     * Creates a new {@link StringJoiner} which is used to store {@link String}s denoting term IDs that have
     * no descendants
     *
     * @return a {@link StringJoiner} used to record term IDs with no descendants
     */
    static StringJoiner createNoDescendantRecorder() {
        return new StringJoiner(DELIMITER);
    }

    /**
     * Checks whether an instance created through {@link #createNoDescendantRecorder()} has recorded any IDs.
     * If yes, then a {@link RetrievalException} is thrown indicating this.
     *
     * @param idsWithNoDescendants a {@link StringJoiner} created via the {@link #createNoDescendantRecorder()} method.
     */
    static void handleNoDescendants(StringJoiner idsWithNoDescendants) {
        if (idsWithNoDescendants.length() > 0) {
            throw new RetrievalException(
                    String.format(ERROR_MESSAGE_ON_NO_DESCENDANTS, idsWithNoDescendants.toString()));
        }
    }

    /**
     * Apply a {@link Consumer} action to each descendant in the descendants of {@code result}.
     *
     * @param result the {@link uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.ConvertedOntologyFilter.Result}
     *               whose descendants are being iterated through
     * @param action the action to apply for each descendant
     */
    static void forEachDescendantApply(
            ConvertedOntologyFilter.Result result,
            Consumer<String> action) {
        result.getDescendants().stream()
                .filter(FilterConverterHelper::notNullOrEmpty)
                .forEach(action);
    }

    /**
     * Adds a value to a {@link StringJoiner} if the value is not null or empty
     * @param joiner the joiner to add to
     * @param value the value to add
     */
    static void updateJoinerIfValid(StringJoiner joiner, String value) {
        if (notNullOrEmpty(value)) {
            joiner.add(value);
        }
    }

    /**
     * Checks whether a value is not null or empty
     * @param value the value to check
     * @return whether the value is not null or empty
     */
    private static boolean notNullOrEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
