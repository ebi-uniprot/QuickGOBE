package uk.ac.ebi.quickgo.annotation.controller;

import uk.ac.ebi.quickgo.annotation.coterms.CoTerm;
import uk.ac.ebi.quickgo.annotation.coterms.CoTermRepository;
import uk.ac.ebi.quickgo.annotation.coterms.CoTermSource;
import uk.ac.ebi.quickgo.rest.ParameterException;
import uk.ac.ebi.quickgo.rest.ResponseExceptionHandler;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import io.swagger.annotations.*;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static uk.ac.ebi.quickgo.common.validator.OntologyIdPredicate.isValidGOTermId;

/**
 * REST controller for accessing GO Term co-occurring term related information.
 *
 * Created 11/10/16
 * @author Tony Wardell
 */
@RestController
@Api(tags = {"co-occurring terms"})
@RequestMapping(value = "/annotation/coterms")
public class CoTermController {

    private static final String LIMIT_ALL = "ALL";
    @Value("${coterm.default.limit:50}")
    private int defaultLimit;
    private CoTermRepository coTermRepository;
    private static final String INVALID_CO_TERM_SOURCE = "The value for source should be one of %s. '%s' is not a " +
            "valid value for source.";

    /**
     * Create the endpoint for Co Terms.
     */
    public CoTermController(CoTermRepository coTermRepository) {
        this.coTermRepository = coTermRepository;
    }

    /**
     * Get co-occurring term information for a single GO Term
     *
     * @param id ontology identifier
     * @param source whether manual only or all
     * @param limit Optional: requires a String so the user can specify 'ALL' or a number.
     * @param similarityThreshold Optional: either not provided or a integer between 0 and 100.
     * @return
     * <ul>
     *     <li>all ids are valid: response consists of a 200 with the co-occurring term information including
     *     statistics</li>
     *     <li>any id is not found: response returns 404</li>
     *     <li>any id with an invalid format: response returns 400</li>
     * </ul>
     */
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All request values are valid, and co-occurring terms identified " +
                    "for the supplied GO term id are returned if they exist."),
            @ApiResponse(code = 500, message = "Internal server error occurred whilst searching for co-occurring terms",
                    response = ResponseExceptionHandler.ErrorInfo.class),
            @ApiResponse(code = 400, message = "Bad request due to a validation issue with one of the request values.",
                    response = ResponseExceptionHandler.ErrorInfo.class)})
    @ApiOperation(value = "Get co-occurring term information for a single GO Term id.")
    @GetMapping(value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<CoTerm>> findCoTerms(
            @ApiParam(value = "The GO term id", required = true) @PathVariable String id,
            @ApiParam(value = "The source from which the co-occurring terms originated. Possible " +
                    "values: ALL / MANUAL. ALL => manual + electronic/automatically generated annotations; " +
                    "MANUAL => only manually generated annotations", defaultValue = "ALL")
            @RequestParam(defaultValue = "ALL") String source,
            @ApiParam(name = "limit", value = "The number of terms returned", required = false)
            @RequestParam(required = false) String limit,
            @ApiParam(name = "similarityThreshold", value = "The similarity threshold used when finding co-occurring " +
                    "terms", defaultValue = "0.0")
            @RequestParam(defaultValue = "0.0") float similarityThreshold) {

        validateGoTerm(id);
        final List<CoTerm> coTerms = coTermRepository.findCoTerms(id, toCoTermSource(source));
        return getResultsResponse(coTerms.size(), coTerms.stream()
                                                         .filter(ct -> ct.getSimilarityRatio() >= similarityThreshold)
                                                         .limit(workoutLimit(limit))
                                                         .collect(Collectors.toList()));
    }

    private int workoutLimit(String limit) {
        if (limitNotSpecified(limit)) {
            return defaultLimit;
        }
        if (userHasRequestedAllCoTerms(limit)) {
            return Integer.MAX_VALUE;
        }
        try {
            int numLimit = Integer.parseInt(limit);
            if (numLimit <= 0) {
                throw new ParameterException("The value for limit should be a positive integer, or 'ALL'");
            }
            return numLimit;
        } catch (NumberFormatException e) {
            throw new ParameterException("The value for limit should be a positive integer, or 'ALL'");
        }
    }

    /**
     * Creates a {@link ResponseEntity} containing a {@link QueryResult} for a list of results.
     *
     * @param results a list of results
     * @return a {@link ResponseEntity} containing a {@link QueryResult} for a list of results
     */
    private <ResponseType> ResponseEntity<QueryResult<ResponseType>> getResultsResponse
    (long numberOfHits, List<ResponseType> results) {

        final List<ResponseType> queryResults = results == null ? Collections.emptyList() : results;
        QueryResult<ResponseType> queryResult = new QueryResult.Builder<>(numberOfHits, queryResults).build();
        return new ResponseEntity<>(queryResult, HttpStatus.OK);
    }

    private void validateGoTerm(String id) {
        if (!isValidGOTermId().test(id)) {
            String errorMessage = "Provided ID: '" + id + "' is invalid";
            throw new ParameterException(errorMessage);
        }
    }

    private CoTermSource toCoTermSource(String source) {
        final String asUpperCase = source.toUpperCase();
        if (!CoTermSource.isValidValue(asUpperCase)) {
            throw new ParameterException(INVALID_CO_TERM_SOURCE.formatted(CoTermSource.valuesAsCSV(), source));

        }
        return CoTermSource.valueOf(asUpperCase);
    }

    private boolean limitNotSpecified(String limit) {
        return limit == null || limit.trim().isEmpty();
    }

    private boolean userHasRequestedAllCoTerms(String limit) {
        return LIMIT_ALL.equalsIgnoreCase(limit);
    }

}
