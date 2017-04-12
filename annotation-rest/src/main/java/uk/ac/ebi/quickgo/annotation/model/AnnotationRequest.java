package uk.ac.ebi.quickgo.annotation.model;

import uk.ac.ebi.quickgo.annotation.validation.service.ReferenceValidator;
import uk.ac.ebi.quickgo.annotation.validation.service.WithFromValidator;
import uk.ac.ebi.quickgo.common.validator.GeneProductIDList;
import uk.ac.ebi.quickgo.rest.ParameterException;
import uk.ac.ebi.quickgo.rest.controller.request.ArrayPattern;
import uk.ac.ebi.quickgo.rest.search.AggregateFunction;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.results.transformer.ResultTransformationRequest;
import uk.ac.ebi.quickgo.rest.search.results.transformer.ResultTransformationRequests;

import io.swagger.annotations.ApiModelProperty;
import java.util.*;
import java.util.stream.Stream;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static com.google.common.base.Preconditions.checkArgument;
import static uk.ac.ebi.quickgo.annotation.common.AnnotationFields.Facetable;
import static uk.ac.ebi.quickgo.annotation.common.AnnotationFields.Searchable;
import static uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelperImpl.*;
import static uk.ac.ebi.quickgo.rest.controller.request.ArrayPattern.Flag.CASE_INSENSITIVE;

/**
 * A data structure for the annotation filtering parameters passed in from the client.
 * <p>
 * Once the comma separated values have been set, then turn then into an object (SimpleFilter) that
 * encapsulates the list and solr field name to use for that argument.
 *
 * @author Tony Wardell
 * Date: 25/04/2016
 * Time: 11:23
 * Created with IntelliJ IDEA.
 */
public class AnnotationRequest {
    static final int MAX_GO_IDS = 600;
    static final int MAX_GENE_PRODUCT_IDS = 500;
    static final int MAX_EVIDENCE_CODE = 100;
    static final int MAX_TAXON_IDS = 50;
    static final int MAX_REFERENCES = 50;
    static final int MIN_DOWNLOAD_NUMBER = 1;
    static final int MAX_DOWNLOAD_NUMBER = 50000;
    static final int DEFAULT_DOWNLOAD_LIMIT = 10000;

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
    static final String INCLUDE_FIELD_PARAM = "Optional fields";

    static final String QUALIFIER_PARAM = "Qualifier";
    static final String TAXON_USAGE_ID = "taxonId";

    static final String TAXON_USAGE_FIELD = "taxonUsage";
    static final String GO_USAGE_ID = "goId";
    static final String GO_USAGE_FIELD = "goUsage";

    static final String GO_USAGE_RELATIONSHIPS = "goUsageRelationships";
    static final String EVIDENCE_CODE_USAGE_ID = "evidenceCode";
    static final String EVIDENCE_CODE_USAGE_FIELD = "evidenceCodeUsage";

    static final String EVIDENCE_CODE_USAGE_RELATIONSHIPS = "evidenceCodeUsageRelationships";
    static final String DESCENDANTS_USAGE = "descendants";
    static final String EXACT_USAGE = "exact";
    static final String SLIM_USAGE = "slim";

    static final String DEFAULT_TAXON_USAGE = DESCENDANTS_USAGE;
    static final String DEFAULT_EVIDENCE_CODE_USAGE = DESCENDANTS_USAGE;
    static final String DEFAULT_GO_USAGE = DESCENDANTS_USAGE;

    /**
     * indicates which fields should be looked at when creating filters
     */
    private static final String[] FILTER_REQUEST_FIELDS = new String[]{
            Searchable.GO_ASPECT,
            Searchable.ASSIGNED_BY,
            Searchable.GENE_PRODUCT_SUBSET,
            Searchable.GENE_PRODUCT_ID,
            Searchable.GENE_PRODUCT_TYPE,
            Searchable.GO_EVIDENCE,
            Searchable.QUALIFIER,
            Searchable.REFERENCE,
            Searchable.TARGET_SET,
            Searchable.WITH_FROM,
            Searchable.EXTENSION
    };

    /**
     * At the moment the definition of the list is hardcoded because we only have need to display annotation and
     * gene product statistics on a subset of types.
     * <p>
     * Note: We can in the future change this from a hard coded implementation, to something that is decided by the
     * client.
     */
    private static List<StatsRequest> DEFAULT_STATS_REQUESTS;

