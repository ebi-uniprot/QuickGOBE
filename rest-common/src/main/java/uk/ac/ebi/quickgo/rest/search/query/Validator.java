package uk.ac.ebi.quickgo.rest.search.query;

/**
 * @author Tony Wardell
 * Date: 10/05/2016
 * Time: 14:15
 * Created with IntelliJ IDEA.
 */
@FunctionalInterface
public interface Validator<T> {

    void validate(T t);
}
