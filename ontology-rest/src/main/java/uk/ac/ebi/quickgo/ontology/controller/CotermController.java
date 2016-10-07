package uk.ac.ebi.quickgo.ontology.controller;

import uk.ac.ebi.quickgo.ontology.common.coterm.CoTerm;
import uk.ac.ebi.quickgo.ontology.common.coterm.CoTermType;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyType;
import uk.ac.ebi.quickgo.ontology.model.GOTerm;
import uk.ac.ebi.quickgo.ontology.service.OntologyService;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import com.google.common.base.Preconditions;
import io.swagger.annotations.ApiOperation;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
public class CotermController {
    private static final Pattern GO_ID_FORMAT = Pattern.compile("^GO:[0-9]{7}$");
    private static final String COTERMS_RESOURCE = "coterms";

    @Value("${coterm.default.limit:50}")
    private int defaultLimit;

    final OntologyService<GOTerm> ontologyService;

    @Autowired
    public CotermController(OntologyService<GOTerm> goOntologyService) {
        this.ontologyService = goOntologyService;
    }


    /**
     * Get co-occurring term information for a single GO Term
     *
     * @param id ontology identifier
     * @param type whether manual only or all
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
    @RequestMapping(value = COTERMS_RESOURCE + "/{id}/", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<CoTerm>> findCoTerms(@PathVariable(value = "id") String id,
            @RequestParam(value = "type", defaultValue = "ALL") String type,
            @RequestParam(value = "limit") String limit,
            @RequestParam(value = "similarityThreshold") String similarityThreshold) {

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

        return getResultsResponse(ontologyService.findCoTermsByOntologyId(id, CoTermType.valueOf(type),
                coTermLimit.workoutLimit(limit), similarityThreshold));
    }


    @Override
    public Predicate<String> idValidator() {
        return id -> GO_ID_FORMAT.matcher(id).matches();
    }

    @Override protected OntologyType getOntologyType() {
        return OntologyType.GO;
    }
}
