package uk.ac.ebi.quickgo.common.search.query;

/**
 * Interface used to traverse the {@link QueryRequest} data structure, and extra information from it.
 */
interface QueryVisitor<T> {
    T visit(FieldQuery query);

    T visit(CompositeQuery query);

    T visit(NoFieldQuery query);
}
