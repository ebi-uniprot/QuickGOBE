package uk.ac.ebi.quickgo.rest.search.query;

/**
 * Created 13/01/17
 * @author Edd
 */
public interface PageVisitor<S> {
    void visit(RegularPage page, S subject);
    void visit(CursorPage page, S subject);
}
