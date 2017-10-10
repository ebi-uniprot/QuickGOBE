package uk.ac.ebi.quickgo.rest.search.results.transformer;

import uk.ac.ebi.quickgo.rest.search.request.converter.RESTFilterConverterFactory;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Update the result of type R using each of the injectors passed to the mutate method.
 * @author Tony Wardell
 * Date: 09/10/2017
 * Time: 10:33
 * Created with IntelliJ IDEA.
 */
public class ValueInjectionToSingleResult<R> implements ResultMutator<R, R> {

    private final RESTFilterConverterFactory restFilterConverterFactory;

    public ValueInjectionToSingleResult(RESTFilterConverterFactory restFilterConverterFactory) {
        checkArgument(restFilterConverterFactory != null, "RESTFilterConverterFactory cannot be null");
        this.restFilterConverterFactory = restFilterConverterFactory;
    }

    @Override public void mutate(R result, List<ResponseValueInjector<R>> requiredInjectors) {
        requiredInjectors.forEach(valueInjector -> valueInjector.inject(restFilterConverterFactory, result));
    }
}
