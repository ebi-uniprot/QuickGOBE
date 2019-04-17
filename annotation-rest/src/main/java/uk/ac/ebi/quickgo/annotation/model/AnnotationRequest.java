package uk.ac.ebi.quickgo.annotation.model;

import uk.ac.ebi.quickgo.annotation.validation.service.ReferenceValidator;
import uk.ac.ebi.quickgo.annotation.validation.service.WithFromValidator;
import uk.ac.ebi.quickgo.common.validator.GeneProductIDList;
import uk.ac.ebi.quickgo.rest.ParameterException;
import uk.ac.ebi.quickgo.rest.controller.request.ArrayPattern;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.results.transformer.ResultTransformationRequest;
import uk.ac.ebi.quickgo.rest.search.results.transformer.ResultTransformationRequests;

import io.swagger.annotations.ApiModelProperty;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static java.util.Optional.of;
import static uk.ac.ebi.quickgo.annotation.common.AnnotationFields.Searchable.*;
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
    public static final String SLIM_USAGE = "slim";

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
    static final String SELECT_FIELD_PARAM = "Selectable fields";

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

    static final String DEFAULT_TAXON_USAGE = DESCENDANTS_USAGE;
    static final String DEFAULT_EVIDENCE_CODE_USAGE = DESCENDANTS_USAGE;
    static final String DEFAULT_GO_USAGE = DESCENDANTS_USAGE;
    static final String DEFAULT_GO_USAGE_RELATIONSHIPS="is_a,part_of,occurs_in";
    /**
     * indicates which fields should be looked at when creating filters
     */
    private static final String[] FILTER_REQUEST_FIELDS = new String[]{GO_ASPECT, ASSIGNED_BY,
            GENE_PRODUCT_ID, GO_EVIDENCE, QUALIFIER, REFERENCE, TARGET_SET, WITH_FROM, EXTENSION
    };

    @ApiModelProperty(
            value = "Number of results per page (" + MIN_ENTRIES_PER_PAGE + "-" + MAX_ENTRIES_PER_PAGE + ")",
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
            value = "The ontology to which associated GO terms belong. " +
                    "Accepts comma separated values. E.g., 'biological_process,molecular_function'.",
            allowableValues = "biological_process,molecular_function,cellular_component")
    private String aspect;

    @ApiModelProperty(value = "The database from which this annotation originates. Accepts comma separated values." +
            "E.g., BHF-UCL,Ensembl")
    private String[] assignedBy;

    @ApiModelProperty(
            value = "Literature id / database reference / database type. " +
                    "Format: DB:Reference or just DB. Accepts comma separated values. E.g., PMID:2676709 or PMID")
    private String reference;

    @ApiModelProperty(
            value = "The id of the gene product annotated with the GO term. Accepts comma separated values." +
                    "E.g., URS00000064B1_559292")
    private String geneProductId;

    @ApiModelProperty(
            value = "Evidence code indicating how the annotation is supported. Accepts comma separated values. " +
                    "E.g., ECO:0000255")
    private String evidenceCode;

    @ApiModelProperty(
            value = "The GO id of an annotation. Accepts comma separated values. " +
                    "E.g., GO:0070125")
    private String goId;

    @ApiModelProperty(
            value = "Aids the interpretation of an annotation. Accepts comma separated values. " +
                    "E.g., enables,involved_in")
    private String qualifier;

    @ApiModelProperty(
            value = "Additional ids for an annotation. Accepts comma separated values. " +
                    "E.g., P63328")
    private String withFrom;

    @ApiModelProperty(
            value = "The taxonomic id of the species encoding the gene product associated to an annotation. " +
                    "Accepts comma separated values. E.g., 1310605")
    private String taxonId;

    @ApiModelProperty(
            value = "Indicates how the taxonomic ids within the annotations should be used. E.g., exact",
            allowableValues = "descendants,exact")
    private String taxonUsage;

    @ApiModelProperty(
            value = "Indicates how the GO terms within the annotations should be used. Used in conjunction with " +
                    "'goUsageRelationships' filter. E.g., descendants",
            allowableValues = "descendants,exact,slim")
    private String goUsage;

    @ApiModelProperty(
            value = "The relationship between the 'goId' values " +
                    "found within the annotations. Allows comma separated values. E.g., is_a,part_of",
            allowableValues = "is_a,part_of,occurs_in,regulates")
    private String goUsageRelationships;

    @ApiModelProperty(
            value = "Indicates how the evidence code terms within the annotations should be used. Is used in " +
                    "conjunction with 'evidenceCodeUsageRelationships' filter. E.g., descendants",
            allowableValues = "descendants,exact")
    private String evidenceCodeUsage;

    @ApiModelProperty(
            value = "The relationship between the provided 'evidenceCode' identifiers. " +
                    "Allows comma separated values. E.g., is_a,part_of",
            allowableValues = "is_a,part_of,occurs_in,regulates")
    private String evidenceCodeUsageRelationships;

    @ApiModelProperty(
            value = "The type of gene product. Accepts comma separated values. E.g., protein,RNA",
            allowableValues = "protein,RNA,complex")
    private String geneProductType;

    @ApiModelProperty(
            value = "Gene product set. " +
                    "Accepts comma separated values. E.g., KRUK,BHF-UCL,Exosome")
    private String targetSet;

    @ApiModelProperty(
            value = "A database that provides a set of gene products. Accepts comma separated " +
                    "values. E.g., TrEMBL")
    private String geneProductSubset;

    @ApiModelProperty(
            value = "Gene ontology evidence codes of the 'goId's found within the annotations. Accepts comma " +
                    "separated values. E.g., EXP,IDA",
            hidden = true)
    private String goIdEvidence;

    @ApiModelProperty(value = "Extensions to annotations, where each extension can be: " +
            "EXTENSION(DB:ID) / EXTENSION(DB) / EXTENSION. ")
    private String extension;

    @ApiModelProperty(
            value = "The number of annotations to download ("+MIN_DOWNLOAD_NUMBER+"-"+MAX_DOWNLOAD_NUMBER+"). Note, " +
                    "the page size parameter 'limit' will be ignored when downloading results. ",
            allowableValues = "range[" + MIN_DOWNLOAD_NUMBER + "," + MAX_DOWNLOAD_NUMBER + "]",
            hidden = true)
    private int downloadLimit = DEFAULT_DOWNLOAD_LIMIT;

    @ApiModelProperty(
            value = "Optional fields retrieved from external services. Accepts comma separated values.",
            allowableValues = "goName,taxonName,name,synonyms")
    private String[] includeFields;

    @ApiModelProperty(
            value = "For TSV downloads only: fields to be downloaded. Accepts comma separated values.",
            allowableValues = "geneProductId,symbol,qualifier,goId,goAspect,goName,evidenceCode,goEvidence,reference," +
                    "withFrom,taxonId,assignedBy,extension,date,taxonName,synonym,name,type,interactingTaxonId",
            hidden = true)
    private String[] selectedFields;

    @ApiModelProperty(
            value = "The proteomic classification of the annotated gene product, if applicable - this is relevant for" +
                    " proteins only. The allowed values are complete; none; gcrpCan (Gene Centric Reference Proteome " +
                    "Canonical) & gcrpIso (Gene Centric Reference Proteome IsoForm).",
            allowableValues = "complete," + "none,gcrpCan,gcrpIso", hidden = true)
    private String[] proteome;

    private AnnotationRequestBody requestBody;

    private final Map<String, String[]> filterMap = new HashMap<>();

    /**
     * E.g. ASPGD,Agbase,..
     * In the format assignedBy=ASPGD,Agbase
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
        filterMap.put(REFERENCE, reference);
    }

    @ReferenceValidator
    @Size(max = MAX_REFERENCES,
            message = "Number of items in '" + REFERENCE_PARAM + "' is larger than: {max}")
    public String[] getReference() {
        return filterMap.get(REFERENCE);
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
     *
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

    @ArrayPattern(regexp = "^(NOT\\|)?[A-Z_]+$", flags = CASE_INSENSITIVE, paramName = QUALIFIER_PARAM)
    public String[] getQualifier() {
        return filterMap.get(QUALIFIER);
    }

    /**
     * A list of with/from values, separated by commas
     * In the format withFrom=PomBase:SPBP23A10.14c,RGD:621207 etc
     * Users can supply just the id (e.g. PomBase) or id SPBP23A10.14c
     *
     * @param withFrom comma separated with/from values
     */
    public void setWithFrom(String... withFrom) {
        filterMap.put(WITH_FROM, withFrom);
    }

    /**
     * Return a list of with/from values, separated by commas
     *
     * @return String containing comma separated list of with/From values.
     */
    @WithFromValidator
    public String[] getWithFrom() {
        return filterMap.get(WITH_FROM);
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
     * A single annotation extension value - commas represent part of the extension, and are not to be used as
     * delimiters between values.
     * In the format extension=occurs_in(PomBase:SPBP23A10.14c),RGD:621207
     */
    public void setExtension(String... extension) {
        String singleExtension = Arrays.stream(extension).collect(Collectors.joining(","));
        filterMap.put(EXTENSION, new String[]{singleExtension});
    }

    /**
     * Return an AnnotationExtension value
     *
     * @return Extension value.
     */
    public String[] getExtension() {
        return filterMap.get(EXTENSION);
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
    @ArrayPattern(regexp = "^goName|taxonName|name|synonyms$", flags = CASE_INSENSITIVE, paramName =
            INCLUDE_FIELD_PARAM)
    public String[] getIncludeFields() {
        return this.includeFields;
    }

    /**
     * Select which fields will appear in the TSV download
     * @param selectedFields a vararg of fields to include
     */
    public void setSelectedFields(String... selectedFields) {
        this.selectedFields = selectedFields;
    }

    /**
     * An array of fields whose values will appear in the TSV download
     * @return the array of fields from external resources to include in the response
     */
    @ArrayPattern(regexp = "^geneProductId|symbol|qualifier|goId|goAspect|goName|evidenceCode|goEvidence|reference" +
            "|withFrom|taxonId|interactingTaxonId|taxonName|assignedBy|extensions|date|name|synonyms|type$", flags = CASE_INSENSITIVE,
            paramName =
            SELECT_FIELD_PARAM)
    public String[] getSelectedFields() {
        return this.selectedFields;
    }

    /**
     * Filter by the annotations by their proteomic classification.
     * @param proteome filtering value(s)
     */
    public void setProteome(String... proteome) {
        filterMap.put(PROTEOME, proteome);
    }

    /**
     * An array of filtering values for proteomic classification.
     * @return
     */
    public String[] getProteome() {
        return filterMap.get(PROTEOME);
    }

    public void setRequestBody(AnnotationRequestBody requestBody) {
        AnnotationRequestBody.putDefaultValuesIfAbsent(requestBody);
        this.requestBody = requestBody;
    }

    public AnnotationRequestBody getRequestBody() {
        return requestBody;
    }

    /**
     * Produces a set of {@link FilterRequest} objects given the filter attributes provided by the user.
     *
     * @return a list of {@link FilterRequest}
     */
    public List<FilterRequest> createFilterRequests() {

        validateFilter();

        List<FilterRequest> filterRequests = new ArrayList<>();

        Stream.of(FILTER_REQUEST_FIELDS)
                .map(this::createSimpleFilter)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(filterRequests::add);

        createGeneProductTypeFilter().ifPresent(filterRequests::add);
        createGoUsageFilter().ifPresent(filterRequests::add);
        createEvidenceCodeUsageFilter().ifPresent(filterRequests::add);
        createTaxonFilter().ifPresent(filterRequests::add);
        createBodyFilter().forEach(filterRequests::add);
        return filterRequests;
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
            Stream.of(includeFields)
                    .map(ResultTransformationRequest::new)
                    .forEach(transformationRequests::addTransformationRequest);
        }
        return transformationRequests;
    }

    @Override public String toString() {
        return "AnnotationRequest{" +
                "limit=" + limit +
                ", page=" + page +
                ", aspect='" + aspect + '\'' +
                ", assignedBy=" + Arrays.toString(assignedBy) +
                ", reference='" + reference + '\'' +
                ", geneProductId='" + geneProductId + '\'' +
                ", evidenceCode='" + evidenceCode + '\'' +
                ", goId='" + goId + '\'' +
                ", qualifier='" + qualifier + '\'' +
                ", withFrom='" + withFrom + '\'' +
                ", taxonId='" + taxonId + '\'' +
                ", taxonUsage='" + taxonUsage + '\'' +
                ", goUsage='" + goUsage + '\'' +
                ", goUsageRelationships='" + goUsageRelationships + '\'' +
                ", evidenceCodeUsage='" + evidenceCodeUsage + '\'' +
                ", evidenceCodeUsageRelationships='" + evidenceCodeUsageRelationships + '\'' +
                ", geneProductType='" + geneProductType + '\'' +
                ", targetSet='" + targetSet + '\'' +
                ", geneProductSubset='" + geneProductSubset + '\'' +
                ", goIdEvidence='" + goIdEvidence + '\'' +
                ", extension='" + extension + '\'' +
                ", downloadLimit=" + downloadLimit +
                ", includeFields=" + Arrays.toString(includeFields) +
                ", selectedFields=" + Arrays.toString(selectedFields) +
                ", filterMap=" + filterMap +
                '}';
    }

    private Optional<FilterRequest> createSimpleFilter(String key) {
        Optional<FilterRequest> request;
        if (filterMap.containsKey(key)) {
            FilterRequest.Builder requestBuilder = FilterRequest.newBuilder();
            requestBuilder.addProperty(key, filterMap.get(key));
            request = of(requestBuilder.build());
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
                    field = of(TAXON_ANCESTORS);
                    break;
                case EXACT_USAGE:
                default:
                    field = of(TAXON_ID);
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

    private Optional<FilterRequest> createGeneProductTypeFilter() {
        Optional<FilterRequest> retVal = Optional.empty();
        if (filterMap.containsKey(GENE_PRODUCT_TYPE)) {
            final FilterRequest.Builder builder = FilterRequest.newBuilder();
            builder.addProperty(GENE_PRODUCT_TYPE, filterMap.get(GENE_PRODUCT_TYPE));

            if (filterMap.containsKey(GENE_PRODUCT_SUBSET)) {
                builder.addProperty(GENE_PRODUCT_SUBSET, filterMap.get(GENE_PRODUCT_SUBSET));
            }

            if (filterMap.containsKey(PROTEOME)) {
                builder.addProperty(PROTEOME, filterMap.get(PROTEOME));
            }

            retVal = of(builder.build());
        }
        return retVal;
    }

    private void throwUsageWithoutIdException(String idParam, String usageParam) {
        throw new ParameterException("Annotation " + usageParam + " requires '" + idParam + "' to be set.");
    }

    private Optional<FilterRequest> createGoUsageFilter() {
        return createUsageFilter(GO_USAGE_FIELD, getGoUsage(), GO_USAGE_ID, GO_ID, GO_USAGE_RELATIONSHIPS);
    }

    private Optional<FilterRequest> createEvidenceCodeUsageFilter() {
        return createUsageFilter(EVIDENCE_CODE_USAGE_FIELD, getEvidenceCodeUsage(), EVIDENCE_CODE_USAGE_ID,
                EVIDENCE_CODE, EVIDENCE_CODE_USAGE_RELATIONSHIPS);
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
                    request = of(filterBuilder.addProperty(usageValue)
                            .addProperty(idParam, filterMap.get(idField))
                            .addProperty(relationshipsParam, filterMap.get(relationshipsParam))
                            .build());
                    break;
                case EXACT_USAGE:
                default:
                    request = of(filterBuilder.addProperty(idField, filterMap.get(idField))
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

    private List<FilterRequest> createBodyFilter() {
        List<FilterRequest> ret = new ArrayList<>();
        if (getRequestBody() == null) {
            return Collections.emptyList();
        }

        createUsageBodyFilter(getRequestBody().getAnd(), GP_RELATED_AND_GO_IDS, "andGoUsageRelationships").ifPresent(ret::add);
        createUsageBodyFilter(getRequestBody().getNot(), GP_RELATED_NOT_GO_IDS, "notGoUsageRelationships").ifPresent(ret::add);
        return ret;
    }

    private Optional<FilterRequest> createUsageBodyFilter(AnnotationRequestBody.GoDescription goDescription,
                                                          String idParam, String relationshipsParam) {
        Optional<FilterRequest> request = Optional.empty();
        if (goDescription.getGoTerms().isEmpty()) {
            return request;
        }

        FilterRequest.Builder filterBuilder = FilterRequest.newBuilder();
        switch (goDescription.getGoUsage()) {
            case SLIM_USAGE:
            case DESCENDANTS_USAGE:
                request = of(filterBuilder.addProperty(goDescription.getGoUsage())
                  .addProperty(idParam, String.join(",",goDescription.getGoTerms()))
                  .addProperty(relationshipsParam, goDescription.getGoUsageRelationships())
                  .build());
                break;
            case EXACT_USAGE:
                request = of(filterBuilder
                  .addProperty(idParam, goDescription.getGoTerms().toArray(new String[goDescription.getGoTerms().size()]))
                  .build());
                break;
        }
        return request;
    }

    private String[] createLowercasedStringArray(String... args) {
        return Stream.of(args)
                .map(String::toLowerCase)
                .toArray(String[]::new);
    }

    private void validateFilter(){
        // GOA-3266 and GOA-3130

        final Optional<String> protein = Stream.of(filterMap.getOrDefault(GENE_PRODUCT_TYPE, new String[0]))
                .filter(p -> GeneProduct.GeneProductType.PROTEIN.getName().equals(p)).findFirst();

        // gene product subset filter only valid when geneProductType protein is set
        if(filterMap.containsKey(GENE_PRODUCT_SUBSET) && !protein.isPresent()){
            throw new ParameterException("Annotation " + GENE_PRODUCT_SUBSET + " requires '" + GENE_PRODUCT_TYPE +
                    "=" + GeneProduct.GeneProductType.PROTEIN.getName()+"' to be set.");
        }
        // proteome filter only valid when geneProductType protein is set
        if(filterMap.containsKey(PROTEOME) && !protein.isPresent()){
            throw new ParameterException("Annotation " + PROTEOME + " requires '" + GENE_PRODUCT_TYPE +
                    "=" + GeneProduct.GeneProductType.PROTEIN.getName()+"' to be set.");
        }
    }
}
