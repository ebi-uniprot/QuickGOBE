package uk.ac.ebi.quickgo.rest.search.results.transformer;

import uk.ac.ebi.quickgo.rest.search.request.converter.RESTFilterConverterFactory;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * For every model held by a {@link QueryResult}, update the model using each injector passed to the mutate method.
 * @author Tony Wardell
 * Date: 09/10/2017
 * Time: 10:33
 * Created with IntelliJ IDEA.
 */
public class ValueInjectionToQueryResults<R> implements ResultMutator<QueryResult<R>, R> {

    private final RESTFilterConverterFactory restFilterConverterFactory;

    public ValueInjectionToQueryResults(RESTFilterConverterFactory restFilterConverterFactory) {
        checkArgument(restFilterConverterFactory != null, "RESTFilterConverterFactory cannot be null");
        this.restFilterConverterFactory = restFilterConverterFactory;
    }

    @Override public void mutate(QueryResult<R> results, List<ResponseValueInjector<R>> requiredInjectors) {
        results.getResults()
               .forEach(result -> requiredInjectors.forEach(valueInjector -> valueInjector.inject(
                       restFilterConverterFactory,
                       result)));
            }
}
