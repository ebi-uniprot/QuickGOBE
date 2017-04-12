package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.common.transformer.ResponseValueInjector;
import uk.ac.ebi.quickgo.rest.comm.FilterContext;
import uk.ac.ebi.quickgo.rest.search.request.converter.RESTFilterConverterFactory;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;
import uk.ac.ebi.quickgo.rest.search.results.transformer.ResultTransformationRequest;
import uk.ac.ebi.quickgo.rest.search.results.transformer.ResultTransformationRequests;
import uk.ac.ebi.quickgo.rest.search.results.transformer.ResultTransformer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * The purpose of this class is to insert ontology related information, available externally via RESTful services,
 * into a {@link QueryResult} of {@link Annotation}s.
 *
 * Created 06/04/17
 * @author Edd
 */
public class ExternalServiceResultsTransformer implements ResultTransformer<QueryResult<Annotation>> {

    private static final ResultTransformationRequests EMPTY_TRANSFORMATION_REQUESTS = new ResultTransformationRequests();
    private final RESTFilterConverterFactory restFilterConverterFactory;
    private final List<ResponseValueInjector> fieldInjectors;
    private final List<String> fieldsToAdd;

    public ExternalServiceResultsTransformer(RESTFilterConverterFactory restFilterConverterFactory,
            List<ResponseValueInjector> fieldInjectors) {
        checkArgument(restFilterConverterFactory != null,
                "RESTFilterConverterFactory cannot be null");
        checkArgument(fieldInjectors != null, "Supplied list of OntologyFieldInjectors cannot be null");

        this.restFilterConverterFactory = restFilterConverterFactory;
        this.fieldInjectors = fieldInjectors;
        this.fieldsToAdd = fieldInjectors.stream().map(ResponseValueInjector::getId).collect(Collectors.toList());
    }

    @Override public QueryResult<Annotation> transform(QueryResult<Annotation> result, FilterContext filterContext) {
        ResultTransformationRequests transformationRequests =
                filterContext
                        .get(ResultTransformationRequests.class)
                        .orElse(EMPTY_TRANSFORMATION_REQUESTS);

        Set<String> allRequests = transformationRequests.getRequests().stream().map(
                ResultTransformationRequest::getId).collect(Collectors.toSet());
        Set<String> requiredRequests = new HashSet<>(allRequests);
        requiredRequests.retainAll(fieldsToAdd);

        if (!requiredRequests.isEmpty()) {
            List<ResponseValueInjector> requiredInjectors =
                    fieldInjectors.stream()
                            .filter(injector -> requiredRequests.contains(injector.getId()))
                            .collect(Collectors.toList());

            result.getResults().forEach(annotation ->
                    requiredInjectors.forEach(valueInjector ->
                            valueInjector.inject(restFilterConverterFactory, annotation))
            );
        }

        return result;
    }
}
