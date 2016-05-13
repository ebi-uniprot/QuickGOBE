package uk.ac.ebi.quickgo.annotation.model;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationFields;
import uk.ac.ebi.quickgo.annotation.validation.AnnotationValidation;
import uk.ac.ebi.quickgo.rest.search.query.FilterProvider;
import uk.ac.ebi.quickgo.rest.search.query.PrototypeFilter;
import uk.ac.ebi.quickgo.rest.search.query.Validator;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Stream;
import javax.validation.constraints.Pattern;
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

    public static final int DEFAULT_ENTRIES_PER_PAGE = 25;
    private static final int DEFAULT_PAGE_NUMBER = 1;

    //Non-data parameters
    private int  limit = DEFAULT_ENTRIES_PER_PAGE;
    private int page = DEFAULT_PAGE_NUMBER;

    private final List<PrototypeFilter> prototypeFilters = new ArrayList<>();

    @Pattern(regexp = "^[A-Za-z][A-Za-z_]+(,[A-Za-z][A-Za-z_]+)*")
    private String assignedBy;

    /**
     *  E.g. ASPGD,Agbase,..
     *  In the format assignedBy=ASPGD,Agbase
     */
    public void setAssignedBy(String assignedBy) {

        this.assignedBy = assignedBy;

        if (!isNullOrEmpty(assignedBy)) {

            final Validator<String> validator = (String s) -> {};   //todo needs to be removed.

            final PrototypeFilter pFilter = PrototypeFilter.create(AnnotationFields.ASSIGNED_BY, assignedBy, validator);
            prototypeFilters.add(pFilter);
        }

    }



    public void setPage(int page) {
        Preconditions.checkArgument(page>-1, "The value for page cannot be negative");
        this.page = page;
    }

    public void setLimit(int limit) {
        Preconditions.checkArgument(limit>-1, "The value for limit cannot be negative");
        this.limit = limit;
    }

    public int getLimit() {
        return limit;
    }

    public int getPage() {
        return page;
    }

    /**
     * Provide a stream of the prototype filters.
     * @return
     */
    public Stream<PrototypeFilter> stream() {
        return prototypeFilters.stream();
    }

}
