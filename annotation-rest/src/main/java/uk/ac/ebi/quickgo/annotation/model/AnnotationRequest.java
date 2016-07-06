package uk.ac.ebi.quickgo.annotation.model;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationFields;
import uk.ac.ebi.quickgo.rest.ParameterException;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

import static uk.ac.ebi.quickgo.annotation.common.document.AnnotationFields.ASSIGNED_BY;
import static uk.ac.ebi.quickgo.annotation.common.document.AnnotationFields.GO_EVIDENCE;
import static uk.ac.ebi.quickgo.annotation.common.document.AnnotationFields.REFERENCE_SEARCH;
import static uk.ac.ebi.quickgo.annotation.common.document.AnnotationFields.TAXON_ID;
import static uk.ac.ebi.quickgo.annotation.common.document.AnnotationFields.WITH_FROM_SEARCH;
import static uk.ac.ebi.quickgo.annotation.common.document.AnnotationFields.QUALIFIER;


/**
 * A data structure for the annotation filtering parameters passed in from the client.
 *
 * Each request parameter value, in CSV format, is encapsulated by a {@link FilterRequest}.
 *
 * @author Tony Wardell
 * Date: 25/04/2016
 * Time: 11:23
 */
public class AnnotationRequest {
    public static final int DEFAULT_ENTRIES_PER_PAGE = 25;
    public static final int MAX_ENTRIES_PER_PAGE = 100;

    private static final String ASPECT_FIELD = "aspect";
    static final String USAGE_FIELD = "usage";
    static final String USAGE_IDS = "usageIds";
    static final String USAGE_RELATIONSHIPS = "usageRelationships";

    private static final int DEFAULT_PAGE_NUMBER = 1;
    private static final String COMMA = ",";

    private final HashMap<String, String> filterMap = new HashMap<>();

    @Min(0) @Max(MAX_ENTRIES_PER_PAGE)
    private int limit = DEFAULT_ENTRIES_PER_PAGE;

    @Min(1)
    private int page = DEFAULT_PAGE_NUMBER;

    private final Map<String, String> filterMap = new HashMap<>();

    /**
     *  E.g. ASPGD,Agbase,..
     *  In the format assignedBy=ASPGD,Agbase
     */
    public void setAssignedBy(String assignedBy) {
        if (assignedBy != null) {
            filterMap.put(ASSIGNED_BY, assignedBy);
        }
    }

    @Pattern(regexp = "^[A-Za-z][A-Za-z\\-_]+(,[A-Za-z][A-Za-z\\-_]+)*",
            message = "At least one 'Assigned By' value is invalid: ${validatedValue}")
    public String getAssignedBy() {
        return requestMap.get(ASSIGNED_BY);
    }

    public void setAspect(String aspect) {
        if (aspect != null) {
            filterMap.put(ASPECT_FIELD, aspect.toLowerCase());
        }
    }

    @Pattern(regexp = "biological_process|molecular_function|cellular_component", flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "At least one 'Aspect' value is invalid: ${validatedValue}")
    public String getAspect() {
        return filterMap.get(ASPECT_FIELD);
    }


        /**
         * The older evidence codes
         * E.g. IEA, IBA, IBD etc. See <a href="http://geneontology.org/page/guide-go-evidence-codes">Guide QuickGO
         * evidence codes</a>
         * @param evidence the evidence code
         */
        public void setGoEvidence(String evidence) {
            filterMap.put(GO_EVIDENCE, evidence);
        }

        /**
         * NOT, enables etc
         * @param qualifier
         */
    public void setQualifier(String qualifier){
        filterMap.put(QUALIFIER, qualifier);
    }

    public String getQualifter(){
        return filterMap.get(QUALIFIER);
    }

    /**
     * A list of with/from values, separated by commas
     * In the format withFrom=PomBase:SPBP23A10.14c,RGD:621207 etc
     * Users can supply just the id (e.g. PomBase) or id SPBP23A10.14c
     * @param withFrom comma separated with/from values
     */
    public void setWithFrom(String withFrom){
        filterMap.put(WITH_FROM_SEARCH, withFrom);
    }

    /**
     * Return a list of with/from values, separated by commas
     * @return String containing comma separated list of with/From values.
     */
    public  String getWithFrom(){
        return filterMap.get(WITH_FROM_SEARCH);
    }

    @Pattern(regexp = "^[A-Za-z]{2,3}(,[A-Za-z]{2,3})*",
            message = "At least one 'GO Evidence' value is invalid: ${validatedValue}")
    public String getGoEvidence() {
        return filterMap.get(GO_EVIDENCE);
    }

    public void setTaxon(String taxId) {
        filterMap.put(TAXON_ID, taxId);
    }

    @Pattern(regexp = "[0-9]+(,[0-9]+)*",
            message = "At least one invalid 'Taxonomic identifier' value is invalid: ${validatedValue}")
    public String getTaxon() {
        return filterMap.get(AnnotationFields.TAXON_ID);
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
            filters.put(USAGE_IDS, usageIds.toLowerCase());
        }
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

    public List<FilterRequest> createFilterRequests() {
        List<FilterRequest> filterRequests = new ArrayList<>();

        Stream.of(ASPECT_FIELD, ASSIGNED_BY, TAXON_ID, GO_EVIDENCE, REFERENCE_SEARCH, QUALIFIER, WITH_FROM_SEARCH)
                .map(this::createSimpleFilter)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(filterRequests::add);

        createUsageFilter().ifPresent(filterRequests::add);

        return filterRequests;
    }

    private Optional<FilterRequest> createSimpleFilter(String key) {
        Optional<FilterRequest> request;
        if (filterMap.containsKey(key)) {
            request = Optional.of(
                    FilterRequest.newBuilder()
                            .addProperty(key, filterMap.get(key).split(COMMA))
                            .build());
        } else {
            request = Optional.empty();
        }

        return request;
    }

    private Optional<FilterRequest> createUsageFilter() {
        Optional<FilterRequest> request;
        FilterRequest.Builder filterBuilder = FilterRequest.newBuilder();
        if (filterMap.containsKey(USAGE_FIELD)) {
            if (filterMap.containsKey(USAGE_IDS)) {
                filterBuilder
                        .addProperty(USAGE_FIELD, filterMap.get(USAGE_FIELD))
                        .addProperty(USAGE_IDS, filterMap.get(USAGE_IDS));
            } else {
                throw new ParameterException("Annotation usage requires 'usageIds' to be set.");
            }

            filterBuilder.addProperty(USAGE_RELATIONSHIPS, filterMap.get(USAGE_RELATIONSHIPS));

            request = Optional.of(filterBuilder.build());
        } else {
            request = Optional.empty();
        }

        return request;
    }
}