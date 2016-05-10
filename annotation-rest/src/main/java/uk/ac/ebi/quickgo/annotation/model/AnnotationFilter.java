package uk.ac.ebi.quickgo.annotation.model;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationFields;
import uk.ac.ebi.quickgo.annotation.validation.AnnotationValidation;
import uk.ac.ebi.quickgo.rest.search.query.FilterProvider;
import uk.ac.ebi.quickgo.rest.search.query.PrototypeFilter;
import uk.ac.ebi.quickgo.rest.search.query.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Stream;
import org.springframework.validation.Errors;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * A data structure for the annotation filtering parameters passed in from the client.
 *
 * Once the comma separated values have been set, then turn then into an object (PrototypeFilter) that
 * encapsulates the list and solr field name to use for that argument.
 *
 * @author Tony Wardell
 * Date: 25/04/2016
 * Time: 11:23
 * Created with IntelliJ IDEA.
 */
public class AnnotationFilter implements FilterProvider {

    public static final String DEFAULT_ENTRIES_PER_PAGE = "25";
    private static final String DEFAULT_PAGE_NUMBER = "1";

    //Non-data parameters
    private String limit = DEFAULT_ENTRIES_PER_PAGE;
    private String page = DEFAULT_PAGE_NUMBER;

    private final List<PrototypeFilter> prototypeFilters = new ArrayList<>();



    // E.g. ASPGD,Agbase,..
    public void setAssignedby(String assignedby) {

        //if we were doing validation in situ

//        if (!isNullOrEmpty(assignedby)) {
//            final PrototypeFilter pFilter = PrototypeFilter.create(AnnotationFields.ASSIGNED_BY, assignedby);
//            pFilter.validate((String a) -> AnnotationValidator.validateAssignedBy(a,errors));
//            prototypeFilters.add(pFilter);
//        }

        if (!isNullOrEmpty(assignedby)) {

            final Validator<String, Errors> validator = (String s, Errors errors) -> {
                Matcher m =AnnotationValidation.ALL_NUMERIC.matcher(s);
                if (m.matches()) {
                    errors.reject( "assignedBy.invalid", "Values for assignedBy cannot be fully numeric");
                }
            };

            final PrototypeFilter pFilter = PrototypeFilter.create(AnnotationFields.ASSIGNED_BY, assignedby, validator);
            prototypeFilters.add(pFilter);
        }

    }



    public void setPage(String page) {
        this.page = page;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public int getLimit() {
        return Integer.parseInt(limit);
    }

    public int getPage() {
        return Integer.parseInt(page);
    }

    public Stream<PrototypeFilter> stream() {
        return prototypeFilters.stream();
    }

}
