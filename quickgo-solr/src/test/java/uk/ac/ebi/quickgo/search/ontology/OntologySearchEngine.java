package uk.ac.ebi.quickgo.search.ontology;

import uk.ac.ebi.quickgo.search.AbstractSearchEngine;
import uk.ac.ebi.quickgo.solr.model.ontology.SolrTerm;

/**
 * A search engine based on the ontology Solr core.
 *
 * Created 02/11/15
 * @author Edd
 */
public class OntologySearchEngine extends AbstractSearchEngine<SolrTerm> {

    private static final String CORE_NAME = "ontology";
    private static final String ID = "id";

    public OntologySearchEngine() {
        super(CORE_NAME);
    }

    /**
     * Note, that the identifier field, {@code id}, is not used by
     * all {@code docType}s!
     * @return
     */
    @Override protected String identifierField() {
        return ID;
    }
}
