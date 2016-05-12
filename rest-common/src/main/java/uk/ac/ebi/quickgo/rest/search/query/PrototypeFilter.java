package uk.ac.ebi.quickgo.rest.search.query;

import java.util.Arrays;
import java.util.List;

/**
 * Holds the data required to specify a filter value
 *
 * @author Tony Wardell
 * Date: 03/05/2016
 * Time: 10:25
 * Created with IntelliJ IDEA.
 */
public class PrototypeFilter {

    private static final String COMMA = ",";
    private String filterField;
    private List<String> args;
    private Validator<String> validator;

    public String getFilterField() {
        return filterField;
    }

    public List<String> getArgs() {
        return args;
    }

    public static final PrototypeFilter create(String filterField, String argIncludingDelimiters, Validator<String>
            validator ){
        PrototypeFilter prototypeFilter = new PrototypeFilter();
        prototypeFilter.filterField = filterField;
        prototypeFilter.args = Arrays.asList(argIncludingDelimiters.split(COMMA));
        prototypeFilter.validator = validator;
        return prototypeFilter;
    }

    /**
     *
     * Test each argument in the list to see if its valid
     */
    public void validate(){
        args.stream().forEach(a -> validator.validate(a));
    }
}
