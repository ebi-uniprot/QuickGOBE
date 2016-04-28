package uk.ac.ebi.quickgo.annotation.search;

import uk.ac.ebi.quickgo.rest.search.query.FieldQuery;
import uk.ac.ebi.quickgo.rest.search.query.QueryRequest;
import uk.ac.ebi.quickgo.rest.search.query.SolrQueryConverter;

import org.apache.solr.client.solrj.SolrQuery;

/**
 * @author Tony Wardell
 * Date: 28/04/2016
 * Time: 13:20
 * Created with IntelliJ IDEA.
 */
public class AnnotationSolrQueryConverter extends SolrQueryConverter{

    public AnnotationSolrQueryConverter(String requestHandler) {
        super(requestHandler);
    }

    @Override public String visit(FieldQuery query) {
        return "(" + query.field() + SOLR_FIELD_SEPARATOR + query.value() + ")";
    }

    /**
     * For annotation filtering (and we use fq only for annotations) don't set a query String
     * @param request
     * @param solrQuery
     */
    protected void assignQuery(QueryRequest request, SolrQuery solrQuery) {
        return;
    }


}
