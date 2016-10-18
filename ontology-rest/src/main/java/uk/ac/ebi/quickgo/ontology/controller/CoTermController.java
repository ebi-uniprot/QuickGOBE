package uk.ac.ebi.quickgo.ontology.controller;

import uk.ac.ebi.quickgo.ontology.common.coterms.CoTerm;
import uk.ac.ebi.quickgo.ontology.common.coterms.CoTermSource;
import uk.ac.ebi.quickgo.ontology.controller.validation.GOTermPredicate;
import uk.ac.ebi.quickgo.ontology.coterms.CoTermLimit;
import uk.ac.ebi.quickgo.ontology.model.GOTerm;
import uk.ac.ebi.quickgo.ontology.service.OntologyService;
import uk.ac.ebi.quickgo.rest.ResponseExceptionHandler;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import com.google.common.base.Preconditions;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static uk.ac.ebi.quickgo.ontology.controller.validation.GOTermPredicate.isValidGOTermId;

/**
 * REST controller for accessing GO Term co-occurring term related information.
 *
 * Created 11/10/16
 * @author Tony Wardell
 */
@RestController
@RequestMapping(value = "/ontology/go/coterms")
public class CoTermController {

    private final CoTermLimit coTermLimit;
    private final OntologyService<GOTerm> ontologyService;

    /**
     * Create the endpoint for Co Terms.
     * @param goOntologyService Service layer class consolidates ontology functionality
     * @param coTermLimit provides the correct limit for the number of co terms to return.
     */
    @Autowired
    public CoTermController(OntologyService<GOTerm> goOntologyService, CoTermLimit coTermLimit) {

        Preconditions.checkArgument(goOntologyService != null, "The goOntologyService must not be null.");
        Preconditions.checkArgument(coTermLimit != null, "The coTermLimit must not be null.");

        this.ontologyService = goOntologyService;
        this.coTermLimit = coTermLimit;
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
    @RequestMapping(value = "/{id}", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<CoTerm>> findCoTerms(@PathVariable(value = "id") String id,
            @RequestParam(value = "source", defaultValue = "ALL") String source,
            @RequestParam(value = "limit", required = false) String limit,
            @RequestParam(value = "similarityThreshold", defaultValue = "0.0") float similarityThreshold) {

        validateGoTerm(id);

        return getResultsResponse(ontologyService.findCoTermsByGoTermId(id, toCoTermSource(source),
                coTermLimit.workoutLimit(limit), similarityThreshold));
    }

    /**
     * Creates a {@link ResponseEntity} containing a {@link QueryResult} for a list of results.
     *
     * @param results a list of results
     * @return a {@link ResponseEntity} containing a {@link QueryResult} for a list of results
     */
    private <ResponseType> ResponseEntity<QueryResult<ResponseType>> getResultsResponse(List<ResponseType> results) {
        if (results == null) {
            results = Collections.emptyList();
        }
        QueryResult<ResponseType> queryResult = new QueryResult.Builder<>(results.size(), results).build();
        return new ResponseEntity<>(queryResult, HttpStatus.OK);
    }

    private void validateGoTerm(String id) {
        if (!isValidGOTermId().test(id)) {
            String errorMessage = "Provided ID: '" + id + "' is invalid";
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private CoTermSource toCoTermSource(String source) {
        final String asUpperCase = source.toUpperCase();
        if(!CoTermSource.isValidValue(asUpperCase)){
            throw new IllegalArgumentException("The value for source should be one of " + CoTermSource.valuesAsCSV() +
                    " and not " + source + ".");
        }
        return CoTermSource.valueOf(asUpperCase);
    }

}
