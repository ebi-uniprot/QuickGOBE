package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.converter;

import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.OntologyRelatives;
import uk.ac.ebi.quickgo.rest.search.RetrievalException;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;
import uk.ac.ebi.quickgo.rest.search.request.converter.FilterConverter;

import com.google.common.base.Strings;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

import static java.util.Objects.nonNull;
import static uk.ac.ebi.quickgo.annotation.common.AnnotationFields.Searchable;
import static uk.ac.ebi.quickgo.common.validator.OntologyIdPredicate.isValidECOTermId;
import static uk.ac.ebi.quickgo.common.validator.OntologyIdPredicate.isValidGOTermId;
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
    protected static final String UNKNOWN_ID_FORMAT =
            "Unknown ID encountered: %s. Expected either GO/ECO term.";
    private final StringJoiner idsWithNoRelatives;

    protected ConvertedFilter<QuickGOQuery> convertedFilter;

    AbstractOntologyFilterConverter() {
        convertedFilter = FILTER_EVERYTHING;
        idsWithNoRelatives = new StringJoiner(DELIMITER);
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
        if (nonNull(response.getResults())) {
            Set<QuickGOQuery> queries = new HashSet<>();

            for (OntologyRelatives.Result result : response.getResults()) {
                if (validResult(result)) {
                    processResult(result, queries);
                } else {
                    addIdWithNoRelative(result.getId());
                }
            }

            convertedFilter = createFilter(queries);
            handleInvalidIds();
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

    /**
     * The implementation of how to process a {@link OntologyRelatives.Result} and insert corresponding
     * {@link QuickGOQuery}s into an existing set of {@link QuickGOQuery}s.
     * @param result the result to process
     * @param queries the queries to to update
     */
    protected abstract void processResult(OntologyRelatives.Result result, Set<QuickGOQuery> queries);

    /**
     * Creates the {@link ConvertedFilter} over {@link QuickGOQuery}s representing the supplied {@code queries}.
     * @param queries the {@link Set} of {@link QuickGOQuery} for which to build a {@link ConvertedFilter}
     * @return the {@link ConvertedFilter} over {@link QuickGOQuery} instances.
     */
    protected abstract ConvertedFilter<QuickGOQuery> createFilter(Set<QuickGOQuery> queries);

    /**
     * Creates a {@link QuickGOQuery} based on a supplied ontology id.
     * @param id the identifier of the ontology for which to create a {@link QuickGOQuery}
     * @return the {@link QuickGOQuery} corresponding to the supplied ontology id
     */
    protected QuickGOQuery createQueryForOntologyId(String id) {
        String field;
        if (isValidGOTermId().test(id)) {
            return QuickGOQuery.createQuery(Searchable.GO_ID, id);
        } else if (isValidECOTermId().test(id)) {
            field = Searchable.EVIDENCE_CODE;
            return QuickGOQuery.createQuery(field, id);
        } else {
            throw new RetrievalException(String.format(UNKNOWN_ID_FORMAT, id));
        }
    }

    /**
     * Checks whether the {@code invalidIds} has recorded any IDs.
     * If yes, then a {@link RetrievalException} is thrown indicating this.
     */
    protected void handleInvalidIds() {
        if (idsWithNoRelatives.length() > 0) {
            throw new RetrievalException(
                    String.format(ERROR_MESSAGE_ON_INVALID_IDS, idsWithNoRelatives.toString()));
        }
    }
    
    /**
     * Capture an additional ID that has no relative.
     * @param id the value to add
     */
    protected void addIdWithNoRelative(String id) {
        if (!Strings.isNullOrEmpty(id)) {
            idsWithNoRelatives.add(id);
        }
    }
}
