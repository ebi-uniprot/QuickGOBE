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
import uk.ac.ebi.quickgo.ontology.service.OntologyService;
import uk.ac.ebi.quickgo.ontology.service.search.SearchServiceConfig;
import uk.ac.ebi.quickgo.rest.headers.HttpHeadersProvider;
import uk.ac.ebi.quickgo.rest.metadata.MetaData;
import uk.ac.ebi.quickgo.rest.metadata.MetaDataProvider;
import uk.ac.ebi.quickgo.rest.search.SearchService;

import com.google.common.base.Preconditions;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
@Api(tags = {"gene ontology"})
@RequestMapping(value = "/ontology/go")
@EnableConfigurationProperties(OntologyRestProperties.class)
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
                        MetaDataProvider metaDataProvider,
            HttpHeadersProvider httpHeadersProvider) {
        super(goOntologyService, ontologySearchService, searchableField, ontologyRetrievalConfig, graphImageService,
                goValidationHelper, ontologyPagingConfig, OntologyType.GO, httpHeadersProvider);
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
}
