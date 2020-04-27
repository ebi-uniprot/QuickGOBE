package uk.ac.ebi.quickgo.client.model.ontology;

import uk.ac.ebi.quickgo.rest.controller.request.AllowableFacets;
import uk.ac.ebi.quickgo.rest.controller.request.ArrayPattern;
import uk.ac.ebi.quickgo.rest.controller.request.ArrayPattern.Flag;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;

import io.swagger.annotations.ApiModelProperty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.validation.constraints.*;

import static javax.validation.constraints.Pattern.Flag.CASE_INSENSITIVE;
import static uk.ac.ebi.quickgo.ontology.common.OntologyFields.Searchable;
import static uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelperImpl.DEFAULT_ENTRIES_PER_PAGE;
import static uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelperImpl.MAX_ENTRIES_PER_PAGE;
import static uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelperImpl.MAX_PAGE_NUMBER;
import static uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelperImpl.MIN_ENTRIES_PER_PAGE;
import static uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelperImpl.MIN_PAGE_NUMBER;
import static uk.ac.ebi.quickgo.rest.search.DefaultSearchQueryTemplate.DEFAULT_PAGE_NUMBER;

/**
 * A data structure used to store the input parameters a client can submit to the Ontology search endpoint
 *
 * Once the comma separated values have been set, then turn then into an object (SimpleFilter) that
 * encapsulates the list and document field name to use for that argument.
 */
public class OntologyRequest {
    private static final String[] TARGET_FIELDS = new String[]{Searchable.ASPECT, Searchable.ONTOLOGY_TYPE, Searchable.IS_OBSOLETE};

    @ApiModelProperty(value = "Indicates whether the result set should be highlighted")
    private boolean highlighting = false;

    @ApiModelProperty(value = "Page number of the result set to display.",
            allowableValues = "range[" + MIN_PAGE_NUMBER + ",  max_result_page_size]")
    private int page = DEFAULT_PAGE_NUMBER;

    @ApiModelProperty(value = "Number of results per page.",
            allowableValues = "range[" + MIN_ENTRIES_PER_PAGE + "," + MAX_ENTRIES_PER_PAGE + "]")
    private int limit = DEFAULT_ENTRIES_PER_PAGE;

    @ApiModelProperty(value = "Fields to generate facets from", allowableValues = "aspect, ontologyType, isObsolete",
            example = "aspect, ontologyType, isObsolete")
    private String[] facet;

    @ApiModelProperty(value = "The query used to filter the gene products", example = "kinase", required = true)
    private String query;

    /*
        The filter fields are only declared here, because there is a bug in springfox that doesn't read annotations on
        setters
     */
    @ApiModelProperty(value = "Further filters the results of the main query based on values chosen from " +
            "the aspect field", allowableValues = "Component,Function,Process", example = "Process")
    private String[] aspect;

    @ApiModelProperty(value = "Further filters the results of the main query based on a value chosen from " +
            "the type field", allowableValues = "GO,ECO", example = "GO")
    private String type;

    @ApiModelProperty(value = "Further filters the results of the main query based on a value chosen from " +
      "the isObsolete field", allowableValues = "true,false", example = "false")
    private String isObsolete;

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

    public void setFacet(String[] facet) {
        this.facet = facet;
    }

    @ArrayPattern(regexp = "Process|Function|Component",
            paramName = "aspect",
            flags = Flag.CASE_INSENSITIVE)
    public String[] getAspect() {
        return filterMap.get(Searchable.ASPECT);
    }

    public void setAspect(String... filterByAspect) {
        if (filterByAspect != null) {
            filterMap.put(Searchable.ASPECT, filterByAspect);
        }
    }

    @Pattern(regexp = "go|eco", flags = CASE_INSENSITIVE,
            message = "Provided ontology type is invalid: ${validatedValue}")
    public String getOntologyType() {
        return filterMap.get(Searchable.ONTOLOGY_TYPE) == null ? null :
                filterMap.get(Searchable.ONTOLOGY_TYPE)[0];
    }

    public void setOntologyType(String filterByType) {
        if (filterByType != null) {
            filterMap.put(Searchable.ONTOLOGY_TYPE, new String[]{filterByType});
        }
    }

    @Pattern(regexp = "true|false", message = "Provided isObsolete is invalid: ${validatedValue}")
    public String getIsObsolete() {
        return filterMap.get(Searchable.IS_OBSOLETE) == null ? null :
          filterMap.get(Searchable.IS_OBSOLETE)[0];
    }

    public void setIsObsolete(String filterByObsolete) {
        if (filterByObsolete != null) {
            filterMap.put(Searchable.IS_OBSOLETE, new String[]{filterByObsolete});
        }
    }

    public QuickGOQuery createQuery() {
        return QuickGOQuery.createQuery(query);
    }

    /**
     * Produces a set of {@link FilterRequest} objects given the filter attributes provided by the user.
     *
     * @return a list of {@link FilterRequest}
     */
    public List<FilterRequest> createFilterRequests() {
        return Stream.of(TARGET_FIELDS)
                .map(this::createFilter)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
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