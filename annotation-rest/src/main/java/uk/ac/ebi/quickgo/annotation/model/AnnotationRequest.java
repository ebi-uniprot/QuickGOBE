package uk.ac.ebi.quickgo.annotation.model;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationFields;
import uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelper;
import uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelperImpl;
import uk.ac.ebi.quickgo.rest.search.request.RESTRequest;
import uk.ac.ebi.quickgo.rest.search.request.SimpleRequest;

import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

import static java.util.Collections.emptySet;

/**
 * A data structure for the annotation filtering parameters passed in from the client.
 *
 * Each request parameter value, in CSV format, is encapsulated by a {@link ClientRequest}.
 *
 * @author Tony Wardell
 * Date: 25/04/2016
 * Time: 11:23
 * Created with IntelliJ IDEA.
 */
public class AnnotationRequest {
    public static final int DEFAULT_ENTRIES_PER_PAGE = 25;
    public static final int MAX_ENTRIES_PER_PAGE = 100;

    private static final int DEFAULT_PAGE_NUMBER = 1;

    private final Set<SimpleRequest> simpleRequests = new HashSet<>();

    @Min(0) @Max(MAX_ENTRIES_PER_PAGE)
    private int limit = DEFAULT_ENTRIES_PER_PAGE;

    @Min(1)
    private int page = DEFAULT_PAGE_NUMBER;

    private ControllerValidationHelper validationHelper = new ControllerValidationHelperImpl();
    private String assignedBy = null;
    private String aspect = null;

    //TODO:change the way the field is referenced
    private static final String ASPECT_FIELD = "aspect";

    /**
     *  E.g., ASPGD,Agbase,..
     *  In the format assignedBy=ASPGD,Agbase
     */
    public void setAssignedBy(String assignedBy) {
        simpleRequests.add(createSimpleRequest(AnnotationFields.ASSIGNED_BY, this.assignedBy = assignedBy));
    }

    @Pattern(regexp = "^[A-Za-z][A-Za-z\\-_]+(,[A-Za-z][A-Za-z\\-_]+)*")
    public String getAssignedBy() {
        return assignedBy;
    }

    public void setAspect(String aspect) {
        if(aspect != null) {
            simpleRequests.add(createSimpleRequest(ASPECT_FIELD, this.aspect = aspect.toLowerCase()));
        }
    }

    @Pattern(regexp = "(?i)biological_process|molecular_function|cellular_component")
    public String getAspect() {
        return aspect;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public Set<SimpleRequest> getSimpleRequests() {
        return simpleRequests;
    }

    // todo: implement fetching of join requests -- there might be multiple types, each added to a set of them
    // whenever one is created
    public Set<SimpleRequest> getJoinRequests() {
        return emptySet();
    }

    // todo: implement fetching of rest requests -- there might be multiple types, each added to a set of them
    // whenever one is created
    public Set<RESTRequest> getRESTRequests() {
        return emptySet();
    }

    private SimpleRequest createSimpleRequest(String field, String value) {
        return new SimpleRequest(field, validationHelper.csvToList(value));
    }
}