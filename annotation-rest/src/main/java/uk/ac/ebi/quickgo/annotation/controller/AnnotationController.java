package uk.ac.ebi.quickgo.annotation.controller;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.model.AnnotationRequest;
import uk.ac.ebi.quickgo.annotation.model.StatisticsGroup;
import uk.ac.ebi.quickgo.annotation.service.search.SearchServiceConfig;
import uk.ac.ebi.quickgo.annotation.service.statistics.StatisticsService;
import uk.ac.ebi.quickgo.rest.ParameterBindingException;
import uk.ac.ebi.quickgo.rest.ResponseExceptionHandler;
import uk.ac.ebi.quickgo.rest.comm.FilterContext;
import uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelper;
import uk.ac.ebi.quickgo.rest.search.DefaultSearchQueryTemplate;
import uk.ac.ebi.quickgo.rest.search.SearchService;
import uk.ac.ebi.quickgo.rest.search.query.QueryRequest;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.converter.FilterConverterFactory;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;
import uk.ac.ebi.quickgo.rest.search.results.transformer.ResultTransformerChain;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.HashSet;
import java.util.Set;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static com.google.common.base.Preconditions.checkArgument;
import static uk.ac.ebi.quickgo.rest.search.SearchDispatcher.searchAndTransform;

/**
 * Provides RESTful endpoints for retrieving Gene Ontology (GO) Annotations to gene products.
 *
 * Gene Ontology: the framework for the model of biology. The GO defines concepts/classes used
 * to describe gene function, and relationships between these concepts.
 *
 * GO annotations: the model of biology. Annotations are statements describing the functions of specific genes,
 * using concepts in the Gene Ontology. The simplest and most common annotation links one gene to one function,
 * e.g. FZD4 + Wnt signaling pathway. Each statement is based on a specified piece of evidence
 *
 * Sets of annotations can be tailored for each user by powerful filtering capabilities
 * Annotations will be downloadable in a variety of formats.
 *
 * taxon=1234,343434
 *
 * gp=A0A000,A0A001
 * gpSet=BHF-UCL,Exosome
 * gpType=protein,miRNA,complex
 *
 * goTerm=GO:0016021,GO:0016022
 * goTermSet=goslim_chembl, goSlimGeneric .. and others.
 *
 * ..the following are only applicable if goTerm ids or sets have been selected
 * goTermUse=ancestor or goTermUse=slim  or goTermUse=exact
 *
 * goTermRelationship=I or goTermRelationship=IPO or goTermRelationship=IPOR
 *
 * aspect=F,P,C
 *
 * evidence=ECO:0000352,ECO0000269
 *
 * goEvidence=IEA etc
 *
 * ..the following is only applicable if any evidence code has been selected
 * evidenceRelationship=ancestor or evidenceRelationship=exact
 *
 * qualifier=enables,not_enables
 *
 * reference=DOI,GO_REF
 *
 * with=AGI_LocusCode,CGD
 *
 * assignedby=ASPGD,Agbase
 *
 * @author Tony Wardell
 *         Date: 21/04/2016
 *         Time: 11:26
 *         Created with IntelliJ IDEA.
 */
@RestController
@RequestMapping(value = "/annotation")
public class AnnotationController {
    private final ControllerValidationHelper validationHelper;

    private final SearchService<Annotation> annotationSearchService;

    private final DefaultSearchQueryTemplate queryTemplate;
    private final FilterConverterFactory converterFactory;
    private final ResultTransformerChain<QueryResult<Annotation>> resultTransformerChain;
    private final StatisticsService statsService;

