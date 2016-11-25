package uk.ac.ebi.quickgo.annotation.validation.model;

import java.util.regex.Pattern;

/**
 * A validation entity for Database Cross References and their associated regular expressions for validating ids.
 *
 * @author Tony Wardell
 * Date: 07/11/2016
 * Time: 18:07
 * Created with IntelliJ IDEA.
 */
public class DBXRefEntity implements ValidationEntity {

    public String database;
    public String entityType;
    public String entityTypeName;
    public Pattern idValidationPattern;
    public String databaseURL;

    @Override
    public boolean test(String value) {
        return idValidationPattern.matcher(value).matches();
    }

    @Override public String keyValue() {
        return database;
    }
}
