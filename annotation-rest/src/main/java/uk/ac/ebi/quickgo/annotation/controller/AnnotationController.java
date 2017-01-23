package uk.ac.ebi.quickgo.annotation.controller;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.model.AnnotationDownloadRequest;
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
import uk.ac.ebi.quickgo.rest.search.query.RegularPage;
import uk.ac.ebi.quickgo.rest.search.request.converter.FilterConverterFactory;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;
import uk.ac.ebi.quickgo.rest.search.results.transformer.ResultTransformerChain;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import static com.google.common.base.Preconditions.checkArgument;
import static org.slf4j.LoggerFactory.getLogger;
import static uk.ac.ebi.quickgo.rest.search.SearchDispatcher.searchAndTransform;
import static uk.ac.ebi.quickgo.rest.search.SearchDispatcher.streamSearchResults;
import static uk.ac.ebi.quickgo.rest.search.query.CursorPage.createFirstCursorPage;

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
    private static final Logger LOGGER = getLogger(AnnotationController.class);

    private final ControllerValidationHelper validationHelper;

    private final SearchService<Annotation> annotationSearchService;

    private final DefaultSearchQueryTemplate queryTemplate;
    private final DefaultSearchQueryTemplate downloadQueryTemplate;
    private final FilterConverterFactory converterFactory;
    private final ResultTransformerChain<QueryResult<Annotation>> resultTransformerChain;
    private final StatisticsService statsService;
    private final TaskExecutor taskExecutor;

    @Autowired
    public AnnotationController(SearchService<Annotation> annotationSearchService,
            SearchServiceConfig.AnnotationCompositeRetrievalConfig annotationRetrievalConfig,
            ControllerValidationHelper validationHelper,
            FilterConverterFactory converterFactory,
            ResultTransformerChain<QueryResult<Annotation>> resultTransformerChain,
            StatisticsService statsService,
            TaskExecutor taskExecutor) {
        checkArgument(annotationSearchService != null, "The SearchService<Annotation> instance passed " +
                "to the constructor of AnnotationController should not be null.");
        checkArgument(annotationRetrievalConfig != null, "The SearchServiceConfig" +
                ".AnnotationCompositeRetrievalConfig instance passed to the constructor of AnnotationController " +
                "should not be null.");
        checkArgument(converterFactory != null, "The FilterConverterFactory cannot be null.");
        checkArgument(resultTransformerChain != null,
                "The ResultTransformerChain<QueryResult<Annotation>> cannot be null.");
        checkArgument(statsService != null, "Annotation stats service cannot be null.");
        checkArgument(taskExecutor != null, "TaskExecutor cannot be null."); // todo: test

        this.annotationSearchService = annotationSearchService;
        this.validationHelper = validationHelper;
        this.converterFactory = converterFactory;

        this.statsService = statsService;
        this.resultTransformerChain = resultTransformerChain;

        this.queryTemplate = createSearchQueryTemplate(annotationRetrievalConfig);
        this.downloadQueryTemplate = createDownloadSearchQueryTemplate(annotationRetrievalConfig);

        this.taskExecutor = taskExecutor;
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
        checkBindingErrors(bindingResult);

        FilterQueryInfo filterQueryInfo = extractFilterQueryInfo(request);

        QueryRequest queryRequest = queryTemplate.newBuilder()
                .setQuery(QuickGOQuery.createAllQuery())
                .addFilters(filterQueryInfo.getFilterQueries())
                .setPage(new RegularPage(request.getPage(), request.getLimit()))
                .build();

        return searchAndTransform(queryRequest, annotationSearchService, resultTransformerChain,
                filterQueryInfo.getFilterContext());
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
        checkBindingErrors(bindingResult);

        QueryResult<StatisticsGroup> stats = statsService.calculate(request);
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    @RequestMapping(value = "/downloadSearch",
            method = {RequestMethod.GET},
            produces = {"text/gaf", "text/gpad"})
    public ResponseEntity<ResponseBodyEmitter> downloadLookup(
            @Valid @ModelAttribute AnnotationDownloadRequest request,
            BindingResult bindingResult,
            @RequestHeader("Accept") MediaType mediaTypeAcceptHeader) {
        checkBindingErrors(bindingResult);

        FilterQueryInfo filterQueryInfo = extractFilterQueryInfo(request);

        QueryRequest queryRequest = downloadQueryTemplate.newBuilder()
                .setQuery(QuickGOQuery.createAllQuery())
                .addFilters(filterQueryInfo.getFilterQueries())
                .build();

        // -1 indicates no timeout
        // todo: instantiate with -1 and find way to test this
        // maybe relevant: https://jira.spring.io/browse/SPR-13079
        ResponseBodyEmitter emitter = new ResponseBodyEmitter();
        taskExecutor.execute(() -> emitStreamWithMediaType(
                emitter,
                streamSearchResults(queryRequest, queryTemplate, annotationSearchService,
                        resultTransformerChain, filterQueryInfo.getFilterContext(), request.getDownloadLimit()),
                mediaTypeAcceptHeader));

        return ResponseEntity
                .ok()
                .headers(createHttpDownloadHeader(mediaTypeAcceptHeader))
                .body(emitter);
    }

    private DefaultSearchQueryTemplate createSearchQueryTemplate(
            SearchServiceConfig.AnnotationCompositeRetrievalConfig retrievalConfig) {
        DefaultSearchQueryTemplate template = new DefaultSearchQueryTemplate();
        template.setReturnedFields(retrievalConfig.getSearchReturnedFields());
        return template;
    }

    private DefaultSearchQueryTemplate createDownloadSearchQueryTemplate(
            SearchServiceConfig.AnnotationCompositeRetrievalConfig retrievalConfig) {
        DefaultSearchQueryTemplate template = new DefaultSearchQueryTemplate();
        template.setReturnedFields(retrievalConfig.getSearchReturnedFields());
        template.setPage(createFirstCursorPage(retrievalConfig.getDownloadPageSize()));
        retrievalConfig.getDownloadSortCriteria()
                .forEach(criterion ->
                        template.addSortCriterion(criterion.getSortField().getField(), criterion.getSortOrder()));
        return template;
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

    private void checkBindingErrors(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ParameterBindingException(bindingResult);
        }
    }

    private HttpHeaders createHttpDownloadHeader(MediaType mediaType) {
        HttpHeaders httpHeaders = new HttpHeaders();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("-N-yyyyMMdd");
        String extension = "." + mediaType.getSubtype();
        String fileName = "QuickGO-annotations" + now.format(format) + extension;
        httpHeaders.setContentDispositionFormData("attachment", fileName);
        return httpHeaders;
    }

    private void emitStreamWithMediaType(
            ResponseBodyEmitter emitter,
            Stream<QueryResult<Annotation>> annotationResultStream,
            MediaType mediaType) {
        try {
            emitter.send(annotationResultStream, mediaType);
        } catch (IOException e) {
            LOGGER.error("Failed to stream annotation results", e);
        }
        emitter.complete();
        LOGGER.info("Emitted response stream -- which will be written by the HTTP message converter for: " + mediaType);
    }

    private interface FilterQueryInfo {
        Set<QuickGOQuery> getFilterQueries();

        FilterContext getFilterContext();
    }
}
