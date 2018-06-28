package uk.ac.ebi.quickgo.geneproduct.model;

import uk.ac.ebi.quickgo.geneproduct.common.GeneProductFields;
import uk.ac.ebi.quickgo.rest.controller.request.AllowableFacets;
import uk.ac.ebi.quickgo.rest.controller.request.ArrayPattern;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;

import io.swagger.annotations.ApiModelProperty;
import java.util.*;
import java.util.stream.Stream;
import javax.validation.constraints.*;

import static javax.validation.constraints.Pattern.Flag.CASE_INSENSITIVE;
import static uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelperImpl.DEFAULT_ENTRIES_PER_PAGE;
import static uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelperImpl.MAX_ENTRIES_PER_PAGE;
import static uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelperImpl.MAX_PAGE_NUMBER;
import static uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelperImpl.MIN_ENTRIES_PER_PAGE;
import static uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelperImpl.MIN_PAGE_NUMBER;
import static uk.ac.ebi.quickgo.rest.search.DefaultSearchQueryTemplate.DEFAULT_PAGE_NUMBER;

/**
 * A data structure used to store the input parameters a client can submit to the GeneProduct search endpoint
 *
 * Once the comma separated values have been set, then turn then into an object (SimpleFilter) that
 * encapsulates the list and solr field name to use for that argument.
 */
public class GeneProductRequest {

    private static final String[] TARGET_FIELDS = new String[]{
            GeneProductFields.Searchable.TYPE,
            GeneProductFields.Searchable.TAXON_ID,
            GeneProductFields.Searchable.DATABASE_SUBSET,
            GeneProductFields.Searchable.PROTEOME};

    @ApiModelProperty(value = "Indicates whether the result set should be highlighted", hidden = true)
    private boolean highlighting = false;

    @ApiModelProperty(value = "Page number of the result set to display.",
            allowableValues = "range[" + MIN_PAGE_NUMBER + ",  max_result_page_size]")
    private int page = DEFAULT_PAGE_NUMBER;

    @ApiModelProperty(value = "Number of results per page.",
            allowableValues = "range[" + MIN_ENTRIES_PER_PAGE + "," + MAX_ENTRIES_PER_PAGE + "]")
    private int limit = DEFAULT_ENTRIES_PER_PAGE;

    @ApiModelProperty(value = "Fields to generate facet from", example = "taxonId, type", hidden = true)
    private String[] facet;

    @ApiModelProperty(value = "The query used to filter the gene products", example = "kinase", required = true)
    private String query;

    /*
        The filter fields are only declared here, because there is a bug in springfox that doesn't read annotations on
        setters
     */
    @ApiModelProperty(value = "Filters the results of the main query based on values chosen from " +
            "the taxonomy identifier field", example = "9606")
    private String[] taxonId;

    @ApiModelProperty(value = "Filters the results of the main query based on a value chosen from " +
            "the type field", allowableValues = "protein,miRNA,complexes", example = "protein")
    private String type;

    @ApiModelProperty(value = "Filters the results of the main query based on a value chosen from " +
            "the dbSubset field", allowableValues = "TrEMBL,Swiss-Prot", example = "TrEMBL")
    private String dbSubset;

    @ApiModelProperty(value = "Filters the results of the main query based on a value chosen from the proteome field." +
            " Proteins with a proteome 'gcrpCan' (aka reference) are part of a subset of proteomes that have been " +
            "selected either manually or algorithmically according to a number of criteria to provide a broad " +
            "coverage of the tree of life and a representative cross-section of the taxonomic diversity found within " +
            "UniProtKB, as well as the proteomes of well-studied model organisms and other species of interest for " +
            "biomedical research. Proteins with a proteome of 'complete' are part of a proteome. A proteome is the " +
            "set of protein sequences that can be derived by translation of all protein coding genes of a completely " +
            "sequenced genome, including alternative products such as splice variants for those species in which " +
            "these may occur. If a gene product is in a reference proteome it is always part of a complete proteome " +
            "but not vice-versa. A proteome of 'none' means the gene product is not assigned to a proteome, but is a " +
            "protein. A proteome with 'gcrpIso' will get isoform entries. If proteome value not given as a part of " +
            "request it will consider 'Not applicable' means the gene product is not a protein, and cannot be part of" +
            " a proteome.",
            allowableValues = "gcrpCan, gcrpIso, complete, none",
            example = "complete")
    private String proteome;

