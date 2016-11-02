package uk.ac.ebi.quickgo.annotation.model;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationFields;
import uk.ac.ebi.quickgo.common.validator.GeneProductIDList;
import uk.ac.ebi.quickgo.rest.ParameterException;
import uk.ac.ebi.quickgo.rest.controller.request.ArrayPattern;
import uk.ac.ebi.quickgo.rest.search.AggregateFunction;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;

import io.swagger.annotations.ApiModelProperty;
import java.util.*;
import java.util.stream.Stream;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static com.google.common.base.Preconditions.checkArgument;
import static uk.ac.ebi.quickgo.annotation.common.document.AnnotationFields.*;
import static uk.ac.ebi.quickgo.rest.controller.request.ArrayPattern.Flag.CASE_INSENSITIVE;

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
    public static final int MIN_ENTRIES_PER_PAGE = 0;

    public static final int DEFAULT_PAGE_NUMBER = 1;
    public static final int MIN_PAGE_NUMBER = 1;

    static final int MAX_GO_IDS = 500;
    static final int MAX_GENE_PRODUCT_IDS = 500;
    static final int MAX_EVIDENCE_CODE = 100;
    static final int MAX_TAXON_IDS = 50;
    static final int MAX_REFERENCES = 50;

    //Names of the parameters in readable format
    static final String ASSIGNED_BY_PARAM = "Assigned By";
    static final String ASPECT_PARAM = "Aspect";
    static final String GO_EVIDENCE_PARAM = "GO Evidence";
    static final String TAXON_ID_PARAM = "Taxonomic identifier";
    static final String GO_ID_PARAM = "GO Id";
    static final String USAGE_RELATIONSHIP_PARAM = "Usage relationship";
    static final String EVIDENCE_CODE_PARAM = "Evidence code identifier";
    static final String GENE_PRODUCT_TYPE_PARAM = "Gene Product Type";
    static final String GENE_PRODUCT_SUBSET_PARAM = "Gene Product Subset identifier";
    static final String GENE_PRODUCT_PARAM = "Gene Product ID";
    static final String REFERENCE_PARAM = "Reference";

    static final String GO_USAGE_FIELD = "goUsage";
    static final String GO_USAGE_RELATIONSHIPS = "goUsageRelationships";

    static final String EVIDENCE_CODE_USAGE_FIELD = "evidenceCodeUsage";
    static final String EVIDENCE_CODE_USAGE_RELATIONSHIPS = "evidenceCodeUsageRelationships";

    /**
     * indicates which fields should be looked at when creating filters
     */
    private static final String[] FILTER_REQUEST_FIELDS = new String[]{
            GO_ASPECT,
            ASSIGNED_BY,
            GENE_PRODUCT_SUBSET,
            GENE_PRODUCT_ID,
            GENE_PRODUCT_TYPE,
            GO_EVIDENCE,
            QUALIFIER,
            REFERENCE_SEARCH,
            TAXON_ID,
            TARGET_SET,
            WITH_FROM_SEARCH
    };

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
                Arrays.asList(GO_ID_INDEXED_ORIGINAL, TAXON_ID, REFERENCE, EVIDENCE_CODE, ASSIGNED_BY, GO_ASPECT);

        StatsRequest annotationStats = new StatsRequest("annotation", AnnotationFields.ID, AggregateFunction
                .COUNT.getName(), statsTypes);
        StatsRequest geneProductStats = new StatsRequest("geneProduct", AnnotationFields.GENE_PRODUCT_ID,
                AggregateFunction.UNIQUE.getName(), statsTypes);

        DEFAULT_STATS_REQUESTS = Collections.unmodifiableList(Arrays.asList(annotationStats, geneProductStats));
    }

    @ApiModelProperty(
            value = "Number of results per page.",
            allowableValues = "range[" + MIN_ENTRIES_PER_PAGE + "," + MAX_ENTRIES_PER_PAGE + "]")
    private int limit = DEFAULT_ENTRIES_PER_PAGE;

    @ApiModelProperty(
            value = "Page number of the result set to display.",
            allowableValues = "range[" + MIN_PAGE_NUMBER + ",max_result_set_size]")
    private int page = DEFAULT_PAGE_NUMBER;

    /*
     * TODO: These state variables are only here until springfox can get the @ApiModelProperty to work with our POJO.
     * When the fix is in place we can move the @ApiModelProperty definitions to the getters
     */
    @ApiModelProperty(
            value = "Filter annotation by the ontology to which the associated GO term belongs. Accepts comma " +
                    "separated values. Accepts comma separated values.",
            allowableValues = "biological_process,molecular_function,cellular_component",
            example = "biological_process,molecular_function")
    private String aspect;

    @ApiModelProperty(value = "The database which made the annotation. Accepts comma separated values.",
            example = "BHF-UCL,Ensembl")
    private String[] assignedBy;

    @ApiModelProperty(
            value = "Identifier of a literature or database reference, cited as an authority " +
                    "for the attribution of the GO ID. It is also possible to filter just by the database type. " +
                    "Format: DB:Reference. Accepts comma separated values.",
            example = "PMID:2676709")
    private String reference;

    @ApiModelProperty(
            value = "Unique identifier of a gene product present within an annotation. Accepts comma separated " +
                    "values.", example = "P99999,URS00000064B1_559292")
    private String geneProductId;

    @ApiModelProperty(
            value = "Evidence code used to indicate how the annotation is supported. Accepts comma separated values.",
            example = "ECO:0000255,ECO:0000305")
    private String evidenceCode;

    @ApiModelProperty(
            value = "The GO identifier attributed to an annotation. Accepts comma separated values.",
            example = "GO:0030533,GO:0070125")
    private String goId;

    @ApiModelProperty(
            value = "Flags that modify the interpretation of an annotation. Accepts comma separated values.",
            example = "enables,involved_in")
    private String qualifier;

    @ApiModelProperty(
            value = "Holds additional identifiers for an annotation. Accepts comma separated values.",
            example = "GO:0030533,P63328")
    private String withFrom;

    @ApiModelProperty(
            value = "The taxonomic identifier of the species encoding the gene product associated to an annotation. " +
                    "Accepts comma separated values.",
            example = "35758,1310605")
    private String taxonId;

    @ApiModelProperty(
            value = "Indicates how the GO terms within the annotations should be used. Is used in conjunction with " +
                    "'goUsageRelationships'.",
            allowableValues = "descendants,slim",
            example = "descendants")
    private String goUsage;

    @ApiModelProperty(
            value = "The relationship between the provided 'goId' (GO) identifiers " +
                    "found within the annotations. If the relationship is fulfilled, " +
                    "the annotation is selected. Allows comma separated values.",
            allowableValues = "is_a,part_of,occurs_in,regulates",
            example = "is_a,part_of")
    private String goUsageRelationships;

    @ApiModelProperty(
            value = "Indicates how the evidence code terms within the annotations should be used. Is used in " +
                    "conjunction with 'evidenceCodeUsageRelationships'.",
            allowableValues = "descendants",
            example = "descendants")
    private String evidenceCodeUsage;

    @ApiModelProperty(
            value = "The relationship between the provided 'evidenceCode' identifiers " +
                    "found within the annotations. If the relationship is fulfilled, " +
                    "the annotation is selected. Allows comma separated values.",
            allowableValues = "is_a,part_of,occurs_in,regulates",
            example = "is_a,part_of")
    private String evidenceCodeUsageRelationships;

    @ApiModelProperty(
            value = "The type of gene product found within an annotation. Accepts comma separated values.",
            allowableValues = "protein,RNA,complex.",
            example = "protein,RNA")
    private String geneProductType;

    @ApiModelProperty(
            value = "A set of gene products that have been identified as being of interest to a certain group. " +
                    "Accepts comma separated values.",
            example = "KRUK,BHF-UCL,Exosome")
    private String targetSet;

    @ApiModelProperty(
            value = "The name of a database specific to gene products. Accepts comma separated values.",
            example = "TrEMBL"
    )
    private String geneProductSubset;

    @ApiModelProperty(
            value = "Gene ontology evidence codes of the 'goId's found within the annotations. Accepts comma " +
                    "separated values.",
            example = "EXP,IDA")
    private String goIdEvidence;

    private final Map<String, String[]> filterMap = new HashMap<>();

    /**
     *  E.g. ASPGD,Agbase,..
     *  In the format assignedBy=ASPGD,Agbase
     */
    public void setAssignedBy(String... assignedBy) {
        if (assignedBy != null) {
            filterMap.put(ASSIGNED_BY, assignedBy);
        }
    }

    @ArrayPattern(regexp = "^[A-Za-z][A-Za-z\\-_]+$", paramName = ASSIGNED_BY_PARAM)
    public String[] getAssignedBy() {
        return filterMap.get(ASSIGNED_BY);
    }

    /**
     * E.g. DOI, DOI:10.1002/adsc.201200590, GO_REF, PMID, PMID:12882977, Reactome, Reactome:R-RNO-912619,
     * GO_REF:0000037 etc
     */
    public void setReference(String... reference) {
        filterMap.put(REFERENCE_SEARCH, reference);
    }

    //todo create validation pattern @Pattern(regexp = "")
    @Size(max = MAX_REFERENCES,
            message = "Number of items in '" + REFERENCE_PARAM + "' is larger than: {max}")
    public String[] getReference() {
        return filterMap.get(REFERENCE_SEARCH);
    }

    public void setAspect(String... aspect) {
        if (aspect != null) {
            filterMap.put(GO_ASPECT, aspect);
        }
    }

    @ArrayPattern(regexp = "^biological_process|molecular_function|cellular_component$", flags = CASE_INSENSITIVE,
            paramName = ASPECT_PARAM)
    public String[] getAspect() {
        return filterMap.get(GO_ASPECT);
    }

    /**
     * Gene Product IDs, in CSV format.
     */
    public void setGeneProductId(String... listOfGeneProductIDs) {
        if (listOfGeneProductIDs != null) {
            filterMap.put(GENE_PRODUCT_ID, listOfGeneProductIDs);
        }
    }

    @GeneProductIDList
    @Size(max = MAX_GENE_PRODUCT_IDS,
            message = "Number of items in '" + GENE_PRODUCT_PARAM + "' is larger than: {max}")
    public String[] getGeneProductId() {
        return filterMap.get(GENE_PRODUCT_ID);
    }

    /**
     * The older evidence codes
     * E.g. IEA, IBA, IBD etc. See <a href="http://geneontology.org/page/guide-go-evidence-codes">Guide QuickGO
     * evidence codes</a>
     * @param evidence the evidence code
     */
    public void setGoIdEvidence(String... evidence) {
        filterMap.put(GO_EVIDENCE, evidence);
    }

    @ArrayPattern(regexp = "^[A-Za-z]{2,3}$", paramName = GO_EVIDENCE_PARAM)
    public String[] getGoIdEvidence() {
        return filterMap.get(GO_EVIDENCE);
    }

    /**
     * NOT, enables etc
     */
    public void setQualifier(String... qualifier) {
        filterMap.put(QUALIFIER, qualifier);
    }

    public String[] getQualifier() {
        return filterMap.get(QUALIFIER);
    }

    /**
     * A list of with/from values, separated by commas
     * In the format withFrom=PomBase:SPBP23A10.14c,RGD:621207 etc
     * Users can supply just the id (e.g. PomBase) or id SPBP23A10.14c
     * @param withFrom comma separated with/from values
     */
    public void setWithFrom(String... withFrom) {
        filterMap.put(WITH_FROM_SEARCH, withFrom);
    }

    /**
     * Return a list of with/from values, separated by commas
     * @return String containing comma separated list of with/From values.
     */
    public String[] getWithFrom() {
        return filterMap.get(WITH_FROM_SEARCH);
    }

    public void setTaxonId(String... taxId) {
        filterMap.put(TAXON_ID, taxId);
    }

    @ArrayPattern(regexp = "^[0-9]+$", paramName = TAXON_ID_PARAM)
    @Size(max = MAX_TAXON_IDS,
            message = "Number of items in '" + TAXON_ID_PARAM + "' is larger than: {max}")

    public String[] getTaxonId() {
        return filterMap.get(TAXON_ID);
    }

    /**
     * Will receive a list of eco ids thus: evidenceCode=ECO:0000256,ECO:0000323
     */
    public void setEvidenceCode(String... evidenceCode) {
        filterMap.put(EVIDENCE_CODE, evidenceCode);
    }

    @ArrayPattern(regexp = "^ECO:[0-9]{7}$", paramName = EVIDENCE_CODE_PARAM, flags = CASE_INSENSITIVE)
    @Size(max = MAX_EVIDENCE_CODE,
            message = "Number of items in '" + EVIDENCE_CODE_PARAM + "' is larger than: {max}")
    public String[] getEvidenceCode() {
        return filterMap.get(EVIDENCE_CODE);
    }

    public void setEvidenceCodeUsage(String usage) {
        if (usage != null) {
            filterMap.put(EVIDENCE_CODE_USAGE_FIELD, new String[]{usage.toLowerCase()});
        }
    }

    @Pattern(regexp = "^descendants$", flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Invalid evidenceCodeUsage: ${validatedValue}")
    public String getEvidenceCodeUsage() {
        return filterMap.get(EVIDENCE_CODE_USAGE_FIELD) == null ? null : filterMap.get(EVIDENCE_CODE_USAGE_FIELD)[0];
    }

    @ArrayPattern(regexp = "^is_a|part_of|occurs_in|regulates$", flags = CASE_INSENSITIVE,
            paramName = USAGE_RELATIONSHIP_PARAM)
    public String[] getEvidenceCodeUsageRelationships() {
        return filterMap.get(EVIDENCE_CODE_USAGE_RELATIONSHIPS);
    }

    public void setEvidenceCodeUsageRelationships(String... usageRelationships) {
        if (usageRelationships != null) {
            String[] usageRelationshipArray = Stream.of(usageRelationships)
                    .map(String::toLowerCase)
                    .toArray(String[]::new);
            filterMap.put(EVIDENCE_CODE_USAGE_RELATIONSHIPS, usageRelationshipArray);
        }
    }

    /**
     * List of Gene Ontology ids in CSV format
     */
    public void setGoId(String... goId) {
        filterMap.put(GO_ID, goId);
    }

    @ArrayPattern(regexp = "^GO:[0-9]{7}$", flags = CASE_INSENSITIVE, paramName = GO_ID_PARAM)
    @Size(max = MAX_GO_IDS,
            message = "Number of items in '" + GO_ID_PARAM + "' is larger than: {max}")
    public String[] getGoId() {
        return filterMap.get(GO_ID);
    }

    public void setGoUsage(String goUsage) {
        if (goUsage != null) {
            filterMap.put(GO_USAGE_FIELD, new String[]{goUsage.toLowerCase()});
        }
    }

    @Pattern(regexp = "^slim|descendants$", flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Invalid goUsage: ${validatedValue}")
    public String getGoUsage() {
        return filterMap.get(GO_USAGE_FIELD) == null ? null : filterMap.get(GO_USAGE_FIELD)[0];
    }

    @ArrayPattern(regexp = "^is_a|part_of|occurs_in|regulates$", flags = CASE_INSENSITIVE,
            paramName = USAGE_RELATIONSHIP_PARAM)
    public String[] getGoUsageRelationships() {
        return filterMap.get(GO_USAGE_RELATIONSHIPS);
    }

    public void setGoUsageRelationships(String... goUsageRelationships) {
        if (goUsageRelationships != null) {
            String[] usageRelationshipArray = Stream.of(goUsageRelationships)
                    .map(String::toLowerCase)
                    .toArray(String[]::new);
            filterMap.put(GO_USAGE_RELATIONSHIPS, usageRelationshipArray);
        }
    }

    public void setGeneProductType(String... geneProductType) {
        filterMap.put(GENE_PRODUCT_TYPE, geneProductType);
    }

    @ArrayPattern(regexp = "^complex|miRNA|protein$", flags = CASE_INSENSITIVE, paramName = GENE_PRODUCT_TYPE_PARAM)
    public String[] getGeneProductType() {
        return filterMap.get(GENE_PRODUCT_TYPE);
    }

    /**
     * Filter by Target Sets e.g. BHF-UCK, KRUK, Parkinsons etc
     */
    public void setTargetSet(String... targetSet) {
        filterMap.put(TARGET_SET, targetSet);
    }

    public String[] getTargetSet() {
        return filterMap.get(TARGET_SET);
    }

    public void setGeneProductSubset(String... geneProductSubset) {
        filterMap.put(GENE_PRODUCT_SUBSET, geneProductSubset);
    }

    @ArrayPattern(regexp = "^[A-Za-z-]+$", paramName = GENE_PRODUCT_SUBSET_PARAM)
    public String[] getGeneProductSubset() {
        return filterMap.get(GENE_PRODUCT_SUBSET);
    }

    @Min(value = MIN_ENTRIES_PER_PAGE, message = "Number of entries per page cannot be less than {value} but " +
            "found: ${validatedValue}")
    @Max(value = MAX_ENTRIES_PER_PAGE, message = "Number of entries per page cannot be more than {value} but " +
            "found: ${validatedValue}")
    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Min(value = MIN_PAGE_NUMBER, message = "Page size cannot be less than {value} but found: ${validatedValue}")
    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    /**
     * Produces a set of {@link FilterRequest} objects given the filter attributes provided by the user.
     *
     * @return a list of {@link FilterRequest}
     */
    public List<FilterRequest> createFilterRequests() {
        List<FilterRequest> filterRequests = new ArrayList<>();

        Stream.of(FILTER_REQUEST_FIELDS)
                .map(this::createSimpleFilter)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(filterRequests::add);

        createGoUsageFilter().ifPresent(filterRequests::add);
        createEvidenceCodeUsageFilter().ifPresent(filterRequests::add);

        return filterRequests;
    }

    private Optional<FilterRequest> createSimpleFilter(String key) {
        Optional<FilterRequest> request;
        if (filterMap.containsKey(key)) {
            FilterRequest.Builder requestBuilder = FilterRequest.newBuilder();
            requestBuilder.addProperty(key, filterMap.get(key));
            request = Optional.of(requestBuilder.build());
        } else {
            request = Optional.empty();
        }

        return request;
    }

    private Optional<FilterRequest> createGoUsageFilter() {
        return createUsageFilter(GO_USAGE_FIELD);
    }

    private Optional<FilterRequest> createEvidenceCodeUsageFilter() {
        return createUsageFilter(EVIDENCE_CODE_USAGE_FIELD);
    }

    private Optional<FilterRequest> createUsageFilter(String usageParam) {
        String idParam;
        String relationshipsParam;
        switch (usageParam) {
            case GO_USAGE_FIELD:
                idParam = GO_ID;
                relationshipsParam = GO_USAGE_RELATIONSHIPS;
                break;
            case EVIDENCE_CODE_USAGE_FIELD:
                idParam = EVIDENCE_CODE;
                relationshipsParam = EVIDENCE_CODE_USAGE_RELATIONSHIPS;
                break;
            default:
                throw new ParameterException("Unknown usage specified: " + usageParam + ". Use either " +
                        GO_USAGE_FIELD + " or " + EVIDENCE_CODE_USAGE_FIELD);
        }

        Optional<FilterRequest> request;
        FilterRequest.Builder filterBuilder = FilterRequest.newBuilder();

        if (filterMap.containsKey(usageParam)) {
            if (filterMap.containsKey(idParam)) {
                assert filterMap.get(usageParam).length == 1 : usageParam + ": can only have one value";

                String usageValue = filterMap.get(usageParam)[0];

                filterBuilder
                        .addProperty(usageValue)
                        .addProperty(idParam, filterMap.get(idParam));

                filterBuilder.addProperty(relationshipsParam, filterMap.get(relationshipsParam));
                request = Optional.of(filterBuilder.build());
            } else {
                throw new ParameterException("Annotation " + usageParam + " requires '" + idParam + "' to be set.");
            }
        } else {
            request = createSimpleFilter(idParam);
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
        private final String aggregateFunction;
        private final List<String> types;

        public StatsRequest(String groupName, String groupField, String aggregateFunction, List<String> types) {
            Preconditions.checkArgument(groupName != null && !groupName.trim().isEmpty(),
                    "Statistics group name cannot be null or empty");
            Preconditions.checkArgument(groupField != null && !groupName.trim().isEmpty(),
                    "Statistics group field cannot be null or empty");
            Preconditions
                    .checkArgument(aggregateFunction != null && !aggregateFunction.trim().isEmpty(), "Statistics " +
                            "aggregate function cannot be null or empty");

            this.groupName = groupName;
            this.groupField = groupField;
            this.aggregateFunction = aggregateFunction;

            if (types == null) {
                this.types = Collections.emptyList();
            } else {
                this.types = types;
            }
        }

        public String getGroupName() {
            return groupName;
        }

        public String getGroupField() {
            return groupField;
        }

        public String getAggregateFunction() {
            return aggregateFunction;
        }

        public Collection<String> getTypes() {
            return Collections.unmodifiableList(types);
        }
    }
}