    @Autowired
    public AnnotationController(SearchService<Annotation> annotationSearchService,
            SearchServiceConfig.AnnotationCompositeRetrievalConfig annotationRetrievalConfig,
            ControllerValidationHelper validationHelper,
            FilterConverterFactory converterFactory,
            ResultTransformerChain<QueryResult<Annotation>> resultTransformerChain,
            StatisticsService statsService) {
        checkArgument(annotationSearchService != null, "The SearchService<Annotation> instance passed " +
                "to the constructor of AnnotationController should not be null.");
        checkArgument(annotationRetrievalConfig != null, "The SearchServiceConfig" +
                ".AnnotationCompositeRetrievalConfig instance passed to the constructor of AnnotationController " +
                "should not be null.");
        checkArgument(converterFactory != null, "The FilterConverterFactory cannot be null.");
        checkArgument(resultTransformerChain != null,
                "The ResultTransformerChain<QueryResult<Annotation>> cannot be null.");
        checkArgument(statsService != null, "Annotation stats service cannot be null.");

        this.annotationSearchService = annotationSearchService;
        this.validationHelper = validationHelper;
        this.converterFactory = converterFactory;

        this.statsService = statsService;
        this.resultTransformerChain = resultTransformerChain;

        this.queryTemplate = new DefaultSearchQueryTemplate();
        this.queryTemplate.setReturnedFields(annotationRetrievalConfig.getSearchReturnedFields());
    }

    /**
     * Search for an Annotations based on their attributes
     * @return a {@link QueryResult} instance containing the results of the search
     */
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Annotation result set has been filtered according to " +
                    "the provided attribute values"),
            @ApiResponse(code = 500, message = "Internal server error occurred whilst searching for " +
                    "matching annotations", response = ResponseExceptionHandler.ErrorInfo.class),
            @ApiResponse(code = 400, message = "Bad request due to a validation issue encountered in one of the " +
                    "filters", response = ResponseExceptionHandler.ErrorInfo.class)})
    @ApiOperation(value = "Search for all annotations that match the filter criteria provided by the client.")
    @RequestMapping(value = "/search", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<Annotation>> annotationLookup(
            @Valid @ModelAttribute AnnotationRequest request, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new ParameterBindingException(bindingResult);
        }

        FilterQueryInfo filterQueryInfo = extractFilterQueryInfo(request);

        QueryRequest queryRequest = queryTemplate.newBuilder()
                .setQuery(QuickGOQuery.createAllQuery())
                .addFilters(filterQueryInfo.getFilterQueries())
                .setPage(request.getPage())
                .setPageSize(request.getLimit())
                .build();

        return searchAndTransform(queryRequest, annotationSearchService, resultTransformerChain,
                filterQueryInfo.getFilterContext());
    }

    private FilterQueryInfo extractFilterQueryInfo(
            AnnotationRequest request) {
        Set<QuickGOQuery> filterQueries = new HashSet<>();
        Set<FilterContext> filterContexts = new HashSet<>();

        request.createFilterRequests().stream()
                .map(converterFactory::convert)
                .forEach(convertedFilter -> {
                    filterQueries.add(convertedFilter.getConvertedValue());
                    convertedFilter.getFilterContext().ifPresent(filterContexts::add);
                });

        return new FilterQueryInfo() {
            @Override public Set<QuickGOQuery> getFilterQueries() {
                return filterQueries;
            }

            @Override public FilterContext getFilterContext() {
                return filterContexts.stream().reduce(new FilterContext(), FilterContext::merge);
            }
        };

    }

    private interface FilterQueryInfo {
        Set<QuickGOQuery> getFilterQueries();

        FilterContext getFilterContext();
    }

    /**
     * Return statistics based on the search result.
     *
     * The statistics are subdivided into two areas, each with
     * @return a {@link QueryResult} instance containing the results of the search
     */
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Statistics have been calculated for the annotation result set " +
                    "obtained from the application of the filter parameters"),
            @ApiResponse(code = 500, message = "Internal server error occurred whilst producing statistics",
                    response = ResponseExceptionHandler.ErrorInfo.class),
            @ApiResponse(code = 400, message = "Bad request due to a validation issue encountered in one of the " +
                    "filters", response = ResponseExceptionHandler.ErrorInfo.class)})
    @ApiOperation(value = "Generate statistic on the annotation result set obtained from applying the filters.")
    @RequestMapping(value = "/stats", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<StatisticsGroup>> annotationStats(
            @Valid @ModelAttribute AnnotationRequest request, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new ParameterBindingException(bindingResult);
        }

        QueryResult<StatisticsGroup> stats = statsService.calculate(request);
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }
}