    private Map<String, String[]> filterMap = new HashMap<>();

    @Min(value = MIN_PAGE_NUMBER, message = "Page number cannot be less than 1")
    @Max(value = MAX_PAGE_NUMBER, message = "Page number cannot be greater than {value}, but found: ${validatedValue}")
    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    @Min(value = MIN_ENTRIES_PER_PAGE, message = "Number of results per page cannot be less than 0")
    @Max(value = MAX_ENTRIES_PER_PAGE,
            message = "Number of results per page cannot be greater than " + MAX_ENTRIES_PER_PAGE)
    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    @NotNull(message = "Query cannot be null")
    @Size(min = 1, message = "Query cannot be empty")
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public boolean isHighlighting() {
        return highlighting;
    }

    public void setHighlighting(boolean useHighlighting) {
        this.highlighting = useHighlighting;
    }

    @AllowableFacets
    public String[] getFacet() {
        return facet;
    }

    public void setFacet(String[] facets) {
        this.facet = facets;
    }

    @ArrayPattern(regexp = "[0-9]+", paramName = "taxonId")
    public String[] getTaxonId() {
        return filterMap.get(GeneProductFields.Searchable.TAXON_ID);
    }

    public void setTaxonId(String... taxonIds) {
        if (taxonIds != null) {
            filterMap.put(GeneProductFields.Searchable.TAXON_ID, taxonIds);
        }
    }

    @Pattern(regexp = "protein|miRNA|complexes", flags = CASE_INSENSITIVE,
            message = "Provided type is invalid: ${validatedValue}")
    public String getType() {
        return filterMap.get(GeneProductFields.Searchable.TYPE) == null ? null :
                filterMap.get(GeneProductFields.Searchable.TYPE)[0];
    }

    public void setType(String type) {
        if (type != null) {
            filterMap.put(GeneProductFields.Searchable.TYPE, new String[]{type});
        }
    }

    @Pattern(regexp = "trembl|swiss-prot", flags = CASE_INSENSITIVE,
            message = "Provided dbSubset is invalid: ${validatedValue}")
    public String getDbSubset() {
        return filterMap.get(GeneProductFields.Searchable.DATABASE_SUBSET) == null ? null :
                filterMap.get(GeneProductFields.Searchable.DATABASE_SUBSET)[0];
    }

    public void setDbSubset(String dbSubset) {
        if (dbSubset != null) {
            filterMap.put(GeneProductFields.Searchable.DATABASE_SUBSET, new String[]{dbSubset});
        }
    }

    @Pattern(regexp = "gcrpCan|complete|none|gcrpIso", flags = CASE_INSENSITIVE,
            message = "Provided proteome is invalid: ${validatedValue}")
    public String getProteome() {
        return filterMap.get(GeneProductFields.Searchable.PROTEOME) == null ? null :
                filterMap.get(GeneProductFields.Searchable.PROTEOME)[0];
    }

    public void setProteome(String proteome) {
        if (proteome != null) {
            filterMap.put(GeneProductFields.Searchable.PROTEOME, new String[]{proteome});
        }
    }

    public QuickGOQuery createQuery() {
        return QuickGOQuery.createQuery(query);
    }

    public List<FilterRequest> createFilterRequests() {
        List<FilterRequest> filterRequests = new ArrayList<>();

        Stream.of(TARGET_FIELDS)
                .map(this::createFilter)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(filterRequests::add);

        return filterRequests;
    }

    private Optional<FilterRequest> createFilter(String key) {
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
}
