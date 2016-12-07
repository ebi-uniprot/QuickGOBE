package uk.ac.ebi.quickgo.annotation.validation.model;

import java.util.List;

/**
 * Defines the methods required to retrieve a zero or more validation objects by their id.
 *
 * @author Tony Wardell
 * Date: 22/11/2016
 * Time: 15:07
 * Created with IntelliJ IDEA.
 */
public interface ValidationEntities{

    /**
     * Return the validation entity for supplied key value
     * @param id identifying value
     * @return an instance of ValidationEntity or null if not found
     */
    List<ValidationEntity> get(String id);

    /**
     * Add a collection of ValidationEntity instances to the class.
     * @param items
     */
    void addEntities(List<? extends ValidationEntity> items);

}
