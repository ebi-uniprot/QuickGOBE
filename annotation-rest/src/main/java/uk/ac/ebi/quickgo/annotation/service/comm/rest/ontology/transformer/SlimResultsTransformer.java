package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.converter.SlimmingConversionInfo;
import uk.ac.ebi.quickgo.rest.comm.QueryContext;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;
import uk.ac.ebi.quickgo.rest.search.results.transformer.ResultTransformer;

import java.util.List;
import java.util.Map;

/**
 * Transforms {@link QueryResult}s of {@link Annotation}s according to slimmed information recorded in
 * {@link SlimmingConversionInfo}.
 * Specifically, slimming information consisting of original GO ids mapped to their slimmed up GO ids, is used
 * to supplement current result information.
 *
 */
public class SlimResultsTransformer implements ResultTransformer<QueryResult<Annotation>> {
    @Override
    public QueryResult<Annotation> transform(QueryResult<Annotation> queryResult, QueryContext queryContext) {
        SlimmingConversionInfo conversionInfo =
                queryContext
                        .get(SlimmingConversionInfo.class)
                        .orElse(new SlimmingConversionInfo());

        Map<String, List<String>> descendantToTermMap = conversionInfo.getInfo();

        List<Annotation> results = queryResult.getResults();
        results.stream()
                .filter(result -> descendantToTermMap.containsKey(result.goId))
                .forEach(result -> result.slimmedGoIds = descendantToTermMap.get(result.goId));

        return queryResult;
    }
}
