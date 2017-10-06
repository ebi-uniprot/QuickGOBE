package uk.ac.ebi.quickgo.rest.search.results.transformer;

import uk.ac.ebi.quickgo.rest.comm.FilterContext;
import uk.ac.ebi.quickgo.rest.search.request.converter.RESTFilterConverterFactory;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * The purpose of this class is to insert information available externally via
 * RESTful services, into a result which has type {@link R}.
 *
 * Created 06/04/17
 * @author Edd
 */
public class ExternalServiceResultsNotQueryResultTransformer<R> implements ResultTransformer<R> {
    private static final ResultTransformationRequests EMPTY_TRANSFORMATION_REQUESTS =
            new ResultTransformationRequests();
    private final RESTFilterConverterFactory restFilterConverterFactory;
    private final List<ResponseValueInjector<R>> fieldInjectors;
    private final List<String> fieldsToAdd;

    public ExternalServiceResultsNotQueryResultTransformer(RESTFilterConverterFactory restFilterConverterFactory,
            List<ResponseValueInjector<R>> fieldInjectors) {
        checkArgument(restFilterConverterFactory != null,
                "RESTFilterConverterFactory cannot be null");
        checkArgument(fieldInjectors != null, "Supplied list of ResponseValueInjectors cannot be null");

        this.restFilterConverterFactory = restFilterConverterFactory;
        this.fieldInjectors = fieldInjectors;
        this.fieldsToAdd = fieldInjectors.stream().map(ResponseValueInjector::getId).collect(Collectors.toList());
    }

    @Override public R transform(R result, FilterContext filterContext) {
        ResultTransformationRequests transformationRequests = filterContext.get(ResultTransformationRequests.class)
                .orElse(EMPTY_TRANSFORMATION_REQUESTS);

        Set<String> requiredRequests = transformationRequests.getRequests()
                .stream()
                .map(ResultTransformationRequest::getId)
                .collect(Collectors.toSet());
        requiredRequests.retainAll(fieldsToAdd);
        if (!requiredRequests.isEmpty()) {
            List<ResponseValueInjector<R>> requiredInjectors =
                    fieldInjectors.stream()
                            .filter(injector -> requiredRequests.contains(injector.getId()))
                            .collect(Collectors.toList());

           //Does not loop through results
            requiredInjectors.forEach(valueInjector ->
                    valueInjector.inject(restFilterConverterFactory, result));
        }
        return result;
    }

}
