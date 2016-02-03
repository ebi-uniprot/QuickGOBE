package uk.ac.ebi.quickgo.common.query.model;


/**
 * Created by rantunes on 29/11/15.
 */
interface QueryConverter<T> {
    T convert(FieldQuery query);

    T convert(CompositeQuery query);
}
