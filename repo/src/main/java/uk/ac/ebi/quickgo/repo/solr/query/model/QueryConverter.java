package uk.ac.ebi.quickgo.repo.solr.query.model;


/**
 * Created by rantunes on 29/11/15.
 */
interface QueryConverter<T> {
    T convert(FieldQuery query);

    T convert(CompositeQuery query);
}
