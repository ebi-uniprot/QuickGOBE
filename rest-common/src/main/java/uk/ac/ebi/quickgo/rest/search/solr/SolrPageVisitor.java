package uk.ac.ebi.quickgo.rest.search.solr;

import uk.ac.ebi.quickgo.rest.search.query.*;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.params.CursorMarkParams;

/**
 *
 *
 * Created 13/01/17
 * @author Edd
 */
public class SolrPageVisitor implements PageVisitor<SolrQuery> {

    @Override public void visit(RegularPage page, SolrQuery subject) {
        subject.setRows(page.getPageSize());
        subject.setStart(calculateRowsFromPage(page.getPageNumber(), page.getPageSize()));
    }

    @Override public void visit(CursorPage page, SolrQuery subject) {
        subject.setRows(page.getPageSize());
        subject.set(CursorMarkParams.CURSOR_MARK_PARAM, page.getCursor());
        subject.addSort("id", SolrQuery.ORDER.asc);
    }

    private int calculateRowsFromPage(int page, int numRows) {
        return page == 0 ? 0 : (page - 1) * numRows;
    }
}
