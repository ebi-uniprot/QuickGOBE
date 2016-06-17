package uk.ac.ebi.quickgo.annotation.model;

import uk.ac.ebi.quickgo.rest.search.request.ClientRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

import static uk.ac.ebi.quickgo.annotation.common.document.AnnotationFields.ASSIGNED_BY;

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
    private static final String COMMA = ",";

    private final HashMap<String, String> requestMap = new HashMap<>();

    @Min(0) @Max(MAX_ENTRIES_PER_PAGE)
    private int limit = DEFAULT_ENTRIES_PER_PAGE;

    @Min(1)
    private int page = DEFAULT_PAGE_NUMBER;

    private String assignedBy = null;
    private String aspect = null;

    private static final String ASPECT_FIELD = "aspect";

    /**
     *  E.g., ASPGD,Agbase,..
     *  In the format assignedBy=ASPGD,Agbase
     */
    public void setAssignedBy(String assignedBy) {
        if (assignedBy != null) {
            requestMap.put(ASSIGNED_BY, assignedBy);
        }
    }

    @Pattern(regexp = "^[A-Za-z][A-Za-z\\-_]+(,[A-Za-z][A-Za-z\\-_]+)*")
    public String getAssignedBy() {
        return requestMap.get(ASSIGNED_BY);
    }

    public void setAspect(String aspect) {
        if(aspect != null) {
            requestMap.put(ASPECT_FIELD, aspect.toLowerCase());
        }
    }

    @Pattern(regexp = "(?i)biological_process|molecular_function|cellular_component")
    public String getAspect() {
        return requestMap.get(ASPECT_FIELD);
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

    public List<ClientRequest> createRequestFilters() {
        List<ClientRequest> clientRequests = new ArrayList<>();

        createSimpleFilter(ASPECT_FIELD).ifPresent(clientRequests::add);
        createSimpleFilter(ASSIGNED_BY).ifPresent(clientRequests::add);

        return clientRequests;
    }

    private Optional<ClientRequest> createSimpleFilter(String key) {
        Optional<ClientRequest> request;
        if (requestMap.containsKey(key)) {
            ClientRequest.Builder requestBuilder = ClientRequest.newBuilder();
            requestBuilder.addProperty(key, requestMap.get(key).split(COMMA));
            request = Optional.of(requestBuilder.build());
        } else {
            request = Optional.empty();
        }

        return request;
    }

}