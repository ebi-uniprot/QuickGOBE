package uk.ac.ebi.quickgo.rest.search.results.transformer;

import uk.ac.ebi.quickgo.rest.comm.FilterContext;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * The purpose of this class is to insert information available externally via
 * RESTful services, into a {@link QueryResult} of where the results have type, {@link R}.
 *
 * Created 06/04/17
 * @author Edd
 */
public class ExternalServiceResultsTransformer<R, M> implements ResultTransformer<R> {
    private static final ResultTransformationRequests EMPTY_TRANSFORMATION_REQUESTS =
            new ResultTransformationRequests();
    private final List<ResponseValueInjector<M>> fieldInjectors;
    private final List<String> fieldsToAdd;
    private final ResultMutator<R, M> resultMutator;

    public ExternalServiceResultsTransformer(List<ResponseValueInjector<M>> fieldInjectors,
            ResultMutator<R, M> resultMutator) {
        checkArgument(fieldInjectors != null, "Supplied list of ResponseValueInjectors cannot be null");
        checkArgument(resultMutator != null, "Supplied ResultMutator cannot be null");
        this.fieldInjectors = fieldInjectors;
        this.fieldsToAdd = fieldInjectors.stream().map(ResponseValueInjector::getId).collect(Collectors.toList());
        this.resultMutator = resultMutator;
    }

    @Override public R transform(R results, FilterContext filterContext) {
        ResultTransformationRequests transformationRequests =
                filterContext.get(ResultTransformationRequests.class).orElse(EMPTY_TRANSFORMATION_REQUESTS);

        Set<String> requiredRequests = requiredRequests(transformationRequests);
        if (!requiredRequests.isEmpty()) {
            this.resultMutator.mutate(results, requiredInjectors(requiredRequests));

        }
        return results;
    }

    private List<ResponseValueInjector<M>> requiredInjectors(Set<String> requiredRequests) {
        return fieldInjectors.stream()
                             .filter(injector -> requiredRequests.contains(injector.getId()))
                             .collect(Collectors.toList());
    }

    private Set<String> requiredRequests(ResultTransformationRequests transformationRequests) {
        Set<String> requiredRequests = transformationRequests.getRequests()
                                                             .stream()
                                                             .map(ResultTransformationRequest::getId)
                                                             .collect(Collectors.toSet());
        requiredRequests.retainAll(fieldsToAdd);
        return requiredRequests;
    }

    @Override public String toString() {
        return "ExternalServiceResultsTransformer{" + "fieldInjectors=" + fieldInjectors + ", fieldsToAdd=" +
                fieldsToAdd + ", resultMutator=" + resultMutator + '}';
    }
}
