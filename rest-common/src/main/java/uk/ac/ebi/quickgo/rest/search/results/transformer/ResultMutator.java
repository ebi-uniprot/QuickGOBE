package uk.ac.ebi.quickgo.rest.search.results.transformer;

import java.util.List;

/**
 * Updates a specified model of type {@code R} using one or more {@link ResponseValueInjector}s
 * instance.
 *
 * Created 09/10/17
 * @author Tony Wardell
 */
@FunctionalInterface

public interface ResultMutator<I, T> {

    /**
     * Update the result using the list of supplied value injectors.
     * @param result model(s) to update.
     * @param requiredInjectors the provide the updates.
     */
    void mutate(I result, List<ResponseValueInjector<T>> requiredInjectors);
}
