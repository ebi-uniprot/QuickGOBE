package uk.ac.ebi.quickgo.rest.search.query;

import com.google.common.base.Preconditions;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

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

    private PrototypeFilter() {}

    public String getFilterField() {
        return filterField;
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

    public Stream<String> provideStream(){
        return args.stream();

    }
}
