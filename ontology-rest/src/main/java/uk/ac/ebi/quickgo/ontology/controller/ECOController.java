package uk.ac.ebi.quickgo.ontology.controller;

import uk.ac.ebi.quickgo.common.SearchableField;
import uk.ac.ebi.quickgo.common.validator.OntologyIdPredicate;
import uk.ac.ebi.quickgo.graphics.service.GraphImageService;
import uk.ac.ebi.quickgo.ontology.common.OntologyType;
import uk.ac.ebi.quickgo.ontology.model.ECOTerm;
import uk.ac.ebi.quickgo.ontology.model.OBOTerm;
import uk.ac.ebi.quickgo.ontology.service.OntologyService;
import uk.ac.ebi.quickgo.ontology.service.search.SearchServiceConfig;
import uk.ac.ebi.quickgo.rest.search.SearchService;

import java.util.function.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping(value = "/ontology/eco")
public class ECOController extends OBOController<ECOTerm> {
   @Autowired
    public ECOController(OntologyService<ECOTerm> ecoOntologyService,
            SearchService<OBOTerm> ontologySearchService,
            SearchableField searchableField,
            SearchServiceConfig.OntologyCompositeRetrievalConfig ontologyRetrievalConfig,
            GraphImageService graphImageService,
           @Value("${ontology.max_page_size:600}") int maxPageSize,
           @Value("${ontology.default_page_size:25}") int defaultPageSize) {
        super(ecoOntologyService, ontologySearchService, searchableField, ontologyRetrievalConfig, graphImageService,
              maxPageSize, defaultPageSize);
    }

    @Override
    public Predicate<String> idValidator() {
        return OntologyIdPredicate.isValidECOTermId();
    }

    @Override protected OntologyType getOntologyType() {
        return OntologyType.ECO;
    }
}
