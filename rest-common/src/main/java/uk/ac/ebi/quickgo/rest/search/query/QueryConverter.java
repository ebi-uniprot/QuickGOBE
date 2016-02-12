package uk.ac.ebi.quickgo.rest.search.query;

/**
 * Created by rantunes on 29/11/15.
 */
interface QueryConverter<T> {
    T convert(FieldQuery query);

    T convert(CompositeQuery query);
}
