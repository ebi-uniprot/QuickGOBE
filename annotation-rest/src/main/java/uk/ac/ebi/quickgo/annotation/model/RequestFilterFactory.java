package uk.ac.ebi.quickgo.annotation.model;

import uk.ac.ebi.quickgo.rest.search.filter.RequestFilter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Tony Wardell
 * Date: 24/05/2016
 * Time: 17:15
 * Created with IntelliJ IDEA.
 */
public class RequestFilterFactory {

    private static final String COMMA = ",";
    private Map<String, FilterModifier> modifierMap = new LinkedHashMap<>();

    void addModifier(String key, FilterModifier filterModifier){
        modifierMap.put(key, filterModifier);
    }

    RequestFilter create(String key, String values){

        FilterModifier modifier = modifierMap.get(key);
        if(modifier!=null){
            return new RequestFilter(key, modifier.modify(splitFilterValues(values)));
        }else{
            return new RequestFilter(key,splitFilterValues(values));
        }
    }

    private String[] splitFilterValues(String values) {
        return values.split(COMMA);
    }
}
