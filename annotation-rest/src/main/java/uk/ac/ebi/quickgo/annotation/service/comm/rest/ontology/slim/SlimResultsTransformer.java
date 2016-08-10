package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.slim;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.rest.comm.ConversionContext;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;
import uk.ac.ebi.quickgo.rest.search.results.transformer.ResultTransformer;

/**
 * Created 09/08/16
 * @author Edd
 */
public class SlimResultsTransformer implements ResultTransformer<QueryResult<Annotation>> {
    @Override
    public QueryResult<Annotation> transform(QueryResult<Annotation> result, ConversionContext conversionContext) {

        return null;
    }
}
