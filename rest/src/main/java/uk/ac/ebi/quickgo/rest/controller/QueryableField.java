package uk.ac.ebi.quickgo.rest.controller;

/**
 * Interface used on the Field enumerations. It checks whether a string is one of the queryable fields of the
 * enumeration.
 */
public interface QueryableField {

    boolean isQueryableField(String field);
}
