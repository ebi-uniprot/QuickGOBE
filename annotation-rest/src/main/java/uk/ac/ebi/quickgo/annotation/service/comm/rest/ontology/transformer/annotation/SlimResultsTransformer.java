package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.annotation;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.converter.SlimmingConversionInfo;
import uk.ac.ebi.quickgo.rest.comm.FilterContext;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;
import uk.ac.ebi.quickgo.rest.search.results.transformer.ResultTransformer;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Transforms {@link QueryResult}s of {@link Annotation}s according to slimmed information recorded in
 * {@link SlimmingConversionInfo}.
 * Specifically, slimming information consisting of original GO ids mapped to their slimmed up GO ids, is used
 * to supplement current result information.
 *
 */
public class SlimResultsTransformer implements ResultTransformer<QueryResult<Annotation>> {

    private static final SlimmingConversionInfo EMPTY_SLIMMING_INFO =
            new SlimmingConversionInfo();

    private static final Logger LOGGER = getLogger(SlimResultsTransformer.class);

    @Override
    public QueryResult<Annotation> transform(QueryResult<Annotation> queryResult, FilterContext filterContext) {
        SlimmingConversionInfo conversionInfo =
                filterContext
                        .get(SlimmingConversionInfo.class)
                        .orElse(EMPTY_SLIMMING_INFO);

        Map<String, List<String>> descendantToTermMap = conversionInfo.getInfo();

        if (!descendantToTermMap.isEmpty()) {
            LOGGER.info("Transforming results to include slimmed IDs");
            queryResult.getResults().stream()
                    .filter(result -> descendantToTermMap.containsKey(result.goId))
                    .forEach(result -> result.slimmedIds = descendantToTermMap.get(result.goId));
        }


        return queryResult;
    }
}
