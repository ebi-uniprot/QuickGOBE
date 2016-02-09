package uk.ac.ebi.quickgo.common.search.solr;

/**
 * Created 08/02/16
 * @author Edd
 */
public interface SolrRetrievalConfig {
    String[] getSearchReturnedFields();
    String getHighlightStartDelim();
    String getHighlightEndDelim();
}
