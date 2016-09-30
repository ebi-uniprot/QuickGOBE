package uk.ac.ebi.quickgo.ontology.controller;

import uk.ac.ebi.quickgo.ontology.common.coterm.CoTerm;
import uk.ac.ebi.quickgo.ontology.common.coterm.CoTermLimit;
import uk.ac.ebi.quickgo.ontology.common.coterm.CoTermType;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyType;
import uk.ac.ebi.quickgo.ontology.model.GOTerm;
import uk.ac.ebi.quickgo.ontology.model.OBOTerm;
import uk.ac.ebi.quickgo.ontology.service.OntologyService;
import uk.ac.ebi.quickgo.ontology.service.search.SearchServiceConfig;
import uk.ac.ebi.quickgo.rest.search.SearchService;
import uk.ac.ebi.quickgo.rest.search.SearchableField;
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
public class GOController extends OBOController<GOTerm> {

    private static final Pattern GO_ID_FORMAT = Pattern.compile("^GO:[0-9]{7}$");
    private static final String COTERMS_RESOURCE = "coterms";

    private CoTermLimit coTermLimit;

    @Value("${coterm.default.limit:50}")
    private int defaultLimit;

    @Autowired
    public GOController(OntologyService<GOTerm> goOntologyService,
            SearchService<OBOTerm> ontologySearchService,
            SearchableField searchableField,
            SearchServiceConfig.OntologyCompositeRetrievalConfig ontologyRetrievalConfig) {
        super(goOntologyService, ontologySearchService, searchableField, ontologyRetrievalConfig);

        coTermLimit = new CoTermLimit(defaultLimit);
    }


    /**
     * Get co-occurring term information about a list of terms in comma-separated-value (CSV) format
     *
     * @param id ontology identifier
     * @param type whether manual only or all
     * @param limit Optional:  requires a String so the user can specify 'ALL' or a number.
     * @param similarityThreshold requires a String so the user can specify 'ALL' or a number
     * @return
     * <ul>
     *     <li>all ids are valid: response consists of a 200 with the chosen information about the ontology terms</li>
     *     <li>any id is not found: response returns 404</li>
     *     <li>any id is of the an invalid format: response returns 400</li>
     * </ul>
     */
    @ApiOperation(value = "Get co-occurring term information about a (CSV) list of terms based on their ids",
            notes = "If possible, response fields include: id, name, definition, probability ratio, similarity ratio," +
                    " together, compared.")
    @RequestMapping(value = TERMS_RESOURCE + "/{id}/" + COTERMS_RESOURCE, method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<CoTerm>> findCoTerms(@PathVariable(value = "id") String id,
            @RequestParam(value = "type", defaultValue = "ALL") String type,
            @RequestParam(value = "limit") String limit,
            @RequestParam(value = "similarityThreshold") int similarityThreshold) {

        Preconditions.checkArgument(similarityThreshold <0 || similarityThreshold>100, "The value for " +
                "similarityThreshold should be an integer between 0 and 100, not {}", similarityThreshold );

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
