package uk.ac.ebi.quickgo.ontology.controller;

import uk.ac.ebi.quickgo.common.SearchableField;
import uk.ac.ebi.quickgo.graphics.service.GraphImageService;
import uk.ac.ebi.quickgo.ontology.OntologyRestConfig;
import uk.ac.ebi.quickgo.ontology.common.OntologyType;
import uk.ac.ebi.quickgo.ontology.controller.validation.OBOControllerValidationHelper;
import uk.ac.ebi.quickgo.ontology.model.ECOTerm;
import uk.ac.ebi.quickgo.ontology.model.OBOTerm;
import uk.ac.ebi.quickgo.ontology.model.OntologySpecifier;
import uk.ac.ebi.quickgo.ontology.service.OntologyService;
import uk.ac.ebi.quickgo.ontology.service.search.SearchServiceConfig;
import uk.ac.ebi.quickgo.rest.headers.HttpHeadersProvider;
import uk.ac.ebi.quickgo.rest.search.SearchService;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static uk.ac.ebi.quickgo.ontology.model.OntologyRelationType.ECO_GRAPH_TRAVERSAL_TYPES;

/**
 * REST controller for accessing ECO related information.
 *
 * For complete list of necessary endpoints, and their behaviour:
 *  refer to https://www.ebi.ac.uk/seqdb/confluence/display/GOA/REST+API
 *
 * Created 16/11/15
 * @author Edd
 */
@RestController
@Api(tags = {"evidence & conclusion ontology"})
@RequestMapping(value = "/ontology/eco")
public class ECOController extends OBOController<ECOTerm> {

    private static final OntologySpecifier ECO_SPECIFIER = new OntologySpecifier(OntologyType.ECO,
                                                                                 ECO_GRAPH_TRAVERSAL_TYPES);

   @Autowired
    public ECOController(OntologyService<ECOTerm> ecoOntologyService,
                         SearchService<OBOTerm> ontologySearchService,
                         SearchableField searchableField,
                         SearchServiceConfig.OntologyCompositeRetrievalConfig ontologyRetrievalConfig,
                         GraphImageService graphImageService,
                         OBOControllerValidationHelper ecoValidationHelper,
                         OntologyRestConfig.OntologyPagingConfig ontologyPagingConfig,
           HttpHeadersProvider httpHeadersProvider
   ) {
        super(ecoOntologyService, ontologySearchService, searchableField, ontologyRetrievalConfig, graphImageService,
              ecoValidationHelper, ontologyPagingConfig, ECO_SPECIFIER, httpHeadersProvider);
    }
}
