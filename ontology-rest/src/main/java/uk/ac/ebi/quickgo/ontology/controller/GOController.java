package uk.ac.ebi.quickgo.ontology.controller;

import uk.ac.ebi.quickgo.common.SearchableField;
import uk.ac.ebi.quickgo.graphics.service.GraphImageService;
import uk.ac.ebi.quickgo.ontology.OntologyRestConfig;
import uk.ac.ebi.quickgo.ontology.common.OntologyType;
import uk.ac.ebi.quickgo.ontology.controller.validation.OBOControllerValidationHelper;
import uk.ac.ebi.quickgo.ontology.model.GOTerm;
import uk.ac.ebi.quickgo.ontology.model.OBOTerm;
import uk.ac.ebi.quickgo.ontology.service.OntologyService;
import uk.ac.ebi.quickgo.ontology.metadata.MetaDataProvider;
import uk.ac.ebi.quickgo.ontology.service.search.SearchServiceConfig;
import uk.ac.ebi.quickgo.rest.metadata.MetaData;
import uk.ac.ebi.quickgo.rest.search.SearchService;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping(value = "/ontology/go")
public class GOController extends OBOController<GOTerm> {

    private final MetaDataProvider metaDataProvider;

    @Autowired
    public GOController(OntologyService<GOTerm> goOntologyService,
            SearchService<OBOTerm> ontologySearchService,
            SearchableField searchableField,
            SearchServiceConfig.OntologyCompositeRetrievalConfig ontologyRetrievalConfig,
            GraphImageService graphImageService,
            OBOControllerValidationHelper goValidationHelper,
            OntologyRestConfig.OntologyPagingConfig ontologyPagingConfig,
            MetaDataProvider metaDataProvider) {
        super(goOntologyService, ontologySearchService, searchableField, ontologyRetrievalConfig, graphImageService,
              goValidationHelper, ontologyPagingConfig, OntologyType.GO);
        this.metaDataProvider = metaDataProvider;
    }


    /**
     * Get core information about a list of terms in comma-separated-value (CSV) format
     *
     * @param ids ontology identifiers in CSV format
     * @return
     * <ul>
     *     <li>all ids are valid: response consists of a 200 with the chosen information about the ontology terms</li>
     *     <li>any id is not found: response returns 200 with an empty result set.</li>
     *     <li>any id is of the an invalid format: response returns 400</li>
     * </ul>
     */
    @ApiOperation(value = "Get core information about a (CSV) list of terms based on their ids",
            notes = "If possible, response fields include: id, isObsolete, name, definition, ancestors, synonyms, " +
                    "aspect and usage.")
    @RequestMapping(value = "/about", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<MetaData> provideMetaData() {
        return getAboutResponse(this.metaDataProvider.lookupMetaData());
    }

    /**
     * Creates a {@link ResponseEntity} containing a {@link MetaData} for an about request.
     *
     * @param metaData result
     * @return a {@link ResponseEntity} containing a {@link MetaData}
     */
    <ResponseType> ResponseEntity<MetaData> getAboutResponse(MetaData metaData) {

        if (metaData == null) {
          return new ResponseEntity<>(metaData, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(metaData, HttpStatus.OK);
    }
}
