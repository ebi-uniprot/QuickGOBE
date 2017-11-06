package uk.ac.ebi.quickgo.ontology.controller;

import uk.ac.ebi.quickgo.common.SearchableField;
import uk.ac.ebi.quickgo.graphics.service.GraphImageService;
import uk.ac.ebi.quickgo.ontology.OntologyRestConfig;
import uk.ac.ebi.quickgo.ontology.OntologyRestProperties;
import uk.ac.ebi.quickgo.ontology.common.OntologyType;
import uk.ac.ebi.quickgo.ontology.controller.validation.OBOControllerValidationHelper;
import uk.ac.ebi.quickgo.ontology.model.About;
import uk.ac.ebi.quickgo.ontology.model.GOTerm;
import uk.ac.ebi.quickgo.ontology.model.OBOTerm;
import uk.ac.ebi.quickgo.ontology.model.SlimTerm;
import uk.ac.ebi.quickgo.ontology.model.OntologySpecifier;
import uk.ac.ebi.quickgo.ontology.service.OntologyService;
import uk.ac.ebi.quickgo.ontology.service.search.SearchServiceConfig;
import uk.ac.ebi.quickgo.rest.ParameterException;
import uk.ac.ebi.quickgo.rest.headers.HttpHeadersProvider;
import uk.ac.ebi.quickgo.rest.metadata.MetaData;
import uk.ac.ebi.quickgo.rest.metadata.MetaDataProvider;
import uk.ac.ebi.quickgo.rest.search.SearchService;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import com.google.common.base.Preconditions;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import static uk.ac.ebi.quickgo.ontology.model.OntologyRelationType.GO_GRAPH_TRAVERSAL_TYPES;

import static uk.ac.ebi.quickgo.ontology.model.OntologyRelationType.DEFAULT_SLIM_TRAVERSAL_TYPES_CSV;

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
@Api(tags = {"gene ontology"})
@RequestMapping(value = "/ontology/go")
@EnableConfigurationProperties(OntologyRestProperties.class)
public class GOController extends OBOController<GOTerm> {

    static final String MISSING_SLIM_SET_ERROR_MESSAGE =
            "Please enter slim-set request parameter: 'ids=<GO_TERM>'";
    private final MetaDataProvider metaDataProvider;
    private static final OntologySpecifier GO_SPECIFIER = new OntologySpecifier(OntologyType.GO,
                                                                                GO_GRAPH_TRAVERSAL_TYPES);

    @Autowired
    public GOController(OntologyService<GOTerm> goOntologyService,
            SearchService<OBOTerm> ontologySearchService,
            SearchableField searchableField,
            SearchServiceConfig.OntologyCompositeRetrievalConfig ontologyRetrievalConfig,
            GraphImageService graphImageService,
            OBOControllerValidationHelper goValidationHelper,
            OntologyRestConfig.OntologyPagingConfig ontologyPagingConfig,
            MetaDataProvider metaDataProvider,
            HttpHeadersProvider httpHeadersProvider) {
        super(goOntologyService, ontologySearchService, searchableField, ontologyRetrievalConfig, graphImageService,
              goValidationHelper, ontologyPagingConfig, GO_SPECIFIER, httpHeadersProvider);
        Preconditions.checkArgument(metaDataProvider != null, "Metadata provider cannot be null.");
        this.metaDataProvider = metaDataProvider;
    }

    /**
     * Get meta data information about the Ontology service
     *
     * @return response with metadata information.
     */
    @ApiOperation(value = "Get meta-data information about the gene ontology service",
            response = About.class,
            notes = "Gene ontology version number and creation date.")
    @RequestMapping(value = "/about", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<MetaData> provideMetaData() {
        return new ResponseEntity<>(this.metaDataProvider.lookupMetaData(), HttpStatus.OK);
    }

    /**
     * Gets slimming information for the provided slim-set, where the slims can be reached only via the
     * provided relationships.
     *
     * @param ids the slim-set
     * @param relations the relationships over which the slims can be reached
     * @return a response containing the id/slim-id mappings
     */
    @RequestMapping(value = "slim", method = RequestMethod.GET, produces = {MediaType
            .APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<SlimTerm>> findSlims(
            @ApiParam(value = "Comma-separated term IDs forming the 'slim-set'", required = true)
            @RequestParam(value = "ids", required = false) String ids,
            @ApiParam(value = "The relationships over which the slimming information is computed")
            @RequestParam(value = "relations", defaultValue = DEFAULT_SLIM_TRAVERSAL_TYPES_CSV) String relations) {

        checkSlimSetIsSet(ids);
        return getResultsResponse(ontologyService.findSlimmedInfoForSlimmedTerms(
                validationHelper.validateCSVIds(ids),
                asOntologyRelationTypeArray(validationHelper.validateRelationTypes(relations))));
    }

    private void checkSlimSetIsSet(String ids) {
        if (ids == null || ids.isEmpty()) {
            throw new ParameterException(MISSING_SLIM_SET_ERROR_MESSAGE);
        }
    }
}
