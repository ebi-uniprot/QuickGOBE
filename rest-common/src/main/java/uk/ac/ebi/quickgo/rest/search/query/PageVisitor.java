package uk.ac.ebi.quickgo.rest.search.query;

/**
 * Interface used to traverse the {@link Page} data structure.
 *
 * @param <S> defines a context which can be used to retrieve
 *            or set additional information
 *
 * Created 13/01/17
 * @author Edd
 */
public interface PageVisitor<S> {
    void visit(RegularPage page, S subject);
    void visit(CursorPage page, S subject);
}
