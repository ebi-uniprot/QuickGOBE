package uk.ac.ebi.quickgo.search.ontology;

import uk.ac.ebi.quickgo.search.AbstractSearchEngine;
import uk.ac.ebi.quickgo.solr.model.ontology.SolrTerm;

/**
 * Created 02/11/15
 * @author Edd
 */
public class OntologySearchEngine extends AbstractSearchEngine<SolrTerm> {

    private static final String CORE_NAME = "ontology";

    public OntologySearchEngine() {
        super(CORE_NAME);
    }

    @Override protected String identifierField() {
        return "id";
    }
}
