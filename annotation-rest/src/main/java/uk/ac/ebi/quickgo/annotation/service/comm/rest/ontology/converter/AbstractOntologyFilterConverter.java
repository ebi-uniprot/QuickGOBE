package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.converter;

import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.OntologyRelatives;
import uk.ac.ebi.quickgo.common.validator.OntologyIdPredicate;
import uk.ac.ebi.quickgo.rest.search.RetrievalException;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;
import uk.ac.ebi.quickgo.rest.search.request.converter.FilterConverter;

import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

import static java.util.Objects.nonNull;
import static uk.ac.ebi.quickgo.annotation.common.AnnotationFields.Searchable;
import static uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery.not;

/**
 * This class is responsible for converting a {@link OntologyRelatives} instances to a
 * {@link ConvertedFilter}. This result encapsulates a {@link QuickGOQuery} applicable to filtering the annotation
 * core data.
 *
 * Created 02/11/16
 * @author Edd
 */
abstract class AbstractOntologyFilterConverter
        implements FilterConverter<OntologyRelatives, QuickGOQuery> {
    static final ConvertedFilter<QuickGOQuery> FILTER_EVERYTHING =
            new ConvertedFilter<>(not(QuickGOQuery.createAllQuery()));
    private static final String ERROR_MESSAGE_ON_INVALID_IDS = "No convertible results found for IDs, %s";
    private static final String DELIMITER = ", ";
    private static final String UNKNOWN_ID_FORMAT =
            "Unknown ID encountered: %s. Expected either GO/ECO term.";

    private ConvertedFilter<QuickGOQuery> convertedFilter;

    AbstractOntologyFilterConverter() {
        convertedFilter = FILTER_EVERYTHING;
    }

    /**
     * Defines the procedure for transforming certain results in a {@link OntologyRelatives} instance into a
     * {@link ConvertedFilter} encapsulating a {@link QuickGOQuery}. Concrete implementations of this class
     * defines {@link #validResult(OntologyRelatives.Result)},
     * {@link #processResult(OntologyRelatives.Result, Set)} and
     * {@link #createFilter(Set)}, which enable the necessary behaviour specialisation.
     *
     * @param response the {@link OntologyRelatives} to transform
     * @return a {@link ConvertedFilter} over {@link QuickGOQuery} instances
     */
    @Override public ConvertedFilter<QuickGOQuery> transform(OntologyRelatives response) {
        StringJoiner idsWithNoRelatives = new StringJoiner(DELIMITER);

        if (nonNull(response.getResults())) {
            Set<QuickGOQuery> queries = new HashSet<>();

            for (OntologyRelatives.Result result : response.getResults()) {
                if (validResult(result)) {
                    processResult(result, queries);
                } else {
                    addToJoiner(idsWithNoRelatives, result.getId());
                }
            }

            convertedFilter = createFilter(queries);
            handleInvalidIds(idsWithNoRelatives);
        }

        return convertedFilter;
    }

    /**
     * Before processing a result in {@link #processResult(OntologyRelatives.Result, Set)}, determine whether it is
     * valid for processing. For example, this could constitute simply whether certain attributes are instantiated
     * within this result.
     * @param result the result to check for suitability for further processing
     * @return whether or not the 
     */
    protected abstract boolean validResult(OntologyRelatives.Result result);

    protected abstract void processResult(OntologyRelatives.Result result, Set<QuickGOQuery> queries);

    /**
     * Creates the {@link ConvertedFilter} over {@link QuickGOQuery}s representing the supplied {@code queries}.
     * @param queries the {@link Set} of {@link QuickGOQuery} for which to build a {@link ConvertedFilter}
     * @return the {@link ConvertedFilter} over {@link QuickGOQuery} instances.
     */
    protected abstract ConvertedFilter<QuickGOQuery> createFilter(Set<QuickGOQuery> queries);

    /**
     * Checks whether the {@code invalidIds} has recorded any IDs.
     * If yes, then a {@link RetrievalException} is thrown indicating this.
     *
     * @param invalidIds an instance of {@link StringJoiner}.
     */
    private static void handleInvalidIds(StringJoiner invalidIds) {
        if (invalidIds.length() > 0) {
            throw new RetrievalException(
                    String.format(ERROR_MESSAGE_ON_INVALID_IDS, invalidIds.toString()));
        }
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
    static boolean notNullOrEmpty(String value) {
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
            throw new RetrievalException(String.format(UNKNOWN_ID_FORMAT, id));
        }

        return QuickGOQuery.createQuery(field, id);
    }
}
