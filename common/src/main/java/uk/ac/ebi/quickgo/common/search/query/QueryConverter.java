package uk.ac.ebi.quickgo.common.search.query;


/**
 * Created by rantunes on 29/11/15.
 */
interface QueryConverter<T> {
    T convert(FieldQuery query);

    T convert(CompositeQuery query);
}
