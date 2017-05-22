package uk.ac.ebi.quickgo.rest.search.results.transformer;

import uk.ac.ebi.quickgo.rest.comm.FilterContext;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents the recording and application of a chain of transformations to a
 * specified result type.
 *
 * @param <R> the type of result to be transformed
 *
 * Created 09/08/16
 * @author Edd
 */
public class ResultTransformerChain<R> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResultTransformerChain.class);
    private final List<ResultTransformer<R>> transformers;

    public ResultTransformerChain() {
        transformers = new ArrayList<>();
    }

    /**
     * Adds a new transformer to the chain of transformations
     * @param resultTransformer a transformation to be applied during
     * {@link #applyTransformations(Object, FilterContext)}.
     */
    public void addTransformer(ResultTransformer<R> resultTransformer) {
        this.transformers.add(resultTransformer);
    }

    /**
     * Apply the recorded list of {@link ResultTransformer}s to the specified result.
     *
     * @param result the result to transform
     * @param filterContext additional context information available during the result transformations
     * @return the transformed result of type {@code R}
     */
    public R applyTransformations(R result, FilterContext filterContext) {
        R transformation = result;
        for (ResultTransformer<R> transformer : transformers) {
            transformation = transformer.transform(result, filterContext);
        }
        return transformation;
    }
}
