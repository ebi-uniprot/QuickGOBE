package uk.ac.ebi.quickgo.rest.search.query;

/**
 * Interface used to traverse the {@link QueryRequest} data structure, and extra information from it.
 */
public interface QueryVisitor<T> {
    T visit(FieldQuery query);

    T visit(CompositeQuery query);

    T visit(NoFieldQuery query);

    T visit(AllQuery query);

    T visit(JoinQuery query);

    T visit(AllNonEmptyFieldQuery query);
}
