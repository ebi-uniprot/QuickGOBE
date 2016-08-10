package uk.ac.ebi.quickgo.rest.search.results.transformer;

import uk.ac.ebi.quickgo.rest.comm.ConversionContext;

import java.util.List;

/**
 * needed? currently we have a use case of just 1 transformer pre request.
 *
 * Created 09/08/16
 * @author Edd
 */
public class ResultTransformerChain<R> {
    private List<ResultTransformer<R>> transformers;

    public void addTransformer(ResultTransformer<R> resultTransformer) {
        this.transformers.add(resultTransformer);
    }

    public R transformChain(R result, ConversionContext conversionContext) {
        R transformation = result;
        for (ResultTransformer<R> transformer : transformers) {
            transformation = transformer.transform(result, conversionContext);
        }
        return transformation;
    }
}
