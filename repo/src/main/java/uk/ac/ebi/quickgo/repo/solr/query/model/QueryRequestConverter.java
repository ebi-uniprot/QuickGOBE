package uk.ac.ebi.quickgo.repo.solr.query.model;

/**
 * Converts a {@link QueryRequest} into a query that an implementing data source can execute.
 */
public interface QueryRequestConverter<T> {
    T convert(QueryRequest request);
}