package uk.ac.ebi.quickgo.annotation.service.search;

import uk.ac.ebi.quickgo.rest.comm.FilterContext;
import uk.ac.ebi.quickgo.rest.model.CompletableValue;
import uk.ac.ebi.quickgo.rest.search.results.transformer.ResultTransformationRequest;
import uk.ac.ebi.quickgo.rest.search.results.transformer.ResultTransformationRequests;
import uk.ac.ebi.quickgo.rest.search.results.transformer.ResultTransformerChain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Provide a lookup service for names, caching results for use later.
 *
 * @author Tony Wardell
 * Date: 13/11/2017
 * Time: 15:32
 * Created with IntelliJ IDEA.
 */
@Service
@CacheConfig(cacheNames = {"names"})
public class NameService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NameService.class);
    private final ResultTransformerChain<CompletableValue> completableValueTransformerChain;

    public NameService(ResultTransformerChain<CompletableValue> completableValueTransformerChain) {
        this.completableValueTransformerChain = completableValueTransformerChain;
    }

    @Cacheable(unless="#result == null")
    public String findName(String targetName, String targetKey) {
        FilterContext filterContext = createFilterContextForName(targetName);
        CompletableValue completableValue = new CompletableValue(targetKey);
        LOGGER.debug("Getting " + filterContext + " : " + targetKey);
        completableValueTransformerChain.applyTransformations(completableValue, filterContext);
        return completableValue.value;
    }

    private static FilterContext createFilterContextForName(String targetName) {
        FilterContext filterContext = new FilterContext();
        ResultTransformationRequests transformationRequests = new ResultTransformationRequests();
        transformationRequests.addTransformationRequest(new ResultTransformationRequest(targetName));
        filterContext.save(ResultTransformationRequests.class, transformationRequests);
        return filterContext;
    }
}
