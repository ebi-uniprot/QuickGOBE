package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.slim;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.OntologyResponseConverter;
import uk.ac.ebi.quickgo.rest.comm.ConversionContext;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;
import uk.ac.ebi.quickgo.rest.search.results.transformer.ResultTransformer;

import java.util.List;
import java.util.Map;

/**
 * Created 09/08/16
 * @author Edd
 */
public class SlimResultsTransformer implements ResultTransformer<QueryResult<Annotation>> {
    @Override
    public QueryResult<Annotation> transform(QueryResult<Annotation> queryResult, ConversionContext conversionContext) {
        OntologyResponseConverter.SlimmingConversionInfo conversionInfo =
                conversionContext
                        .get(OntologyResponseConverter.SlimmingConversionInfo.class)
                        .orElse(new OntologyResponseConverter.SlimmingConversionInfo());

        Map<String, List<String>> descendantToTermMap = conversionInfo.getInfo();

        List<Annotation> results = queryResult.getResults();
        results.stream()
                .filter(result -> descendantToTermMap.containsKey(result.goId))
                .forEach(result -> result.slimmedGoIds = descendantToTermMap.get(result.goId));

        return queryResult;
    }
}
