package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.converter;

import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.OntologyDescendants;
import uk.ac.ebi.quickgo.common.validator.OntologyIdPredicate;
import uk.ac.ebi.quickgo.rest.search.RetrievalException;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;
import uk.ac.ebi.quickgo.rest.search.request.converter.FilterConverter;

import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Consumer;

import static uk.ac.ebi.quickgo.annotation.common.AnnotationFields.Searchable;
import static uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery.not;

/**
 * This class is responsible for converting an ontology model containing descendant information, to a
 * {@link ConvertedFilter}. This result encapsulates a {@link QuickGOQuery} applicable to filtering the annotation
 * core data, by these descendants.
 *
 * Created 02/11/16
 * @author Edd
 */
abstract class AbstractDescendantFilterConverter
        implements FilterConverter<OntologyDescendants, QuickGOQuery> {
    static final ConvertedFilter<QuickGOQuery> FILTER_EVERYTHING =
            new ConvertedFilter<>(not(QuickGOQuery.createAllQuery()));
    private static final String ERROR_MESSAGE_ON_NO_DESCENDANTS = "No descendants found for IDs, %s";
    private static final String DELIMITER = ", ";
    private static final String UNKNOWN_DESCENDANT_FORMAT =
            "Unknown descendant encountered: %s. Expected either GO/ECO term.";

    private ConvertedFilter<QuickGOQuery> convertedFilter;

    AbstractDescendantFilterConverter() {
        convertedFilter = FILTER_EVERYTHING;
    }

    /**
     * Defines the procedure for transforming each descendant in a {@link OntologyDescendants} instance into a
     * {@link OntologyDescendants} encapsulating a {@link QuickGOQuery}. Concrete implementations of this class
     * defines both {@link #processDescendant(OntologyDescendants.Result, Set)} and
     * {@link #createFilterForAllDescendants(Set)}, which enable the necessary behaviour specialisation.
     *
     * @param response the {@link OntologyDescendants} to transform
     * @return a {@link ConvertedFilter} over {@link QuickGOQuery} instances
     */
    @Override public ConvertedFilter<QuickGOQuery> transform(OntologyDescendants response) {
        StringJoiner idsWithNoDescendants = new StringJoiner(DELIMITER);

        if (isNotNull(response.getResults())) {
            Set<QuickGOQuery> queries = new HashSet<>();

            for (OntologyDescendants.Result result : response.getResults()) {
                if (isNotNull(result.getDescendants())) {
                    forEachDescendantApply(result, processDescendant(result, queries));
                } else {
                    addToJoiner(idsWithNoDescendants, result.getId());
                }
            }

            convertedFilter = createFilterForAllDescendants(queries);
            handleNoDescendants(idsWithNoDescendants);
        }

        return convertedFilter;
    }

    /**
     * Defines the logic for how to process a descendant within the {@link #transform(OntologyDescendants)} method.
     *
     * @param result the result in which the descendants reside
     * @param queries the queries to progressively build on each invocation of this method
     * @return a {@link Consumer} over {@link String}s, each of which represent a descendant identifier
     */
    protected abstract Consumer<String> processDescendant(
            OntologyDescendants.Result result, Set<QuickGOQuery> queries);

    /**
     * Creates the {@link ConvertedFilter} over {@link QuickGOQuery}s representing the supplied {@code queries}.
     * @param queries the {@link Set} of {@link QuickGOQuery} for which to build a {@link ConvertedFilter}
     * @return the {@link ConvertedFilter} over {@link QuickGOQuery} instances.
     */
    protected abstract ConvertedFilter<QuickGOQuery> createFilterForAllDescendants(Set<QuickGOQuery> queries);

    /**
     * Check whether an object is null (a simple check, but using a more fluent api style)
     * @param object the object to check
     * @return whether or not the object is null
     */
    private static boolean isNotNull(Object object) {
        return object != null;
    }

    /**
     * Checks whether the {@code idsWithNoDescendants} has recorded any IDs.
     * If yes, then a {@link RetrievalException} is thrown indicating this.
     *
     * @param idsWithNoDescendants an instance of {@link StringJoiner}.
     */
    private static void handleNoDescendants(StringJoiner idsWithNoDescendants) {
        if (idsWithNoDescendants.length() > 0) {
            throw new RetrievalException(
                    String.format(ERROR_MESSAGE_ON_NO_DESCENDANTS, idsWithNoDescendants.toString()));
        }
    }

    /**
     * Apply a {@link Consumer} action to each descendant in the descendants of {@code result}.
     *
     * @param result the {@link OntologyDescendants.Result}
     *               whose descendants are being iterated through
     * @param action the action to apply for each descendant
     */
    private static void forEachDescendantApply(
            OntologyDescendants.Result result,
            Consumer<String> action) {
        result.getDescendants().stream()
                .filter(AbstractDescendantFilterConverter::notNullOrEmpty)
                .forEach(action);
    }

    /**
     * Adds a value to a {@link StringJoiner} if the value is not null or empty
     * @param joiner the joiner to add to
     * @param value the value to add
     */
    private static void addToJoiner(StringJoiner joiner, String value) {
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

    /**
     * Creates a {@link QuickGOQuery} based on a supplied ontology id.
     * @param id the identifier of the ontology for which to create a {@link QuickGOQuery}
     * @return the {@link QuickGOQuery} corresponding to the supplied ontology id
     */
    static QuickGOQuery createQueryForOntologyId(String id) {
        String field;
        if (OntologyIdPredicate.isValidGOTermId().test(id)) {
            field = Searchable.GO_ID;
        } else if (OntologyIdPredicate.isValidECOTermId().test(id)) {
            field = Searchable.EVIDENCE_CODE;
        } else {
            throw new RetrievalException(String.format(UNKNOWN_DESCENDANT_FORMAT, id));
        }

        return QuickGOQuery.createQuery(field, id);
    }
}
