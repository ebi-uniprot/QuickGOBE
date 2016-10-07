package uk.ac.ebi.quickgo.ontology.controller;

import uk.ac.ebi.quickgo.rest.search.SearchService;
import uk.ac.ebi.quickgo.rest.search.SearchableField;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyType;
import uk.ac.ebi.quickgo.ontology.model.GOTerm;
import uk.ac.ebi.quickgo.ontology.model.OBOTerm;
import uk.ac.ebi.quickgo.ontology.service.OntologyService;
import uk.ac.ebi.quickgo.ontology.service.search.SearchServiceConfig;

import java.util.function.Predicate;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
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
@RequestMapping(value = "/QuickGO/services/go")
public class GOController extends OBOController<GOTerm> {

    private static final Pattern GO_ID_FORMAT = Pattern.compile("^GO:[0-9]{7}$");

    @Autowired
    public GOController(OntologyService<GOTerm> goOntologyService,
            SearchService<OBOTerm> ontologySearchService,
            SearchableField searchableField,
            SearchServiceConfig.OntologyCompositeRetrievalConfig ontologyRetrievalConfig) {
        super(goOntologyService, ontologySearchService, searchableField, ontologyRetrievalConfig);
    }

    @Override
    public Predicate<String> idValidator() {
        return id -> GO_ID_FORMAT.matcher(id).matches();
    }

    @Override protected OntologyType getOntologyType() {
        return OntologyType.GO;
    }
}
