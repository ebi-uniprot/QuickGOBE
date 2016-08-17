package uk.ac.ebi.quickgo.annotation.model;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationFields;
import uk.ac.ebi.quickgo.common.validator.GeneProductIDList;
import uk.ac.ebi.quickgo.rest.ParameterException;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;

import com.google.common.base.Preconditions;
import java.util.*;
import java.util.stream.Stream;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

import static javax.validation.constraints.Pattern.Flag.CASE_INSENSITIVE;
import static uk.ac.ebi.quickgo.annotation.common.document.AnnotationFields.*;

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

    static final String USAGE_FIELD = "usage";
    static final String USAGE_IDS = "usageIds";
    static final String USAGE_RELATIONSHIPS = "usageRelationships";
    private static final String ASPECT_FIELD = "aspect";
    private static final String[] TARGET_FIELDS = new String[]{ASPECT_FIELD, ASSIGNED_BY, TAXON_ID, GO_EVIDENCE,
            QUALIFIER, REFERENCE_SEARCH, WITH_FROM_SEARCH, EVIDENCE_CODE, GENE_PRODUCT_ID, GO_ID, GENE_PRODUCT_TYPE,
            DB_SUBSET, TARGET_SET};

    private static final int DEFAULT_PAGE_NUMBER = 1;
    private static final String COMMA = ",";

    /**
     * At the moment the definition of the list is hardcoded because we only have need to display annotation and
     * gene product statistics on a subset of types.
     *
     * Note: We can in the future change this from a hard coded implementation, to something that is decided by the
     * client.
     */
    private static List<StatsRequest> DEFAULT_STATS_REQUESTS;

    static {
        List<String> statsTypes =
                Arrays.asList(GO_ID_INDEXED_ORIGINAL, TAXON_ID, REFERENCE, EVIDENCE_CODE, ASSIGNED_BY);

        StatsRequest annotationStats = new StatsRequest("annotation", AnnotationFields.ID, statsTypes);
        StatsRequest geneProductStats = new StatsRequest("geneProduct", AnnotationFields.GENE_PRODUCT_ID, statsTypes);

        DEFAULT_STATS_REQUESTS = Collections.unmodifiableList(Arrays.asList(annotationStats, geneProductStats));
    }

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
        return filterMap.get(ASSIGNED_BY);
    }

    /**
     * E.g. DOI, DOI:10.1002/adsc.201200590, GO_REF, PMID, PMID:12882977, Reactome, Reactome:R-RNO-912619,
     * GO_REF:0000037 etc
     * @param reference
     * @return
     */
    public void setReference(String reference) {
        filterMap.put(REFERENCE_SEARCH, reference);
    }

    //todo create validation pattern @Pattern(regexp = "")
    public String getReference() {
        return filterMap.get(REFERENCE_SEARCH);
    }

    public void setAspect(String aspect) {
        if (aspect != null) {
            filterMap.put(ASPECT_FIELD, aspect.toLowerCase());
        }
    }

    @Pattern(regexp = "biological_process|molecular_function|cellular_component", flags = CASE_INSENSITIVE,
            message = "At least one 'Aspect' value is invalid: ${validatedValue}")
    public String getAspect() {
        return filterMap.get(ASPECT_FIELD);
    }

    /**
     * Gene Product IDs, in CSV format.
     */

    public void setGpId(String listOfGeneProductIDs) {
        if (listOfGeneProductIDs != null) {
            filterMap.put(GENE_PRODUCT_ID, listOfGeneProductIDs);
        }
    }

    @GeneProductIDList
    public String getGpId() {
        return filterMap.get(GENE_PRODUCT_ID);
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

    @Pattern(regexp = "^[A-Za-z]{2,3}(,[A-Za-z]{2,3})*",
            message = "At least one 'GO Evidence' value is invalid: ${validatedValue}")
    public String getGoEvidence() {
        return filterMap.get(GO_EVIDENCE);
    }

    /**
     * NOT, enables etc
     * @param qualifier
     */
    public void setQualifier(String qualifier) {
        filterMap.put(QUALIFIER, qualifier);
    }

    public String getQualifier() {
        return filterMap.get(QUALIFIER);
    }

    /**
     * A list of with/from values, separated by commas
     * In the format withFrom=PomBase:SPBP23A10.14c,RGD:621207 etc
     * Users can supply just the id (e.g. PomBase) or id SPBP23A10.14c
     * @param withFrom comma separated with/from values
     */
    public void setWithFrom(String withFrom) {
        filterMap.put(WITH_FROM_SEARCH, withFrom);
    }

    /**
     * Return a list of with/from values, separated by commas
     * @return String containing comma separated list of with/From values.
     */
    public String getWithFrom() {
        return filterMap.get(WITH_FROM_SEARCH);
    }

    public void setTaxon(String taxId) {
        filterMap.put(TAXON_ID, taxId);
    }

    @Pattern(regexp = "[0-9]+(,[0-9]+)*",
            message = "At least one 'Taxonomic identifier' value is invalid: ${validatedValue}")
    public String getTaxon() {
        return filterMap.get(TAXON_ID);
    }

    /**
     * List of Gene Ontology ids in CSV format
     * @param goId
     */
    public void setGoId(String goId) {
        filterMap.put(GO_ID, goId);
    }

    @Pattern(regexp = "go:[0-9]{7}(,go:[0-9]{7})*", flags = CASE_INSENSITIVE,
            message = "At least one 'GO Id' value is invalid: ${validatedValue}")
    public String getGoId() {
        return filterMap.get(GO_ID);
    }

    /**
     * Will receive a list of eco ids thus: evidenceCode=ECO:0000256,ECO:0000323
     * @param evidenceCode
     */
    public void setEvidenceCode(String evidenceCode) {
        filterMap.put(EVIDENCE_CODE, evidenceCode);
    }

    @Pattern(regexp = "ECO:[0-9]{7}(,ECO:[0-9]{7})*", flags = CASE_INSENSITIVE,
            message = "At least one 'Evidence code identifier' value is invalid: ${validatedValue}")
    public String getEvidenceCode() {
        return filterMap.get(EVIDENCE_CODE);
    }

    @Pattern(regexp = "^exact|slim|descendants$", flags = CASE_INSENSITIVE, message = "Invalid usage: " +
            "${validatedValue})")
    public String getUsage() {
        return filterMap.get(USAGE_FIELD);
    }

    public void setUsage(String usage) {
        if (usage != null) {
            filterMap.put(USAGE_FIELD, usage.toLowerCase());
        }
    }

    @Pattern(regexp = "GO:[0-9]+(,GO:[0-9]+)*", flags = CASE_INSENSITIVE,
            message = "Invalid GO IDs specified: ${validatedValue})")
    public String getUsageIds() {
        return filterMap.get(USAGE_IDS);
    }

    public void setUsageIds(String usageIds) {
        if (usageIds != null) {
            filterMap.put(USAGE_IDS, usageIds.toUpperCase());
        }
    }

    @Pattern(regexp = "(is_a|part_of|occurs_in|regulates)(,is_a|part_of|occurs_in|regulates)*",
            flags = CASE_INSENSITIVE)
    public String getUsageRelationships() {
        return filterMap.get(USAGE_RELATIONSHIPS);
    }

    public void setUsageRelationships(String usageRelationships) {
        if (usageRelationships != null) {
            filterMap.put(USAGE_RELATIONSHIPS, usageRelationships.toLowerCase());
        }
    }

    public void setGpType(String geneProductType) {
        filterMap.put(GENE_PRODUCT_TYPE, geneProductType.toLowerCase());
    }

    @Pattern(regexp = "^(complex|rna|protein)(,(complex|rna|protein)){0,2}", flags = CASE_INSENSITIVE,
            message = "At least one 'Gene Product Type' value is invalid: ${validatedValue}")
    public String getGpType() {
        return filterMap.get(GENE_PRODUCT_TYPE);
    }

    /**
     * Filter by Target Sets e.g. BHF-UCK, KRUK, Parkinsons etc
     * @return
     */
    public void setTargetSet(String targetSet) {
        filterMap.put(TARGET_SET, targetSet);
    }

    public String getTargetSet() {
        return filterMap.get(TARGET_SET);
    }

    public void setGpSubset(String gpSubset) {
        filterMap.put(DB_SUBSET, gpSubset);
    }

    @Pattern(regexp = "^[A-Za-z-]+(,[A-Za-z-]+)*",
            message = "At least one 'Gene Product Subset identifier' value is invalid: ${validatedValue}")
    public String getGpSubset() {
        return filterMap.get(DB_SUBSET);
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

        Stream.of(TARGET_FIELDS)
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
            FilterRequest.Builder requestBuilder = FilterRequest.newBuilder();
            requestBuilder.addProperty(key, filterMap.get(key).split(COMMA));
            request = Optional.of(requestBuilder.build());
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

    public List<StatsRequest> createStatsRequests() {
        return DEFAULT_STATS_REQUESTS;
    }

    /**
     * Defines which statistics the client would like to to retrieve.
     */
    public static class StatsRequest {
        private final String groupName;
        private final String groupField;
        private final List<String> types;

        public StatsRequest(String groupName, String groupField, List<String> types) {
            Preconditions.checkArgument(groupName != null && !groupName.trim().isEmpty(),
                    "Statistics group name cannot be null or empty");
            Preconditions.checkArgument(groupField != null && !groupName.trim().isEmpty(),
                    "Statistics group field cannot be null or empty");
            Preconditions.checkArgument(types != null, "Types collection cannot be null or empty");

            this.groupName = groupName;
            this.groupField = groupField;
            this.types = types;
        }

        public String getGroupName() {
            return groupName;
        }

        public String getGroupField() {
            return groupField;
        }

        public Collection<String> getTypes() {
            return Collections.unmodifiableList(types);
        }
    }
}