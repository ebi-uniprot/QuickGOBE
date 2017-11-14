package uk.ac.ebi.quickgo.annotation.service.search;

import uk.ac.ebi.quickgo.rest.comm.FilterContext;
import uk.ac.ebi.quickgo.rest.model.CompletableValue;
import uk.ac.ebi.quickgo.rest.search.results.transformer.ResultTransformationRequest;
import uk.ac.ebi.quickgo.rest.search.results.transformer.ResultTransformationRequests;
import uk.ac.ebi.quickgo.rest.search.results.transformer.ResultTransformerChain;

import java.util.Objects;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.stereotype.Service;

/**
 * Provide a lookup service for names, caching results for use later when not null
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
    private static final String CACHE_NAME = "names";
    private final ResultTransformerChain<CompletableValue> completableValueTransformerChain;
    private final CacheManager cacheManager;

    public NameService(
            ResultTransformerChain<CompletableValue> completableValueTransformerChain, CacheManager cacheManager) {
        this.completableValueTransformerChain = completableValueTransformerChain;
        this.cacheManager = cacheManager;
    }

    public String findName(String targetName, String targetKey) {
        String returnValue;
        SimpleKey simpleKey = new SimpleKey(targetName, targetKey);

        Element value = cacheManager.getEhcache("names").get(simpleKey);
        if(Objects.nonNull(value)){
            returnValue = (String)value.getObjectValue();
        }else {
            returnValue = nameLookup(targetName, targetKey);
            saveToCache(simpleKey, returnValue);
        }
        return returnValue;
    }

    private String nameLookup(String targetName, String targetKey) {
        FilterContext filterContext = createFilterContextForName(targetName);
        CompletableValue completableValue = new CompletableValue(targetKey);
        LOGGER.debug("Getting " + filterContext + " : " + targetKey);
        completableValueTransformerChain.applyTransformations(completableValue, filterContext);
        return completableValue.value;
    }

    private void saveToCache(SimpleKey simpleKey, String returnValue) {
        if(Objects.nonNull(returnValue)) {
            Element element = new Element(simpleKey, returnValue);
            cacheManager.getEhcache(CACHE_NAME).put(element);
        }
    }

    private static FilterContext createFilterContextForName(String targetName) {
        FilterContext filterContext = new FilterContext();
        ResultTransformationRequests transformationRequests = new ResultTransformationRequests();
        transformationRequests.addTransformationRequest(new ResultTransformationRequest(targetName));
        filterContext.save(ResultTransformationRequests.class, transformationRequests);
        return filterContext;
    }
}
