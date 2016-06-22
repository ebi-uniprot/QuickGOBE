package uk.ac.ebi.quickgo.annotation.model;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationFields;
import uk.ac.ebi.quickgo.rest.ParameterException;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;

import java.util.*;
import java.util.stream.Stream;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

import static uk.ac.ebi.quickgo.annotation.common.document.AnnotationFields.ASSIGNED_BY;
import static uk.ac.ebi.quickgo.annotation.common.document.AnnotationFields.GO_EVIDENCE;
import static uk.ac.ebi.quickgo.annotation.common.document.AnnotationFields.TAXON_ID;

/**
 * Represents the annotation filtering parameters that have been specified by the client
 * via the request URL. This data-structure is populating using Spring bindings.
 *
 * @author Tony Wardell
 * Date: 25/04/2016
 * Time: 11:23
 */
public class AnnotationRequest {
    public static final int DEFAULT_ENTRIES_PER_PAGE = 25;
    public static final int MAX_ENTRIES_PER_PAGE = 100;

    private static final String COMMA = ",";
    private static final int DEFAULT_PAGE_NUMBER = 1;

    private static final String ASPECT_FIELD = "aspect";
    static final String USAGE_FIELD = "usage";
    static final String USAGE_IDS = "usageIds";
    private static final String USAGE_RELATIONSHIPS = "usageRelationships";
    private final Map<String, String> filters = new HashMap<>();

    @Min(0)
    @Max(MAX_ENTRIES_PER_PAGE)
    private int limit = DEFAULT_ENTRIES_PER_PAGE;
    @Min(1)
    private int page = DEFAULT_PAGE_NUMBER;

    @Pattern(regexp = "^[A-Za-z][A-Za-z\\-_]+(,[A-Za-z][A-Za-z\\-_]+)*")
    public String getAssignedBy() {
        return filters.get(ASSIGNED_BY);
    }

    /**
     *  E.g. ASPGD,Agbase,..
     *  In the format assignedBy=ASPGD,Agbase
     */
    public void setAssignedBy(String assignedBy) {
        filters.put(ASSIGNED_BY, assignedBy);
    }

    @Pattern(regexp = "^exact|slim|descendants$", flags = Pattern.Flag.CASE_INSENSITIVE, message = "Invalid usage: " +
            "${validatedValue})")
    public String getUsage() {
        return filters.get(USAGE_FIELD);
    }

    public void setUsage(String usage) {
        if (usage != null) {
            filters.put(USAGE_FIELD, usage.toLowerCase());
        }
    }

    @Pattern(regexp = "GO:[0-9]+(,GO:[0-9]+)*", flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Invalid GO IDs specified: ${validatedValue})")
    public String getUsageIds() {
        return filters.get(USAGE_IDS);
    }

    public void setUsageIds(String usageIds) {
        if (usageIds != null) {
            filters.put(USAGE_IDS, usageIds);
        }
    }

    @Pattern(regexp = "biological_process|molecular_function|cellular_component", flags = Pattern.Flag.CASE_INSENSITIVE)
    public String getAspect() {
        return filters.get(ASPECT_FIELD);
    }

    public void setAspect(String aspect) {
        if (aspect != null) {
            filters.put(ASPECT_FIELD, aspect.toLowerCase());
        }
    }

    @Pattern(regexp = "^[A-Za-z]{2,3}(,[A-Za-z]{2,3})*")
    public String getGoEvidence() {
        return filters.get(AnnotationFields.GO_EVIDENCE);
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

    @Pattern(regexp = "[0-9]+(,[0-9]+)*", message = "At least one invalid taxonomic identifier(s): ${validatedValue}")
    public String getTaxon() {
        return filters.get(AnnotationFields.TAXON_ID);
    }

    public void setTaxon(String taxId) {
        filters.put(AnnotationFields.TAXON_ID, taxId);
    }

    public List<FilterRequest> createFilterRequests() {
        List<FilterRequest> filterRequests = new ArrayList<>();

        Stream.of(ASPECT_FIELD, ASSIGNED_BY, TAXON_ID, GO_EVIDENCE)
                .map(this::createSimpleFilter)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(filterRequests::add);

        createUsageFilter().ifPresent(filterRequests::add);

        return filterRequests;
    }

    private Optional<FilterRequest> createSimpleFilter(String key) {
        Optional<FilterRequest> request;
        if (filters.containsKey(key)) {
            request = Optional.of(
                    FilterRequest.newBuilder()
                            .addProperty(key, filters.get(key).split(COMMA))
                            .build());
        } else {
            request = Optional.empty();
        }

        return request;
    }

    private Optional<FilterRequest> createUsageFilter() {
        Optional<FilterRequest> request;
        FilterRequest.Builder filterBuilder = FilterRequest.newBuilder();
        if (filters.containsKey(USAGE_FIELD)) {
            if (filters.containsKey(USAGE_IDS)) {
                filterBuilder
                        .addProperty(USAGE_FIELD, filters.get(USAGE_FIELD))
                        .addProperty(USAGE_IDS, filters.get(USAGE_IDS));
            } else {
                throw new ParameterException("Annotation usage requires 'usageIds' to be set.");
            }

            filterBuilder.addProperty(USAGE_RELATIONSHIPS, filters.get(USAGE_RELATIONSHIPS));

            request = Optional.of(filterBuilder.build());
        } else {
            request = Optional.empty();
        }

        return request;
    }
}