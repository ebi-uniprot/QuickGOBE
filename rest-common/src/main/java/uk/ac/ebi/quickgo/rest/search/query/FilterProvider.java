package uk.ac.ebi.quickgo.rest.search.query;

import java.util.stream.Stream;

/**
 * Instances of class that implement this method are expected to be able to provide a stream of PrototypeFilters.
 *
 * @author Tony Wardell
 * Date: 03/05/2016
 * Time: 10:56
 * Created with IntelliJ IDEA.
 */
public interface FilterProvider {

    Stream<PrototypeFilter> stream();

    int getPage();

    int getLimit();
}
