package uk.ac.ebi.quickgo.search.miscellaneous;

import uk.ac.ebi.quickgo.search.AbstractSearchEngine;
import uk.ac.ebi.quickgo.solr.model.miscellaneous.SolrMiscellaneous;

/**
 * A search engine based on the miscellaneous Solr core.
 *
 * Created 02/11/15
 * @author Edd
 */
public class MiscellaneousSearchEngine extends AbstractSearchEngine<SolrMiscellaneous> {

    private static final String CORE_NAME = "miscellaneous";
    private static final String ID = "id";

    public MiscellaneousSearchEngine() {
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
