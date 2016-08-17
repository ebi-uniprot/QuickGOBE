package uk.ac.ebi.quickgo.ontology.controller;

import uk.ac.ebi.quickgo.ontology.common.document.OntologyType;
import uk.ac.ebi.quickgo.ontology.model.ECOTerm;
import uk.ac.ebi.quickgo.ontology.model.OBOTerm;
import uk.ac.ebi.quickgo.ontology.service.OntologyService;
import uk.ac.ebi.quickgo.ontology.service.search.SearchServiceConfig;
import uk.ac.ebi.quickgo.rest.search.SearchService;

import java.util.function.Predicate;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping(value = "/QuickGO/services/eco")
public class ECOController extends OBOController<ECOTerm> {

    private static final Pattern ECO_ID_FORMAT = Pattern.compile("^ECO:[0-9]{7}$");

    @Autowired
    public ECOController(OntologyService<ECOTerm> ecoOntologyService,
            SearchService<OBOTerm> ontologySearchService,
            SearchServiceConfig.OntologyCompositeRetrievalConfig ontologyRetrievalConfig) {
        super(ecoOntologyService, ontologySearchService, ontologyRetrievalConfig);
    }

    @Override
    public Predicate<String> idValidator() {
        return id -> ECO_ID_FORMAT.matcher(id).matches();
    }

    @Override protected OntologyType getOntologyType() {
        return OntologyType.ECO;
    }
}
