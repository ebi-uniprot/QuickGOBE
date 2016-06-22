package uk.ac.ebi.quickgo.annotation.model;

import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;

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
 * Once the comma separated values have been set, then turn then into an object (SimpleFilter) that
 * encapsulates the list and solr field name to use for that argument.
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

    private final Map<String, String> filters = new HashMap<>();

    /**
     *  E.g. ASPGD,Agbase,..
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
        if (aspect != null) {
            filters.put(ASPECT_FIELD, aspect.toLowerCase());
        if(aspect != null) {
            requestMap.put(ASPECT_FIELD, aspect.toLowerCase());
        }
    }

    @Pattern(regexp = "biological_process|molecular_function|cellular_component", flags = Pattern.Flag.CASE_INSENSITIVE)
    public String getAspect() {
        return filters.get(ASPECT_FIELD);
    }

    /**
     * The older evidence codes
     * E.g. IEA, IBA, IBD etc. See <a href="http://geneontology.org/page/guide-go-evidence-codes">Guide QuickGO
     * evidence codes</a>
     * @param evidence the evidence code
     */
    public void setGoEvidence(String evidence) {
        filters.put(AnnotationFields.GO_EVIDENCE, evidence);
    }

    @Pattern(regexp = "^[A-Za-z]{2,3}(,[A-Za-z]{2,3})*")
    public String getGoEvidence() {
        return filters.get(AnnotationFields.GO_EVIDENCE);
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setLimit(int limit) {
        this.limit = limit;
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

    public List<FilterRequest> createRequestFilters() {
        List<FilterRequest> filterRequests = new ArrayList<>();

        createSimpleFilter(ASPECT_FIELD).ifPresent(filterRequests::add);
        createSimpleFilter(ASSIGNED_BY).ifPresent(filterRequests::add);

        return filterRequests;
    }

    public void setTaxon(String taxId) {
        filters.put(AnnotationFields.TAXON_ID, taxId);
    }

    private Optional<FilterRequest> createSimpleFilter(String key) {
        Optional<FilterRequest> request;
        if (requestMap.containsKey(key)) {
            FilterRequest.Builder requestBuilder = FilterRequest.newBuilder();
            requestBuilder.addProperty(key, requestMap.get(key).split(COMMA));
            request = Optional.of(requestBuilder.build());
        } else {
            request = Optional.empty();
        }

    @Pattern(regexp = "[0-9]+(,[0-9]+)*", message = "At least one invalid taxonomic identifier(s): ${validatedValue}")
    public String getTaxon() {
        return filters.get(AnnotationFields.TAXON_ID);
    }
}
        return request;
    }

}