    static {
        List<String> statsTypes =
                Arrays.asList(Facetable.GO_ID, Facetable.TAXON_ID, Facetable.REFERENCE, Facetable.EVIDENCE_CODE,
                        Facetable.ASSIGNED_BY, Facetable.GO_ASPECT);

        StatsRequest annotationStats = new StatsRequest("annotation", Facetable.ID, AggregateFunction
                .COUNT.getName(), statsTypes);
        StatsRequest geneProductStats = new StatsRequest("geneProduct", Facetable.GENE_PRODUCT_ID,
                AggregateFunction.UNIQUE.getName(), statsTypes);

        DEFAULT_STATS_REQUESTS = Collections.unmodifiableList(Arrays.asList(annotationStats, geneProductStats));
    }

    @ApiModelProperty(
            value = "Number of results per page.",
            allowableValues = "range[" + MIN_ENTRIES_PER_PAGE + "," + MAX_ENTRIES_PER_PAGE + "]")
    protected int limit = DEFAULT_ENTRIES_PER_PAGE;

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
            value = "Indicates how the taxonomic identifier within the annotations should be used.",
            allowableValues = "descendants,exact",
            example = "exact")
    private String taxonUsage;

    @ApiModelProperty(
            value = "Indicates how the GO terms within the annotations should be used. Is used in conjunction with " +
                    "'goUsageRelationships'.",
            allowableValues = "descendants,exact,slim",
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
            allowableValues = "descendants,exact",
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

    @ApiModelProperty(value = "An annotation extension is used to extend " +
            "(i.e., add more specificity to) the GO term used in an annotation; the combination of the GO term plus " +
            "the" +
            " extension is equivalent to a more specific GO term. " +
            "An annotation extension is stored in the database, and transmitted in annotation files, as a single " +
            "string, structured as a pipe-separated list of comma-separated lists of components.",
            example = "occurs_in(CL:0000032),transports_or_maintains_localization_of(UniProtKB:P10288)|" +
                    "results_in_formation_of(UBERON:0003070),occurs_in(CL:0000032),occurs_in(CL:0000008)," +
                    "results_in_formation_of(UBERON:0001675)")
    private String extension;

    @ApiModelProperty(
            value = "The number of annotations to download. Note, the page size parameter [limit] will be ignored " +
                    "when downloading results.",
            allowableValues = "range[" + MIN_DOWNLOAD_NUMBER + "," + MAX_DOWNLOAD_NUMBER + "]")
    private int downloadLimit = DEFAULT_DOWNLOAD_LIMIT;

    @ApiModelProperty(
            value = "Optional fields to include in the response.",
            allowableValues = "goName",
            example = "goName")
    private String[] includeFields;

    private final Map<String, String[]> filterMap = new HashMap<>();

    /**
     * E.g. ASPGD,Agbase,..
     * In the format assignedBy=ASPGD,Agbase
     */
    public void setAssignedBy(String... assignedBy) {
        if (assignedBy != null) {
            filterMap.put(Searchable.ASSIGNED_BY, assignedBy);
        }
    }

    @ArrayPattern(regexp = "^[A-Za-z][A-Za-z\\-_]+$", paramName = ASSIGNED_BY_PARAM)
    public String[] getAssignedBy() {
        return filterMap.get(Searchable.ASSIGNED_BY);
    }

    /**
     * E.g. DOI, DOI:10.1002/adsc.201200590, GO_REF, PMID, PMID:12882977, Reactome, Reactome:R-RNO-912619,
     * GO_REF:0000037 etc
     */
    public void setReference(String... reference) {
        filterMap.put(Searchable.REFERENCE, reference);
    }

    @ReferenceValidator
    @Size(max = MAX_REFERENCES,
            message = "Number of items in '" + REFERENCE_PARAM + "' is larger than: {max}")
    public String[] getReference() {
        return filterMap.get(Searchable.REFERENCE);
    }

    public void setAspect(String... aspect) {
        if (aspect != null) {
            filterMap.put(Searchable.GO_ASPECT, aspect);
        }
    }

    @ArrayPattern(regexp = "^biological_process|molecular_function|cellular_component$", flags = CASE_INSENSITIVE,
            paramName = ASPECT_PARAM)
    public String[] getAspect() {
        return filterMap.get(Searchable.GO_ASPECT);
    }

    /**
     * Gene Product IDs, in CSV format.
     */
    public void setGeneProductId(String... listOfGeneProductIDs) {
        if (listOfGeneProductIDs != null) {
            filterMap.put(Searchable.GENE_PRODUCT_ID, listOfGeneProductIDs);
        }
    }

    @GeneProductIDList
    @Size(max = MAX_GENE_PRODUCT_IDS,
            message = "Number of items in '" + GENE_PRODUCT_PARAM + "' is larger than: {max}")
    public String[] getGeneProductId() {
        return filterMap.get(Searchable.GENE_PRODUCT_ID);
    }

    /**
     * The older evidence codes
     * E.g. IEA, IBA, IBD etc. See <a href="http://geneontology.org/page/guide-go-evidence-codes">Guide QuickGO
     * evidence codes</a>
     *
     * @param evidence the evidence code
     */
    public void setGoIdEvidence(String... evidence) {
        filterMap.put(Searchable.GO_EVIDENCE, evidence);
    }

    @ArrayPattern(regexp = "^[A-Za-z]{2,3}$", paramName = GO_EVIDENCE_PARAM)
    public String[] getGoIdEvidence() {
        return filterMap.get(Searchable.GO_EVIDENCE);
    }

    /**
     * NOT, enables etc
     */
    public void setQualifier(String... qualifier) {
        filterMap.put(Searchable.QUALIFIER, qualifier);
    }

    @ArrayPattern(regexp = "^(NOT\\|)?[A-Z_]+$", flags = CASE_INSENSITIVE, paramName = QUALIFIER_PARAM)
    public String[] getQualifier() {
        return filterMap.get(Searchable.QUALIFIER);
    }

    /**
     * A list of with/from values, separated by commas
     * In the format withFrom=PomBase:SPBP23A10.14c,RGD:621207 etc
     * Users can supply just the id (e.g. PomBase) or id SPBP23A10.14c
     *
     * @param withFrom comma separated with/from values
     */
    public void setWithFrom(String... withFrom) {
        filterMap.put(Searchable.WITH_FROM, withFrom);
    }

    /**
     * Return a list of with/from values, separated by commas
     *
     * @return String containing comma separated list of with/From values.
     */
    @WithFromValidator
    public String[] getWithFrom() {
        return filterMap.get(Searchable.WITH_FROM);
    }

    public void setTaxonId(String... taxId) {
        filterMap.put(TAXON_USAGE_ID, taxId);
    }

    @ArrayPattern(regexp = "^[0-9]+$", paramName = TAXON_ID_PARAM)
    @Size(max = MAX_TAXON_IDS,
            message = "Number of items in '" + TAXON_ID_PARAM + "' is larger than: {max}")
    public String[] getTaxonId() {
        return filterMap.get(TAXON_USAGE_ID);
    }

    public void setTaxonUsage(String usage) {
        if (usage != null) {
            filterMap.put(TAXON_USAGE_FIELD, new String[]{usage.toLowerCase()});
        }
    }

    @Pattern(regexp = "^exact|descendants$", message = "Invalid taxonUsage: ${validatedValue}",
            flags = {Pattern.Flag.CASE_INSENSITIVE})
    public String getTaxonUsage() {
        return filterMap.get(TAXON_USAGE_FIELD) == null ? DEFAULT_TAXON_USAGE : filterMap.get(TAXON_USAGE_FIELD)[0];
    }

    /**
     * Will receive a list of eco ids thus: evidenceCode=ECO:0000256,ECO:0000323
     */
    public void setEvidenceCode(String... evidenceCode) {
        filterMap.put(Searchable.EVIDENCE_CODE, evidenceCode);
    }

    @ArrayPattern(regexp = "^ECO:[0-9]{7}$", paramName = EVIDENCE_CODE_PARAM, flags = CASE_INSENSITIVE)
    @Size(max = MAX_EVIDENCE_CODE,
            message = "Number of items in '" + EVIDENCE_CODE_PARAM + "' is larger than: {max}")
    public String[] getEvidenceCode() {
        return filterMap.get(Searchable.EVIDENCE_CODE);
    }

    public void setEvidenceCodeUsage(String usage) {
        if (usage != null) {
            filterMap.put(EVIDENCE_CODE_USAGE_FIELD, new String[]{usage.toLowerCase()});
        }
    }

    @Pattern(regexp = "^descendants|exact$", flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Invalid evidenceCodeUsage: ${validatedValue}")
    public String getEvidenceCodeUsage() {
        return filterMap.get(EVIDENCE_CODE_USAGE_FIELD) == null ?
                DEFAULT_EVIDENCE_CODE_USAGE : filterMap.get(EVIDENCE_CODE_USAGE_FIELD)[0];
    }

    @ArrayPattern(regexp = "^is_a|part_of|occurs_in|regulates$", flags = CASE_INSENSITIVE,
            paramName = USAGE_RELATIONSHIP_PARAM)
    public String[] getEvidenceCodeUsageRelationships() {
        return filterMap.get(EVIDENCE_CODE_USAGE_RELATIONSHIPS);
    }

    public void setEvidenceCodeUsageRelationships(String... usageRelationships) {
        if (usageRelationships != null) {
            String[] usageRelationshipArray = createLowercasedStringArray(usageRelationships);
            filterMap.put(EVIDENCE_CODE_USAGE_RELATIONSHIPS, usageRelationshipArray);
        }
    }

    /**
     * List of Gene Ontology ids in CSV format
     */
    public void setGoId(String... goId) {
        filterMap.put(Searchable.GO_ID, goId);
    }

    @ArrayPattern(regexp = "^GO:[0-9]{7}$", flags = CASE_INSENSITIVE, paramName = GO_ID_PARAM)
    @Size(max = MAX_GO_IDS,
            message = "Number of items in '" + GO_ID_PARAM + "' is larger than: {max}")
    public String[] getGoId() {
        return filterMap.get(Searchable.GO_ID);
    }

    public void setGoUsage(String goUsage) {
        if (goUsage != null) {
            filterMap.put(GO_USAGE_FIELD, new String[]{goUsage.toLowerCase()});
        }
    }

    @Pattern(regexp = "^slim|descendants|exact$", flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Invalid goUsage: ${validatedValue}")
    public String getGoUsage() {
        return filterMap.get(GO_USAGE_FIELD) == null ? DEFAULT_GO_USAGE : filterMap.get(GO_USAGE_FIELD)[0];
    }

    @ArrayPattern(regexp = "^is_a|part_of|occurs_in|regulates$", flags = CASE_INSENSITIVE,
            paramName = USAGE_RELATIONSHIP_PARAM)
    public String[] getGoUsageRelationships() {
        return filterMap.get(GO_USAGE_RELATIONSHIPS);
    }

    public void setGoUsageRelationships(String... goUsageRelationships) {
        if (goUsageRelationships != null) {
            String[] usageRelationshipArray = createLowercasedStringArray(goUsageRelationships);
            filterMap.put(GO_USAGE_RELATIONSHIPS, usageRelationshipArray);
        }
    }

    public void setGeneProductType(String... geneProductType) {
        filterMap.put(Searchable.GENE_PRODUCT_TYPE, geneProductType);
    }

    @ArrayPattern(regexp = "^complex|miRNA|protein$", flags = CASE_INSENSITIVE, paramName = GENE_PRODUCT_TYPE_PARAM)
    public String[] getGeneProductType() {
        return filterMap.get(Searchable.GENE_PRODUCT_TYPE);
    }

    /**
     * Filter by Target Sets e.g. BHF-UCK, KRUK, Parkinsons etc
     */
    public void setTargetSet(String... targetSet) {
        filterMap.put(Searchable.TARGET_SET, targetSet);
    }

    public String[] getTargetSet() {
        return filterMap.get(Searchable.TARGET_SET);
    }

    public void setGeneProductSubset(String... geneProductSubset) {
        filterMap.put(Searchable.GENE_PRODUCT_SUBSET, geneProductSubset);
    }

    @ArrayPattern(regexp = "^[A-Za-z-]+$", paramName = GENE_PRODUCT_SUBSET_PARAM)
    public String[] getGeneProductSubset() {
        return filterMap.get(Searchable.GENE_PRODUCT_SUBSET);
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

    @Min(value = MIN_PAGE_NUMBER, message = "Page number cannot be less than {value}, but found: ${validatedValue}")
    @Max(value = MAX_PAGE_NUMBER, message = "Page number cannot be greater than {value}, but found: ${validatedValue}")
    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    @Min(value = MIN_DOWNLOAD_NUMBER, message = "Number of entries to download cannot be less than {value} " +
            "but found: ${validatedValue}")
    @Max(value = MAX_DOWNLOAD_NUMBER, message = "Number of entries to download cannot be more than {value} " +
            "but found: ${validatedValue}")
    public int getDownloadLimit() {
        return downloadLimit;
    }

    public void setDownloadLimit(int downloadLimit) {
        this.downloadLimit = downloadLimit;
    }

    /**
     * A list of extension relationship values, separated by commas
     * In the format extension=occurs_in(PomBase:SPBP23A10.14c),RGD:621207 etc
     * Users can supply just the database (e.g. PomBase) or id SPBP23A10.14c
     */
    public void setExtension(String... extension) {
        filterMap.put(Searchable.EXTENSION, extension);
    }

    /**
     * Return a list of annotation extension values, separated by commas
     *
     * @return String array containing comma separated list of extension values.
     */
    public String[] getExtension() {
        return filterMap.get(Searchable.EXTENSION);
    }

    /**
     * Include fields whose values derive from external resources
     * @param includeFields a vararg of fields to include
     */
    public void setIncludeFields(String... includeFields) {
        this.includeFields = includeFields;
    }

    /**
     * An array of fields whose values derive from external resources, which are to be included in the response
     * @return the array of fields from external resources to include in the response
     */
    @ArrayPattern(regexp = "^goName|taxonName$", flags = CASE_INSENSITIVE, paramName = INCLUDE_FIELD_PARAM)
    public String[] getIncludeFields() {
        return this.includeFields;
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
        createTaxonFilter().ifPresent(filterRequests::add);

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

    private Optional<FilterRequest> createTaxonFilter() {
        Optional<String> field = Optional.empty();
        if (filterMap.containsKey(TAXON_USAGE_ID)) {
            switch (getTaxonUsage()) {
                case DESCENDANTS_USAGE:
                    field = Optional.of(Searchable.TAXON_ANCESTORS);
                    break;
                case EXACT_USAGE:
                default:
                    field = Optional.of(Searchable.TAXON_ID);
                    break;
            }
        } else {
            if (filterMap.containsKey(TAXON_USAGE_FIELD)) {
                throwUsageWithoutIdException(TAXON_ID_PARAM, TAXON_USAGE_FIELD);
            }
        }
        return field.map(f -> FilterRequest
                .newBuilder()
                .addProperty(f, filterMap.get(TAXON_USAGE_ID))
                .build());
    }

    private void throwUsageWithoutIdException(String idParam, String usageParam) {
        throw new ParameterException("Annotation " + usageParam + " requires '" + idParam + "' to be set.");
    }

    private Optional<FilterRequest> createGoUsageFilter() {
        return createUsageFilter(GO_USAGE_FIELD, getGoUsage(), GO_USAGE_ID, Searchable.GO_ID, GO_USAGE_RELATIONSHIPS);
    }

    private Optional<FilterRequest> createEvidenceCodeUsageFilter() {
        return createUsageFilter(EVIDENCE_CODE_USAGE_FIELD, getEvidenceCodeUsage(), EVIDENCE_CODE_USAGE_ID,
                Searchable.EVIDENCE_CODE, EVIDENCE_CODE_USAGE_RELATIONSHIPS);
    }
    
    /**
     * Create a {@link ResultTransformationRequest}s instance, indicating how the results
     * should be transformed to fulfil the initial client request. For example, this instance
     * would include a {@link ResultTransformationRequest} instance for each field in "includeFields=goName".
     *
     * @return a {@link ResultTransformationRequests} instance indicating how the request's results
     * should be transformed
     */
    public ResultTransformationRequests createResultTransformationRequests() {
        ResultTransformationRequests transformationRequests = new ResultTransformationRequests();
        if (includeFields != null && includeFields.length > 0) {
            Stream.of(includeFields).map(ResultTransformationRequest::new).forEach(transformationRequests::addTransformationRequest);
        }
        return transformationRequests;
    }

    private Optional<FilterRequest> createUsageFilter(String usageParam, String usageValue, String idParam, String
            idField,
            String relationshipsParam) {
        Optional<FilterRequest> request;
        FilterRequest.Builder filterBuilder = FilterRequest.newBuilder();

        if (filterMap.containsKey(idField)) {
            // term id provided
            switch (usageValue) {
                case SLIM_USAGE:
                case DESCENDANTS_USAGE:
                    request = Optional.of(filterBuilder.addProperty(usageValue)
                            .addProperty(idParam, filterMap.get(idField))
                            .addProperty(relationshipsParam, filterMap.get(relationshipsParam))
                            .build());
                    break;
                case EXACT_USAGE:
                default:
                    request = Optional.of(filterBuilder.addProperty(idField, filterMap.get(idField))
                            .build());
                    break;
            }
        } else {
            // no term id
            if (filterMap.containsKey(usageParam)) {
                throw new ParameterException("Annotation " + usageParam + " requires '" + idParam + "' to be set.");
            }
            request = Optional.empty();
        }

        return request;
    }

    public List<StatsRequest> createStatsRequests() {
        return DEFAULT_STATS_REQUESTS;
    }

    private String[] createLowercasedStringArray(String... args) {
        return Stream.of(args)
                .map(String::toLowerCase)
                .toArray(String[]::new);
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
            checkArgument(groupName != null && !groupName.trim().isEmpty(),
                    "Statistics group name cannot be null or empty");
            checkArgument(groupField != null && !groupName.trim().isEmpty(),
                    "Statistics group field cannot be null or empty");
            checkArgument(aggregateFunction != null && !aggregateFunction.trim().isEmpty(), "Statistics " +
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
