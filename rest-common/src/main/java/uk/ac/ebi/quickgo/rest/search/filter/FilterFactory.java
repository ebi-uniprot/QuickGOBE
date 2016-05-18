package uk.ac.ebi.quickgo.rest.search.filter;

/**
 * A factory responsible for creating {@link RequestFilter} instances based on the type of field and the values
 * provided.
 *
 * @author Ricardo Antunes
 */
public interface FilterFactory {

    RequestFilter createFilter(String field, String[] values);
}
