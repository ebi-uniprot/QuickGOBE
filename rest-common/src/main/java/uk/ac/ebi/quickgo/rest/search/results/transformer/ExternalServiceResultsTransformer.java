package uk.ac.ebi.quickgo.rest.search.results.transformer;

import uk.ac.ebi.quickgo.rest.comm.FilterContext;
import uk.ac.ebi.quickgo.rest.search.request.converter.RESTFilterConverterFactory;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * The purpose of this class is to insert information available externally via
 * RESTful services, into a {@link QueryResult} of where the results have type, {@link R}.
 *
 * Created 06/04/17
 * @author Edd
 */
public class ExternalServiceResultsTransformer<R> implements ResultTransformer<QueryResult<R>> {
    Logger LOGGER = LoggerFactory.getLogger(ExternalServiceResultsTransformer.class);

    private static final ResultTransformationRequests EMPTY_TRANSFORMATION_REQUESTS =
            new ResultTransformationRequests();
    private final RESTFilterConverterFactory restFilterConverterFactory;
    private final List<ResponseValueInjector<R>> fieldInjectors;
    private final List<String> fieldsToAdd;

    public ExternalServiceResultsTransformer(RESTFilterConverterFactory restFilterConverterFactory,
            List<ResponseValueInjector<R>> fieldInjectors) {
        checkArgument(restFilterConverterFactory != null,
                "RESTFilterConverterFactory cannot be null");
        checkArgument(fieldInjectors != null, "Supplied list of ResponseValueInjectors cannot be null");

        this.restFilterConverterFactory = restFilterConverterFactory;
        this.fieldInjectors = fieldInjectors;
        this.fieldsToAdd = fieldInjectors.stream().map(ResponseValueInjector::getId).collect(Collectors.toList());
    }

    @Override public QueryResult<R> transform(QueryResult<R> result, FilterContext filterContext) {
        LOGGER.info("ExternalServiceResultsTransformer::transform starting.");

        ResultTransformationRequests transformationRequests = filterContext.get(ResultTransformationRequests.class)
                                                                           .orElse(EMPTY_TRANSFORMATION_REQUESTS);

        LOGGER.info("ExternalServiceResultsTransformer::transform build collection of required requests.");
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

            LOGGER.info("ExternalServiceResultsTransformer.transform build collection of required injectors.");
            result.getResults().forEach(annotation ->
                    requiredInjectors.forEach(valueInjector ->
                            valueInjector.inject(restFilterConverterFactory, annotation))
            );
        }
        LOGGER.info("ExternalServiceResultsTransformer.transform return transformed results.");
        return result;
    }

    @Override public String toString() {
        return "ExternalServiceResultsTransformer{" +
                "restFilterConverterFactory=" + restFilterConverterFactory +
                ", fieldInjectors=" + fieldInjectors +
                ", fieldsToAdd=" + fieldsToAdd +
                '}';
    }
}
