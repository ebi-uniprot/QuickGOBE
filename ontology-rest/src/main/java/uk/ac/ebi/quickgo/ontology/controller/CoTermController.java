package uk.ac.ebi.quickgo.ontology.controller;

import uk.ac.ebi.quickgo.ontology.common.coterm.CoTerm;
import uk.ac.ebi.quickgo.ontology.coterms.CoTermLimit;
import uk.ac.ebi.quickgo.ontology.common.coterm.CoTermSource;
import uk.ac.ebi.quickgo.ontology.model.GOTerm;
import uk.ac.ebi.quickgo.ontology.service.OntologyService;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import com.google.common.base.Preconditions;
import io.swagger.annotations.ApiOperation;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static uk.ac.ebi.quickgo.ontology.controller.GOController.GO_ID_FORMAT;

/**
 * REST controller for accessing GO related information.
 *
 * For complete list of necessary endpoints, and their behaviour:
 *  refer to https://www.ebi.ac.uk/seqdb/confluence/display/GOA/REST+API
 *
 * Created 16/11/15
 * @author Edd
 */
@RestController
@RequestMapping(value = "/QuickGO/services/go")
public class CoTermController {

    private static final String COTERMS_RESOURCE = "coterms";
    private final CoTermLimit coTermLimit;
    final OntologyService<GOTerm> ontologyService;

    /**
     * Create the endpoint for Co Terms.
     * @param goOntologyService Service layer class consolidates ontology functionality
     * @param coTermLimit provides the correct limit for the number of co terms to return.
     */
    @Autowired
    public CoTermController(OntologyService<GOTerm> goOntologyService, CoTermLimit coTermLimit) {
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
     *     <li>any id is of the an invalid format: response returns 400</li>
     * </ul>
     */
    @ApiOperation(value = "Get co-occurring term information for a single GO Term id.",
            notes = "If possible, response fields include: id, name, definition, probability ratio, similarity ratio," +
                    " together, compared.")
    @RequestMapping(value = COTERMS_RESOURCE + "/{id}", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<CoTerm>> findCoTerms(@PathVariable(value = "id") String id,
            @RequestParam(value = "source", defaultValue = "ALL") String source,
            @RequestParam(value = "limit", required = false) String limit,
            @RequestParam(value = "similarityThreshold", required = false) String similarityThreshold) {

        int similarityNumeric = validateSimilarity(similarityThreshold);
        CoTermSource coTermSource = validateCoTermSource(source, similarityThreshold);
        validateGoTerm(id);

        return getResultsResponse(ontologyService.findCoTermsByOntologyId(id, coTermSource,
                coTermLimit.workoutLimit(limit), similarityNumeric));
    }

    private void validateGoTerm(@PathVariable(value = "id") String id) {
        if(!idValidator().test(id)){
            String errorMessage = "Provided ID: '" + id + "' is invalid";
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private CoTermSource validateCoTermSource(@RequestParam(value = "source", defaultValue = "ALL") String source,
            @RequestParam(value = "similarityThreshold") String similarityThreshold) {
        CoTermSource coTermSource = null;
        try {
            coTermSource = CoTermSource.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("The value for similarityThreshold should be an integer between 0 " +
                    "and 100, not " + similarityThreshold);
        }
        return coTermSource;
    }

    private int validateSimilarity(@RequestParam(value = "similarityThreshold") String similarityThreshold) {
        int similarityNumeric = 0;
        if(similarityThreshold!=null) {
            try {
                similarityNumeric = Integer.parseInt(similarityThreshold);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("The value for similarityThreshold should be an integer between 0 " +
                        "and 100, not " + similarityThreshold);
            }
            Preconditions.checkArgument(similarityNumeric < 0 || similarityNumeric > 100, "The value for " +
                    "similarityThreshold should be an integer between 0 and 100, not {}", similarityThreshold);
        }
        return similarityNumeric;
    }

    public Predicate<String> idValidator() {
        return id -> GO_ID_FORMAT.matcher(id).matches();
    }

    /**
     * Creates a {@link ResponseEntity} containing a {@link QueryResult} for a list of results.
     *
     * @param results a list of results
     * @return a {@link ResponseEntity} containing a {@link QueryResult} for a list of results
     */
    <ResponseType> ResponseEntity<QueryResult<ResponseType>> getResultsResponse(List<ResponseType> results) {
        List<ResponseType> resultsToShow;
        if (results == null) {
            resultsToShow = Collections.emptyList();
        } else {
            resultsToShow = results;
        }

        QueryResult<ResponseType> queryResult = new QueryResult.Builder<>(resultsToShow.size(), resultsToShow).build();
        return new ResponseEntity<>(queryResult, HttpStatus.OK);
    }
}
